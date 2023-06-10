package mytool.parser.formula;

import groovy.transform.CompileStatic;

import java.util.ArrayList;
@CompileStatic
public class CupFormula {
    static class List extends ArrayList<CupFormula> {

    }

    public String left;
    public CupExpr right;

    static List list(List list, CupFormula formula) {
        List list2 = new List();
        list2.addAll(list);
        list2.add(formula);
        return list2;
    }

    static List list(CupFormula formula) {
        List list2 = new List();
        list2.add(formula);
        return list2;
    }

    public static CupFormula valueOf(String left, CupExpr right) {
        CupFormula cupFormula = new CupFormula();
        cupFormula.left = left;
        cupFormula.right = right;

        return cupFormula;
    }

}
