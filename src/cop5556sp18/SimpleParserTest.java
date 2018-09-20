 /**
 * JUunit tests for the Parser for the class project in COP5556 Programming Language Principles 
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

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import cop5556sp18.Scanner.LexicalException;
import cop5556sp18.SimpleParser.SyntaxException;

public class SimpleParserTest {

	//set Junit to be able to catch exceptions
	@Rule
	public ExpectedException thrown = ExpectedException.none();

	
	//To make it easy to print objects and turn this output on and off
	static final boolean doPrint = true;
	private void show(Object input) {
		if (doPrint) {
			System.out.println(input.toString());
		}
	}


	//creates and returns a parser for the given input.
	private SimpleParser makeParser(String input) throws LexicalException {
		show(input);        //Display the input 
		Scanner scanner = new Scanner(input).scan();  //Create a Scanner and initialize it
		show(scanner);   //Display the Scanner
		SimpleParser parser = new SimpleParser(scanner);
		return parser;
	}
	
	

	/**
	 * Simple test case with an empty program.  This throws an exception 
	 * because it lacks an identifier and a block. The test case passes because
	 * it expects an exception
	 *  
	 * @throws LexicalException
	 * @throws SyntaxException 
	 */
	@Test
	public void testEmpty() throws LexicalException, SyntaxException {
		String input = "";  //The input is the empty string.  
		SimpleParser parser = makeParser(input);
		thrown.expect(SyntaxException.class);
		parser.parse();
	}
	
	/**
	 * Smallest legal program.
	 *   
	 * @throws LexicalException
	 * @throws SyntaxException 
	 */
	@Test
	public void testSmallest() throws LexicalException, SyntaxException {
		String input = "b{}";  
		SimpleParser parser = makeParser(input);
		parser.parse();
	}	
	
	
	//This test should pass in your complete parser.  It will fail in the starter code.
	//Of course, you would want a better error message. 
	@Test
	public void testDec0() throws LexicalException, SyntaxException {
		String input = "b{int c;}";
		SimpleParser parser = makeParser(input);
		parser.parse();
	}
	
	//To test image
	@Test
	public void testCase1() throws LexicalException, SyntaxException {
		String input = "b{int c; int d; image test[4,4];}";
		SimpleParser parser = makeParser(input);
		parser.parse();
	}
	
	//to test STatements
	@Test
	public void testCase2() throws LexicalException, SyntaxException {
		String input = "testing{input test2 from @ three; write four to five; six := seven; six:= seven [4,4]; six:= red(seven[4,4]);  }";
		SimpleParser parser = makeParser(input);
		parser.parse();
	}
	
	@Test
	public void testCase3() throws LexicalException, SyntaxException {
		String input = "b{while(!x) { }; while(x<width(im)) {int y;y:=0;};}";
		SimpleParser parser = makeParser(input);
		parser.parse();
	}
	@Test
	public void testCase4() throws LexicalException, SyntaxException {
		String input = "demo1{image h;input h from @0;show h; sleep(4000);image g[width(h),height(h)];int x;x:=0;while(x<width(g)){int y;y:=0;while(y<height(g)){g[x,y]:=h[y,x];y:=y+1;};x:=x+1;};show g;sleep(4000);}";
		SimpleParser parser = makeParser(input);
		parser.parse();
	}
	@Test
	public void testCase5() throws LexicalException, SyntaxException {
		String input = "makeRedImage{image im[256,256];int x;int y;x:=0;y:=0;while(x<width(im)) {y:=0;while(y<height(im)) {im[x,y]:=<<255,255,0,0>>;y:=y+1;};x:=x+1;};show im;}";
		SimpleParser parser = makeParser(input);
		parser.parse();
	}
	@Test
	public void testCase6() throws LexicalException, SyntaxException {
		String input = "PolarR2{image im[1024,1024];int x;x:=0;while(x<width(im)) {int y;y:=0;while(y<height(im)) {float p;p:=polar_r[x,y];int r;r:=int(p)%Z;im[x,y]:=<<Z,0,0,r>>;y:=y+1;};x:=x+1;};show im;}";
		SimpleParser parser = makeParser(input);
		parser.parse();
	}
	
	@Test
	public void testCase7() throws LexicalException, SyntaxException {
		String input = "samples{image bird; input bird from @0;show bird;sleep(4000);image bird2[width(bird),height(bird)];int x;x:=0;while(x<width(bird2)) {int y;y:=0;while(y<height(bird2)) {blue(bird2[x,y]):=red(bird[x,y]);green(bird2[x,y]):=blue(bird[x,y]);red(bird2[x,y]):=green(bird[x,y]);alpha(bird2[x,y]):=Z;y:=y+1;};x:=x+1;};show bird2;sleep(4000);}";
		SimpleParser parser = makeParser(input);
		parser.parse();
	}
	@Test
	public void testCase8() throws LexicalException, SyntaxException {
		String input = "b{if(x == ((2+3) * (5/2))) {};}";
		SimpleParser parser = makeParser(input);
		parser.parse();
	}
	@Test
	public void testCase9() throws LexicalException, SyntaxException {
		String input = "b{if((2+3) * (5/2)) {};}";
		SimpleParser parser = makeParser(input);
		parser.parse();
	}
	@Test
	public void testCase10() throws LexicalException, SyntaxException {
		String input = "b{if((2+3) * (5/2)) {};}";
		SimpleParser parser = makeParser(input);
		parser.parse();
	}
	
	@Test
	public void testCase11() throws LexicalException, SyntaxException {
		String input = "b{int c; float b;boolean c;image d; filename e;image f [true,4]; }";
		SimpleParser parser = makeParser(input);
		parser.parse();
	}
	
	@Test
	public void testCase12() throws LexicalException, SyntaxException {
		String input = "b{input IDENTIFIER from @ 1;write IDENTIFIER to IDENTIFIER;while (abc>def){}; if (false){};show atan(1);sleep Z;}";
		SimpleParser parser = makeParser(input);
		parser.parse();
	}
	@Test
	public void testCase13() throws LexicalException, SyntaxException {
		String input ="b{show +true;red ( IDENTIFIER  [ 3+5+2-1-5 , 9**9/3%2 ] ):= 45!= 9;sleep true  ?  123.56  :  1;while(default_height){}; }";
		SimpleParser parser = makeParser(input);
		parser.parse();
	}
	
	
	
	
	

}
	

