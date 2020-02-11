package com.github.iskrenyp.slackreporter.slack

import com.github.iskrenyp.core.api.config.IConfigurableEntity
import com.github.iskrenyp.slackreporter.SlackReporterConfigEntity
import com.github.iskrenyp.slackreporter.exception.SpockSlackReporterException
import com.ullink.slack.simpleslackapi.SlackChannel
import com.ullink.slack.simpleslackapi.SlackPreparedMessage
import com.ullink.slack.simpleslackapi.SlackSession
import com.ullink.slack.simpleslackapi.impl.SlackSessionFactory
import groovy.util.logging.Slf4j
import static com.github.iskrenyp.slackreporter.exception.SlackReporterExceptionMessages.CONNECTING_TO_SLACK_FAILED
import static com.github.iskrenyp.slackreporter.exception.SlackReporterExceptionMessages.DISCONNECTING_FROM_SLACK_FAILED
import static com.github.iskrenyp.slackreporter.exception.SlackReporterExceptionMessages.SLACK_CHANNEL_NOT_FOUND

@Slf4j
class SlackSimpleClient implements ISlackClient {

    SlackSession slackSession

    @Override
    def connectToSlack(String userToken) throws SpockSlackReporterException {
        slackSession = SlackSessionFactory.createWebSocketSlackSession userToken
        try {
            slackSession.connect()
        } catch (IOException e) {
            throw new SpockSlackReporterException(CONNECTING_TO_SLACK_FAILED, e)
        }
    }

    @Override
    def disconnectFromSlack() throws SpockSlackReporterException {
        try {
            slackSession.disconnect()
        } catch (IOException e) {
            throw new SpockSlackReporterException(DISCONNECTING_FROM_SLACK_FAILED, e)
        }
    }

    @Override
    def <T> T prepareMessage(SlackReporterConfigEntity entity, Closure<T> consumeMessage) {
        entity.message.with consumeMessage
    }

    @Override
    def sendMessage(SlackPreparedMessage message, String channelName) throws SpockSlackReporterException {
        if (slackSession.connected) {
            log.info("Sending message ${message.message} to the $channelName channel")
            SlackChannel channel = slackSession.findChannelByName(channelName)
            if (!channel) throw new SpockSlackReporterException("$SLACK_CHANNEL_NOT_FOUND $channelName")
            else slackSession.sendMessage(channel, message)
        }
    }
}
