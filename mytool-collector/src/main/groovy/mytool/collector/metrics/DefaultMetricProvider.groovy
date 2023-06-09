package mytool.collector.metrics

import groovy.transform.CompileStatic
import mytool.collector.*
import mytool.collector.database.ReportDataAccessor
import mytool.parser.formula.CupExpr
import mytool.parser.formula.CupFormula
import mytool.util.CollectUtil

import java.math.RoundingMode

@CompileStatic
class DefaultMetricProvider extends MetricProvider {

    private ReportDataAccessor reportDataAccessor

    @Override
    BigDecimal[] calculate(MetricsContext metricsContext, String metric, String corpId, Date[] dates) {
        return doCalculate(metricsContext, null, metric, corpId, dates)
    }

    CupFormula resolveFormula(String alias) {
        Set<String> aliases = MetricTypes.getAliases(alias)
        for (String aliasI : aliases) {
            CupFormula formula = MetricTypes.formulaMap.get(aliasI)
            if (formula) {
                return formula
            }
        }
        return null
    }

    BigDecimal[] doCalculate(MetricsContext metricsContext, MetricType metricType, String metric, String corpId, Date[] dates) {

        if (metricType) {
            return reportDataAccessor.getReportValues(metricType.reportType, corpId, dates, metricType.name)
        }

        //find metric type by alias
        MetricType metricType1 = MetricTypes.getMetricByAlias(metric, false)
        if (metricType1) {
            return reportDataAccessor.getReportValues(metricType1.reportType, corpId, dates, metricType1.name)
        }

        //find formula
        CupFormula formula = MetricTypes.formulaMap.get(metric)
        if (!formula) {
            throw new RtException("cannot resolve metric report type or formula by alias:${metric}")
        }
        return evaluateExpr(formula.right, metricsContext, corpId, dates)

    }

    BigDecimal add(BigDecimal it1, BigDecimal it2) {
        if (it1 && it2) {
            return it1.add(it2)
        }
        return null
    }

    BigDecimal minus(BigDecimal it1, BigDecimal it2) {
        if (it1 && it2) {
            return it1.minus(it2)
        }
        return null
    }

    BigDecimal multiply(BigDecimal it1, BigDecimal it2) {
        if (it1 && it2) {
            return it1.multiply(it2)
        }
        return null
    }

    BigDecimal divide(BigDecimal it1, BigDecimal it2) {
        if (it1 && it2) {
            return it1.divide(it2, 4, RoundingMode.HALF_EVEN)
        }
        return null
    }

    BigDecimal[] evaluateExpr(CupExpr expr, MetricsContext metricsContext, String corpId, Date[] dates) {
        Class cls = expr.getClass()
        BigDecimal[] rtValue = null
        switch (cls) {
            case CupExpr.CupExprBinary:
                CupExpr.CupExprBinary exprBin = expr as CupExpr.CupExprBinary
                BigDecimal[] left = evaluateExpr(exprBin.exprLeft, metricsContext, corpId, dates)
                BigDecimal[] right = evaluateExpr(exprBin.exprRight, metricsContext, corpId, dates)

                switch (exprBin.oper) {
                    case CupExpr.PLUS:
                        rtValue = CollectUtil.collect(left, right, { BigDecimal it1, BigDecimal it2 -> add(it2, it2) })
                        break
                    case CupExpr.MINUS:

                        rtValue = CollectUtil.collect(left, right, { BigDecimal it1, BigDecimal it2 -> minus(it1, it2) })
                        break
                    case CupExpr.TIMES:
                        rtValue = CollectUtil.collect(left, right, { BigDecimal it1, BigDecimal it2 -> multiply(it1, it2) })
                        break
                    case CupExpr.DIV:
                        rtValue = CollectUtil.collect(left, right, { BigDecimal it1, BigDecimal it2 -> divide(it1, it2) })
                        break
                    default:
                        throw new RuntimeException("not supported operator:${exprBin.oper}")
                }
                break
            case CupExpr.CupExprMetric:
                CupExpr.CupExprMetric exprMetric = expr as CupExpr.CupExprMetric
                String alias2 = exprMetric.identifier
                int tx = 0
                rtValue = doCalculate(metricsContext, null, alias2, corpId, dates)
                break
            case CupExpr.CupExprNumber:
                CupExpr.CupExprNumber exprNumber = expr as CupExpr.CupExprNumber

                rtValue = dates.collect({ exprNumber.value }) as BigDecimal[]
                break
            case CupExpr.CupExprParen:
                CupExpr.CupExprParen exprParen = expr as CupExpr.CupExprParen
                rtValue = evaluateExpr(exprParen.expr, metricsContext, corpId, dates)
                break
            default:
                throw new RuntimeException("not supported class type:${expr}")
        }
        return rtValue
    }

}
