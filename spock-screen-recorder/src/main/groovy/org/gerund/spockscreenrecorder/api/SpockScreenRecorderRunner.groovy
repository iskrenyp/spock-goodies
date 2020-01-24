package org.gerund.spockscreenrecorder.api

import groovy.util.logging.Slf4j
import org.gerund.core.api.constants.CommonFileUtils
import org.gerund.core.api.exception.ISpockReporterException
import org.gerund.spockscreenrecorder.ScreenRecorderConfigManager
import org.gerund.spockscreenrecorder.SpockScreenRecorderConfig
import org.gerund.spockscreenrecorder.SpockScreenRecorderConfigValidator

@Slf4j
class SpockScreenRecorderRunner {

    ScreenRecorderConfigManager configManager
    SpockScreenRecorderConfig config
    SpockScreenRecorderService service
    String recorderName

    SpockScreenRecorderRunner(String recorderName) {
        this.recorderName = recorderName
    }

    def recorderStart() {
        configManager = new ScreenRecorderConfigManager(recorderName)
        try {
            config = configManager
                    .findConfigFile(CommonFileUtils.SCREEN_RECORDER_CONFIG_FILE_NAME, CommonFileUtils.CURRENT_USER_DIR_FILE_PATH)
                    .toConfig()
                    .fetchJobConfig(new SpockScreenRecorderConfigValidator())
        } catch (ISpockReporterException e) {
            config = null
            log.error("There was an error while fetching the ScreenRecorderConfig.groovy file, caused by: $e")
            log.info("Cancelling video recording")
        }
        if (config) {
            service = new SpockScreenRecorderService(config)
            service
                    .configureRecorder()
                    .startRecording()
        }
    }

    def recorderStop(){
        if (service) service.stopRecording()
    }
}
