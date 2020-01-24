package org.gerund.api

import groovy.util.logging.Slf4j
import org.gerund.SpockSlackNotification
import org.gerund.core.api.exception.ISpockReporterException
import org.gerund.core.api.service.ITestReportingService
import org.gerund.exceptions.SlackReporterConnectionException
import org.gerund.core.api.client.ISlackClient
import static org.gerund.core.api.constants.ExceptionCommonMessages.ERROR_WHILE_SENDING_SLACK_NOTIFICATION

@Slf4j
class SlackReportingService implements ITestReportingService {

    ISlackClient slackClient
    SpockSlackNotification notification

    SlackReportingService(ISlackClient slackClient, SpockSlackNotification notification) {
        this.slackClient = slackClient
        this.notification = notification
    }

    @Override
    def sendNotification() throws SlackReporterConnectionException {
        try {
            slackClient
                    .connectToSlack(notification.userToken)
                    .sendMessage(notification.message, notification.channelName)
                    .disconnectFromSlack()
        } catch(ISpockReporterException e) { log.error ERROR_WHILE_SENDING_SLACK_NOTIFICATION, e }
    }
}
