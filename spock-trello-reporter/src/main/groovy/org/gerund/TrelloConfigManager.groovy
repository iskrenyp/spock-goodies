package org.gerund

import org.gerund.core.api.config.IConfigFileManager
import org.gerund.core.api.config.IJobConfigValidator
import org.gerund.core.api.config.IJobConfigurable
import org.gerund.core.api.exception.ISpockReporterException

class TrelloConfigManager implements IConfigFileManager {

    @Override
    IConfigFileManager findConfigFile(String configFileName, String startingPath) throws ISpockReporterException {
        return null
    }

    @Override
    IConfigFileManager toConfig() throws ISpockReporterException {
        return null
    }

    @Override
    IJobConfigurable fetchJobConfig(IJobConfigValidator validator) throws ISpockReporterException {
        return null
    }
}
