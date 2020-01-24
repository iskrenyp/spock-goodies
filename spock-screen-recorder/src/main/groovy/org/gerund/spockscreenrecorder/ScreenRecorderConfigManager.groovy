package org.gerund.spockscreenrecorder

import groovy.util.logging.Slf4j
import org.gerund.core.api.config.IConfigFileManager
import org.gerund.core.api.config.IJobConfigValidator
import org.gerund.core.api.exception.ISpockReporterException
import org.gerund.spockscreenrecorder.exception.SpockScreenRecorderException
import static org.gerund.spockscreenrecorder.exception.SpockScreenRecorderExceptionMessages.CONFIG_FILE_NOT_FOUND
import static org.gerund.spockscreenrecorder.exception.SpockScreenRecorderExceptionMessages.INVALID_URL_FOR_CONFIG_FILE
import static org.gerund.spockscreenrecorder.exception.SpockScreenRecorderExceptionMessages.MORE_THAN_ONE_CONFIG_FILES_FOUND
import static org.gerund.spockscreenrecorder.exception.SpockScreenRecorderExceptionMessages.SCREEN_RECORDER_NOT_FOUND

class ScreenRecorderConfigManager implements IConfigFileManager {

    String screenRecorderName

    ScreenRecorderConfigManager(String screenRecorderName) {
        this.screenRecorderName = screenRecorderName
    }

    @Override
    ScreenRecorderConfigManager findConfigFile(String configFileName, String startingPath) throws ISpockReporterException {
        try {
            findSourceFilesRecursively(configFileName, startingPath) { List<File> files ->
                if (files.size() > 1) throw new SpockScreenRecorderException(MORE_THAN_ONE_CONFIG_FILES_FOUND)
                configFile = files.first()
            }
        } catch (FileNotFoundException e) {
            throw new SpockScreenRecorderException(CONFIG_FILE_NOT_FOUND, e)
        }
        return this
    }

    @Override
    ScreenRecorderConfigManager toConfig() throws ISpockReporterException {
        try {
            config = new ConfigSlurper().parse(configFile.toURI().toURL())
        } catch (MalformedURLException e) {
            throw new SpockScreenRecorderException(INVALID_URL_FOR_CONFIG_FILE, e)
        }
        this
    }

    @Override
    SpockScreenRecorderConfig fetchJobConfig(IJobConfigValidator validator) throws ISpockReporterException {
        if (config.containsKey(screenRecorderName) && config.get(screenRecorderName) instanceof Map) {
            return new SpockScreenRecorderConfig(validator.validate(config[screenRecorderName]))
        } else throw new SpockScreenRecorderException(SCREEN_RECORDER_NOT_FOUND)
    }
}
