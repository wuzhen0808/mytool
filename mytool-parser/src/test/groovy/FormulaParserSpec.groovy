import mytool.parser.formula.FormulaParser
import spock.lang.Specification

class FormulaParserSpec extends Specification {

    def "test1"() {
        def formula = FormulaParser.parse(formulaString)
        System.out.println(formula);
        expect:
        1 == 1
        where:
        formulaString            | ok
        "有息负债率=(T0.有息负债/T0.总资产)" | true
        "有息负债率 = ( T0.有息负债 / T0.总资产 )" | true
    }

}
