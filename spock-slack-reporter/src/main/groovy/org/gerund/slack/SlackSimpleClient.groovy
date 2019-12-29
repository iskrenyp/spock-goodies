package org.gerund.slack

import com.ullink.slack.simpleslackapi.SlackChannel
import com.ullink.slack.simpleslackapi.SlackPreparedMessage
import com.ullink.slack.simpleslackapi.SlackSession
import com.ullink.slack.simpleslackapi.impl.SlackSessionFactory
import groovy.util.logging.Slf4j
import org.gerund.core.api.client.ISlackClient
import org.gerund.exceptions.SlackReporterConnectionException
import static org.gerund.core.api.constants.ExceptionCommonMessages.SLACK_CHANNEL_NOT_FOUND
import static org.gerund.core.api.constants.ExceptionCommonMessages.DISCONNECTING_FROM_SLACK_FAILED
import static org.gerund.core.api.constants.ExceptionCommonMessages.CONNECTION_TO_SLACK_FAILED

@Slf4j
class SlackSimpleClient implements ISlackClient {

    SlackSession slackSession

    @Override
    SlackSimpleClient connectToSlack(String userToken) throws SlackReporterConnectionException {
        slackSession = SlackSessionFactory.createWebSocketSlackSession userToken
        try {
            slackSession.connect()
            return this
        } catch (IOException e) {
            throw new SlackReporterConnectionException(CONNECTION_TO_SLACK_FAILED, e)
        }
    }

    @Override
    SlackSimpleClient disconnectFromSlack() throws SlackReporterConnectionException {
        try {
            slackSession.disconnect()
            return this
        } catch (IOException e) {
            throw new SlackReporterConnectionException(DISCONNECTING_FROM_SLACK_FAILED, e)
        }
    }

    @Override
    SlackSimpleClient sendMessage(String message, String channelName) throws SlackReporterConnectionException {
        if (slackSession.connected) {
            log.info("Sending message $message to the $channelName channel")
            SlackChannel channel = slackSession.findChannelByName(channelName)
            if (!channel) throw new SlackReporterConnectionException("$SLACK_CHANNEL_NOT_FOUND $channelName")
            else {
                SlackPreparedMessage slackMessage = new SlackPreparedMessage.Builder()
                        .withMessage(message)
                        .build()
                slackSession.sendMessage channel, slackMessage
            }
        }
        this
    }
}
