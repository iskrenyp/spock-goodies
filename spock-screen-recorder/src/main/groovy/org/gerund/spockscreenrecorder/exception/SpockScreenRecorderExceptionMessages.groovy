package org.gerund.spockscreenrecorder.exception

class SpockScreenRecorderExceptionMessages {

    static final String MORE_THAN_ONE_CONFIG_FILES_FOUND = 'More than 1 ScreenRecorderConfig file was found withing the project root!'
    static final String CONFIG_FILE_NOT_FOUND = 'The ScreenRecorderConfig.groovy file was not found within the root of the project. Please make sure it is within the current project'
    static final String INVALID_URL_FOR_CONFIG_FILE = 'There was an error while getting the URL of the ScreenRecorderConfig.groovy file'
    static final String SCREEN_RECORDER_NOT_FOUND = 'The screen recorder name you specify was not found in ScreenRecorderConfig.groovy'
    static final String INSTANTIATING_SCREEN_RECORDER_FAILED = 'Creating an instance of the Screen Recorder failed!'
    static final String STARTING_SCREEN_RECORDER_FAILED = 'Starting video recording has failed!'
    static final String STOPPING_SCREEN_RECORDER_FAILED = 'Stopping video recording has failed!'
}
