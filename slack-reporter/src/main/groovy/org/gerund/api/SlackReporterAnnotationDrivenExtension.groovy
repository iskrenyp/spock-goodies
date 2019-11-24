package org.gerund.api

import groovy.io.FileType
import org.gerund.RecursiveFileFinder
import org.gerund.SlackConfigFileManager
import org.gerund.SpockSlackNotification
import org.spockframework.runtime.AbstractRunListener
import org.spockframework.runtime.extension.AbstractAnnotationDrivenExtension
import org.spockframework.runtime.model.SpecInfo

import static groovy.io.FileType.FILES
import static org.gerund.constants.CommonFilePaths.getCURRENT_USER_DIR_FILE_PATH
import static org.gerund.constants.CommonFilePaths.getSLACK_CONFIG_FILE_NAME

class SlackReporterAnnotationDrivenExtension extends AbstractAnnotationDrivenExtension<ReportOnSlack> implements RecursiveFileFinder {


    Boolean configFileValid = SlackConfigFileManager.instance.configObject != null
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
                try {
                    def dir = new File('')
                    dir.eachFileRecurse (FILES) { file ->
                        if (file.name.contains(".${specInfo.name}.html")) {
                            File report = new File("")
                            assert report : println("ERROR. The $report.name file should not be null!")
                            if (existingReportFileContent && existingReportFileContent == report.text) {
                                slackReporter.reportProbablyFailedBuild()
                            } else if (report) {
                                slackReporter.reportTestSuit report
                            }
                        }
                    }
                } catch(IOException e) {
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
                // slackReporter.announceStartOfSuitExecution specInfo
                if (slackReporter.isReportingEnabled) {
                    try {
                        def dir = new File(org.tarya.commons.ast.transformations.skypereporter.SkypeReporterAnnotationDrivenExtension.getREPORTS_DIR)
                        dir.eachFileRecurse (FileType.FILES) { file ->
                            if (file.name.contains(".${specInfo.name}.html")) {
                                existingReportFileContent = new File("${org.tarya.commons.ast.transformations.skypereporter.SkypeReporterAnnotationDrivenExtension.getREPORTS_DIR}/$file.name").text
                            }
                        }
                    } catch(FileNotFoundException e) {
                        println("Report file for the ${specInfo.name} was not found. The Exception: $e")
                        existingReportFileContent = null
                    }
                }
            }
        })
    }
}
