package mytool.collector

import groovy.json.JsonSlurper
import groovy.transform.CompileStatic
import mytool.parser.formula.CupFormula
import mytool.parser.formula.FormulaParser
import mytool.util.IoUtil

import javax.swing.text.html.Option

@CompileStatic
class MetricTypes {

    static class Options {
        boolean isLeaf
        BigDecimal defaultValue
    }

    static Map<String, MetricType> aliasMap = [:]

    static Map<MetricType, Set<String>> reverseAliasMap = [:]

    static Map<ReportType, Map<String, MetricType>> metricTypes = [:]

    static Map<String, CupFormula> formulaMap = [:]

    static Map<String, Options> optionsMap = [:]

    static {
        loadFormulas()
        loadMetrics()
    }

    static Options getOptions(String metric) {
        return optionsMap.get(metric)
    }

    static BigDecimal getDefaultValue(String metric) {
        return optionsMap.get(metric)?.defaultValue
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

            ReportType reportType = ReportType.get(type as int)
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
            Options options = new Options()
            if (props) {
                String defaultValueS = props.get("defaultValue")
                if (defaultValueS) {
                    BigDecimal decimal = defaultValueS as BigDecimal

                }
                //
                options.isLeaf = props.get("isLeaf") as boolean
            }
            add(reportType, name, options, alias as String[])

        }
    }

    private static void loadFormulas() {
        Map map = new JsonSlurper().parse(IoUtil.getResourceAsReader(MetricType, "formulas.json")) as Map
        (map["formulas"] as List).each {
            String formulaS = it
            CupFormula formula = FormulaParser.parseOne(formulaS)
            formulaMap.put(formula.left, formula)
        }
    }

    private static MetricType add(ReportType reportType, String name, Options options, String... alias) {
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
        optionsMap.put(metricType as String, options)
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
