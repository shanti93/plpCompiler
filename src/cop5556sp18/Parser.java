package cop5556sp18;
/* *
 * Initial code for SimpleParser for the class project in COP5556 Programming Language Principles 
 * at the University of Florida, Spring 2018.
 * 
 * This software is solely for the educational benefit of students 
 * enrolled in the course during the Spring 2018 semester.  
 * 
 * This software, and any software derived from it,  may not be shared with others or posted to public web sites,
 * either during the course or afterwards.
 * 
 *  @Beverly A. Sanders, 2018
 */

import static cop5556sp18.Scanner.Kind.BOOLEAN_LITERAL;
import static cop5556sp18.Scanner.Kind.COMMA;
import static cop5556sp18.Scanner.Kind.EOF;
import static cop5556sp18.Scanner.Kind.FLOAT_LITERAL;
import static cop5556sp18.Scanner.Kind.IDENTIFIER;
import static cop5556sp18.Scanner.Kind.INTEGER_LITERAL;
import static cop5556sp18.Scanner.Kind.KW_Z;
import static cop5556sp18.Scanner.Kind.KW_abs;
import static cop5556sp18.Scanner.Kind.KW_alpha;
import static cop5556sp18.Scanner.Kind.KW_atan;
import static cop5556sp18.Scanner.Kind.KW_blue;
import static cop5556sp18.Scanner.Kind.KW_boolean;
import static cop5556sp18.Scanner.Kind.KW_cart_x;
import static cop5556sp18.Scanner.Kind.KW_cart_y;
import static cop5556sp18.Scanner.Kind.KW_cos;
import static cop5556sp18.Scanner.Kind.KW_default_height;
import static cop5556sp18.Scanner.Kind.KW_default_width;
import static cop5556sp18.Scanner.Kind.KW_filename;
import static cop5556sp18.Scanner.Kind.KW_float;
import static cop5556sp18.Scanner.Kind.KW_from;
import static cop5556sp18.Scanner.Kind.KW_green;
import static cop5556sp18.Scanner.Kind.KW_height;
import static cop5556sp18.Scanner.Kind.KW_if;
import static cop5556sp18.Scanner.Kind.KW_image;
import static cop5556sp18.Scanner.Kind.KW_input;
import static cop5556sp18.Scanner.Kind.KW_int;
import static cop5556sp18.Scanner.Kind.KW_log;
import static cop5556sp18.Scanner.Kind.KW_polar_a;
import static cop5556sp18.Scanner.Kind.KW_polar_r;
import static cop5556sp18.Scanner.Kind.KW_red;
import static cop5556sp18.Scanner.Kind.KW_show;
import static cop5556sp18.Scanner.Kind.KW_sin;
import static cop5556sp18.Scanner.Kind.KW_sleep;
import static cop5556sp18.Scanner.Kind.KW_to;
import static cop5556sp18.Scanner.Kind.KW_while;
import static cop5556sp18.Scanner.Kind.KW_width;
import static cop5556sp18.Scanner.Kind.KW_write;
import static cop5556sp18.Scanner.Kind.LBRACE;
import static cop5556sp18.Scanner.Kind.LPAREN;
import static cop5556sp18.Scanner.Kind.LPIXEL;
import static cop5556sp18.Scanner.Kind.LSQUARE;
import static cop5556sp18.Scanner.Kind.OP_AND;
import static cop5556sp18.Scanner.Kind.OP_ASSIGN;
import static cop5556sp18.Scanner.Kind.OP_AT;
import static cop5556sp18.Scanner.Kind.OP_COLON;
import static cop5556sp18.Scanner.Kind.OP_DIV;
import static cop5556sp18.Scanner.Kind.OP_EQ;
import static cop5556sp18.Scanner.Kind.OP_EXCLAMATION;
import static cop5556sp18.Scanner.Kind.OP_GE;
import static cop5556sp18.Scanner.Kind.OP_GT;
import static cop5556sp18.Scanner.Kind.OP_LE;
import static cop5556sp18.Scanner.Kind.OP_LT;
import static cop5556sp18.Scanner.Kind.OP_MINUS;
import static cop5556sp18.Scanner.Kind.OP_MOD;
import static cop5556sp18.Scanner.Kind.OP_NEQ;
import static cop5556sp18.Scanner.Kind.OP_OR;
import static cop5556sp18.Scanner.Kind.OP_PLUS;
import static cop5556sp18.Scanner.Kind.OP_POWER;
import static cop5556sp18.Scanner.Kind.OP_QUESTION;
import static cop5556sp18.Scanner.Kind.OP_TIMES;
import static cop5556sp18.Scanner.Kind.RBRACE;
import static cop5556sp18.Scanner.Kind.RPAREN;
import static cop5556sp18.Scanner.Kind.RPIXEL;
import static cop5556sp18.Scanner.Kind.RSQUARE;
import static cop5556sp18.Scanner.Kind.SEMI;

import java.util.ArrayList;
import java.util.List;

import cop5556sp18.Scanner.Kind;
import cop5556sp18.Scanner.Token;
import cop5556sp18.AST.ASTNode;
import cop5556sp18.AST.Block;
import cop5556sp18.AST.Declaration;
import cop5556sp18.AST.Expression;
import cop5556sp18.AST.ExpressionBinary;
import cop5556sp18.AST.ExpressionBooleanLiteral;
import cop5556sp18.AST.ExpressionConditional;
import cop5556sp18.AST.ExpressionFloatLiteral;
import cop5556sp18.AST.ExpressionFunctionAppWithExpressionArg;
import cop5556sp18.AST.ExpressionFunctionAppWithPixel;
import cop5556sp18.AST.ExpressionIdent;
import cop5556sp18.AST.ExpressionIntegerLiteral;
import cop5556sp18.AST.ExpressionPixel;
import cop5556sp18.AST.ExpressionPixelConstructor;
import cop5556sp18.AST.ExpressionPredefinedName;
import cop5556sp18.AST.ExpressionUnary;
import cop5556sp18.AST.LHS;
import cop5556sp18.AST.LHSIdent;
import cop5556sp18.AST.LHSPixel;
import cop5556sp18.AST.LHSSample;
import cop5556sp18.AST.PixelSelector;
import cop5556sp18.AST.Program;
import cop5556sp18.AST.Statement;
import cop5556sp18.AST.StatementAssign;
import cop5556sp18.AST.StatementIf;
import cop5556sp18.AST.StatementInput;
import cop5556sp18.AST.StatementShow;
import cop5556sp18.AST.StatementSleep;
import cop5556sp18.AST.StatementWhile;
import cop5556sp18.AST.StatementWrite;

public class Parser {

	@SuppressWarnings("serial")
	public static class SyntaxException extends Exception {
		Token t;

		public SyntaxException(Token t, String message) {
			super(message);
			this.t = t;
		}

	}

	Scanner scanner;
	Token t;

	Parser(Scanner scanner) {
		this.scanner = scanner;
		t = scanner.nextToken();
	}

	public Program parse() throws SyntaxException {
		Program p = program();
		matchEOF();
		return p;
	}

	/*
	 * Program ::= Identifier Block
	 */
	public Program program() throws SyntaxException {
		Program prog = null;
		Token first = t;
		Token identifier = null;

		if (t.kind == Kind.IDENTIFIER) {
			identifier = t;
			consume();
		}

		Block b = block();
		prog = new Program(first, identifier, b);
		return prog;
	}

	/*
	 * Block ::= { ( (Declaration | Statement) ; )* }
	 */

	Kind[] firstStatement = { KW_input, KW_write, IDENTIFIER, KW_red, KW_blue, KW_green, KW_alpha, KW_while, KW_if,
			KW_show, KW_sleep };
	Kind[] firstDec = { KW_int, KW_boolean, KW_image, KW_float, KW_filename };
	Kind[] firstColor = { KW_red, KW_green, KW_blue, KW_alpha };
	Kind[] funcName = { KW_sin, KW_cos, KW_atan, KW_abs, KW_log, KW_cart_x, KW_cart_y, KW_polar_a, KW_polar_r, KW_int,
			KW_float, KW_width, KW_height, KW_red, KW_green, KW_blue, KW_alpha };
	Kind[] firstPredefinedName = { KW_Z, KW_default_height, KW_default_width };
	Kind[] firstPrimary = { INTEGER_LITERAL, BOOLEAN_LITERAL, FLOAT_LITERAL, LPAREN, IDENTIFIER, LPIXEL };
	

	public Block block() throws SyntaxException {
		Block b = null;
		Declaration d = null;
		Statement s = null;
		List<ASTNode> decsOrStatements = new ArrayList<>();
		Token first = t;
		match(LBRACE);
		while (isKind(firstDec) | isKind(firstStatement)) {
			if (isKind(firstDec)) {
				d = declaration();
				decsOrStatements.add(d);
			} else if (isKind(firstStatement)) {
				s = statement();
				decsOrStatements.add(s);
			}
			match(SEMI);
		}
		match(RBRACE);
		b = new Block(first, decsOrStatements);
		return b;
	}

	/*
	 * Declaration ::= Type IDENTIFIER | image IDENTIFIER [ Expression , Expression
	 * ]
	 */

	public Declaration declaration() throws SyntaxException {
		// matching Type
		Declaration d = null;
		Expression width = null, height = null;
		Token first = t;

		if(isKind(KW_image))
		{
			consume();
			Token ident = t;
			if (ident.kind == Kind.IDENTIFIER) {
				consume();
			}
			if (isKind(LSQUARE)) {
				match(LSQUARE);
				width = expression();
				match(COMMA);
				height = expression();
				match(RSQUARE);
			}
			d = new Declaration(first, first, ident, width, height);
			
			
		}
		else if (isKind(KW_int) || isKind(KW_boolean) || isKind(KW_image) || isKind(KW_float) || isKind(KW_filename))
			{
			consume();
			Token ident = t;
			if (ident.kind == Kind.IDENTIFIER) {
				consume();
			}
			d = new Declaration(first, first, ident, width, height);
			
			
			}
		return d;

		// match(IDENTIFIER);
		

		
		
	}
	/* StatementInput ::= input IDENTIFIER from @ Expression */

	public StatementInput statementInput() throws SyntaxException {
		Token first = t;
		Token identifier;
		StatementInput stmtInput = null;
		Expression exp = null;

		match(KW_input);

		identifier = t;
		match(IDENTIFIER);

		match(KW_from);
		match(OP_AT);

		exp = expression();

		stmtInput = new StatementInput(first, identifier, exp);
		return stmtInput;
	}

	/*
	 * Statement ::= StatementInput | StatementWrite | StatementAssignment |
	 * StatementWhile | StatementIf | StatementShow | StatementSleep
	 */

	public Statement statement() throws SyntaxException {

		Statement stmt = null;

		if (isKind(KW_input))
			stmt = statementInput();

		else if (isKind(KW_write))
			stmt = statementWrite();

		else if (isKind(IDENTIFIER) || isKind(KW_red) || isKind(KW_green) || isKind(KW_blue) || isKind(KW_alpha))
			stmt = statementAssignment();

		else if (isKind(KW_while))
			stmt = statementWhile();

		else if (isKind(KW_if))
			stmt = statementIf();

		else if (isKind(KW_show))
			stmt = statementShow();

		else if (isKind(KW_sleep))
			stmt = statementSleep();

		else
			throw new UnsupportedOperationException();
		return stmt;
	}

	

	/* StatementWrite ::= write IDENTIFIER to IDENTIFIER */

	public StatementWrite statementWrite() throws SyntaxException {
		Token first = t;
		StatementWrite stmtWrite = null;
		Token identifier1, identifier2;

		match(KW_write);

		identifier1 = t;
		match(IDENTIFIER);

		match(KW_to);

		identifier2 = t;
		match(IDENTIFIER);

		stmtWrite = new StatementWrite(first, identifier1, identifier2);
		return stmtWrite;
	}

	/* StatementAssignment ::= LHS := Expression */

	public StatementAssign statementAssignment() throws SyntaxException {
		Token first = t;
		StatementAssign stmtAssignment = null;
		Expression exp = null;
		LHS lhs = null;

		if (isKind(IDENTIFIER) || isKind(KW_red) || isKind(KW_green) || isKind(KW_blue) || isKind(KW_alpha))
			{
			lhs = LHS();
			}
		System.out.println(lhs);
		System.out.println("This is done!");
		match(OP_ASSIGN);
		

		exp = expression();

		stmtAssignment = new StatementAssign(first, lhs, exp);
		return stmtAssignment;
	}

	/* StatementWhile ::= while (Expression ) Block */

	public StatementWhile statementWhile() throws SyntaxException {
		Token first = t;
		StatementWhile stmtWhile = null;
		Expression exp = null;
		Block blk = null;

		match(KW_while);
		match(LPAREN);

		exp = expression();

		match(RPAREN);

		blk = block();

		stmtWhile = new StatementWhile(first, exp, blk);
		return stmtWhile;
	}
	/* StatementShow ::= show Expression */

	public StatementShow statementShow() throws SyntaxException {
		Token first = t;
		StatementShow stmtShow = null;
		Expression exp = null;

		match(KW_show);

		exp = expression();

		stmtShow = new StatementShow(first, exp);
		return stmtShow;

	}


	/* StatementIf ::= if ( Expression ) Block */

	public StatementIf statementIf() throws SyntaxException {
		Token first = t;
		StatementIf stmtIf = null;
		Expression exp = null;
		Block blk = null;

		match(KW_if);
		match(LPAREN);

		exp = expression();

		match(RPAREN);

		blk = block();

		stmtIf = new StatementIf(first, exp, blk);
		return stmtIf;
	}

	
	/* StatementSleep ::= sleep Expression */

	public StatementSleep statementSleep() throws SyntaxException {
		Token first = t;
		StatementSleep stmtSleep = null;
		Expression exp = null;

		match(KW_sleep);
		exp = expression();

		stmtSleep = new StatementSleep(first, exp);
		return stmtSleep;
	}

	

	/* Color ::= red | green | blue | alpha */

	public void color() throws SyntaxException {
		if (isKind(firstColor)) {
			consume();
		}
	}

	/*
	 * LHS ::= IDENTIFIER | IDENTIFIER PixelSelector | Color ( IDENTIFIER
	 * PixelSelector )
	 */

	public LHS LHS() throws SyntaxException {
		LHS lhs = null;
		Token first = t;
		PixelSelector ps = null;

		if (isKind(IDENTIFIER) || isKind(firstColor)) {
			if (isKind(IDENTIFIER)) {
				
				Token identifier = t;
				consume();
				if (isKind(LSQUARE)) {
					ps = pixelSelector();
					return new LHSPixel(first, identifier, ps);
				} else {
					return new LHSIdent(first, identifier);
				}

			} else if (isKind(firstColor)) {
				//System.out.println("Entered correct one");
				Token color = t;
				consume();
				match(LPAREN);
				Token identifier = t;
				match(IDENTIFIER);
				ps = pixelSelector();
				match(RPAREN);
				return new LHSSample(first, identifier, ps, color);
			}
		}

		return lhs;

	}

	/* PixelSelector ::= [ Expression , Expression ] */

	public PixelSelector pixelSelector() throws SyntaxException {
		Token first = t;
		PixelSelector ps = null;
		Expression el1 = null, el2 = null;
		match(Kind.LSQUARE);
		el1 = expression();
		match(Kind.COMMA);
		el2 = expression();
		match(Kind.RSQUARE);

		ps = new PixelSelector(first, el1, el2);
		return ps;

	}

	

	public void pixelExpression() throws SyntaxException {
		match(IDENTIFIER);
		pixelSelector();
	}

	/*
	 * PixelConstructor ::= << Expression , Expression , Expression , Expression >>
	 */

	public ExpressionPixelConstructor pixelConstructor() throws SyntaxException {
		Token first = t;
		ExpressionPixelConstructor epc = null;
		Expression el1 = null, el2 = null, el3 = null, el4 = null;
		match(LPIXEL);
		el1 = expression();
		match(COMMA);
		el2 = expression();
		match(COMMA);
		el3 = expression();
		match(COMMA);
		el4 = expression();
		match(RPIXEL);

		epc = new ExpressionPixelConstructor(first, el1, el2, el3, el4);
		return epc;
	}

	

	/*
	 * FunctionApplication ::= FunctionName ( Expression ) | FunctionName [
	 * Expression , Expression ]
	 */

	public Expression functionApplication() throws SyntaxException {
		Token first = t;
		Token funcName = t;
		funcName = functionName();

		if (isKind(LPAREN)) {
			match(LPAREN);
			Expression exp = expression();
			match(RPAREN);
			return new ExpressionFunctionAppWithExpressionArg(first, funcName, exp);

		} else if (isKind(LSQUARE)) {
			match(LSQUARE);
			Expression exp1 = expression();
			match(COMMA);
			Expression exp2 = expression();
			match(RSQUARE);
			return new ExpressionFunctionAppWithPixel(first, funcName, exp1, exp2);
		}
		else {
			throw new SyntaxException(t,
					"Syntax Exception");
		}

	}
	/*
	 * FunctionName ::= sin | cos | atan | abs | log | cart_x | cart_y | polar_a |
	 * polar_r int | float | width | height | Color
	 */

	public Token functionName() throws SyntaxException {
		Token token = t;
		if (isKind(funcName)) {
			consume();
		}
		return token;
	}


	

	/* PredefinedName ::= Z | default_height | default_width */

	public ExpressionPredefinedName predefinedName() throws SyntaxException {
		Token first = t;
		if (isKind(KW_Z) || isKind(KW_default_height) || isKind(KW_default_width)) {
			Token predefinedName = t;
			consume();
			return new ExpressionPredefinedName(first, predefinedName);
		}
		return null;
	}

	

	/*
	 * Primary ::= INTEGER_LITERAL | BOOLEAN_LITERAL | FLOAT_LITERAL | ( Expression
	 * ) | FunctionApplication | IDENTIFIER | PixelExpression | PredefinedName |
	 * PixelConstructor
	 */

	public Expression primary() throws SyntaxException {
		Token first = t;
		if (isKind(firstPrimary) || isKind(funcName) || isKind(firstPredefinedName)) {
			if (isKind(INTEGER_LITERAL)) {
				Token intVal = t;
				consume();
				return new ExpressionIntegerLiteral(first, intVal);

			} else if (isKind(BOOLEAN_LITERAL)) {
				Token boolVal = t;
				consume();
				return new ExpressionBooleanLiteral(first, boolVal);

			} else if (isKind(FLOAT_LITERAL)) {
				Token floatVal = t;
				consume();
				return new ExpressionFloatLiteral(first, floatVal);

			} else if (isKind(LPAREN)) {
				match(LPAREN);
				Expression el0 = expression();
				match(RPAREN);
				return el0;

			} else if (isKind(funcName)) {
				Expression el0 = functionApplication();
				return el0;

			} else if (isKind(IDENTIFIER)) {
				Token identifier = t;
				consume();

				if (isKind(LSQUARE)) {
					PixelSelector ps = pixelSelector();
					return new ExpressionPixel(first, identifier, ps);
				} else {
					return new ExpressionIdent(first, identifier);
				}

			} else if (isKind(firstPredefinedName)) {
				Token pname = t;
				consume();
				return new ExpressionPredefinedName(first, pname);

			} else if (isKind(LPIXEL)) {
				ExpressionPixelConstructor epc = pixelConstructor();
				return epc;
			}
		} else {
			throw new SyntaxException(t, "Syntax Error");
		}
		return null;
	}

	/* UnaryExpressionNotPlusMinus ::= ! UnaryExpression | Primary */

	public Expression unaryExpressionNotPlusMinus() throws SyntaxException {
		Token first = t;
		if (isKind(OP_EXCLAMATION) || isKind(firstPrimary) || isKind(funcName) || isKind(firstPredefinedName)) {
			if (isKind(OP_EXCLAMATION)) {
				Token op = t;
				consume();
				Expression eu = unaryExpression();
				return new ExpressionUnary(first, op, eu);

			} else {
				Expression prim = primary();
				return prim;
			}
		}
		return null;
	}

	/*
	 * UnaryExpression ::= + UnaryExpression | - UnaryExpression |
	 * UnaryExpressionNotPlusMinus
	 */

	public Expression unaryExpression() throws SyntaxException {
		Token first = t;
		if (isKind(OP_PLUS) || isKind(OP_MINUS) || isKind(OP_EXCLAMATION) || isKind(firstPrimary) || isKind(funcName)
				|| isKind(firstPredefinedName)) {
			Token op = t;

			if (isKind(OP_PLUS)) {
				consume();
				Expression eu = unaryExpression();
				return new ExpressionUnary(first, op, eu);

			} else if (isKind(OP_MINUS)) {
				consume();
				Expression eu = unaryExpression();
				return new ExpressionUnary(first, op, eu);

			} else {
				Expression eunpm = unaryExpressionNotPlusMinus();
				return eunpm;
			}
		} else {
			String message = "Expected expression at line " + t.line() + ": position " + t.posInLine() + " found "
					+ t.kind.toString();
			throw new SyntaxException(t, message);
		}
	}

	/* PowerExpression := UnaryExpression (** PowerExpression | ε) */

	public Expression powerExpression() throws SyntaxException {
		Token first = t;
		Token op = null;
		Expression el0 = null;
		Expression el1 = null;

		el0 = unaryExpression();

		if (isKind(OP_POWER)) {
			op = t;
			consume();
			el1 = powerExpression();
			el0 = new ExpressionBinary(first, el0, op, el1);
		}
		return el0;
	}

	/* MultExpression := PowerExpression ( ( * | / | % ) PowerExpression ) **/

	public Expression multExpression() throws SyntaxException {
		Token first = t;
		Token op = null;
		Expression el0 = null;
		Expression el1 = null;

		el0 = powerExpression();

		while (isKind(OP_TIMES) || isKind(OP_DIV) || isKind(OP_MOD)) {
			op = t;
			consume();
			el1 = powerExpression();
			el0 = new ExpressionBinary(first, el0, op, el1);
		}
		return el0;
	}

	/* AddExpression ::= MultExpression ( ( + | - ) MultExpression ) **/

	public Expression addExpression() throws SyntaxException {
		Token first = t;
		Token op = null;
		Expression el0 = null;
		Expression el1 = null;

		el0 = multExpression();

		while (isKind(OP_PLUS) || isKind(OP_MINUS)) {
			op = t;
			consume();
			el1 = multExpression();
			el0 = new ExpressionBinary(first, el0, op, el1);
		}
		return el0;
	}

	/* RelExpression ::= AddExpression ( (< | > | <= | >= ) AddExpression) **/

	public Expression relExpression() throws SyntaxException {
		Token first = t;
		Token op = null;
		Expression el0 = null;
		Expression el1 = null;

		el0 = addExpression();

		while (isKind(OP_LT) || isKind(OP_GT) || isKind(OP_LE) || isKind(OP_GE)) {
			op = t;
			consume();
			el1 = addExpression();
			el0 = new ExpressionBinary(first, el0, op, el1);
		}
		return el0;
	}

	/* EqExpression ::= RelExpression ( (== | != ) RelExpression ) **/

	public Expression eqExpression() throws SyntaxException {
		Token first = t;
		Token op = null;
		Expression el0 = null;
		Expression el1 = null;

		el0 = relExpression();

		while (isKind(OP_EQ) || isKind(OP_NEQ)) {
			op = t;
			consume();
			el1 = relExpression();
			el0 = new ExpressionBinary(first, el0, op, el1);
		}
		return el0;
	}

	/* AndExpression ::= EqExpression ( & EqExpression ) **/

	public Expression andExpression() throws SyntaxException {
		Token first = t;
		Token op = null;
		Expression el0 = null;
		Expression el1 = null;

		el0 = eqExpression();

		while (isKind(OP_AND)) {
			op = t;
			consume();
			el1 = eqExpression();
			el0 = new ExpressionBinary(first, el0, op, el1);
		}
		return el0;
	}

	/* OrExpression ::= AndExpression ( | AndExpression ) **/

	public Expression orExpression() throws SyntaxException {
		Token first = t;
		Token op = null;
		Expression el0 = null;
		Expression el1 = null;

		el0 = andExpression();

		while (isKind(OP_OR)) {
			op = t;
			consume();
			el1 = andExpression();
			el0 = new ExpressionBinary(first, el0, op, el1);
		}
		return el0;
	}

	/*
	 * Expression ::= OrExpression ? Expression : Expression | OrExpression
	 */

	public Expression expression() throws SyntaxException {
		Token first = t;
		Expression el0 = null, el1 = null, el2 = null;
		el0 = orExpression();

		if (isKind(OP_QUESTION)) {
			consume();
			el1 = expression();
			match(OP_COLON);
			el2 = expression();
			return new ExpressionConditional(first, el0, el1, el2);
		}
		return el0;
	}

	protected boolean isKind(Kind kind) {
		return t.kind == kind;
	}

	protected boolean isKind(Kind... kinds) {
		for (Kind k : kinds) {
			if (k == t.kind)
				return true;
		}
		return false;
	}

	/**
	 * Precondition: kind != EOF
	 * 
	 * @param kind
	 * @return
	 * @throws SyntaxException
	 */
	private Token match(Kind kind) throws SyntaxException {
		Token tmp = t;
		if (isKind(kind)) {
			consume();
			return tmp;
		}
		String message = "Expected " + kind.toString() + " at line " + t.line() + ": position " + t.posInLine()
				+ " found " + t.kind.toString();
		throw new SyntaxException(t, message); // TODO give a better error message!
	}

	private Token consume() throws SyntaxException {
		Token tmp = t;
		if (isKind(EOF)) {
			throw new SyntaxException(t, "Syntax Error"); // TODO give a better error message!
			// Note that EOF should be matched by the matchEOF method which is called only
			// in parse().
			// Anywhere else is an error. */
		}
		t = scanner.nextToken();
		return tmp;
	}

	/**
	 * Only for check at end of program. Does not "consume" EOF so no attempt to get
	 * nonexistent next Token.
	 * 
	 * @return
	 * @throws SyntaxException
	 */
	private Token matchEOF() throws SyntaxException {
		if (isKind(EOF)) {
			return t;
		}
		String message = "Expected EOL at " + t.line() + ":" + t.posInLine();
		throw new SyntaxException(t, message); // TODO give a better error message!
	}

}
