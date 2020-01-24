package org.gerund

import groovy.transform.builder.Builder
import groovy.util.logging.Slf4j
import org.gerund.core.api.config.IConfigFileManager
import org.gerund.core.api.exception.ISpockReporterException

import static org.gerund.core.api.constants.CommonFileUtils.CURRENT_USER_DIR_FILE_PATH
import static org.gerund.core.api.constants.CommonFileUtils.SLACK_CONFIG_FILE_NAME
import static org.gerund.core.api.constants.SlackReporterJobConfigProps.MESSAGE_DEFAULT_VALUE
import static org.gerund.core.api.config.JobConfigValidator.SLACK_REPORTER_JOB_CONFIG_VALIDATOR

@Slf4j
@Builder
class SpockSlackNotification {

    Boolean enabled, jobConfigValidated
    String message, userToken, environment, channelName, linkToReportFile

    SpockSlackNotification() {}

    @Builder(builderClassName = 'ConfigManagerBuilder', builderMethodName = 'withConfigFileManager')
    static SpockSlackNotification create(IConfigFileManager configManager, String specName) {
        SpockSlackNotification notification = new SpockSlackNotification()
        try {
            SlackReporterJobConfig config = configManager
                    .findConfigFile(SLACK_CONFIG_FILE_NAME, CURRENT_USER_DIR_FILE_PATH)
                    .toConfig()
                    .fetchJobConfig(SLACK_REPORTER_JOB_CONFIG_VALIDATOR)
            if (config.isHostAllowedToReport() && config.reportFileUrlIsValid()) {
                notification.jobConfigValidated = true
                notification.enabled = true
                notification.linkToReportFile = config.linkToReportFile
                notification.environment = config.testEnvironment
                notification.userToken = config.userToken
                notification.channelName = config.slackChannel
                notification.message = "${notification.environment} : $specName : $MESSAGE_DEFAULT_VALUE ${notification.linkToReportFile}"
            } else notification.enabled = false
        } catch (ISpockReporterException e) {
            log.error("There was an error while trying to configure Slack notification. Reporting disabled due to $e")
            notification.jobConfigValidated = false
        }
        notification
    }
}
