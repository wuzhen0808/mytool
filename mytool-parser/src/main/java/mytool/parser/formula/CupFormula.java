package mytool.parser.formula;

public class CupFormula {

    public String left;
    public CupExpr right;

    public static CupFormula valueOf(String left, CupExpr right) {
        CupFormula cupFormula = new CupFormula();
        cupFormula.left = left;
        cupFormula.right = right;

        return cupFormula;
    }

}
