package mytool.collector

import groovy.json.JsonSlurper
import groovy.transform.CompileStatic
import mytool.parser.formula.CupFormula
import mytool.parser.formula.FormulaParser
import mytool.util.IoUtil

@CompileStatic
class MetricTypes {

    static Map<String, MetricType> aliasMap = [:]

    static Map<MetricType, Set<String>> reverseAliasMap = [:]

    static Map<ReportType, Map<String, MetricType>> metricTypes = [:]

    static Map<String, CupFormula> formulaMap = [:]

    static Map<String, BigDecimal> defaultValues = [:]//
    static {
        loadFormulas()
        loadMetrics()
    }

    static BigDecimal getDefaultValue(String metric) {
        return defaultValues.get(metric)
    }

    private static void loadMetrics() {
        Map map = new JsonSlurper().parse(IoUtil.getResourceAsReader(MetricType, "metric-types.json")) as Map

        List metrics = map["metrics"] as List
        for (int i = 0; i < metrics.size(); i++) {
            Object[] metricRow = metrics.get(i) as Object[]
            String type = metricRow[0] as String
            if (type == "") {
                continue
            }

            ReportType reportType = ReportType.valueOf(type as int)
            if (reportType == null) {
                throw new RtException("no such type:${type}")
            }
            String name = metricRow[1] as String

            //all left are aliases
            List<String> alias = []
            Map props
            for (int j = 2; j < metricRow.length; j++) {
                if (metricRow[j] instanceof String) {
                    alias.add(metricRow[j] as String)
                } else {
                    props = metricRow[j] as Map
                }
            }

            MetricType metricType = add(reportType, name, alias as String[])

            if (props) {
                String defaultValueS = props.get("defaultValue")
                if (defaultValueS) {
                    BigDecimal decimal = defaultValueS as BigDecimal
                    defaultValues.put(metricType as String, decimal)
                }
            }
        }
    }

    private static void loadFormulas() {
        Map map = new JsonSlurper().parse(IoUtil.getResourceAsReader(MetricType, "formulas.json")) as Map
        (map["formulas"] as List).each {
            String formulaS = it
            CupFormula formula = FormulaParser.parse(formulaS)
            formulaMap.put(formula.left, formula)
        }
    }

    private static MetricType add(ReportType reportType, String name, String... alias) {
        MetricType metricType = MetricType.valueOf(reportType, name)
        Map<String, MetricType> map2 = metricTypes.get(reportType)
        if (!map2) {
            map2 = [:]
            metricTypes.put(reportType, map2)
        }

        if (map2.put(name, metricType)) {
            throw new RtException("duplicated metric type:${name}")
        }

        alias.each {
            if (aliasMap.put(it, metricType)) {
                throw new RtException("duplicated metric alas:${it}")
            }
        }

        reverseAliasMap.put(metricType, alias as Set<String>)
        return metricType
    }

    static MetricType getMetricByAlias(String alias, boolean force) {
        MetricType metricType = aliasMap.get(alias)
        if (force && !metricType) {
            throw new RtException("no metric type with alias:${alias}")
        }
        return metricType
    }

    static Set<String> getAliases(String alias) {
        MetricType metricType = getMetricByAlias(alias, false)
        if (metricType) {
            return reverseAliasMap.get(metricType)
        }
        return null
    }

}
