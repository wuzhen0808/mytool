package mytool.collector.metrics

import groovy.transform.CompileStatic
import mytool.collector.*
import mytool.collector.database.ReportDataAccessor
import mytool.parser.formula.BigDecimalCalculator
import mytool.parser.formula.CupFormula
import mytool.parser.formula.FormulaEvaluator
import mytool.parser.formula.MetricResolver

@CompileStatic
class DefaultMetricProvider extends MetricProvider {

    private ReportDataAccessor reportDataAccessor
    private FormulaEvaluator<BigDecimal> formulaEvaluator
    private MetricResolver<BigDecimal> resolver
    private MetricSettings metricTypeResolver

    DefaultMetricProvider() {
        formulaEvaluator = new FormulaEvaluator<>()

        resolver = new MetricResolver<BigDecimal>() {
            @Override
            BigDecimal[] resolve(Object model, Object[] models, String tx, String metricI) {
                MetricsContext metricsContextI = (model as Map)["context"]
                String corpIdI = (model as Map)["corpId"]
                Date[] dates = models as Date[]
                doCalculate(metricsContextI, null, metricI, corpIdI, dates)
            }
        }
    }

    @Override
    BigDecimal[] calculate(MetricsContext metricsContext, String metric, String corpId, Date[] dates) {
        return doCalculate(metricsContext, null, metric, corpId, dates)
    }

    BigDecimal[] doCalculate(MetricsContext metricsContext, MetricType metricType, String metric, String corpId, Date[] dates) {

        if (metricType) {
            return reportDataAccessor.getReportValues(metricType.reportType, corpId, dates, metricType.name)
        }

        //find metric type by alias
        MetricType metricType1 = metricTypeResolver.getMetricByAlias(metric, false)
        if (metricType1) {
            return reportDataAccessor.getReportValues(metricType1.reportType, corpId, dates, metricType1.name)
        }

        //find formula
        CupFormula formula = metricTypeResolver.getFormula(metric)
        if (!formula) {
            throw new RtException("cannot resolve metric report type or formula by alias:${metric}")
        }

        def calculator = new BigDecimalCalculator()


        return formulaEvaluator.evaluateExprValues(formula.right, [context: metricsContext, corpId: corpId], dates, calculator, resolver)

    }

}
