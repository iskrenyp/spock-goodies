
// Describe your particular CI jobs here
tinySlackReporter {
    reportFrom = ['127.0.1.1']
    linkToReportFile = 'http://localhost:8080/job/tinySlackReporter/ws/slack-reporter/build/reports/tests/test/index.html'
    pathToReportFile = 'build/reports/tests/test/classes/SampleSpec.html'
    testEnvironment = 'local'
    slackChannel = 'ipenchev_test'
    userToken = 'xoxp-627582634913-679212617360-667866249650-a4cafa531d11005eb3e13e737462acfc'
}