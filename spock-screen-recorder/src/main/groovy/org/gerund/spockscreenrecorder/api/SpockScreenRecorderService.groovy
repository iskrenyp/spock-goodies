package org.gerund.spockscreenrecorder.api

import groovy.util.logging.Slf4j
import org.gerund.spockscreenrecorder.SpockScreenRecorderConfig
import org.gerund.spockscreenrecorder.exception.SpockScreenRecorderException
import org.monte.screenrecorder.ScreenRecorder
import static org.gerund.spockscreenrecorder.exception.SpockScreenRecorderExceptionMessages.STARTING_SCREEN_RECORDER_FAILED
import static org.gerund.spockscreenrecorder.exception.SpockScreenRecorderExceptionMessages.STOPPING_SCREEN_RECORDER_FAILED
import static org.gerund.spockscreenrecorder.exception.SpockScreenRecorderExceptionMessages.INSTANTIATING_SCREEN_RECORDER_FAILED

@Slf4j
class SpockScreenRecorderService implements ISpockScreenRecorder {

    SpockScreenRecorderConfig config

    SpockScreenRecorderService(SpockScreenRecorderConfig config) {
        this.config = config
    }

    @Override
    SpockScreenRecorderService configureRecorder() throws SpockScreenRecorderException {
        log.info("Starting configuration for screen recorder with following properties: ${config.toMap()}")
        try {
            this.graphicsConfiguration = config.graphicsConfiguration
            this.screenRecorder = new ScreenRecorder(
                    this.graphicsConfiguration,
                    config.captureArea,
                    config.fileFormat,
                    config.screenFormat,
                    config.mouseFormat,
                    config.audioFormat,
                    new File(config.pathToMovieFolder))
            log.info("Screen recorder successfully configured")
            return this
        } catch (Exception e) {
            log.error("There was an error while configuring the screen recorder")
            throw new SpockScreenRecorderException(INSTANTIATING_SCREEN_RECORDER_FAILED, e)
        }
    }

    @Override
    def startRecording() throws SpockScreenRecorderException {
        log.info("Starting video recording...")
        try {
            this.screenRecorder.start()
        } catch (IOException e) {
            throw new SpockScreenRecorderException(STARTING_SCREEN_RECORDER_FAILED, e)
        }
    }

    @Override
    def stopRecording() throws SpockScreenRecorderException {
        log.info("Stopping video recording")
        try {
            this.screenRecorder.stop()
        } catch (IOException e) {
            throw new SpockScreenRecorderException(STOPPING_SCREEN_RECORDER_FAILED, e)
        }
    }
}
