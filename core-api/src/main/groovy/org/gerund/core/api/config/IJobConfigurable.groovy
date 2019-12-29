package org.gerund.core.api.config

import org.gerund.core.api.exception.ISpockReporterException

trait IJobConfigurable {

    abstract Boolean isHostAllowedToReport()

    abstract Boolean reportFileUrlIsValid() throws ISpockReporterException

    Map<String, ?> toMap() {
        this.class.declaredFields.findAll { !it.synthetic }.collectEntries {
            [ (it.name):this."$it.name" ]
        }
    }
}