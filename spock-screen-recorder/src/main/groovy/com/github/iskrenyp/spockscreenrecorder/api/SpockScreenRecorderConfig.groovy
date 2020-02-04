package com.github.iskrenyp.spockscreenrecorder.api

import com.github.iskrenyp.core.api.config.IConfigurableEntity
import groovy.transform.TupleConstructor
import org.monte.media.Format

import java.awt.GraphicsConfiguration
import java.awt.Rectangle

@TupleConstructor
class SpockScreenRecorderConfig implements IConfigurableEntity {

    GraphicsConfiguration graphicsConfiguration
    Rectangle captureArea
    Format fileFormat
    Format screenFormat
    Format mouseFormat
    Format audioFormat
    String pathToMovieFolder
}

