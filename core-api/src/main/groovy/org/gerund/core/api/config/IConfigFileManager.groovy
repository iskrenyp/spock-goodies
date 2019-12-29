package org.gerund.core.api.config

import org.gerund.core.api.exception.ISpockReporterException

trait IConfigFileManager {

    String jobName
    File configFile
    ConfigObject config

    abstract IConfigFileManager findConfigFile(String configFileName, String startingPath) throws ISpockReporterException

    abstract IConfigFileManager toConfig() throws ISpockReporterException

    abstract IJobConfigurable fetchJobConfig(IJobConfigValidator validator) throws ISpockReporterException

}