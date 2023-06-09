package mytool.parser.formula;

import java_cup.runtime.*;
%%

%public
%class scanner
%unicode
%cup
%line
%column

%{
	StringBuffer string = new StringBuffer();

	private Symbol symbol(int type) {
		return new Symbol(type, yyline, yycolumn);
	}
	private Symbol symbol(int type, Object value) {
		//System.out.println("type:"+type+",value:"+value);
		return new Symbol(type, yyline, yycolumn, value);
	}
%}
LineTerminator = \r|\n|\r\n

WhiteSpace     = {LineTerminator} | [ \t\f]

Identifier = [:jletter:] [:jletterdigit:]*

DecIntegerLiteral = 0 | [1-9][0-9]*

DateLiteral = [1-9][0-9][0-9][0-9][/][0-9][0-9][/][0-9][0-9]

%%

// keywords
	
<YYINITIAL> {
	// literals 
    {DecIntegerLiteral}            { Integer value = new Integer(yytext());;return symbol(sym.NUMBER, value); }
    {Identifier}            	   { String value = yytext();return symbol(sym.IDENTIFIER, value); }
    // operators
    "+"                            { return symbol(sym.PLUS); }
	"-"                            { return symbol(sym.MINUS); }
	"*"                            { return symbol(sym.TIMES); }
	"/"                            { return symbol(sym.DIV); }
	"("                            { return symbol(sym.LPAREN); }
	")"                            { return symbol(sym.RPAREN); }
    "@"                            { return symbol(sym.AT); }
    "."                            { return symbol(sym.DOT); }
    "="                            { return symbol(sym.EQ); }
    // whitespace
    {WhiteSpace}                   {  }
}

    // error fallback
[^]                              { throw new Error("Illegal character <"+yytext()+">"); }
