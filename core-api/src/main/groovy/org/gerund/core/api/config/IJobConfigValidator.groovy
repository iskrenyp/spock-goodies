package org.gerund.core.api.config

import org.gerund.core.api.exception.ISpockReporterException

trait IJobConfigValidator {

    abstract Map<String, ?> validate(Map<String, ?> jobConfigurable) throws ISpockReporterException

}