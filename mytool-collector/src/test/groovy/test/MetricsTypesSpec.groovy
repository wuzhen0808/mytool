package test

import groovy.transform.CompileStatic
import spock.lang.Specification

@CompileStatic
class MetricsTypesSpec extends Specification {
    def "test"() {
        //Set<String> aliases = DefaultMetricSettings.getAliases("ROE")
        expect:
        1 == 1
    }
}
