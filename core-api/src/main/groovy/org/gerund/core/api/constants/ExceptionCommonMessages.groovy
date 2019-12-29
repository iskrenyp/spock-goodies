package org.gerund.core.api.constants

class ExceptionCommonMessages {

    static final String SLACK_CONFIG_FILE_NOT_FOUND = "The SlackConfig.groovy file was not found within the current user.dir"
    static final String INVALID_URL_FOR_SLACK_CONFIG_FILE = "There was an error while getting the URL of the SlackConfig file"
    static final String MORE_THAN_ONE_SLACK_CONFIG_FILE_FOUND = "More that one SlackConfig.groovy files were found within user.dir"
    static final String JOB_CONFIG_NOT_FOUND = "The configuration for your job was not found"
    static final String CONNECTION_TO_SLACK_FAILED = "Connection to slack failed!"
    static final String DISCONNECTING_FROM_SLACK_FAILED = "Disconnecting from slack failed!"
    static final String SLACK_CHANNEL_NOT_FOUND = "No slack channel was found for the respective name: "
    static final String INVALID_LINK_TO_REPORT = "There was an error while validating the report link inside your config file"
    static final String BAD_SLACK_JOB_CONFIGURATION = "Improper job configuration"
}
