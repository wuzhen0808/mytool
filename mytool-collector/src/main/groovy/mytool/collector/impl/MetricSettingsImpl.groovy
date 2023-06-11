package mytool.collector.impl

import groovy.json.JsonSlurper
import groovy.transform.CompileStatic
import mytool.collector.MetricSettings
import mytool.collector.MetricType
import mytool.collector.ReportType
import mytool.collector.RtException
import mytool.parser.formula.CupFormula
import mytool.parser.formula.FormulaParser
import mytool.parser.formula.MetricResolver
import mytool.util.IoUtil
import org.springframework.stereotype.Component

import javax.annotation.PostConstruct

@CompileStatic
@Component
class MetricSettingsImpl implements MetricSettings {
    static class MetricSetting {
        MetricType metricType
        String[] aliases
        Attributes attributes
    }

    Map<String, MetricSetting> aliasMap = [:]

    Map<String, Set<String>> reverseAliasMap = [:]

    Map<String, MetricSetting> metricMap = [:]

    Map<String, CupFormula> formulaMap = [:]

    @PostConstruct
    void init() {
        loadFormulas()
        loadMetrics()
    }

    MetricSetting get(String metric) {
        return metricMap.get(metric)
    }

    @Override
    Attributes getAttributes(String metric) {
        return get(metric)?.attributes
    }

    @Override
    BigDecimal getDefaultValue(String metric) {
        return getAttributes(metric)?.defaultValue
    }

    @Override
    CupFormula getFormula(String metric) {
        return formulaMap.get(metric)
    }

    @Override
    MetricType getMetricByAlias(String alias, boolean force) {
        MetricType metricType = aliasMap.get(alias)?.metricType
        if (force && !metricType) {
            throw new RtException("no metric type with alias:${alias}")
        }
        return metricType
    }

    @Override
    Set<String> getAliases(String alias) {
        MetricType metricType = getMetricByAlias(alias, false)
        if (metricType) {
            return reverseAliasMap.get(metricType)
        }
        return null
    }

    @Override
    String getFirstAlias(String metric) {
        return get(metric)?.aliases ?[0]
    }

    void loadMetrics() {
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
            Attributes options = new Attributes()
            if (props) {
                String defaultValueS = props.get("defaultValue")
                if (defaultValueS) {
                    BigDecimal decimal = defaultValueS as BigDecimal

                }
                //tags
                options.tags = props.get("tags") as Set<String>
            }
            add(reportType, name, options, alias as String[])

        }
    }

    void loadFormulas() {
        Map map = new JsonSlurper().parse(IoUtil.getResourceAsReader(MetricType, "formulas.json")) as Map
        (map["formulas"] as List).each {
            String formulaS = it
            CupFormula formula = FormulaParser.parseOne(formulaS)
            formulaMap.put(formula.left, formula)
        }
    }

    private void add(ReportType reportType, String name, Attributes attributes, String... alias) {
        MetricType metricType = MetricType.valueOf(reportType, name)

        String metric = metricType as String
        MetricSetting metricSetting = new MetricSetting(metricType: metricType, attributes: attributes, aliases: alias)

        if (metricMap.put(metric, metricSetting)) {
            throw new RtException("duplicated metric type:${name}")
        }

        alias.each {
            if (aliasMap.put(it, metricSetting)) {
                throw new RtException("duplicated metric alas:${it}")
            }
        }

        reverseAliasMap.put(metric, alias as Set<String>)
    }
}
