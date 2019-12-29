import org.gerund.SlackConfigManager
import org.gerund.SpockSlackNotification
import org.gerund.api.ReportOnSlack
import spock.lang.Specification

@ReportOnSlack(jobName = 'tinySlackReporter')
class SampleSpec extends Specification {

    def "sample case" () {
        when:
        println(System.getProperty("user.dir"))
        println("hell yeah")
        def x = 1 + 1
        then:
        x > 1
    }
}
