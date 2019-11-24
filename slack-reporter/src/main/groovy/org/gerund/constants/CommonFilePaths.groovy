package org.gerund.constants

import static java.io.File.separator

class CommonFilePaths {

    static final String CURRENT_USER_DIR_FILE_PATH = '.'
    static final String OUT_DIR_FILE_PATH = "${separator}out$separator"
    static final String BUILD_DIR_FILE_PATH = "${separator}build$separator"
    static final String SLACK_CONFIG_FILE_NAME = 'SlackConfig.groovy'
}
