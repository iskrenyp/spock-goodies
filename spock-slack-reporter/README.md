# spock-slack-reporter

Spock-slack-reporter is a simple groovy library, which provides an annotation driven local extension for the [Spock Framework], allowing you to notify your team mates on Slack after each test run. The notification contains the link to your (HTML) reports, the number of the failed cases and their names. 
It is built on top of the [simple-slack-api] lib and the Spock extensibility's beauty.

A common use case for using this lib is when you have different types of auto tests (unit, integration, e2e) and your team is using Slack. You can increase your team's velocity by providing auto notifications in different channels, so that your team gets the test reports ASAP. Here's an [example-spock-slack-reporter] project.

### Example usage

The spock-slack-reporter lib can be found under mavenCentral repository:

```groovy
    repositories {
        mavenCentral()
    }
```

Add the spock-screen-recorder lib to your project dependencies (assuming you're using Gradle as build tool):

```groovy
    // Use this declaration if you don't want to use the spock screen recorder dependencies
    // It avoids affecting your version of Groovy/Spock
    testImplementation('com.github.iskrenyp:spock-slack-reporter:1.0+') {
        exclude group: 'org.codehaus.groovy'
        exclude group: 'org.spockframework'
    }
```

Then create file within the root (src/test/resources is recommended) of you project with name SlackReporterConfig.groovy and inside the file add your reporters' description, like for example:

```groovy
    myToken = '{YOUR_TOKEN_GOES_HERE}'
    
    devReporter {
        // All fields are mandatory to specify
        reportUrl = "http://localhost:8080/job/my_dev_env_job/workspace/build/report.html"
        channelName = 'ipenchev_test'
        userToken = myToken
        environment = 'DEV'
    }
    
    stagingReporter {
        reportUrl = "http://localhost:8080/job/my_stage_env_job/workspace/build/report.html"
        channelName = 'staging'
        userToken = myToken
        environment = 'STAGING'
    }
    
    // You can actually go and do some specialization within your reporters
    
    backendStagingReporter {
        reportUrl = "http://localhost:8080/job/my_stage_env_backend_job/workspace/build/report.html"
        channelName = 'backend'
        userToken = myToken
        environment = 'STAGING - BACKEND'
    }
    
    userAcceptanceReporter {
        reportUrl = "http://localhost:8080/job/my_user_acceptance_job/workspace/build/report.html"
        channelName = 'release'
        userToken = myToken
        environment = 'E2E-UAT'
    }
```

Then just put the @ReportOnSlack annotation and pass the desired reporter to the Spec you want to report with it:

```groovy
@ReportOnSlack('devReporter')
class MyUnitTestsSpec extends Specification {}
```
or
```groovy
@ReportOnSlack('userAcceptanceReporter')
class MyUserAcceptanceSpec extends Specification {}
```

You can use the full-powered features of the ConfigSurpler, but always pass the four properties for each reporter: reportUrl, channelName, userToken, environment. All of them should be strings.

After the test execution is finished, the spock-slack reporter will login to slack api by using the token for the specific reporter, find the channel you've specified and post a message in which provide the name of the reporter, the environment, the reportUrl, the test fails count and the names of the test cases, which failed.

[Spock Framework]: <http://spockframework.org/spock/docs/1.3/all_in_one.html>
[simple-slack-api]: <https://github.com/Itiviti/simple-slack-api>
[Geb]: <https://gebish.org/manual/current/>
[example-spock-slack-reporter]: <https://github.com/iskrenyp/spock-goodies-examples/tree/master/screen-recorder-example>
