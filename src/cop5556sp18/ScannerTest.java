 /**
 * JUunit tests for the Scanner for the class project in COP5556 Programming Language Principles 
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

import static cop5556sp18.Scanner.Kind.LPAREN;
import static cop5556sp18.Scanner.Kind.RPAREN;
import static cop5556sp18.Scanner.Kind.SEMI;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import cop5556sp18.Scanner.LexicalException;
import cop5556sp18.Scanner.Token;

public class ScannerTest {

	//set Junit to be able to catch exceptions
	@Rule
	public ExpectedException thrown = ExpectedException.none();

	
	//To make it easy to print objects and turn this output on and off
	static boolean doPrint = true;
	private void show(Object input) {
		if (doPrint) {
			System.out.println(input.toString());
		}
	}

	/**
	 *Retrieves the next token and checks that it is an EOF token. 
	 *Also checks that this was the last token.
	 *
	 * @param scanner
	 * @return the Token that was retrieved
	 */
	
	Token checkNextIsEOF(Scanner scanner) {
		Scanner.Token token = scanner.nextToken();
		assertEquals(Scanner.Kind.EOF, token.kind);
		assertFalse(scanner.hasTokens());
		return token;
	}


	/**
	 * Retrieves the next token and checks that its kind, position, length, line, and position in line
	 * match the given parameters.
	 * 
	 * @param scanner
	 * @param kind
	 * @param pos
	 * @param length
	 * @param line
	 * @param pos_in_line
	 * @return  the Token that was retrieved
	 */
	Token checkNext(Scanner scanner, Scanner.Kind kind, int pos, int length, int line, int pos_in_line) {
		Token t = scanner.nextToken();
		assertEquals(kind, t.kind);
		assertEquals(pos, t.pos);
		assertEquals(length, t.length);
		assertEquals(line, t.line());
		assertEquals(pos_in_line, t.posInLine());
		return t;
	}

	/**
	 * Retrieves the next token and checks that its kind and length match the given
	 * parameters.  The position, line, and position in line are ignored.
	 * 
	 * @param scanner
	 * @param kind
	 * @param length
	 * @return  the Token that was retrieved
	 */
	Token checkNext(Scanner scanner, Scanner.Kind kind, int length) {
		Token t = scanner.nextToken();
		assertEquals(kind, t.kind);
		assertEquals(length, t.length);
		return t;
	}
	


	/**
	 * Simple test case with an empty program.  The only Token will be the EOF Token.
	 *   
	 * @throws LexicalException
	 */
	@Test
	public void testEmpty() throws LexicalException {
		String input = "";  //The input is the empty string.  This is legal
		show(input);        //Display the input 
		Scanner scanner = new Scanner(input).scan();  //Create a Scanner and initialize it
		show(scanner);   //Display the Scanner
		checkNextIsEOF(scanner);  //Check that the only token is the EOF token.
	}
	
	/**
	 * Test illustrating how to put a new line in the input program and how to
	 * check content of tokens.
	 * 
	 * Because we are using a Java String literal for input, we use \n for the
	 * end of line character. (We should also be able to handle \n, \r, and \r\n
	 * properly.)
	 * 
	 * Note that if we were reading the input from a file, the end of line 
	 * character would be inserted by the text editor.
	 * Showing the input will let you check your input is 
	 * what you think it is.
	 * 
	 * @throws LexicalException
	 */
	@Test
	public void testSemi() throws LexicalException {
		String input = ";;\n;;";
		Scanner scanner = new Scanner(input).scan();
		show(input);
		show(scanner);
		checkNext(scanner, SEMI, 0, 1, 1, 1);
		checkNext(scanner, SEMI, 1, 1, 1, 2);
		checkNext(scanner, SEMI, 3, 1, 2, 1);
		checkNext(scanner, SEMI, 4, 1, 2, 2);
		checkNextIsEOF(scanner);
	}
	

	
	/**
	 * This example shows how to test that your scanner is behaving when the
	 * input is illegal.  In this case, we are giving it an illegal character '~' in position 2
	 * 
	 * The example shows catching the exception that is thrown by the scanner,
	 * looking at it, and checking its contents before rethrowing it.  If caught
	 * but not rethrown, then JUnit won't get the exception and the test will fail.  
	 * 
	 * The test will work without putting the try-catch block around 
	 * new Scanner(input).scan(); but then you won't be able to check 
	 * or display the thrown exception.
	 * 
	 * @throws LexicalException
	 */
	@Test
	public void failIllegalChar() throws LexicalException {
		String input = ";;~";
		show(input);
		thrown.expect(LexicalException.class);  //Tell JUnit to expect a LexicalException
		try {
			new Scanner(input).scan();
		} catch (LexicalException e) {  //Catch the exception
			show(e);                    //Display it
			assertEquals(2,e.getPos()); //Check that it occurred in the expected position
			throw e;                    //Rethrow exception so JUnit will see it
		}
	}




	@Test
	public void testParens() throws LexicalException {
		String input = "()";
		Scanner scanner = new Scanner(input).scan();
		show(input);
		show(scanner);
		checkNext(scanner, LPAREN, 0, 1, 1, 1);
		checkNext(scanner, RPAREN, 1, 1, 1, 2);
		checkNextIsEOF(scanner);
	}
	
	
	@Test
	public void t1floatFailure() throws LexicalException {
		String input = "70008900974920000001864819739739274797000000000000000.4028235";
		
		show(input);
		
		//thrown.expect(LexicalException.class);  
		try {
			show(new Scanner(input).scan());
			
		} catch (LexicalException e) {  
			show(e);                    
			//throw e;
		}
		
	}
	
	
	
	
	@Test
	public void t2Intfailure() throws LexicalException {
		String input = "22312321312313123131313212312312312312313131313231432424285748957847487"   ;
		show(input);
		
		
		try {
			Scanner scanner = new Scanner(input).scan();
			show(scanner);
			
		} catch (LexicalException e) {  //Catch the exception
			show(e);                    //Display it
			
		}
	}
	
	
	@Test
	public void t3expression() throws LexicalException {
		String input = "([2+3]/5.0)"   ;
		show(input);
		
		 
		try {
			Scanner scanner = new Scanner(input).scan();
			show(scanner);
			
		} catch (LexicalException e) {  //Catch the exception
			show(e); 
			//throw e;
			
		}
		
		
	}
	

	@Test
	public void t4Comment1() throws LexicalException {
		String input = "/**//*"   ;
		show(input);
		thrown.expect(LexicalException.class);
		
		 
		try {
			Scanner scanner = new Scanner(input).scan();
			show(scanner);
			
		} catch (LexicalException e) {  //Catch the exception
			show(e); 
			throw e;
			
		}
		
		
	}
	
	@Test
	public void t5everything() throws LexicalException {
		String input = " \t\r\n*9 \rs  ab_$|&@true FALSE char /*^&(&#^~!@)?>\n<*/ | .53439499237927392 0.423242* 2173873174.312 313. 098. .hj 0.h"   ;
		show(input);
		
		 
		try {
			Scanner scanner = new Scanner(input).scan();
			show(scanner);
			
		} catch (LexicalException e) {  //Catch the exception
			show(e); 
			//throw e;
			
		}
		
		
	}
	
	@Test
	public void t6Expression() throws LexicalException {
		String input = "if(4.0<=7){/* **abcd* */float abc:= 5;}"   ;
		show(input);
		
		 
		try {
			Scanner scanner = new Scanner(input).scan();
			show(scanner);
			
		} catch (LexicalException e) {  //Catch the exception
			show(e); 
			//throw e;
			
		}
		
		
	}
	
	@Test
	public void t7Comment() throws LexicalException {
		String input = "** *"   ;
		show(input);
		
		 
		try {
			Scanner scanner = new Scanner(input).scan();
			show(scanner);
			
		} catch (LexicalException e) {  //Catch the exception
			show(e); 
			//throw e;
			
		}
		
		
	}
	
	
	
	@Test
	public void t8Casesensitive() throws LexicalException {
		String input = "IF|tRUE|sleep"   ;
		show(input);
		
		 
		try {
			Scanner scanner = new Scanner(input).scan();
			show(scanner);
			
		} catch (LexicalException e) {  //Catch the exception
			show(e); 
			//throw e;
			
		}
		
		
	}
	
	
}
	

