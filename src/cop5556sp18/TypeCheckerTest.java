package cop5556sp18;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import cop5556sp18.TypeChecker.SemanticException;
import cop5556sp18.AST.ASTVisitor;
import cop5556sp18.AST.Program;

public class TypeCheckerTest {

	/*
	 * set Junit to be able to catch exceptions
	 */
	@Rule
	public ExpectedException thrown = ExpectedException.none();

	/**
	 * Prints objects in a way that is easy to turn on and off
	 */
	static final boolean doPrint = true;

	private void show(Object input) {
		if (doPrint) {
			System.out.println(input.toString());
		}
	}

	/**
	 * Scans, parses, and type checks the input string
	 * 
	 * @param input
	 * @throws Exception
	 */
	void typeCheck(String input) throws Exception {
		show(input);
		// instantiate a Scanner and scan input
		Scanner scanner = new Scanner(input).scan();
		show(scanner);
		// instantiate a Parser and parse input to obtain and AST
		Program ast = new Parser(scanner).parse();
		show(ast);
		// instantiate a TypeChecker and visit the ast to perform type checking and
		// decorate the AST.
		ASTVisitor v = new TypeChecker();
		ast.visit(v, null);
	}



	/**
	 * Simple test case with an almost empty program.
	 * 
	 * @throws Exception
	 */
	@Test
	public void emptyProg() throws Exception {
		String input = "emptyProg{}";
		typeCheck(input);
	}

	@Test
	public void expression1() throws Exception {
		String input = "prog {show 3+4;}";
		typeCheck(input);
	}

	@Test
	public void expression2_fail() throws Exception {
		String input = "prog { show true+4; }"; //error, incompatible types in binary expression
		thrown.expect(SemanticException.class);
		try {
			typeCheck(input);
		} catch (SemanticException e) {
			show(e);
			throw e;
		}
	}
	@Test
	public void simpleImage() throws Exception {
		String input = "X{ image im[1,2]; }";
		typeCheck(input);
	}
	@Test
	public void simpleImageFail() throws Exception {
		String input = "X{ image im[1.0, 2]; }";
		thrown.expect(SemanticException.class);
		try {
			typeCheck(input);
		} catch (SemanticException e) {
			show(e);
			throw e;
		}
		
	}
	@Test
	public void nestedDec1() throws Exception {
		String input = "X{int x; int y; while (x == y) {int x;};}";
		
			typeCheck(input);
		
	}
	
	@Test
	public void nestedDec2() throws Exception {
		String input = "X{ int x; int z; while (x == y) {int x;}; }";
		thrown.expect(SemanticException.class);
		try {
			typeCheck(input);
		} catch (SemanticException e) {
			show(e);
			throw e;
		}
	}
	
	@Test
	public void nestedDec3() throws Exception {
		String input = "X{ int x; int y; while (x == y) { show x;}; }";
		typeCheck(input);
}
	
	@Test
	public void nestedDec4() throws Exception {
		String input = "prog{int var; int var2;input var from @1; input var from @var2; input var from @<<1,2,3,4>>;}";
		//thrown.expect(SemanticException.class);
		try { typeCheck(input); } catch (SemanticException e) { show(e); throw e; }
	}
	
	@Test
	public void nestedDec5() throws Exception {
		String input = "prog {int var1; var1 := abs(1); var1 := red(1); var1 := green(1); var1 := blue(1); var1 := int(1); var1 := alpha(1);}";
		//thrown.expect(SemanticException.class);
		try { typeCheck(input); } catch (SemanticException e) { show(e); throw e; }
	}
	
	@Test
	public void nestedDec6() throws Exception {
		String input = "prog {float var2;var2 := abs(1.0); var2 := sin(1.0); var2 := cos(1.0); var2 := atan(1.0); var2 := float(1.0); var2 := log(1.0);}";
		//thrown.expect(SemanticException.class);
		try { typeCheck(input); } catch (SemanticException e) { show(e); throw e; }
	}
	@Test
	public void nestedDec7() throws Exception {
		String input = "prog {int var1; float var2; image var3;var1 := width(var3); var1 := height(var3); var2 := float(1); var1 := int(1.0);}";
		//thrown.expect(SemanticException.class);
		try { typeCheck(input); } catch (SemanticException e) { show(e); throw e; }
	}
	@Test
	public void nestedDec8() throws Exception {
		String input = "prog{int var1[500, 1];}";
		//thrown.expect(SyntaxException.class)
		thrown.expect(Exception.class);;
		try { typeCheck(input); } catch (SemanticException e) { show(e); throw e; }
	}
	@Test
	public void nestedDec9() throws Exception {
		String input = "prog{ int var1; boolean var2;var1 := 1 + 2; var1 := 1 - 2; var1 := 1 * 2; var1 := 1 / 2; var1 := 1 ** 2; var1 := 1 % 2; var1 := 1 & 2; var1 := 1 | 2; var2 := 1 == 2; var2 := 1 != 2; var2 := 1 > 2; var2 := 1 >= 2; var2 := 1 < 2; var2 := 1 <= 2;}";
		//thrown.expect(SemanticException.class);
		try { typeCheck(input); } catch (SemanticException e) { show(e); throw e; }
	}
	@Test
	public void nestedDec10() throws Exception {
		String input = "prog{boolean var1;var1 := true & false; var1 := true | false; var1 := true == false; var1 := true != false; var1 := true > false; var1 := true >= false; var1 := true < false; var1 := true <= false;}";
		//thrown.expect(SemanticException.class);
		try { typeCheck(input); } catch (SemanticException e) { show(e); throw e; }
	}
	@Test
	public void nestedDec11() throws Exception {
		String input = "prog{ int a; float b; boolean c;a := +a; a := -a; a := !a; b := +b; b := -b; b := !b; c := +c; c := -c; c := !c;}";
		//thrown.expect(SyntaxException.class)
		//thrown.expect(Exception.class);;
		try { typeCheck(input); } catch (SemanticException e) { show(e); throw e; }
	}
	@Test
	public void nestedDec12() throws Exception {
		String input = "prog{boolean cond;int var1; var1 := cond ? var1+ 1 : var1;float var2; var2 := cond ? var2 + 1.0 : var2;}";
		//thrown.expect(SyntaxException.class)
		//thrown.expect(Exception.class);;
		try { typeCheck(input); } catch (SemanticException e) { show(e); throw e; }
	}
	@Test
	public void nestedDec13() throws Exception {
		String input = "prog{image var; var[0,0] := 1; alpha(var[0,0]) := 1; red(var[0,0]) := 1; green(var[0,0]) := 1; blue(var[0,0]) := 1;}";
		//thrown.expect(SyntaxException.class)
		//thrown.expect(SemanticException.class);;
		try { typeCheck(input); } catch (SemanticException e) { show(e); throw e; }
	}
	@Test
	public void nestedDec14() throws Exception {
		String input = "prog {int var1; float var2; image var3;var1 := width(var3); var1 := height(var3); var2 := float(1); var1 := int(1.0);}";
		//thrown.expect(SyntaxException.class);
		//thrown.expect(SemanticException.class);
		try { typeCheck(input); } catch (SemanticException e) { show(e); throw e; }
	}
	
	
	




}
