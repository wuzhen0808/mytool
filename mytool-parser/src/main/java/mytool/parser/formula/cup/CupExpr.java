package mytool.parser.formula.cup;


/**
 * Expression recognized by CUP parser.
 *
 * @author wuzhen
 */
public abstract class CupExpr {
    static final int PLUS = 1;
    static final int MINUS = 2;
    static final int TIMES = 3;
    static final int DIV = 4;

    static class CupExprBinary extends CupExpr {
        int oper;
        CupExpr exprLeft;
        CupExpr exprRight;

        CupExprBinary(int pLUS, CupExpr e1, CupExpr e2) {
            this.oper = pLUS;
            this.exprLeft = e1;
            this.exprRight = e2;
        }


    }

    static class CupExprNumber extends CupExpr {
        Integer value;

        CupExprNumber(Integer value) {
            this.value = value;
        }

    }

    /**
     * Index Expression, for instance: RateA@date0
     *
     * @author wuzhen
     */
    static class CupExprMetric extends CupExpr {

        String identifier;

        String tx;//T0/T1

        CupExprMetric(String tx, String metric) {
            this.identifier = metric;
            this.tx = tx;
        }

    }

    static class CupExprParen extends CupExpr {
        CupExpr expr;

        CupExprParen(CupExpr e) {

            this.expr = e;
        }

    }

    public static CupExpr plus(CupExpr e1, CupExpr e2) {
        return new CupExprBinary(PLUS, e1, e2);
    }

    public static CupExpr minus(CupExpr e1, CupExpr e2) {
        return new CupExprBinary(MINUS, e1, e2);
    }

    public static CupExpr times(CupExpr e1, CupExpr e2) {
        return new CupExprBinary(TIMES, e1, e2);
    }

    public static CupExpr div(CupExpr e1, CupExpr e2) {
        return new CupExprBinary(DIV, e1, e2);
    }

    public static CupExpr minus(CupExpr e) {
        return new CupExprBinary(PLUS, null, e);
    }

    public static CupExpr paren(CupExpr e) {
        return new CupExprParen(e);
    }

    public static CupExpr number(Integer value) {
        return new CupExprNumber(value);
    }

    public static CupExpr metric(String tx, String identifier) {
        return new CupExprMetric(tx, identifier);
    }

}
