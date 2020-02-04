package com.github.iskrenyp.core.api.config

import com.github.iskrenyp.core.api.exception.ISpockGoodyExtensionException

import static com.github.iskrenyp.core.api.constants.CommonFileUtils.BUILD_DIR_FILE_PATH
import static com.github.iskrenyp.core.api.constants.CommonFileUtils.OUT_DIR_FILE_PATH
import static com.github.iskrenyp.core.api.constants.ExceptionCommonMessages.BAD_CONFIG_FILE_AS_URL
import static com.github.iskrenyp.core.api.constants.ExceptionCommonMessages.ENTRY_NOT_FOUND_IN_CONFIG
import static com.github.iskrenyp.core.api.constants.ExceptionCommonMessages.MORE_THAN_ONE_CONFIG_FILE_FOUND
import static groovy.io.FileType.FILES
import static com.github.iskrenyp.core.api.constants.ExceptionCommonMessages.CONFIG_FILE_NOT_FOUND

trait IConfigFileRuler {

    File configFile

    def <T> T findSourceFilesRecursively(String fileName, String startingPath, Closure<T> consumeFiles) throws ISpockGoodyExtensionException {
        List<File> foundFiles = []
        try {
            new File(startingPath).eachFileRecurse(FILES) { File file ->
                if (file.name.equalsIgnoreCase(fileName)
                        && !file.path.contains(OUT_DIR_FILE_PATH)
                        && !file.path.contains(BUILD_DIR_FILE_PATH) ) foundFiles << file
            }
            foundFiles.with consumeFiles
        } catch (FileNotFoundException | IllegalArgumentException e) {
            throw new ISpockGoodyExtensionException("$CONFIG_FILE_NOT_FOUND $fileName", e)
        }
    }

    IConfigFileRuler findConfigFile(String fileName, String startingPath) throws ISpockGoodyExtensionException {
        findSourceFilesRecursively(fileName, startingPath) { List<File> configFiles ->
            if (configFiles.size() > 1) throw new ISpockGoodyExtensionException("$MORE_THAN_ONE_CONFIG_FILE_FOUND $fileName . Please position just a single file within your current project root")
            else this.configFile = configFiles.first()
            this
        }
    }

    def <T> T toConfig(Closure<T> consumeConfig) throws ISpockGoodyExtensionException {
        try {
            new ConfigSlurper().parse(configFile.toURI().toURL()).with consumeConfig
        } catch (MalformedURLException e) {
            throw new ISpockGoodyExtensionException("$BAD_CONFIG_FILE_AS_URL ${this.configFile.name}", e)
        }
    }

    def <T> T withEntry(String entryName, Closure<T> consumeEntry) throws ISpockGoodyExtensionException {
        this.toConfig { Map config ->
            if (config.containsKey(entryName) && config[entryName] instanceof Map) config[entryName].with consumeEntry
            else throw new ISpockGoodyExtensionException("$ENTRY_NOT_FOUND_IN_CONFIG $entryName")
        }
    }

    Boolean isEntryValid(String entryName, Map<String, Class> validator = null) throws ISpockGoodyExtensionException {
        this.withEntry(entryName) { Map<String, ?> entry ->
            validator ? validator.every { k, v -> entry.keySet().contains(k) && entry[k].class == v } : true
        }
    }

    def <T extends IConfigurableEntity> T configureEntity(String entryName, Closure<T> entityInstanceInit) {
        Map<String, ?> entry = toConfig { it[entryName] as Map }
        entry.with entityInstanceInit
    }





}