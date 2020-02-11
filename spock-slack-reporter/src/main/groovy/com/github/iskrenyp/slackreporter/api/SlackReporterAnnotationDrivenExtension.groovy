package com.github.iskrenyp.slackreporter.api

import com.github.iskrenyp.slackreporter.SlackReporterConfigEntity
import com.github.iskrenyp.slackreporter.service.SlackReportingServiceImpl
import com.github.iskrenyp.slackreporter.slack.SlackSimpleClient
import org.spockframework.runtime.AbstractRunListener
import org.spockframework.runtime.extension.AbstractAnnotationDrivenExtension
import org.spockframework.runtime.model.ErrorInfo
import org.spockframework.runtime.model.SpecInfo

class SlackReporterAnnotationDrivenExtension extends AbstractAnnotationDrivenExtension<ReportOnSlack> {

    SlackReportingServiceImpl service
    SlackReporterConfigEntity configEntity

    List<String> failedMethods = []
    Integer failsCount = 0

    @Override
    void visitSpecAnnotation(ReportOnSlack annotation, SpecInfo spec) {
        spec.addListener(new AbstractRunListener() {

            @Override
            void afterSpec(SpecInfo specInfo) {
                configEntity = service.prepareNotification(annotation.value(), failsCount, failedMethods)
                if (configEntity) service.sendNotification(configEntity)
            }

            @Override
            void beforeSpec(SpecInfo specInfo) {
                service = new SlackReportingServiceImpl(annotation.value(), new SlackSimpleClient())
            }

            @Override
            void error(ErrorInfo error) {
                failsCount++
                failedMethods << error.method.feature.name
            }
        })
    }
}
