package org.gerund.api

import groovy.util.logging.Slf4j
import org.gerund.SlackConfigManager
import org.gerund.SpockSlackNotification
import org.gerund.slack.SlackSimpleClient
import org.spockframework.runtime.AbstractRunListener
import org.spockframework.runtime.extension.AbstractAnnotationDrivenExtension
import org.spockframework.runtime.model.SpecInfo


@Slf4j
class SlackReporterAnnotationDrivenExtension extends AbstractAnnotationDrivenExtension<ReportOnSlack> {

    SlackConfigManager configManager
    SpockSlackNotification notification

    @Override
    void visitSpecAnnotation(ReportOnSlack annotation, SpecInfo spec) {
        spec.addListener(new AbstractRunListener() {
            /**
             * If there's a (html) report file for a particular suit, and this report's content
             * is different from the one in the initially intercepted report file, then the
             * suit will be reported on Skype. Otherwise it is implied that the build has failed with exception
             * different than the one related to failing test cases
             * @param specInfo
             */
            @Override
            void afterSpec(SpecInfo specInfo) {
                if (notification.jobConfigValidated && notification.enabled) {
                    new SlackReportingService(new SlackSimpleClient(), notification).sendNotification()
                }
            }
            /**
             * Reports on Skype the start of the test execution for a particular suit
             * If there's an already existing report (html) file for that suit,
             * it will be stored for evaluation in the afterSpec method
             * @param specInfo
             */
            @Override
            void beforeSpec(SpecInfo specInfo) {
                configManager = new SlackConfigManager(annotation.jobName())
                notification = SpockSlackNotification.create(configManager, specInfo.name)
            }
        })
    }
}
