package mytool.parser.formula

import groovy.transform.CompileStatic

@CompileStatic
class FormulaParser {

    static Map<String, CupFormula> parseAsFormulaMap(String formulas) {
        Map<String, CupFormula> map = new HashMap<>();
        List<CupFormula> list = parse(formulas);
        for (CupFormula formula : list) {
            map.put(formula.left, formula);
        }
        return map;
    }

    static CupFormula parseOne(String formula) {
        CupFormula.List list = parse(formula);
        if (list.isEmpty()) {
            throw new RuntimeException("");
        } else if (list.size() == 1) {
            return list.get(0);
        } else {
            throw new RuntimeException("");
        }
    }

    static CupFormula.List parse(String formula) {
        (CupFormula.List) doParse(formula)
    }

    static Object doParse(String formula) {
        formula = formula.replaceAll(" ", "");
        Reader r = new java.io.StringReader(formula);

        try {
            return new parser(new scanner(r)).parse().value;
        } catch (Exception e) {
            throw new RuntimeException("cannot parse formula or expr:${formula}", e);
        }

    }
}
