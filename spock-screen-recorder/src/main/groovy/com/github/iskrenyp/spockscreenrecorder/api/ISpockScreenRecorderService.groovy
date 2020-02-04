package com.github.iskrenyp.spockscreenrecorder.api

import com.github.iskrenyp.spockscreenrecorder.exception.SpockScreenRecorderException
import org.monte.screenrecorder.ScreenRecorder

import java.awt.GraphicsConfiguration

trait ISpockScreenRecorderService {

    GraphicsConfiguration graphicsConfiguration
    ScreenRecorder screenRecorder

    abstract ISpockScreenRecorderService configureRecorder() throws SpockScreenRecorderException

    abstract def startRecording() throws SpockScreenRecorderException

    abstract def stopRecording() throws SpockScreenRecorderException

}