package org.gerund

import java.util.regex.Pattern

import static org.gerund.constants.CommonFilePaths.BUILD_DIR_FILE_PATH
import static org.gerund.constants.CommonFilePaths.OUT_DIR_FILE_PATH

enum FileFinderPredicate {

    FIND_UNIQUE_FILE_IN_SOURCE_BY_NAME( { File file, String fileName ->
        file.name.equalsIgnoreCase(fileName) && !file.path.contains(OUT_DIR_FILE_PATH) && !file.path.contains(BUILD_DIR_FILE_PATH)
    } )

    Closure<Boolean> predicate

    FileFinderPredicate(Closure<Boolean> predicate) {
        this.predicate = predicate
    }

}