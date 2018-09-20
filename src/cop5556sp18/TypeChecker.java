package cop5556sp18;



import cop5556sp18.Scanner.Kind;
//import cop5556sp18.AST.;
import cop5556sp18.Scanner.Token;
import cop5556sp18.Types.Type;
import cop5556sp18.AST.ASTNode;
import cop5556sp18.AST.ASTVisitor;
import cop5556sp18.AST.Block;
import cop5556sp18.AST.Declaration;
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
import cop5556sp18.AST.LHSIdent;
import cop5556sp18.AST.LHSPixel;
import cop5556sp18.AST.LHSSample;
import cop5556sp18.AST.PixelSelector;
import cop5556sp18.AST.Program;
import cop5556sp18.AST.StatementAssign;
import cop5556sp18.AST.StatementIf;
import cop5556sp18.AST.StatementInput;
import cop5556sp18.AST.StatementShow;
import cop5556sp18.AST.StatementSleep;
import cop5556sp18.AST.StatementWhile;
import cop5556sp18.AST.StatementWrite;




public class TypeChecker implements ASTVisitor {
	symbolTable symtable = new symbolTable();
	TypeChecker() {
	}

	@SuppressWarnings("serial")
	public static class SemanticException extends Exception {
		Token t;

		public SemanticException(Token t, String message) {
			super(message);
			this.t = t;
		}
	}

	
	
	
	@Override
	public Object visitProgram(Program program, Object arg) throws Exception {
		//program.visit(this,null);
		program.block.visit(this, arg);
		return program;
	}

	@Override
	public Object visitBlock(Block block, Object arg) throws Exception {
		symtable.enterScope();
		// TODO Auto-generated method stub
		
		for(ASTNode n : block.decsOrStatements)
		{
			n.visit(this,arg);
		}
		symtable.leaveScope();
		return block;
	}

	@Override
	public Object visitDeclaration(Declaration declaration, Object arg) throws Exception {
		
		String name = declaration.name;
		Declaration dec = symtable.lookkupinCurrentScope(name);
		if(dec != null){
			throw new SemanticException(declaration.firstToken, "IdentExpr:ident is already declared in current scope");
		}
		cop5556sp18.AST.Expression e0 = declaration.width;
		cop5556sp18.AST.Expression e1 = declaration.height;
		/*System.out.println(e0);
		System.out.println(e1);
		System.out.println(declaration);*/
		if((e0 != null && e1 != null))
		{
			e0.visit(this, arg);
			e1.visit(this, arg);
		}
		
		if(!((e0 == null)|| (e0.type.equals(Types.Type.INTEGER) && Types.getType(declaration.type).equals(Types.Type.IMAGE))))
		{
			throw new SemanticException(e0.firstToken, "Type check exception");
		}
		if(!((e1 == null)|| (e1.type.equals(Types.Type.INTEGER) && Types.getType(declaration.type).equals((Types.Type.IMAGE)))))
		{
			throw new SemanticException(e1.firstToken, "Type check exception");
		}
		if((e0 == null && e1 != null) || (e0 != null && e1 == null))
		{
			throw new SemanticException(declaration.firstToken, "Type check exception");
		}
		
		Boolean check = symtable.insertintomap(name, declaration);
		if(!check){
			throw new SemanticException(declaration.firstToken,"Inserting duplicate variables under same scope is not allowed");
		}
		return declaration;
	}

	

	@Override
	public Object visitStatementWrite(StatementWrite statementWrite, Object arg) throws Exception {
		
		Declaration dec1 = symtable.lookup(statementWrite.sourceName);
		
		if(dec1 == null){
			throw new SemanticException(statementWrite.firstToken, "IdentExpr:ident is not declared in current scope");
		}
		statementWrite.sourceDec = dec1;
		Declaration dec2 = symtable.lookup(statementWrite.destName);
		
		if(dec2 == null){
			throw new SemanticException(statementWrite.firstToken, "IdentExpr:ident is not declared in current scope");
		}
		statementWrite.destDec = dec2;
		System.out.println(dec1.type+" Testing "+dec2.type);
		Types.getType(Kind.KW_image);
		Types.getType(Kind.KW_filename);
		if(dec1.type.equals(Kind.KW_image) && dec2.type.equals(Kind.KW_filename))
		{
			
		}
		else
		{
			throw new SemanticException(statementWrite.firstToken, "Type mismatch");
		}
		
		return statementWrite;
	}

	@Override
	public Object visitStatementInput(StatementInput statementInput, Object arg) throws Exception {
		//statementInput.firstToken = statementInput.destName;
		statementInput.e.visit(this, arg);
		
		
		Declaration dec = symtable.lookup(statementInput.destName);
		statementInput.dec= dec;
		if(dec == null){
			throw new SemanticException(null, "IdentExpr:ident is not declared in current scope");
		}
		
		if(!statementInput.e.type.equals(Types.getType(Kind.KW_int)))
			{
			throw new SemanticException(statementInput.firstToken, "Type mismatch");
			}
		
		return statementInput;
		
	}

	@Override
	public Object visitPixelSelector(PixelSelector pixelSelector, Object arg) throws Exception {
		pixelSelector.ex.visit(this, arg);
		pixelSelector.ey.visit(this, arg);
		Type extype = pixelSelector.ex.type;
		Type eytype = pixelSelector.ey.type;

		if (!(extype.equals(eytype))) {
			String message = "Type Mismatch";
			throw new SemanticException(pixelSelector.firstToken, message);
		}

		
			if (!(extype.equals(Types.getType(Kind.KW_int)) || eytype.equals(Types.getType(Kind.KW_float)))) {
				String message = "Type Mismatch";
				throw new SemanticException(pixelSelector.firstToken, message);
			}
		return pixelSelector;
	}

	@Override
	public Object visitExpressionConditional(ExpressionConditional expressionConditional, Object arg) throws Exception {
		expressionConditional.guard.visit(this, arg);
		expressionConditional.trueExpression.visit(this, arg);
		expressionConditional.falseExpression.visit(this, arg);
		if(!expressionConditional.guard.type.equals(Types.getType(Kind.KW_boolean)))
		{
			throw new SemanticException(expressionConditional.guard.firstToken, "Expression0 has type error");
		}
		if(!(expressionConditional.trueExpression.type.equals(expressionConditional.falseExpression.type))) {
			throw new SemanticException(null, "Expression2 has type mismatch with Expression1");
		}
		expressionConditional.type = expressionConditional.falseExpression.type;
		return expressionConditional;
	}

	@Override
	public Object visitExpressionBinary(ExpressionBinary expressionBinary, Object arg) throws Exception {
		expressionBinary.leftExpression.visit(this, arg);
		expressionBinary.rightExpression.visit(this, arg);
		Type typeleft = expressionBinary.leftExpression.type;
		Type typeright = expressionBinary.rightExpression.type;
		expressionBinary.type = inferredTypePart1(typeleft,typeright ,expressionBinary.op);
		return expressionBinary;
	}

	@Override
	public Object visitExpressionUnary(ExpressionUnary expressionUnary, Object arg) throws Exception {
		expressionUnary.expression.visit(this, arg);
		expressionUnary.type = expressionUnary.expression.type;
		return expressionUnary;
	}

	@Override
	public Object visitExpressionIntegerLiteral(ExpressionIntegerLiteral expressionIntegerLiteral, Object arg)
			throws Exception {
		expressionIntegerLiteral.type = Types.getType(Kind.KW_int);
		return expressionIntegerLiteral;
	}

	@Override
	public Object visitBooleanLiteral(ExpressionBooleanLiteral expressionBooleanLiteral, Object arg) throws Exception {
		expressionBooleanLiteral.type = Types.getType(Kind.KW_boolean);
		return expressionBooleanLiteral;
	}

	@Override
	public Object visitExpressionPredefinedName(ExpressionPredefinedName expressionPredefinedName, Object arg)
			throws Exception {
		expressionPredefinedName.type = Types.getType(Kind.KW_int);
		return expressionPredefinedName;
	}

	@Override
	public Object visitExpressionFloatLiteral(ExpressionFloatLiteral expressionFloatLiteral, Object arg)
			throws Exception {
		expressionFloatLiteral.type = Types.getType(Kind.KW_float);
		return expressionFloatLiteral;
	}

	@Override
	public Object visitExpressionFunctionAppWithExpressionArg(
			ExpressionFunctionAppWithExpressionArg expressionFunctionAppWithExpressionArg, Object arg)
			throws Exception {
		expressionFunctionAppWithExpressionArg.e.visit(this, arg);
		//System.out.println(expressionFunctionAppWithExpressionArg.function + "testing" +expressionFunctionAppWithExpressionArg.e.type);
		expressionFunctionAppWithExpressionArg.type = inferredTypePart2Function(expressionFunctionAppWithExpressionArg.function,expressionFunctionAppWithExpressionArg.e.type);
		
	
		return expressionFunctionAppWithExpressionArg;
	}

	@Override
	public Object visitExpressionFunctionAppWithPixel(ExpressionFunctionAppWithPixel expressionFunctionAppWithPixel,
			Object arg) throws Exception {
		expressionFunctionAppWithPixel.e0.visit(this, arg);
		expressionFunctionAppWithPixel.e1.visit(this, arg);
		if(expressionFunctionAppWithPixel.name.equals(Kind.KW_cart_x) || expressionFunctionAppWithPixel.name.equals(Kind.KW_cart_y) )
		{
			if(!(expressionFunctionAppWithPixel.e0.type.equals(Types.getType(Kind.KW_float)) && expressionFunctionAppWithPixel.e1.type.equals(Types.getType(Kind.KW_float))))
			{
				throw new SemanticException(expressionFunctionAppWithPixel.firstToken,"Type check exception");
			}
			expressionFunctionAppWithPixel.type = Types.getType(Kind.KW_int);
		}
		if(expressionFunctionAppWithPixel.name.equals(Kind.KW_polar_a) || expressionFunctionAppWithPixel.name.equals(Kind.KW_polar_r) )
		{
			if(!(expressionFunctionAppWithPixel.e0.type.equals(Types.getType(Kind.KW_int)) && expressionFunctionAppWithPixel.e1.type.equals(Types.getType(Kind.KW_int))))
			{
				throw new SemanticException(expressionFunctionAppWithPixel.firstToken,"Type check exception");
			}
			expressionFunctionAppWithPixel.type = Types.getType(Kind.KW_float);
		}
		return expressionFunctionAppWithPixel;
		
	}

	@Override
	public Object visitExpressionPixelConstructor(ExpressionPixelConstructor expressionPixelConstructor, Object arg)
			throws Exception {
		expressionPixelConstructor.alpha.visit(this, arg);
		expressionPixelConstructor.blue.visit(this, arg);
		expressionPixelConstructor.green.visit(this, arg);
		expressionPixelConstructor.red.visit(this, arg);
		if(!(expressionPixelConstructor.alpha.type.equals(Types.getType(Kind.KW_int)) && expressionPixelConstructor.blue.type.equals(Types.getType(Kind.KW_int)) && expressionPixelConstructor.green.type.equals(Types.getType(Kind.KW_int)) && expressionPixelConstructor.red.type.equals(Types.getType(Kind.KW_int))))
		{
			throw new SemanticException(expressionPixelConstructor.firstToken,"Type mismatch");
		}
		expressionPixelConstructor.type = Types.getType(Kind.KW_int);
		return expressionPixelConstructor;
	}

	@Override
	public Object visitStatementAssign(StatementAssign statementAssign, Object arg) throws Exception {
		// TODO Auto-generated method stub
		statementAssign.lhs.visit(this, arg);
		statementAssign.e.visit(this, arg);
		//System.out.println(statementAssign.e.type+"testing"+statementAssign.lhs.type);
		if(!statementAssign.e.type.equals(statementAssign.lhs.type))
		{
			throw new SemanticException(statementAssign.firstToken, "Type mismatch");
		}
		
		return statementAssign;
	} 

	@Override
	public Object visitStatementShow(StatementShow statementShow, Object arg) throws Exception {
		statementShow.e.visit(this, arg);
		if(!(statementShow.e.type.equals(Types.getType(Kind.KW_int)) || statementShow.e.type.equals(Types.getType(Kind.KW_boolean))|| statementShow.e.type.equals(Types.getType(Kind.KW_float)) || statementShow.e.type.equals(Types.getType(Kind.KW_image))))
		{
			throw new SemanticException(statementShow.firstToken, "Type mismatch");
		}
		return statementShow;
	}
  
	@Override
	public Object visitExpressionPixel(ExpressionPixel expressionPixel, Object arg) throws Exception {
		expressionPixel.pixelSelector.visit(this, arg);
		Declaration dec = symtable.lookup(expressionPixel.name);
		expressionPixel.dec = dec;
		if(dec==null)
		{
			throw new SemanticException(null, "IdentExpr:ident is not declared in current scope");
		}
		if(!dec.type.equals(Kind.KW_image))
		{
			throw new SemanticException(dec.firstToken, "Type mismatch");
		}
		expressionPixel.type = Types.getType(Kind.KW_int);
		return expressionPixel;
	}

	@Override
	public Object visitExpressionIdent(ExpressionIdent expressionIdent, Object arg) throws Exception {
		Declaration dec = symtable.lookup(expressionIdent.name);
		expressionIdent.dec= dec;
		if(dec == null)
		{
			throw new SemanticException(expressionIdent.firstToken,"Ident is not declared in current scope");
		}
		expressionIdent.type = Types.getType(dec.type);
		return expressionIdent;
	}

	@Override
	public Object visitLHSSample(LHSSample lhsSample, Object arg) throws Exception {
		lhsSample.pixelSelector.visit(this, arg);
		Declaration dec = symtable.lookup(lhsSample.name);
		lhsSample.dec= dec;
		if(dec == null)
		{
			throw new SemanticException(lhsSample.firstToken, "IdentExpr:ident is not declared in current scope");
		}
		if(!dec.type.equals(Kind.KW_image))
		{
			throw new SemanticException(dec.firstToken,"Type Mismatch");
		}
		lhsSample.type = Types.Type.INTEGER;
		return lhsSample;
	}

	@Override
	public Object visitLHSPixel(LHSPixel lhsPixel, Object arg) throws Exception {
		lhsPixel.pixelSelector.visit(this, arg);
		Declaration dec = symtable.lookup(lhsPixel.name);
		lhsPixel.dec= dec;
		if(dec == null)
		{
			throw new SemanticException(null, "IdentExpr:ident is not declared in current scope");
		}
		if(!dec.type.equals(Kind.KW_image))
		{
			throw new SemanticException(dec.firstToken,"Type Mismatch");
		}
		lhsPixel.type = Types.Type.INTEGER;
		return lhsPixel;
	}

	@Override
	public Object visitLHSIdent(LHSIdent lhsIdent, Object arg) throws Exception {
		Declaration dec = symtable.lookup(lhsIdent.name);
		lhsIdent.dec= dec;
		if(dec == null)
		{
			throw new SemanticException(lhsIdent.firstToken, "IdentExpr:ident is not declared in current scope");
		}
		lhsIdent.type = Types.getType(lhsIdent.dec.type);
		return lhsIdent;
	}

	@Override
	public Object visitStatementIf(StatementIf statementIf, Object arg) throws Exception {
		statementIf.guard.visit(this, arg);
		statementIf.b.visit(this, arg);
		//Expression.type == boolean
				if(!statementIf.guard.type.equals(Types.Type.BOOLEAN))
				{
					throw new SemanticException(statementIf.firstToken, "Type mismatch");
				}
				
				return statementIf;
	}

	@Override
	public Object visitStatementWhile(StatementWhile statementWhile, Object arg) throws Exception {
		statementWhile.guard.visit(this, arg);
		statementWhile.b.visit(this, arg);
		//Expression.type == boolean
		if(!statementWhile.guard.type.equals(Types.Type.BOOLEAN))
		{
			throw new SemanticException(statementWhile.firstToken, "Type mismatch");
		}
		
		return statementWhile;
	}

	@Override
	public Object visitStatementSleep(StatementSleep statementSleep, Object arg) throws Exception {
		statementSleep.duration.visit(this, arg);
		//Expression.type == Integer
				if(!statementSleep.duration.type.equals(Types.Type.INTEGER))
				{
					throw new SemanticException(statementSleep.firstToken, "Type mismatch");
				}
				
				return statementSleep;
	}
public Type inferredTypePart2Function(Kind function, Type type) throws SemanticException {
		
		if ((function.equals(Kind.KW_abs) || function.equals(Kind.KW_red) || function.equals(Kind.KW_green)
				|| function.equals(Kind.KW_blue) || function.equals(Kind.KW_alpha)) && type.equals(Type.INTEGER)) {
			return Type.INTEGER;
			
		} else if ((function.equals(Kind.KW_abs) || function.equals(Kind.KW_sin) || function.equals(Kind.KW_cos)
				|| function.equals(Kind.KW_atan) || function.equals(Kind.KW_log)) && type.equals(Type.FLOAT)) {
			return Type.FLOAT;
			
		} else if ((function.equals(Kind.KW_width) || function.equals(Kind.KW_height)) && type.equals(Type.IMAGE)) {
			return Type.INTEGER;
			
		} else if (function.equals(Kind.KW_float) && type.equals(Type.INTEGER)) {
			return Type.FLOAT;
			
		} else if (function.equals(Kind.KW_float) && type.equals(Type.FLOAT)) {
			return Type.FLOAT;
			
		} else if (function.equals(Kind.KW_int) && type.equals(Type.FLOAT)) {
			return Type.INTEGER;
			
		} else if (function.equals(Kind.KW_int) && type.equals(Type.INTEGER)) {
			return Type.INTEGER;
			
		} else {
			String message = "Semantic Error";
			throw new SemanticException(null, message);
		}
	}
public Type inferredTypePart1(Type typeleft, Type typeright, Kind op) throws SemanticException {
	switch (op) {
	case OP_PLUS:
	case OP_MINUS:
	case OP_TIMES:
	case OP_DIV:
	case OP_POWER: {
		if (typeleft.equals(Type.INTEGER) && typeright.equals(Type.INTEGER)) {
			return Type.INTEGER;
		} else if (typeleft.equals(Type.FLOAT) && typeright.equals(Type.FLOAT)) {
			return Type.FLOAT;
		} else if (typeleft.equals(Type.FLOAT) && typeright.equals(Type.INTEGER)) {
			return Type.FLOAT;
		} else if (typeleft.equals(Type.INTEGER) && typeright.equals(Type.FLOAT)) {
			return Type.FLOAT;
		} else {
			throw new SemanticException(null, "Unsupported Operation");
		}
	}

	case OP_MOD: {
		if (typeleft.equals(Type.INTEGER) && typeright.equals(Type.INTEGER)) {
			return Type.INTEGER;
		} else {
			throw new SemanticException(null, "Unsupported Operation");
		}
	}
	case OP_AND:
	case OP_OR: {
		if (typeleft.equals(Type.INTEGER) && typeright.equals(Type.INTEGER)) {
			return Type.INTEGER;
		} else if (typeleft.equals(Type.BOOLEAN) && typeright.equals(Type.BOOLEAN)) {
			return Type.BOOLEAN;
		} else {
			throw new SemanticException(null, "Unsupported Operation");
		}
	}

	case OP_EQ:
	case OP_NEQ:
	case OP_GT:
	case OP_GE:
	case OP_LT:
	case OP_LE: {
		if (typeleft.equals(Type.INTEGER) && typeright.equals(Type.INTEGER)) {
			return Type.BOOLEAN;
		} else if (typeleft.equals(Type.FLOAT) && typeright.equals(Type.FLOAT)) {
			return Type.BOOLEAN;
		} else if (typeleft.equals(Type.BOOLEAN) && typeright.equals(Type.BOOLEAN)) {
			return Type.BOOLEAN;
		} else {
			throw new SemanticException(null, "Unsupported Operation");
		}
	}
	default:
		throw new SemanticException(null, "Unsupported Operation");

	}
}



}
