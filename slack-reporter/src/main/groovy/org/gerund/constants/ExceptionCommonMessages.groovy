package org.gerund.constants

class ExceptionCommonMessages {

    static final String SLACK_CONFIG_FILE_NOT_FOUND = "The SlackConfig.groovy file was not found within the current user.dir"
    static final String NO_FILES_OR_DIRECTORIES_FOUND = "No corresponding files/directories were found"
    static final String MORE_THAN_ONE_FILE_FOUND = { String fileName -> "More that one file with name $fileName were found" }
}
