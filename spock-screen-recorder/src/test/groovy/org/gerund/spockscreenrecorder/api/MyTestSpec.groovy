package org.gerund.spockscreenrecorder.api


import spock.lang.Specification

@MakeVideo
class MyTestSpec extends Specification {

    def "test1" () {
        given:
        sleep(10000)
        expect:
        true
    }

    def "test2" () {
        given:
        sleep(5000)
        expect:
        true
    }
}
