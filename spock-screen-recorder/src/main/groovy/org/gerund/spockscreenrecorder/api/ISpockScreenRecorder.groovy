package org.gerund.spockscreenrecorder.api

import org.gerund.spockscreenrecorder.exception.SpockScreenRecorderException
import org.monte.screenrecorder.ScreenRecorder

import java.awt.GraphicsConfiguration

trait ISpockScreenRecorder {

    GraphicsConfiguration graphicsConfiguration
    ScreenRecorder screenRecorder

    abstract ISpockScreenRecorder configureRecorder() throws SpockScreenRecorderException

    abstract def startRecording() throws SpockScreenRecorderException

    abstract def stopRecording() throws SpockScreenRecorderException

}