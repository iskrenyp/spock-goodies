package com.github.iskrenyp.slackreporter.slack

import com.github.iskrenyp.slackreporter.SlackReporterConfigEntity
import com.github.iskrenyp.slackreporter.exception.SpockSlackReporterException
import com.ullink.slack.simpleslackapi.SlackPreparedMessage

trait ISlackClient {

    abstract def connectToSlack(String userToken) throws SpockSlackReporterException

    abstract def disconnectFromSlack() throws SpockSlackReporterException

    abstract <T> T prepareMessage(SlackReporterConfigEntity entity, Closure<T> consumeMessage)

    abstract def sendMessage(SlackPreparedMessage message, String channelName) throws SpockSlackReporterException

}