package org.gerund.spockscreenrecorder.api

import groovy.util.logging.Slf4j
import org.spockframework.runtime.AbstractRunListener
import org.spockframework.runtime.extension.AbstractAnnotationDrivenExtension
import org.spockframework.runtime.extension.IMethodInterceptor
import org.spockframework.runtime.extension.IMethodInvocation
import org.spockframework.runtime.model.FeatureInfo
import org.spockframework.runtime.model.SpecInfo

@Slf4j
class SpockScreenRecorderAnnotationDrivenExtension extends AbstractAnnotationDrivenExtension<MakeVideo> {

    @Override
    void visitSpecAnnotation(MakeVideo annotation, SpecInfo spec) {
        spec.addListener(new AbstractRunListener() {

            SpockScreenRecorderRunner recorderRunner

            @Override
            void afterSpec(SpecInfo specInfo) {
                recorderRunner.recorderStop()
            }

            @Override
            void beforeSpec(SpecInfo specInfo) {
                recorderRunner = new SpockScreenRecorderRunner(annotation.value())
                recorderRunner.recorderStart()
            }
        })
    }

    @Override
    void visitFeatureAnnotation(MakeVideo annotation, FeatureInfo feature) {
        feature.addInterceptor(new ScreenRecordingMethodInterceptor(recorderName: annotation.value()))
    }

    private static class ScreenRecordingMethodInterceptor implements IMethodInterceptor {

        String recorderName

        @Override
        void intercept(IMethodInvocation invocation) throws Throwable {
            SpockScreenRecorderRunner recorderRunner = new SpockScreenRecorderRunner(recorderName)
            recorderRunner.recorderStart()
            try {
                invocation.proceed()
            } finally {
                recorderRunner.recorderStop()
            }
        }
    }
}
