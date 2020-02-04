package com.github.iskrenyp.spockscreenrecorder.api

import com.github.iskrenyp.core.api.config.IConfigFileRuler
import com.github.iskrenyp.core.api.exception.ISpockGoodyExtensionException
import com.github.iskrenyp.spockscreenrecorder.exception.SpockScreenRecorderException
import groovy.util.logging.Slf4j
import org.monte.media.Format
import org.monte.media.FormatKeys
import org.monte.media.math.Rational

import java.awt.GraphicsConfiguration
import java.awt.GraphicsEnvironment
import java.awt.Rectangle

import static com.github.iskrenyp.core.api.constants.CommonFileUtils.CURRENT_USER_DIR_FILE_PATH
import static org.monte.media.FormatKeys.EncodingKey
import static org.monte.media.FormatKeys.FrameRateKey
import static org.monte.media.FormatKeys.KeyFrameIntervalKey
import static org.monte.media.FormatKeys.MIME_AVI
import static org.monte.media.FormatKeys.MediaTypeKey
import static org.monte.media.FormatKeys.MimeTypeKey
import static org.monte.media.VideoFormatKeys.CompressorNameKey
import static org.monte.media.VideoFormatKeys.DepthKey
import static org.monte.media.VideoFormatKeys.ENCODING_AVI_TECHSMITH_SCREEN_CAPTURE
import static org.monte.media.VideoFormatKeys.QualityKey

@Slf4j
class SpockScreenRecorderRunner implements IConfigFileRuler {

    static final String SCREEN_RECORDER_CONFIG_FILE_NAME = 'ScreenRecorderConfig.groovy'

    static final GraphicsConfiguration DEFAULT_GRAPHICS_CONFIGURATION = GraphicsEnvironment
            .localGraphicsEnvironment
            .defaultScreenDevice
            .defaultConfiguration
    static final Format DEFAULT_FILE_FORMAT = new Format(MediaTypeKey, FormatKeys.MediaType.FILE, MimeTypeKey, MIME_AVI)
    static final Format DEFAULT_SCREEN_FORMAT = new Format(
            MediaTypeKey, FormatKeys.MediaType.VIDEO, EncodingKey, ENCODING_AVI_TECHSMITH_SCREEN_CAPTURE,
            CompressorNameKey, ENCODING_AVI_TECHSMITH_SCREEN_CAPTURE,
            DepthKey, 24, FrameRateKey, Rational.valueOf(15),
            QualityKey, 1.0f,
            KeyFrameIntervalKey, 15 * 60)
    static final Format DEFAULT_MOUSE_FORMAT = new Format(MediaTypeKey, FormatKeys.MediaType.VIDEO, EncodingKey, "black", FrameRateKey, Rational.valueOf(30))

    SpockScreenRecorderConfig config
    SpockScreenRecorderServiceImpl service
    String recorderName

    SpockScreenRecorderRunner(String recorderName) {
        this.recorderName = recorderName
    }

    def recorderStart() {
        try {
            findConfigFile SCREEN_RECORDER_CONFIG_FILE_NAME, CURRENT_USER_DIR_FILE_PATH
            if (isEntryValid(recorderName)) {
                config = configureEntity(recorderName) { Map<String, ?> entry ->
                    config = new SpockScreenRecorderConfig()
                    config.graphicsConfiguration = (entry.containsKey('graphicsConfiguration') && entry.graphicsConfiguration instanceof GraphicsConfiguration)
                            ? entry.graphicsConfiguration
                            : DEFAULT_GRAPHICS_CONFIGURATION
                    config.captureArea = (entry.containsKey('captureArea') && entry.captureArea instanceof Rectangle)
                            ? entry.captureArea
                            : config.graphicsConfiguration.getBounds() as Rectangle
                    config.fileFormat = (entry.containsKey('fileFormat') && entry.fileFormat instanceof Format)
                            ? entry.fileFormat
                            : DEFAULT_FILE_FORMAT
                    config.screenFormat = (entry.containsKey('screenFormat') && entry.screenFormat instanceof Format)
                            ? entry.screenFormat
                            : DEFAULT_SCREEN_FORMAT
                    config.mouseFormat = (entry.containsKey('mouseFormat') && entry.mouseFormat instanceof Format)
                            ? entry.mouseFormat
                            : DEFAULT_MOUSE_FORMAT
                    config.audioFormat = (entry.containsKey('audioFormat') && entry.audioFormat instanceof Format)
                            ? entry.audioFormat
                            : null
                    config.pathToMovieFolder = (entry.containsKey('pathToMovieFolder') && entry.pathToMovieFolder instanceof String)
                            ? entry.pathToMovieFolder
                            : 'build/video'
                    config
                }
            }
        } catch (ISpockGoodyExtensionException e) {
            config = null
            log.error("There was an error while fetching the ScreenRecorderConfig.groovy file, caused by: $e")
            log.info("Cancelling video recording")
        }
        if (config) {
            service = new SpockScreenRecorderServiceImpl(config)
            try {
                service
                        .configureRecorder()
                        .startRecording()
            } catch (SpockScreenRecorderException e) {
                log.error("There was an error, while configuring and starting the Screen Recorder: $e")
                service = null
            }
        }
    }

    def recorderStop(){
        if (service) {
            try {
                service.stopRecording()
            } catch (SpockScreenRecorderException e) {
                log.error("There was an error, while stopping the Screen Recorder: $e")
            }
        }
    }
}
