package mytool.collector

import groovy.transform.CompileStatic
import mytool.parser.formula.CupFormula

@CompileStatic
interface MetricSettings {

    static class Options {
        boolean isLeaf
        BigDecimal defaultValue
        Set<String> tags
    }

    Options getOptions(String metric)

    BigDecimal getDefaultValue(String metric)

    CupFormula getFormula(String metric)

    MetricType getMetricByAlias(String alias, boolean force)

    Set<String> getAliases(String alias)

}