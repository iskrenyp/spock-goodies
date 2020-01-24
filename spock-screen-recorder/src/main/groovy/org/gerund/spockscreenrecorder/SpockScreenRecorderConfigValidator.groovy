package org.gerund.spockscreenrecorder

import org.gerund.core.api.config.IJobConfigValidator
import org.gerund.core.api.exception.ISpockReporterException
import org.monte.media.Format
import org.monte.media.FormatKeys
import org.monte.media.math.Rational

import java.awt.GraphicsConfiguration
import java.awt.GraphicsEnvironment
import java.awt.Rectangle

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

class SpockScreenRecorderConfigValidator implements IJobConfigValidator {

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

    @Override
    Map<String, ?> validate(Map<String, ?> jobConfigurable) throws ISpockReporterException {
        Map<String, ?> validatedConfig = [:]
        validatedConfig.graphicsConfiguration = (jobConfigurable.containsKey('graphicsConfiguration') && jobConfigurable.graphicsConfiguration instanceof GraphicsConfiguration)
                ? jobConfigurable.graphicsConfiguration
                : DEFAULT_GRAPHICS_CONFIGURATION
        validatedConfig.captureArea = (jobConfigurable.containsKey('captureArea') && jobConfigurable.captureArea instanceof Rectangle)
                ? jobConfigurable.captureArea
                : validatedConfig.graphicsConfiguration.getBounds() as Rectangle
        validatedConfig.fileFormat = (jobConfigurable.containsKey('fileFormat') && jobConfigurable.fileFormat instanceof Format)
                ? jobConfigurable.fileFormat
                : DEFAULT_FILE_FORMAT
        validatedConfig.screenFormat = (jobConfigurable.containsKey('screenFormat') && jobConfigurable.screenFormat instanceof Format)
                ? jobConfigurable.screenFormat
                : DEFAULT_SCREEN_FORMAT
        validatedConfig.mouseFormat = (jobConfigurable.containsKey('mouseFormat') && jobConfigurable.mouseFormat instanceof Format)
                ? jobConfigurable.mouseFormat
                : DEFAULT_MOUSE_FORMAT
        validatedConfig.audioFormat = (jobConfigurable.containsKey('audioFormat') && jobConfigurable.audioFormat instanceof Format)
                ? jobConfigurable.audioFormat
                : null
        validatedConfig.pathToMovieFolder = (jobConfigurable.containsKey('pathToMovieFolder') && jobConfigurable.pathToMovieFolder instanceof String)
                ? jobConfigurable.pathToMovieFolder
                : 'build/video'
        return validatedConfig
    }
}
