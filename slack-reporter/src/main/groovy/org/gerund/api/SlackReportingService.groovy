package org.gerund.api

import com.ullink.slack.simpleslackapi.SlackChannel
import com.ullink.slack.simpleslackapi.SlackPreparedMessage
import com.ullink.slack.simpleslackapi.SlackSession
import com.ullink.slack.simpleslackapi.impl.SlackSessionFactory
import groovy.util.logging.Slf4j
import org.spockframework.runtime.model.SpecInfo

@Slf4j
class SlackReportingService {

    Boolean isReportingEnabled
    String userToken
    String channel

    SlackSession slackSession
    SlackChannel slackChannel

    SlackReportingService(Boolean isReportingEnabled = true,
                          String userToken = '',
                          String channel = '') {

        this.isReportingEnabled = isReportingEnabled ? org.tarya.commons.config.Configuration.isHostAllowedForReporting() : false
        this.userToken = userToken
        this.channel = channel
    }

    def connectToSlack() {
        assert userToken : log.error("You are trying to connect to Slack with token = NULL")
        try {
            slackSession = SlackSessionFactory.createWebSocketSlackSession userToken
            slackSession.connect()
            log.info("Connection to Slack is SUCCESSFUL")
        } catch(IOException e) {
            log.error("There was an error while connecting to Slack! The error was $e")
        }
    }

    def disconnectFromSlack() {
        log.info("Closing the slack client")
        try {
            slackSession.disconnect()
        }catch(IOException e) {
            log.error("There was an error while trying to close the slack connection. The error is $e")
        }
    }


    def sendMessage(SlackPreparedMessage message, String channel=this.channel) {
        connectToSlack()
        log.info("Sending message: ${message.message} to the $channel channel")
        slackChannel = slackSession.findChannelByName channel
        assert slackChannel : log.error("You are trying to post a slack message to a NULL channel")
        slackSession.sendMessage slackChannel, message
        disconnectFromSlack()
    }

    def announceStartOfSuitExecution(SpecInfo spec) {
        if (isReportingEnabled) {
            SlackPreparedMessage message = new SlackPreparedMessage.Builder()
                    .withMessage("(Automated message) @here Execution of $spec.filename has just started against ${org.tarya.commons.config.Configuration.getConfigFile.name}")
                    .build()
            sendMessage message
        }
    }

    def reportProbablyFailedBuild() {
        if (isReportingEnabled) {
            SlackPreparedMessage message = new SlackPreparedMessage.Builder()
                .withMessage("(Automated message) It is very likely that the recent build has just unexpectedly FAILED!")
                .build()
            sendMessage message
        }
    }

    def reportTestSuit(File report) {
        if (isReportingEnabled) {
            log.info("Slack test results reporting is enabled. Starting process...")
            String reportUrl = org.tarya.commons.ast.transformations.skypereporter.SkypeReporter.buildJenkinsReportLink(report.name - '.html', new Date())
            assert reportUrl: log.error("Report URL is EMPTY! Please check")
            SlackPreparedMessage message = new SlackPreparedMessage.Builder()
                .withMessage("(Automated message) @here Dear subscribers, please find the link towards the most recent execution of ${report.name - '.html'} against ${org.tarya.commons.config.Configuration.getConfigFile.name} right here: $reportUrl")
                .build()
            sendMessage message
        }
    }
}
