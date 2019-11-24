package org.gerund


import groovy.transform.CompileStatic
import static groovy.io.FileType.FILES
import static org.gerund.constants.CommonFilePaths.CURRENT_USER_DIR_FILE_PATH
import static org.gerund.constants.CommonFilePaths.SLACK_CONFIG_FILE_NAME

@CompileStatic
@Singleton
class SlackConfigFileManager implements RecursiveFileFinder{

    File file = findFileInSourceRecursively FILES, SLACK_CONFIG_FILE_NAME, CURRENT_USER_DIR_FILE_PATH
    ConfigObject configObject = new ConfigSlurper().parse file.toURI().toURL()

}
