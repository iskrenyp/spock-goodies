package org.gerund

import groovy.util.logging.Slf4j
import org.gerund.core.api.config.IConfigFileManager
import org.gerund.core.api.config.IJobConfigValidator
import org.gerund.core.api.exception.ISpockReporterException
import org.gerund.exceptions.SlackReporterFileException

import static groovy.io.FileType.FILES
import static org.gerund.core.api.constants.CommonFileUtils.BUILD_DIR_FILE_PATH
import static org.gerund.core.api.constants.ExceptionCommonMessages.JOB_CONFIG_NOT_FOUND
import static org.gerund.core.api.constants.ExceptionCommonMessages.INVALID_URL_FOR_SLACK_CONFIG_FILE
import static org.gerund.core.api.constants.ExceptionCommonMessages.SLACK_CONFIG_FILE_NOT_FOUND
import static org.gerund.core.api.constants.ExceptionCommonMessages.MORE_THAN_ONE_SLACK_CONFIG_FILE_FOUND
import static org.gerund.core.api.constants.CommonFileUtils.OUT_DIR_FILE_PATH

@Slf4j
class SlackConfigManager implements IConfigFileManager {

    SlackConfigManager(String jobName) {
        this.jobName = jobName
    }

    @Override
    SlackConfigManager findConfigFile(String configFileName, String startingPath) throws SlackReporterFileException {
        log.info("Looking for the SlackConfig.groovy file")
        List<File> foundFiles = []
        try {
            new File(startingPath).eachFileRecurse(FILES) { File file ->
                if (file.name.equalsIgnoreCase(configFileName)
                        && !file.path.contains(OUT_DIR_FILE_PATH)
                        && !file.path.contains(BUILD_DIR_FILE_PATH) ) foundFiles << file
            }
        } catch (FileNotFoundException e) {
            log.error("There was an error while trying to find the SlackConfig.groovy file. Please make sure it is within the current project")
            throw new SlackReporterFileException(SLACK_CONFIG_FILE_NOT_FOUND, e)
        }
        if (foundFiles.size() > 1) throw new SlackReporterFileException(MORE_THAN_ONE_SLACK_CONFIG_FILE_FOUND)
        configFile = foundFiles.first()
        log.info("SlackConfig file was successfully found")
        this
    }

    @Override
    SlackReporterJobConfig fetchJobConfig(IJobConfigValidator validator) throws ISpockReporterException {
        if (config.containsKey(jobName) && config.get(jobName) instanceof Map) {
            log.info("Slack reporting configuration for job $jobName fetched")
            return new SlackReporterJobConfig(validator.validate(config[jobName]))
        } else throw new SlackReporterFileException(JOB_CONFIG_NOT_FOUND)
    }

    @Override
    SlackConfigManager toConfig() throws SlackReporterFileException {
        try {
            config = new ConfigSlurper().parse(configFile.toURI().toURL())
        } catch (MalformedURLException e) {
            throw new SlackReporterFileException(INVALID_URL_FOR_SLACK_CONFIG_FILE, e)
        }
        this
    }
}
