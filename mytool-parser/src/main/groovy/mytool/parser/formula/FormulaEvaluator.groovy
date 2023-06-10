package mytool.parser.formula

import groovy.transform.CompileStatic
import mytool.util.CollectUtil

@CompileStatic
class FormulaEvaluator<T> {

    T evaluateExprValue(String exprOrFormula, Object model, Object childModel, Calculator<T> calculator, MetricResolver<T> metricResolver) {
        Object expOrFormula = FormulaParser.doParse(exprOrFormula)
        if (expOrFormula instanceof CupFormula.List) {
            return evaluateExprValue(((CupFormula.List) expOrFormula).get(0).right, model, childModel, calculator, metricResolver)
        } else if (expOrFormula instanceof CupExpr) {
            return evaluateExprValue((CupExpr) expOrFormula, model, childModel, calculator, metricResolver)
        } else {
            throw new RuntimeException("")
        }
    }

    T evaluateExprValue(CupExpr expr, Object model, Object childModel, Calculator<T> calculator, MetricResolver<T> metricResolver) {
        return (evaluateExprValues(expr, model, [childModel] as Object[], calculator, metricResolver) as T[])[0]
    }

    T[] evaluateExprValues(CupExpr expr, Object model, Object[] models, Calculator calculator, MetricResolver<T> metricResolver) {
        Class cls = expr.getClass()
        T[] rtValue = null
        switch (cls) {
            case CupExpr.CupExprBinary:
                CupExpr.CupExprBinary exprBin = expr as CupExpr.CupExprBinary
                T[] left = evaluateExprValues(exprBin.exprLeft, model, models, calculator, metricResolver) as T[]
                T[] right = evaluateExprValues(exprBin.exprRight, model, models, calculator, metricResolver) as T[]

                switch (exprBin.oper) {
                    case CupExpr.PLUS:
                        rtValue = CollectUtil.collect(left, right, { int idx, T t1, T t2 -> calculator.add(models, idx, t1, t2) }) as T[]
                        break
                    case CupExpr.MINUS:
                        rtValue = CollectUtil.collect(left, right, { int idx, T t1, T t2 -> calculator.minus(models, idx, t1, t2) }) as T[]
                        break
                    case CupExpr.TIMES:
                        rtValue = CollectUtil.collect(left, right, { int idx, T t1, T t2 -> calculator.times(models, idx, t1, t2) }) as T[]
                        break
                    case CupExpr.DIVIDE:
                        rtValue = CollectUtil.collect(left, right, { int idx, T t1, T t2 -> calculator.divide(models, idx, t1, t2) }) as T[]
                        break
                    default:
                        throw new RuntimeException("not supported operator:${exprBin.oper}")
                }
                break
            case CupExpr.CupExprMetric:
                CupExpr.CupExprMetric exprMetric = expr as CupExpr.CupExprMetric
                rtValue = metricResolver.resolve(model, models, exprMetric.tx, exprMetric.metric)
                break
            case CupExpr.CupExprNumber:
                CupExpr.CupExprNumber exprNumber = expr as CupExpr.CupExprNumber

                rtValue = CollectUtil.collect(models, { int idx, Object mi ->
                    calculator.number(models, idx, exprNumber.value)
                }) as T[]
                break
            default:
                throw new RuntimeException("not supported class type:${expr}")
        }
        return rtValue
    }

}
