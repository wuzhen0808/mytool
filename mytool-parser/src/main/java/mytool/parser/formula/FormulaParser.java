package mytool.parser.formula;

import groovy.transform.CompileStatic;
import java_cup.runtime.Symbol;

import java.io.Reader;
import java.util.*;


public class FormulaParser {
    public static Map<String, CupFormula> parseAsFormulaMap(String formulas) {
        Map<String, CupFormula> map = new HashMap<>();
        List<CupFormula> list = parseAsFormulaList(formulas);
        for (CupFormula formula : list) {
            map.put(formula.left, formula);
        }
        return map;
    }

    public static List<CupFormula> parseAsFormulaList(String formulas) {
        String[] strings = formulas.split(";");
        List<CupFormula> list = new ArrayList<>();
        for (String string : strings) {
            list.add(parse(string));
        }
        return list;
    }

    public static CupFormula parse(String formula) {
        formula = formula.replaceAll(" ", "");
        Reader r = new java.io.StringReader(formula);

        try {
            return (CupFormula) new parser(new scanner(r)).parse().value;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }
}
