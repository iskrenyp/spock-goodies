package com.github.iskrenyp.slackreporter.service

import com.github.iskrenyp.core.api.config.IConfigFileRuler
import com.github.iskrenyp.core.api.exception.ISpockGoodyExtensionException
import com.github.iskrenyp.slackreporter.SlackReporterConfigEntity
import com.github.iskrenyp.slackreporter.exception.SpockSlackReporterException
import com.github.iskrenyp.slackreporter.slack.ISlackClient
import com.ullink.slack.simpleslackapi.SlackPreparedMessage
import groovy.util.logging.Slf4j
import static com.github.iskrenyp.core.api.constants.CommonFileUtils.CURRENT_USER_DIR_FILE_PATH



@Slf4j
class SlackReportingServiceImpl implements ISlackReportingService, IConfigFileRuler  {

    static final String SLACK_REPORTER_CONFIG_NAME = 'SlackReporterConfig.groovy'

    String reporterName
    ISlackClient slackClient

    SlackReportingServiceImpl(String reporterName, ISlackClient slackClient) {
        this.reporterName = reporterName
        this.slackClient = slackClient
    }

    @Override
    SlackReporterConfigEntity prepareNotification(String reporterName, Integer failsCount, List<String> failedMethods) {
        log.info("$failsCount fails identified. Preparing Slack notification for $reporterName...")
        SlackReporterConfigEntity configEntity
        try {
            findConfigFile SLACK_REPORTER_CONFIG_NAME, CURRENT_USER_DIR_FILE_PATH
            if (isEntryValid(reporterName)) configureEntity(reporterName) { Map<String, ?> entry ->
                configEntity = new SlackReporterConfigEntity()
                configEntity.reportUrl = entry.reportUrl
                configEntity.channelName = entry.channelName
                configEntity.userToken = entry.userToken
                configEntity.environment = entry.environment
                configEntity.message = (entry.containsKey('message') && entry.message instanceof SlackPreparedMessage)
                        ? entry.message
                        : {
                    String defaultMessageText = """
${this.reporterName} : Test report for ${configEntity.environment} env: ${configEntity.reportUrl}
[There were $failsCount fails, related to the following cases: ${ failedMethods.each { "$it, " } }]
                    """
                    new SlackPreparedMessage.Builder().withMessage(defaultMessageText).build()
                }.call()
                configEntity
            } else {
                configEntity = null
                log.error("$reporterName couldn't be found in the $SLACK_REPORTER_CONFIG_NAME file")
            }
        } catch (ISpockGoodyExtensionException e) {
            configEntity = null
            log.error("There was an error while configure the slack notification, caused by: $e")
            log.info("Cancelling slack notification")
        }
        configEntity
    }

    @Override
    def sendNotification(SlackReporterConfigEntity configEntity) {
        log.info("Sending Slack notification...")
        try {
            slackClient.connectToSlack(configEntity.userToken)
            slackClient.prepareMessage(configEntity) { SlackPreparedMessage message ->
                slackClient.sendMessage(message, configEntity.channelName)
            }
            slackClient.disconnectFromSlack()
        } catch(SpockSlackReporterException e) {
            log.error("There was an error while sending the Slack message", e)
        }
    }
}
