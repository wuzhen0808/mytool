package test

import groovy.transform.CompileStatic
import mytool.collector.MetricTypes
import spock.lang.Specification

@CompileStatic
class MetricsTypesSpec extends Specification {
    def "test"() {
        Set<String> aliases = MetricTypes.getAliases("ROE")
        expect:
        1 == 1
    }
}
