package mytool.parser.formula

import groovy.transform.CompileStatic

import java.math.RoundingMode
@CompileStatic
class BigDecimalCalculator implements Calculator<BigDecimal> {

    @Override
    BigDecimal add(Object[] model, int idx, BigDecimal left, BigDecimal right) {
        if (left == null || right == null) {
            return null
        }
        return left.add(right)
    }

    @Override
    BigDecimal minus(Object[] model, int idx, BigDecimal left, BigDecimal right) {
        if (left == null || right == null) {
            return null
        }
        return left.minus(right)
    }

    @Override
    BigDecimal times(Object[] model, int idx, BigDecimal left, BigDecimal right) {
        if (left == null || right == null) {
            return null
        }
        return left.multiply(right)
    }

    @Override
    BigDecimal divide(Object[] model, int idx, BigDecimal left, BigDecimal right) {
        if (left == null || right == null) {
            return null
        }
        return left.divide(right, 4, RoundingMode.HALF_EVEN)
    }

    @Override
    BigDecimal number(Object[] objects, int idx, Number value) {
        return value as BigDecimal
    }
}
