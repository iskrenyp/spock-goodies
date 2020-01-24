package org.gerund.spockscreenrecorder

import groovy.transform.TupleConstructor
import org.gerund.core.api.config.IJobConfigurable
import org.gerund.core.api.exception.ISpockReporterException
import org.monte.media.Format

import java.awt.GraphicsConfiguration
import java.awt.Rectangle

@TupleConstructor
class SpockScreenRecorderConfig implements IJobConfigurable {

    GraphicsConfiguration graphicsConfiguration
    Rectangle captureArea
    Format fileFormat
    Format screenFormat
    Format mouseFormat
    Format audioFormat
    String pathToMovieFolder

    @Override
    Boolean isHostAllowedToReport() {
        return null
    }

    @Override
    Boolean reportFileUrlIsValid() throws ISpockReporterException {
        return null
    }
}
