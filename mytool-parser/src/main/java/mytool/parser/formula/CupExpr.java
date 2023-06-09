package mytool.parser.formula;


import java.math.BigDecimal;
import java.util.Map;

/**
 * Expression recognized by CUP parser.
 *
 * @author wuzhen
 */
public abstract class CupExpr {
    public static final int PLUS = 1;
    public static final int MINUS = 2;
    public static final int TIMES = 3;
    public static final int DIV = 4;

    public static class CupExprBinary extends CupExpr {
        public int oper;
        public CupExpr exprLeft;
        public CupExpr exprRight;

        CupExprBinary(int pLUS, CupExpr e1, CupExpr e2) {
            this.oper = pLUS;
            this.exprLeft = e1;
            this.exprRight = e2;
        }

    }

    public static class CupExprNumber extends CupExpr {
        public Integer value;

        CupExprNumber(Integer value) {
            this.value = value;
        }

    }

    /**
     * Index Expression, for instance: RateA@date0
     *
     * @author wuzhen
     */
    public static class CupExprMetric extends CupExpr {

        public String identifier;

        public String tx;//T0/T1

        CupExprMetric(String tx, String metric) {
            this.identifier = metric;
            this.tx = tx;
        }

    }

    public static class CupExprParen extends CupExpr {
        public CupExpr expr;

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
