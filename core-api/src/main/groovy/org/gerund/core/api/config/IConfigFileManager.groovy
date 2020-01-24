package org.gerund.core.api.config

import org.gerund.core.api.exception.ISpockReporterException

import static groovy.io.FileType.FILES
import static org.gerund.core.api.constants.CommonFileUtils.BUILD_DIR_FILE_PATH
import static org.gerund.core.api.constants.CommonFileUtils.OUT_DIR_FILE_PATH

trait IConfigFileManager {

    String jobName
    File configFile
    ConfigObject config

    def <T> T findSourceFilesRecursively(String fileName, String startingPath, Closure<T> consumeFiles) {
        List<File> foundFiles = []
        new File(startingPath).eachFileRecurse(FILES) { File file ->
            if (file.name.equalsIgnoreCase(fileName)
                    && !file.path.contains(OUT_DIR_FILE_PATH)
                    && !file.path.contains(BUILD_DIR_FILE_PATH) ) foundFiles << file
        }
        foundFiles.with consumeFiles
    }

    abstract IConfigFileManager findConfigFile(String configFileName, String startingPath) throws ISpockReporterException

    abstract IConfigFileManager toConfig() throws ISpockReporterException

    abstract IJobConfigurable fetchJobConfig(IJobConfigValidator validator) throws ISpockReporterException

}