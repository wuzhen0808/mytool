import java_cup.runtime.Symbol
import mytool.parser.formula.cup.CupExpr
import spock.lang.Specification

class FormulaParserSpec extends Specification {

    def "test"() {
        Reader r = new java.io.StringReader("T0.负债合计/T0.资产总计");
        Symbol result = new formula_parser(new formula_scanner(r)).parse();
        CupExpr expr = (CupExpr) result.value;
        System.out.println(expr);
        expect:
        1 == 1
    }
}
