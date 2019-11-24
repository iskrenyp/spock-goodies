import org.gerund.SlackConfigFileManager
import spock.lang.Specification

class SampleSpec extends Specification {

    def "sample case" () {
        when:
        println(SlackConfigFileManager.instance.configObject)
        then:
        true
    }

    def "1+1" () {
        when:
        println(SlackConfigFileManager.instance.configObject)
        then:
        true
    }
}
