package mytool.collector

import groovy.transform.CompileStatic
import mytool.parser.formula.CupFormula

@CompileStatic
interface MetricSettings {

    static class Attributes {
        BigDecimal defaultValue
        Set<String> tags
    }

    Attributes getAttributes(String metric)

    BigDecimal getDefaultValue(String metric)

    CupFormula getFormula(String metric)

    MetricType getMetricByAlias(String alias, boolean force)

    Set<String> getAliases(String alias)

    String getFirstAlias(String metric)

}