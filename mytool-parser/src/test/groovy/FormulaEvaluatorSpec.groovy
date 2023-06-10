import mytool.parser.formula.BigDecimalCalculator
import mytool.parser.formula.FormulaEvaluator
import mytool.parser.formula.MetricResolver
import spock.lang.Specification

class FormulaEvaluatorSpec extends Specification {

    def "test1"() {
        FormulaEvaluator<BigDecimal> evaluator = new FormulaEvaluator<>()
        def calculator = new BigDecimalCalculator()
        MetricResolver<BigDecimal> resolver = new MetricResolver<BigDecimal>() {
            @Override
            BigDecimal[] resolve(Object top, Object[] models, String tx, String metric) {
                models.collect({
                    it[metric]
                }) as BigDecimal[]

            }
        }
        BigDecimal bigDecimal = evaluator.evaluateExprValue(formulaString, null, model, calculator, resolver)

        System.out.println(formulaString)
        expect:
        1 == 1
        bigDecimal == result as BigDecimal

        where:
        formulaString              | model                 | result
        "r = 1 + 2"                | [a: 1, b: 2]          | 3
        "r = 1 - 2"                | [a: 1, b: 2]          | -1
        "r = 2 * 3"                | [a: 2, b: 3]          | 6
        "r = 3 / 2"                | [a: 3, b: 2]          | 1.5
        "r = 3 - 2 - 1"            | [a: 3, b: 2, c: 1]    | 0
        "r = 10 - 2 * 2.5"         | [a: 10, b: 2, c: 2.5] | 5
        "r = 10 - 2 * 2.5 - 3 - 2" | [a: 10, b: 2, c: 2.5] | 0
        "r = 10 - 2 * 2.5 - 2 * 1" | [a: 10, b: 2, c: 2.5] | 3
        //
        "r = a + b"                | [a: 1, b: 2]          | 3
        "r = a - b"                | [a: 1, b: 2]          | -1
        "r = a * b"                | [a: 2, b: 3]          | 6
        "r = a / b"                | [a: 3, b: 2]          | 1.5
        "r = a - b - c"            | [a: 3, b: 2, c: 1]    | 0
        "r = a - b * c"            | [a: 10, b: 2, c: 2.5] | 5

    }

}
