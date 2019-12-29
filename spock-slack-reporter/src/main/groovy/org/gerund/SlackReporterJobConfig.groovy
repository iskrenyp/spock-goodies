package org.gerund

import groovy.transform.TupleConstructor
import groovy.util.logging.Slf4j
import org.gerund.core.api.config.IJobConfigurable
import org.gerund.core.api.config.JobConfigValidator
import org.gerund.core.api.exception.ISpockReporterException
import org.gerund.exceptions.SlackReporterConfigException
import static org.gerund.core.api.constants.ExceptionCommonMessages.INVALID_LINK_TO_REPORT

import static org.gerund.core.api.constants.SlackReporterJobConfigProps.MANDATORY_FIELDS_MAP

@Slf4j
@TupleConstructor
class SlackReporterJobConfig implements IJobConfigurable {

    List<String> reportFrom
    String linkToReportFile
    String testEnvironment
    String slackChannel
    String userToken


    @Override
    Boolean isHostAllowedToReport() {
        try {
            log.info("Checking if current host ${Inet4Address.localHost.hostAddress} is allowed to perform Slack notifications")
            Inet4Address.localHost.hostAddress in this.reportFrom
        } catch (UnknownHostException e) {
            log.info("There was an error while looking for the host address. $e")
            false
        }
    }

    @Override
    Boolean reportFileUrlIsValid() throws ISpockReporterException {
        try {
            new URL(linkToReportFile).toURI()
            return true
        } catch (Exception e) {
            throw new SlackReporterConfigException(INVALID_LINK_TO_REPORT, e)
        }
    }
}
