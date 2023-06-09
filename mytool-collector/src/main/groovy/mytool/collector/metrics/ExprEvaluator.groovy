package mytool.collector.metrics

import groovy.transform.CompileStatic
import mytool.parser.formula.CupExpr
import mytool.util.CollectUtil

import java.math.RoundingMode

@CompileStatic
class ExprEvaluator {

    static interface Resolver {
        BigDecimal[] resolve(int tx, String metric, Date[] date)
    }


}
