package org.gerund.core.api.config

import org.gerund.core.api.exception.ISpockReporterException

import static org.gerund.core.api.constants.SlackReporterJobConfigProps.MANDATORY_FIELDS_MAP
import static org.gerund.core.api.constants.ExceptionCommonMessages.BAD_SLACK_JOB_CONFIGURATION

enum JobConfigValidator implements IJobConfigValidator {

    SLACK_REPORTER_JOB_CONFIG_VALIDATOR {
        @Override
        Map<String, ?> validate(Map<String, ?> jobConfigurable) {
            Map<String, ?> job = jobConfigurable.findAll {
                MANDATORY_FIELDS_MAP.keySet().contains(it.key) && it.value.class == MANDATORY_FIELDS_MAP[it.key]
            }
            if (job && !job.isEmpty() && job.size() == MANDATORY_FIELDS_MAP.size()) return job
            else throw new ISpockReporterException("$BAD_SLACK_JOB_CONFIGURATION ")
        }
    }

    @Override
    Map<String, ?> validate(Map<String, ?> jobConfigurable) {
        return null
    }
}