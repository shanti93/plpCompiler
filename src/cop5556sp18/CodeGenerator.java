/**
 * Starter code for CodeGenerator.java used n the class project in COP5556 Programming Language Principles 
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


package cop5556sp18;


import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import cop5556sp18.Scanner.Kind;
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
import cop5556sp18.AST.Statement;
import cop5556sp18.AST.StatementAssign;
import cop5556sp18.AST.StatementIf;
import cop5556sp18.AST.StatementInput;
import cop5556sp18.AST.StatementShow;
import cop5556sp18.AST.StatementSleep;
import cop5556sp18.AST.StatementWhile;
import cop5556sp18.AST.StatementWrite;

public class CodeGenerator implements ASTVisitor, Opcodes {

	/**
	 * All methods and variable static.
	 */

	
	int decCount = 1;
	int slot = 1;
	Label codeStart;
	Label codeEnd;
	static final int Z = 255;

	ClassWriter cw;
	String className;
	String classDesc;
	String sourceFileName;

	MethodVisitor mv; // visitor of method currently under construction

	public static final int ALPHA = 0;
	public static final int RED = 1;
	public static final int GREEN = 2;
	public static final int BLUE = 3;

	/** Indicates whether genPrint and genPrintTOS should generate code. */
	final boolean DEVEL;
	final boolean GRADE;

	final int defaultWidth;
	final int defaultHeight;
	// final boolean itf = false;
	/**
	 * @param DEVEL
	 *            used as parameter to genPrint and genPrintTOS
	 * @param GRADE
	 *            used as parameter to genPrint and genPrintTOS
	 * @param sourceFileName
	 *            name of source file, may be null.
	 * @param defaultWidth
	 *            default width of images
	 * @param defaultHeight
	 *            default height of images
	 */
	public CodeGenerator(boolean DEVEL, boolean GRADE, String sourceFileName,
			int defaultWidth, int defaultHeight) {
		super();
		this.DEVEL = DEVEL;
		this.GRADE = GRADE;
		this.sourceFileName = sourceFileName;
		this.defaultWidth = defaultWidth;
		this.defaultHeight = defaultHeight;
	}

	@Override
	public Object visitBlock(Block block, Object arg) throws Exception {
		Label label0 = new Label();
		Label label1 = new Label();
		codeStart = label0;
		codeEnd = label1;
		for (ASTNode node : block.decsOrStatements) {
			if(node instanceof Declaration)
			{
				((Declaration) node).setSlotNum(slot);;
				slot++;
				((Declaration) node).visit(this, mv);
			}
		}
		
	
		mv.visitLabel(label0);
		for (ASTNode node : block.decsOrStatements) {
			if(node instanceof Statement)
			{
				((Statement) node).visit(this, arg);
			}
		}
		
		mv.visitLabel(label1);
		
		return null;
	}

	@Override
	public Object visitBooleanLiteral(
			ExpressionBooleanLiteral expressionBooleanLiteral, Object arg)
			throws Exception {
		mv.visitLdcInsn(expressionBooleanLiteral.value);
		return null;
	}

	@Override
	public Object visitDeclaration(Declaration declaration, Object arg)
			throws Exception {
		//////System.out.println("declaation etnered"+ declaration.getSlotNum());
		Type type = Types.getType(declaration.type);
		////System.out.println(type);
		
		if(type.equals(Types.Type.INTEGER))
		{
			////System.out.println("reached integer declaration");
			 mv.visitLocalVariable(declaration.name, "I", null, codeStart, codeEnd, declaration.getSlotNum());
		}
		else if(type.equals(Types.Type.BOOLEAN))
		{
			 mv.visitLocalVariable(declaration.name, "Z", null, codeStart, codeEnd, declaration.getSlotNum());
		}
		else if(type.equals(Types.Type.FLOAT))
		{
			 mv.visitLocalVariable(declaration.name, "F", null, codeStart, codeEnd, declaration.getSlotNum());
		}
		
		else if(type.equals(Types.Type.FILE))
		{
			 mv.visitLocalVariable(declaration.name, "Ljava/lang/String;", null, codeStart, codeEnd, declaration.getSlotNum());
			 //mv.visitVarInsn(ASTORE, declaration.getSlotNum());
		}
		
		else if(type.equals(Types.Type.IMAGE))
		{
			
			if(declaration.height!=null && declaration.width!=null)
			{
				declaration.width.visit(this, arg);
				declaration.height.visit(this, arg);
				mv.visitMethodInsn(INVOKESTATIC, "cop5556sp18/RuntimeImageSupport", "makeImage", RuntimeImageSupport.makeImageSig, false);
				
				mv.visitVarInsn(ASTORE, declaration.getSlotNum());
				
				
			}
			else
			{
				
				mv.visitLdcInsn(980);
				mv.visitLdcInsn(1024);
	
				mv.visitMethodInsn(INVOKESTATIC, "cop5556sp18/RuntimeImageSupport", "makeImage", RuntimeImageSupport.makeImageSig, false);
				mv.visitVarInsn(ASTORE, declaration.getSlotNum());
				//mv.visitVarInsn(ASTORE, declaration.getSlotNum());
			
				
			}
			
			
			
			
			}
		
		return null;
	}

	@Override
	public Object visitExpressionBinary(ExpressionBinary expressionBinary,
			Object arg) throws Exception {
		Label set_true = new Label();
		Label lab = new Label();
		Type type1 = expressionBinary.leftExpression.type;
		Type type2 = expressionBinary.rightExpression.type;
		
		 if(expressionBinary.op.equals(Kind.OP_POWER))
		{
			
			if(type1.equals(Type.INTEGER) && type2.equals(Type.INTEGER))
			{
				//////System.out.println("Entered power of two ints"+ expressionBinary.leftExpression + expressionBinary.rightExpression);
				expressionBinary.leftExpression.visit(this, arg);
				mv.visitInsn(I2D);
				expressionBinary.rightExpression.visit(this, arg);
				mv.visitInsn(I2D);
				mv.visitMethodInsn(INVOKESTATIC,  "java/lang/Math", "pow", "(DD)D", false);
				mv.visitInsn(D2I);
				
				
			}
			
			else if(type1.equals(Type.INTEGER) && type2.equals(Type.FLOAT))
			{
				
				expressionBinary.leftExpression.visit(this, arg);
				mv.visitInsn(I2D);
				expressionBinary.rightExpression.visit(this, arg);
				mv.visitInsn(F2D);
				mv.visitMethodInsn(INVOKESTATIC,  "java/lang/Math", "pow", "(DD)D", false);
				mv.visitInsn(D2F);
				
			}
			else if(type1.equals(Type.FLOAT) && type2.equals(Type.INTEGER))
			{
				
				expressionBinary.leftExpression.visit(this, arg);
				mv.visitInsn(F2D);
				expressionBinary.rightExpression.visit(this, arg);
				mv.visitInsn(I2D);
				mv.visitMethodInsn(INVOKESTATIC,  "java/lang/Math", "pow", "(DD)D", false);
				mv.visitInsn(D2F);
				
			}
			else if(type1.equals(Type.FLOAT) && type2.equals(Type.FLOAT))
			{
				
				expressionBinary.leftExpression.visit(this, arg);
				mv.visitInsn(F2D);
				expressionBinary.rightExpression.visit(this, arg);
				mv.visitInsn(F2D);
				mv.visitMethodInsn(INVOKESTATIC,  "java/lang/Math", "pow", "(DD)D", false);
				mv.visitInsn(D2F);
				
			}
			
			
		}
		else if(expressionBinary.op.equals(Kind.OP_TIMES))
		{
			if(type1.equals(Type.INTEGER) && type2.equals(Type.INTEGER))
			{
				expressionBinary.leftExpression.visit(this, arg);
				expressionBinary.rightExpression.visit(this, arg);
				mv.visitInsn(IMUL);
			}
			else
			{
				expressionBinary.leftExpression.visit(this, arg);
				if(type1.equals(Type.INTEGER))
				{
					
					mv.visitInsn(I2F);
					
					
					
				}
				expressionBinary.rightExpression.visit(this, arg);
				if(type2.equals(Type.INTEGER))
				{
					
					mv.visitInsn(I2F);
					
					
				}
				
				mv.visitInsn(FMUL);
			}
		}
		
		
		else if(expressionBinary.op.equals(Kind.OP_DIV))
		{
			if(type1.equals(Type.INTEGER) && type2.equals(Type.INTEGER))
			{
				expressionBinary.leftExpression.visit(this, arg);
				expressionBinary.rightExpression.visit(this, arg);
				mv.visitInsn(IDIV);
			}
			else
			{
				if(type1.equals(Type.INTEGER))
				{
					expressionBinary.leftExpression.visit(this, arg);
					mv.visitInsn(I2F);
					expressionBinary.rightExpression.visit(this, arg);
					
					
				}
				else if(type2.equals(Type.INTEGER))
				{
					
					
					expressionBinary.leftExpression.visit(this, arg);
					expressionBinary.rightExpression.visit(this, arg);
					mv.visitInsn(I2F);
					
				}
				else
				{
					expressionBinary.leftExpression.visit(this, arg);
					expressionBinary.rightExpression.visit(this, arg);
				}
				mv.visitInsn(FDIV);
			}
		}
		else if(expressionBinary.op.equals(Kind.OP_MOD))
		{
			expressionBinary.leftExpression.visit(this, arg);
			if(type1.equals(Type.FLOAT))
			{
				
				mv.visitInsn(F2I);
			}
			expressionBinary.rightExpression.visit(this, arg);
			if(type2.equals(Type.FLOAT))
			{
				
				mv.visitInsn(F2I);
			}
			mv.visitInsn(IREM);
		}
		
		else if(expressionBinary.op.equals(Kind.OP_PLUS))
		{
			if(type1.equals(Type.INTEGER) && type2.equals(Type.INTEGER))
			{
				expressionBinary.leftExpression.visit(this, arg);
				expressionBinary.rightExpression.visit(this, arg);
				mv.visitInsn(IADD);
			}
			else
			{
				expressionBinary.leftExpression.visit(this, arg);
				if(type1.equals(Type.INTEGER))
				{
					
					mv.visitInsn(I2F);
					
					
					
				}
				expressionBinary.rightExpression.visit(this, arg);
				if(type2.equals(Type.INTEGER))
				{
					
					mv.visitInsn(I2F);
					
					
				}
				mv.visitInsn(FADD);
			}
		}
		else if(expressionBinary.op.equals(Kind.OP_MINUS))
		{
			if(type1.equals(Type.INTEGER) && type2.equals(Type.INTEGER))
			{
				expressionBinary.leftExpression.visit(this, arg);
				expressionBinary.rightExpression.visit(this, arg);
				mv.visitInsn(ISUB);
			}
			else
			{
				expressionBinary.leftExpression.visit(this, arg);
				if(type1.equals(Type.INTEGER))
				{
					
					mv.visitInsn(I2F);
					
					
					
					
				}
				expressionBinary.rightExpression.visit(this, arg);
				if(type2.equals(Type.INTEGER))
				{
					
					mv.visitInsn(I2F);
					
					
				}
				
				mv.visitInsn(FSUB);
				
			}
		}
		
		
		
		
		else if(expressionBinary.op.equals(Kind.OP_LT))
		{
			expressionBinary.leftExpression.visit(this, arg);
			expressionBinary.rightExpression.visit(this, arg);
			
			if(type1.equals(Type.INTEGER) &&type2.equals(Type.INTEGER))
			{
				mv.visitJumpInsn(IF_ICMPLT, set_true);
				mv.visitLdcInsn(false);
			}
			
			else if(type1.equals(Type.FLOAT) && type2.equals(Type.FLOAT))
			{
				
				mv.visitInsn(FCMPG);
				mv.visitJumpInsn(IFLT, set_true);
				mv.visitLdcInsn(false);
			}
			
		}
		else if(expressionBinary.op.equals(Kind.OP_GT))
		{
			expressionBinary.leftExpression.visit(this, arg);
			expressionBinary.rightExpression.visit(this, arg);
			
			if(type1.equals(Type.INTEGER) &&type2.equals(Type.INTEGER))
			{
				mv.visitJumpInsn(IF_ICMPGT, set_true);
				mv.visitLdcInsn(false);
			}
			
			else if(type1.equals(Type.FLOAT) && type2.equals(Type.FLOAT))
			{
				
				mv.visitInsn(FCMPG);
				mv.visitJumpInsn(IFGT, set_true);
				mv.visitLdcInsn(false);
			}
			
		}
		else if(expressionBinary.op.equals(Kind.OP_LE))
		{
			expressionBinary.leftExpression.visit(this, arg);
			expressionBinary.rightExpression.visit(this, arg);
			
			if(type1.equals(Type.INTEGER) &&type2.equals(Type.INTEGER))
			{
				mv.visitJumpInsn(IF_ICMPLE, set_true);
				mv.visitLdcInsn(false);
			}
			
			else if(type1.equals(Type.FLOAT) && type2.equals(Type.FLOAT))
			{
				
				mv.visitInsn(FCMPG);
				mv.visitJumpInsn(IFLE, set_true);
				mv.visitLdcInsn(false);
			}
			
		}
		
		else if(expressionBinary.op.equals(Kind.OP_GE))
		{
			expressionBinary.leftExpression.visit(this, arg);
			expressionBinary.rightExpression.visit(this, arg);
			
			if(type1.equals(Type.INTEGER) &&type2.equals(Type.INTEGER))
			{
				mv.visitJumpInsn(IF_ICMPGE, set_true);
				mv.visitLdcInsn(false);
			}
			
			else if(type1.equals(Type.FLOAT) && type2.equals(Type.FLOAT))
			{
				
				mv.visitInsn(FCMPG);
				mv.visitJumpInsn(IFGE, set_true);
				mv.visitLdcInsn(false);
			}
			
		}
		else if(expressionBinary.op.equals(Kind.OP_EQ))
		{
			expressionBinary.leftExpression.visit(this, arg);
			expressionBinary.rightExpression.visit(this, arg);
			
			if(type1.equals(Type.INTEGER) &&type2.equals(Type.INTEGER))
			{
				
				mv.visitJumpInsn(IF_ICMPEQ, set_true);
				mv.visitLdcInsn(false);
			}
		
			else if(type1.equals(Type.FLOAT) && type2.equals(Type.FLOAT))
			{
				mv.visitInsn(FCMPG);
				mv.visitJumpInsn(IFEQ, set_true);
				mv.visitLdcInsn(false);
			}
			
			else if(type1.equals(Type.BOOLEAN) && type2.equals(Type.BOOLEAN))
			{
				
				mv.visitJumpInsn(IF_ICMPEQ, set_true);
				mv.visitLdcInsn(false);
			}
		}
		
		else if(expressionBinary.op.equals(Kind.OP_NEQ))
		{
			expressionBinary.leftExpression.visit(this, arg);
			expressionBinary.rightExpression.visit(this, arg);
			
			if(type1.equals(Type.INTEGER) &&type2.equals(Type.INTEGER))
			{
				
				mv.visitJumpInsn(IF_ICMPNE, set_true);
				mv.visitLdcInsn(false);
			}
			
			else if(type1.equals(Type.FLOAT) && type2.equals(Type.FLOAT))
			{
				mv.visitInsn(FCMPG);
				mv.visitJumpInsn(IFNE, set_true);
				mv.visitLdcInsn(false);
			}
			
			else if(type1.equals(Type.BOOLEAN) && type2.equals(Type.BOOLEAN))
			{
			
				mv.visitJumpInsn(IF_ICMPNE, set_true);
				mv.visitLdcInsn(false);
			}
		}
		else if(expressionBinary.op.equals(Kind.OP_AND))
		{
			if(type1.equals(Type.INTEGER))
			{
				expressionBinary.leftExpression.visit(this, arg);
				expressionBinary.rightExpression.visit(this, arg);
				mv.visitInsn(IAND);
			}
			else
			{
				boolean bol1 = ((ExpressionBooleanLiteral) expressionBinary.leftExpression).value;
				boolean bol2 = ((ExpressionBooleanLiteral) expressionBinary.rightExpression).value;
				mv.visitLdcInsn(bol1&bol2);
			}
		}
		else if(expressionBinary.op.equals(Kind.OP_OR))
		{
			if(type1.equals(Type.INTEGER))
			{
				expressionBinary.leftExpression.visit(this, arg);
				expressionBinary.rightExpression.visit(this, arg);
				
				mv.visitInsn(IOR);
			}
			else
			{
				boolean bol1 = ((ExpressionBooleanLiteral) expressionBinary.leftExpression).value;
				boolean bol2 = ((ExpressionBooleanLiteral) expressionBinary.rightExpression).value;
				mv.visitLdcInsn(bol1|bol2);
			}
		}
		
		mv.visitJumpInsn(GOTO, lab);
		mv.visitLabel(set_true);
		mv.visitLdcInsn(true);
		mv.visitLabel(lab);				
		return null;
	}

	@Override
	public Object visitExpressionConditional(
			ExpressionConditional expressionConditional, Object arg)
			throws Exception {
		expressionConditional.guard.visit(this, arg);
		Label falselabel = new Label();
		Label truelabel = new Label();
		Label truend = new Label();
		Label falseend = new Label();
		Label endlabel = new Label();
		mv.visitJumpInsn(IFEQ, falselabel);
		
		mv.visitLabel(truelabel);
		//System.out.println("Reached this true expression!");
		expressionConditional.trueExpression.visit(this, arg);
		//System.out.println("Finished this true expression!");
		mv.visitLabel(truend);
		mv.visitJumpInsn(GOTO, endlabel);	
		mv.visitLabel(falselabel);
		expressionConditional.falseExpression.visit(this, arg);
		mv.visitLabel(falseend);
		mv.visitLabel(endlabel);
		
		return null;
		
	}

	@Override
	public Object visitExpressionFloatLiteral(
			ExpressionFloatLiteral expressionFloatLiteral, Object arg)
			throws Exception {
		mv.visitLdcInsn(expressionFloatLiteral.value);
		return null;
	}

	@Override
	public Object visitExpressionFunctionAppWithExpressionArg(
			ExpressionFunctionAppWithExpressionArg expressionFunctionAppWithExpressionArg,
			Object arg) throws Exception {
		expressionFunctionAppWithExpressionArg.e.visit(this, arg);
		Type type = expressionFunctionAppWithExpressionArg.e.type;
		if(expressionFunctionAppWithExpressionArg.function.equals(Kind.KW_sin))
		{
			mv.visitInsn(F2D);
			mv.visitMethodInsn(INVOKESTATIC, "java/lang/Math", "sin", "(D)D", false);
			mv.visitInsn(D2F);
		}
		else if(expressionFunctionAppWithExpressionArg.function.equals(Kind.KW_cos))
		{
			mv.visitInsn(F2D);
			mv.visitMethodInsn(INVOKESTATIC, "java/lang/Math", "cos", "(D)D", false);
			mv.visitInsn(D2F);
		}
		else if(expressionFunctionAppWithExpressionArg.function.equals(Kind.KW_atan))
		{
			mv.visitInsn(F2D);
			mv.visitMethodInsn(INVOKESTATIC, "java/lang/Math", "atan", "(D)D", false);
			mv.visitInsn(D2F);
		}
		else if(expressionFunctionAppWithExpressionArg.function.equals(Kind.KW_log))
		{
			mv.visitInsn(F2D);
			mv.visitMethodInsn(INVOKESTATIC, "java/lang/Math", "log", "(D)D", false);
			mv.visitInsn(D2F);
		}
		else if(expressionFunctionAppWithExpressionArg.function.equals(Kind.KW_abs))
		{
			if(type.equals(Type.FLOAT))
			{
				mv.visitMethodInsn(INVOKESTATIC, "java/lang/Math", "abs", "(F)F", false);
			}
			else if(type.equals(Type.INTEGER))
			{
				mv.visitMethodInsn(INVOKESTATIC, "java/lang/Math", "abs", "(I)I", false);
			}
		}
		else if(expressionFunctionAppWithExpressionArg.function.equals(Kind.KW_int))
		{
			if(type.equals(Type.FLOAT))
			{
				mv.visitInsn(F2I);
			}
		}
		else if(expressionFunctionAppWithExpressionArg.function.equals(Kind.KW_float))
		{
			if(type.equals(Type.INTEGER))
			{
				mv.visitInsn(I2F);
			}
		}
		else if(expressionFunctionAppWithExpressionArg.function.equals(Kind.KW_alpha))
		{
			mv.visitMethodInsn(INVOKESTATIC, "cop5556sp18/RuntimePixelOps", "getAlpha", RuntimePixelOps.getAlphaSig, false);
		}
		else if(expressionFunctionAppWithExpressionArg.function.equals(Kind.KW_red))
		{
			mv.visitMethodInsn(INVOKESTATIC, "cop5556sp18/RuntimePixelOps", "getRed", RuntimePixelOps.getRedSig, false);
		}
		else if(expressionFunctionAppWithExpressionArg.function.equals(Kind.KW_blue))
		{
			mv.visitMethodInsn(INVOKESTATIC, "cop5556sp18/RuntimePixelOps", "getBlue", RuntimePixelOps.getBlueSig, false);
		}
		else if(expressionFunctionAppWithExpressionArg.function.equals(Kind.KW_green))
		{
			mv.visitMethodInsn(INVOKESTATIC, "cop5556sp18/RuntimePixelOps", "getGreen", RuntimePixelOps.getRedSig, false);
		}
		else if(expressionFunctionAppWithExpressionArg.function.equals(Kind.KW_width))
		{
			mv.visitMethodInsn(INVOKESTATIC, "cop5556sp18/RuntimeImageSupport", "getWidth", RuntimeImageSupport.getWidthSig, false);
		}
		else if(expressionFunctionAppWithExpressionArg.function.equals(Kind.KW_height))
		{
			mv.visitMethodInsn(INVOKESTATIC, "cop5556sp18/RuntimeImageSupport", "getHeight", RuntimeImageSupport.getHeightSig, false);
		}
		
		
		return null;
	}

	@Override
	public Object visitExpressionFunctionAppWithPixel(
			ExpressionFunctionAppWithPixel expressionFunctionAppWithPixel,
			Object arg) throws Exception {
		Kind kind = expressionFunctionAppWithPixel.name;
		if(kind.equals(Kind.KW_cart_x)|| kind.equals(Kind.KW_cart_y))
		{
			expressionFunctionAppWithPixel.e0.visit(this, arg);	
			mv.visitInsn(F2D);	
			expressionFunctionAppWithPixel.e1.visit(this, arg);
			mv.visitInsn(F2D);	
			
			if(kind.equals(Kind.KW_cart_x))
			{
				mv.visitMethodInsn(INVOKESTATIC, "java/lang/Math", "cos", "(D)D", false);
				mv.visitInsn(DMUL);
				mv.visitInsn(D2I);
			}
			else if(kind.equals(Kind.KW_cart_y))
			{
				mv.visitMethodInsn(INVOKESTATIC, "java/lang/Math", "sin", "(D)D", false);
				mv.visitInsn(DMUL);
				mv.visitInsn(D2I);
			}
		}
		else if(kind.equals(Kind.KW_polar_a)|| kind.equals(Kind.KW_polar_r))
		{
			
			
			if(kind.equals(Kind.KW_polar_a))
			{
				expressionFunctionAppWithPixel.e1.visit(this, arg);	
				mv.visitInsn(I2D);	
				expressionFunctionAppWithPixel.e0.visit(this, arg);
				mv.visitInsn(I2D);
				mv.visitMethodInsn(INVOKESTATIC, "java/lang/Math", "atan2", "(DD)D", false);
				mv.visitInsn(D2F);
			}
			else if(kind.equals(Kind.KW_polar_r))
			{
				expressionFunctionAppWithPixel.e0.visit(this, arg);	
				mv.visitInsn(I2D);	
				expressionFunctionAppWithPixel.e1.visit(this, arg);
				mv.visitInsn(I2D);
				mv.visitMethodInsn(INVOKESTATIC, "java/lang/Math", "hypot", "(DD)D", false);
				mv.visitInsn(D2F);
			}
		
		}
		
		
		
		return null;
	}

	@Override
	public Object visitExpressionIdent(ExpressionIdent expressionIdent,
			Object arg) throws Exception {
		//////System.out.println("This is classname" + className);
		//System.out.println("Reached expression Ident"+expressionIdent.dec);
		
		//mv.visitLdcInsn(expressionIdent.name);
		if(expressionIdent.type.equals(Types.Type.INTEGER))
		{
			////System.out.println("reached this"+ expressionIdent.name);
			mv.visitVarInsn(ILOAD,expressionIdent.dec.getSlotNum());
		}
		else if(expressionIdent.type.equals(Types.Type.BOOLEAN))
		{
			mv.visitVarInsn(ILOAD,expressionIdent.dec.getSlotNum());
		}
		else if(expressionIdent.type.equals(Types.Type.FLOAT))
		{
			mv.visitVarInsn(FLOAD,expressionIdent.dec.getSlotNum());
		}
		else if(expressionIdent.type.equals(Types.Type.FILE))
		{
			mv.visitVarInsn(ALOAD, expressionIdent.dec.getSlotNum());
		}
		
		else if(expressionIdent.type.equals(Types.Type.IMAGE))
		{

			mv.visitVarInsn(ALOAD, expressionIdent.dec.getSlotNum());			
			
			
			
			
		}
		
		
		
		return null;
	}

	@Override
	public Object visitExpressionIntegerLiteral(
			ExpressionIntegerLiteral expressionIntegerLiteral, Object arg)
			throws Exception {
		// This one is all done!
		mv.visitLdcInsn(expressionIntegerLiteral.value);
		return null;
	}

	@Override
	public Object visitExpressionPixel(ExpressionPixel expressionPixel,
			Object arg) throws Exception {
		////System.out.println("Entered Expression pixel");
		mv.visitVarInsn(ALOAD, expressionPixel.dec.getSlotNum());
		expressionPixel.pixelSelector.visit(this, arg);
		mv.visitMethodInsn(INVOKESTATIC, "cop5556sp18/RuntimeImageSupport", "getPixel", RuntimeImageSupport.getPixelSig, false);
		return null;
	}

	@Override
	public Object visitExpressionPixelConstructor(
			ExpressionPixelConstructor expressionPixelConstructor, Object arg)
			throws Exception {
		expressionPixelConstructor.alpha.visit(this, arg);
		expressionPixelConstructor.red.visit(this, arg);
		expressionPixelConstructor.green.visit(this, arg);
		expressionPixelConstructor.blue.visit(this, arg);
		mv.visitMethodInsn(INVOKESTATIC, "cop5556sp18/RuntimePixelOps", "makePixel", RuntimePixelOps.makePixelSig, false);
		return null;
	}

	@Override
	public Object visitExpressionPredefinedName(
			ExpressionPredefinedName expressionPredefinedName, Object arg)
			throws Exception {
		if (expressionPredefinedName.name.equals(Kind.KW_Z))
		{
			mv.visitLdcInsn(255);
		}
		else if (expressionPredefinedName.name.equals(Kind.KW_default_width))
		{
			mv.visitLdcInsn(defaultWidth);
		}
		else if (expressionPredefinedName.name.equals(Kind.KW_default_height))
		{
			mv.visitLdcInsn(defaultHeight);
		}
		return null;
	}

	@Override
	public Object visitExpressionUnary(ExpressionUnary expressionUnary,
			Object arg) throws Exception {
		Type type1 = (expressionUnary.expression).type;
		(expressionUnary.expression).visit(this, arg);
		Label set_true = new Label();
		Label lab = new Label();
		if(expressionUnary.op.equals(Kind.OP_PLUS))
		{
			if(type1.equals(Type.INTEGER))
			{
				
				
					
				
			}
			else if(type1.equals(Type.FLOAT))
			{
				
					
				
			}
		}
		else if(expressionUnary.op.equals(Kind.OP_MINUS))
		{
			if(type1.equals(Type.INTEGER))
			{
				//System.out.println("Now reached negative binary expression");
				
					mv.visitInsn(INEG);
				
			}
			else if(type1.equals(Type.FLOAT))
			{
				
					mv.visitInsn(FNEG);
				
			}
		}
		else if(expressionUnary.op.equals(Kind.OP_EXCLAMATION))
		{
			if(type1.equals(Type.INTEGER))
			{
				
				/*
				mv.visitInsn(POP);
				Expression exp = expressionUnary.expression;
				
				int i = ((ExpressionIntegerLiteral) exp).value;
				i = ~i;
				mv.visitLdcInsn(i);
				*/
				mv.visitInsn(INEG);
				mv.visitLdcInsn(-1);
				mv.visitInsn(IADD);
			
			}
			else if(type1.equals(Type.BOOLEAN))
			{
				
				/*mv.visitInsn(POP);
				boolean b = ((ExpressionBooleanLiteral) ((expressionUnary.expression))).value;
				if(b)
				{
					b = false;
				}
				else
				{
					b = true;
				}
				
				mv.visitLdcInsn(b);
				*/
				mv.visitLdcInsn(true);
				mv.visitInsn(IXOR);
			}
		}
		mv.visitJumpInsn(GOTO, lab);
		mv.visitLabel(set_true);
		mv.visitLdcInsn(true);
		mv.visitLabel(lab);
		return null;
	}

	@Override
	public Object visitLHSIdent(LHSIdent lhsIdent, Object arg)
			throws Exception {
		
		if(lhsIdent.type.equals(Type.INTEGER) || lhsIdent.type.equals(Type.BOOLEAN))
		{
			mv.visitVarInsn(ISTORE, lhsIdent.dec.getSlotNum());
		}
		else if(lhsIdent.type.equals(Type.FLOAT))
		{
			mv.visitVarInsn(FSTORE, lhsIdent.dec.getSlotNum());
		}
		else if(lhsIdent.type.equals(Type.FILE))
		{
			mv.visitVarInsn(ASTORE, lhsIdent.dec.getSlotNum());
			//mv.visitFieldInsn(PUTFIELD, className, lhsIdent.dec.name, "Ljava/lang/String");
		}
		else if(lhsIdent.type.equals(Type.IMAGE))
		{
			
			mv.visitMethodInsn(INVOKESTATIC, "cop5556sp18/RuntimeImageSupport", "deepCopy", RuntimeImageSupport.deepCopySig, false);
			
			mv.visitVarInsn(ASTORE, lhsIdent.dec.getSlotNum());
		}
		return null;
	}

	@Override
	public Object visitLHSPixel(LHSPixel lhsPixel, Object arg)
			throws Exception {
		
		//mv.visitVarInsn(ISTORE, lhsPixel.dec.getSlotNum());
		////System.out.println("Entered lhsPixel");
		mv.visitVarInsn(ALOAD, lhsPixel.dec.getSlotNum());
		lhsPixel.pixelSelector.visit(this, arg);
		mv.visitMethodInsn(INVOKESTATIC, "cop5556sp18/RuntimeImageSupport", "setPixel", RuntimeImageSupport.setPixelSig, false);
		//mv.visitVarInsn(opcode, var);
		return null;
	}

	@Override
	public Object visitLHSSample(LHSSample lhsSample, Object arg)
			throws Exception {
		//mv.visitVarInsn(ISTORE, lhsSample.dec.getSlotNum());
		mv.visitVarInsn(ALOAD, lhsSample.dec.getSlotNum());
		lhsSample.pixelSelector.visit(this, arg);
		if(lhsSample.color.equals(Kind.KW_alpha))
		{
			mv.visitLdcInsn(0);
		}
		else if(lhsSample.color.equals(Kind.KW_red))
		{
			mv.visitLdcInsn(1);
		}
		else if(lhsSample.color.equals(Kind.KW_green))
		{
			mv.visitLdcInsn(2);
		}
		else if(lhsSample.color.equals(Kind.KW_blue))
		{
			mv.visitLdcInsn(3);
		}
		
		mv.visitMethodInsn(INVOKESTATIC, "cop5556sp18/RuntimeImageSupport", "updatePixelColor", RuntimeImageSupport.updatePixelColorSig, false);
		return null;
	}

	@Override
	public Object visitPixelSelector(PixelSelector pixelSelector, Object arg)
			throws Exception {
		pixelSelector.ex.visit(this, arg);
		pixelSelector.ey.visit(this, arg);
		return null;
	}

	@Override
	public Object visitProgram(Program program, Object arg) throws Exception {
		// TODO refactor and extend as necessary
		cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
		// cw = new ClassWriter(0); //If the call to mv.visitMaxs(1, 1) crashes,
		// it is
		// sometime helpful to
		// temporarily run it without COMPUTE_FRAMES. You probably
		// won't get a completely correct classfile, but
		// you will be able to see the code that was
		// generated.
		className = program.progName;
		classDesc = "L" + className + ";";
		String sourceFileName = (String) arg;
		cw.visit(52, ACC_PUBLIC + ACC_SUPER, className, null,
				"java/lang/Object", null);
		cw.visitSource(sourceFileName, null);

		// create main method
		mv = cw.visitMethod(ACC_PUBLIC + ACC_STATIC, "main",
				"([Ljava/lang/String;)V", null, null);
		// initialize
		mv.visitCode();

		// add label before first instruction
		Label mainStart = new Label();
		mv.visitLabel(mainStart);

		CodeGenUtils.genLog(DEVEL, mv, "entering main");

		program.block.visit(this, arg);

		// generates code to add string to log
		CodeGenUtils.genLog(DEVEL, mv, "leaving main");

		// adds the required (by the JVM) return statement to main
		mv.visitInsn(RETURN);

		// adds label at end of code
		Label mainEnd = new Label();
		mv.visitLabel(mainEnd);
		mv.visitLocalVariable("args", "[Ljava/lang/String;", null, mainStart,
				mainEnd, 0);
		// Because we use ClassWriter.COMPUTE_FRAMES as a parameter in the
		// constructor,
		// asm will calculate this itself and the parameters are ignored.
		// If you have trouble with failures in this routine, it may be useful
		// to temporarily change the parameter in the ClassWriter constructor
		// from COMPUTE_FRAMES to 0.
		// The generated classfile will not be correct, but you will at least be
		// able to see what is in it.
		mv.visitMaxs(0,0);

		// terminate construction of main method
		mv.visitEnd();

		// terminate class construction
		cw.visitEnd();

		// generate classfile as byte array and return
		return cw.toByteArray();
	}

	@Override
	public Object visitStatementAssign(StatementAssign statementAssign,
			Object arg) throws Exception {
		
		statementAssign.e.visit(this, arg);
		statementAssign.lhs.visit(this, arg);
		return null;
	}

	@Override
	public Object visitStatementIf(StatementIf statementIf, Object arg)
			throws Exception {
		
		Label endIf = new Label();
		statementIf.guard.visit(this, arg); // Expression
		mv.visitJumpInsn(IFEQ, endIf); 
		Label startIf = new Label();
		mv.visitLabel(startIf);
		statementIf.b.visit(this, arg); 		
		mv.visitLabel(endIf);
		return null;
	}

	@Override
	public Object visitStatementInput(StatementInput statementInput, Object arg)
			throws Exception {
	
		mv.visitInsn(NULL);
		
		statementInput.e.visit(this, arg);
		int index = ((ExpressionIntegerLiteral) statementInput.e).value;
		
		
		Type t = Types.getType(statementInput.getDec().getType());
		mv.visitVarInsn(ALOAD, 0);
		mv.visitLdcInsn(index);
		
		mv.visitInsn(AALOAD);
	
		if(t.equals(Type.INTEGER))
		{
			////System.out.println("Reached statement Input");
			
			mv.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "parseInt", "(Ljava/lang/String;)I", false);	
			mv.visitVarInsn(ISTORE,statementInput.dec.getSlotNum());
		}
		else if(t.equals(Type.BOOLEAN))
		{
			
			mv.visitMethodInsn(INVOKESTATIC, "java/lang/Boolean", "parseBoolean", "(Ljava/lang/String;)Z", false);	
			mv.visitVarInsn(ISTORE,statementInput.dec.getSlotNum());
		}
		else if(t.equals(Type.FLOAT))
		{
			
			mv.visitMethodInsn(INVOKESTATIC, "java/lang/Float", "parseFloat", "(Ljava/lang/String;)F", false);
			mv.visitVarInsn(FSTORE,statementInput.dec.getSlotNum());
		}
		else if(t.equals(Type.FILE))
		{
			mv.visitVarInsn(ASTORE,statementInput.dec.getSlotNum());
			//mv.visitFieldInsn(PUTFIELD, className, statementInput.dec.name, "Ljava/lang/String");
		}
		else if(t.equals(Type.IMAGE))
		{
			if(statementInput.getDec().height!=null && statementInput.getDec().width!=null)
			{
				
				statementInput.getDec().width.visit(this, arg);
				mv.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;", false);
				statementInput.getDec().height.visit(this, arg);
				mv.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;", false);
				
			}
			else
			{
				
				mv.visitInsn(ACONST_NULL);
				mv.visitInsn(ACONST_NULL);
				
			}	
			mv.visitMethodInsn(INVOKESTATIC, "cop5556sp18/RuntimeImageSupport", "readImage", RuntimeImageSupport.readImageSig, false);
			//System.out.println("statemenInput's" + statementInput.getDec().getSlotNum());
			mv.visitVarInsn(ASTORE, statementInput.getDec().getSlotNum());
			statementInput.dec.sIP=true;
			
			////System.out.println("Finished this statementInput of Image");
		}
		
	
		

		return null;
	}

	@Override
	public Object visitStatementShow(StatementShow statementShow, Object arg)
			throws Exception {
		/**
		 * TODO refactor and complete implementation.
		 * 
		 * For integers, booleans, and floats, generate code to print to
		 * console. For images, generate code to display in a frame.
		 * 
		 * In all cases, invoke CodeGenUtils.genLogTOS(GRADE, mv, type); before
		 * consuming top of stack.
		 */
		statementShow.e.visit(this, arg);
		Type type = statementShow.e.getType();
		switch (type) {
			case INTEGER : {
				CodeGenUtils.genLogTOS(GRADE, mv, type);
				mv.visitFieldInsn(Opcodes.GETSTATIC, "java/lang/System", "out",
						"Ljava/io/PrintStream;");
				mv.visitInsn(Opcodes.SWAP);
				mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/io/PrintStream",
						"println", "(I)V", false);
			}
				break;
			case BOOLEAN : {
				CodeGenUtils.genLogTOS(GRADE, mv, type);
				mv.visitFieldInsn(Opcodes.GETSTATIC, "java/lang/System", "out",
						"Ljava/io/PrintStream;");
				mv.visitInsn(Opcodes.SWAP);
				mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/io/PrintStream",
						"println", "(Z)V", false);
			}
				break;
			case FLOAT : {
				CodeGenUtils.genLogTOS(GRADE, mv, type);
				mv.visitFieldInsn(Opcodes.GETSTATIC, "java/lang/System", "out",
						"Ljava/io/PrintStream;");
				mv.visitInsn(Opcodes.SWAP);
				mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/io/PrintStream",
						"println", "(F)V", false);
			}
				break;
			case IMAGE : {
				
				CodeGenUtils.genLogTOS(GRADE, mv, type);
				//mv.visitInsn(Opcodes.SWAP);
				mv.visitMethodInsn(INVOKESTATIC, "cop5556sp18/RuntimeImageSupport", "makeFrame", RuntimeImageSupport.makeFrameSig, false);
			}
			break;
			case NONE : {
				 
			}
			break;
			case FILE: {
				CodeGenUtils.genLogTOS(GRADE, mv, type);
				mv.visitFieldInsn(Opcodes.GETSTATIC, "java/lang/System", "out",
						"Ljava/io/PrintStream;");
				mv.visitInsn(Opcodes.SWAP);
				mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/io/PrintStream",
						"println", "(Ljava/lang/String)V", false);
			}
			break;
		}
		return null;
	}

	@Override
	public Object visitStatementSleep(StatementSleep statementSleep, Object arg)
			throws Exception {
		statementSleep.duration.visit(this, arg);
		mv.visitInsn(I2L);
		mv.visitMethodInsn(INVOKESTATIC, "java/lang/Thread", "sleep", "(J)V", false);
		return null;
	}

	@Override
	public Object visitStatementWhile(StatementWhile statementWhile, Object arg)
			throws Exception {
		Label whileExp = new Label();
		Label whileBlock = new Label();
		mv.visitJumpInsn(GOTO, whileExp); 
		mv.visitLabel(whileBlock);
		statementWhile.b.visit(this, arg);
		Label endWhileBlock = new Label();
		mv.visitLabel(endWhileBlock);
		mv.visitLabel(whileExp);
		statementWhile.guard.visit(this, arg); 
		Label endWhileExp = new Label();
		mv.visitLabel(endWhileExp);
		mv.visitJumpInsn(IFNE, whileBlock); 
		return null;
		
	}

	@Override
	public Object visitStatementWrite(StatementWrite statementWrite, Object arg)
			throws Exception {
	
		mv.visitVarInsn(ALOAD, statementWrite.sourceDec.getSlotNum());
		mv.visitVarInsn(ALOAD, statementWrite.destDec.getSlotNum());
		
		mv.visitMethodInsn(INVOKESTATIC, "cop5556sp18/RuntimeImageSupport", "write", RuntimeImageSupport.writeSig, false);
		
		
		
		return null;
	}

}
