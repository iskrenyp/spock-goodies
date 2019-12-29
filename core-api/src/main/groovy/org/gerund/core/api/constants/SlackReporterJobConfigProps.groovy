package org.gerund.core.api.constants

class SlackReporterJobConfigProps {

    static final Map<String, Class> MANDATORY_FIELDS_MAP =
            [
                    'reportFrom' : ArrayList,
                    'linkToReportFile' : String,
                    'testEnvironment' : String,
                    'slackChannel' : String,
                    'userToken' : String
            ]

    static final String MESSAGE_DEFAULT_VALUE = 'Find the most recent report here: '
}
