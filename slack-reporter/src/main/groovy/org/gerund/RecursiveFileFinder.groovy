package org.gerund

import groovy.io.FileType
import groovy.transform.Memoized
import org.gerund.exceptions.SlackReporterFileException
import org.gerund.exceptions.SlackReporterFileNotFoundException
import static org.gerund.FileFinderPredicate.FIND_UNIQUE_FILE_IN_SOURCE_BY_NAME
import static org.gerund.constants.ExceptionCommonMessages.MORE_THAN_ONE_FILE_FOUND
import static org.gerund.constants.ExceptionCommonMessages.NO_FILES_OR_DIRECTORIES_FOUND

trait RecursiveFileFinder {

    @Memoized
    List<File> findFilesRecursively(FileType fileType, String startingPath, Object fileIdentifier, FileFinderPredicate predicate) throws SlackReporterFileException {
        List<File> foundFiles = []
        try {
            new File(startingPath).eachFileRecurse(fileType) {
                if (predicate.predicate(it, fileIdentifier)) foundFiles << it
            }
        } catch (FileNotFoundException e) {
            throw new SlackReporterFileNotFoundException(NO_FILES_OR_DIRECTORIES_FOUND, e)
        }
        foundFiles
    }

    File findFileInSourceRecursively(FileType fileType, String fileName, String startingPath) throws SlackReporterFileException {
        println("looking for slack config file")
        List<File> foundFiles = findFilesRecursively(fileType, startingPath, fileName, FIND_UNIQUE_FILE_IN_SOURCE_BY_NAME)
        if (foundFiles.size() > 1) throw new SlackReporterFileException(MORE_THAN_ONE_FILE_FOUND(fileName) as String)
        foundFiles.first()
    }
}