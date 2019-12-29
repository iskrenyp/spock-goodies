package org.gerund.api

import org.gerund.SpockSlackNotification
import org.gerund.core.api.service.ITestReportingService
import org.gerund.exceptions.SlackReporterConnectionException
import org.gerund.core.api.client.ISlackClient

class SlackReportingService implements ITestReportingService {

    ISlackClient slackClient
    SpockSlackNotification notification

    SlackReportingService(ISlackClient slackClient, SpockSlackNotification notification) {
        this.slackClient = slackClient
        this.notification = notification
    }

    @Override
    def sendNotification() throws SlackReporterConnectionException {
        slackClient
                .connectToSlack(notification.userToken)
                .sendMessage(notification.message, notification.channelName)
                .disconnectFromSlack()
    }
}
