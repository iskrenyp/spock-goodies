package com.github.iskrenyp.spockscreenrecorder.api

import org.spockframework.runtime.extension.ExtensionAnnotation

import java.lang.annotation.ElementType
import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy
import java.lang.annotation.Target

@Retention(RetentionPolicy.RUNTIME)
@Target([ElementType.TYPE, ElementType.METHOD])
@ExtensionAnnotation(SpockScreenRecorderAnnotationDrivenExtension.class)
@interface MakeVideo {

    String value() default 'defaultRecorder'

}