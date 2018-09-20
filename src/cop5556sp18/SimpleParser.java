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

//import cop5556sp18.SimpleParser.SyntaxException;
import cop5556sp18.Scanner.Kind;
import cop5556sp18.Scanner.Token;


public class SimpleParser {
	
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

	SimpleParser(Scanner scanner) {
		this.scanner = scanner;
		t = scanner.nextToken();
	}


	public void parse() throws SyntaxException {
		program();
		matchEOF();
	}

	/*
	 * Program ::= Identifier Block
	 */
	public void program() throws SyntaxException {
		match(IDENTIFIER);
		block();
	}
	
	/*
	 * Block ::=  { (  (Declaration | Statement) ; )* }

	 */
	public void block() throws SyntaxException {
		match(LBRACE);
		while (isKind(firstDec)||isKind(firstStatement)) {
	     if (isKind(firstDec)) {
			declaration();
		} else if (isKind(firstStatement)) {
			statement();
		}
			match(SEMI);
		}
		match(RBRACE);

	}
	
	
	Kind[] firstDec = { KW_int, KW_boolean, KW_image, KW_float, KW_filename };
	Kind[] firstStatement = {KW_input,KW_show, KW_write, KW_if, KW_while,KW_sleep,IDENTIFIER,KW_blue,KW_red,KW_green,KW_alpha };
	Kind[] firstFunctionName = {KW_sin, KW_cos, KW_atan, KW_abs, KW_log, KW_cart_x, KW_cart_y, KW_polar_a, KW_polar_r, KW_int, KW_float, KW_width, KW_height, KW_red,KW_green, KW_blue, KW_alpha};
	Kind[] firstPredefinedName = {KW_Z, KW_default_height, KW_default_width};
	Kind[] firstOrExpression = {OP_EXCLAMATION,OP_PLUS, OP_MINUS,INTEGER_LITERAL,BOOLEAN_LITERAL,FLOAT_LITERAL, LPAREN,KW_sin, KW_cos, KW_atan, KW_abs, KW_log, KW_cart_x, KW_cart_y, KW_polar_a, KW_polar_r, KW_int, KW_float, KW_width, KW_height, KW_red,KW_green, KW_blue, KW_alpha,IDENTIFIER,LPIXEL,KW_Z, KW_default_height, KW_default_width} ;
	Kind[] firstColor = {KW_blue,KW_red,KW_green,KW_alpha};
	Kind[] firstPrimary = {INTEGER_LITERAL,BOOLEAN_LITERAL,FLOAT_LITERAL, LPAREN,KW_sin, KW_cos, KW_atan, KW_abs, KW_log, KW_cart_x, KW_cart_y, KW_polar_a, KW_polar_r, KW_int, KW_float, KW_width, KW_height, KW_red,KW_green, KW_blue, KW_alpha,IDENTIFIER,LPIXEL,KW_Z, KW_default_height, KW_default_width};
	
	
	
	//Declaration ::= Type IDENTIFIER | image IDENTIFIER [ Expression , Expression ]
	
	public void declaration() throws SyntaxException {
		if(isKind(KW_image))
		{
			match(KW_image);
			match(IDENTIFIER);
			if(isKind(LSQUARE))
			{
				match(LSQUARE);
				Expression();
				match(COMMA);
				Expression();
				match(RSQUARE);
			}
			
		}
		else if(isKind(firstDec))
		{			
			consume();
			match(IDENTIFIER);
		}
		//doubt??
		else throw new SyntaxException(t,"Syntax Error"); //TODO  give a better error message!
			
		
		//throw new UnsupportedOperationException();
	}

	

	//PixelSelector ::= [ Expression , Expression ]
	public void PixelSelector() throws SyntaxException {
		match(LSQUARE);
		Expression();
		match(COMMA);
		Expression();
		match(RSQUARE);
	}
	
	//PixelConstructor ::=  <<  Expression , Expression , Expression , Expression  >> 
	public void PixelConstructor() throws SyntaxException {
		match(LPIXEL);
		Expression();
		match(COMMA);
		Expression();
		match(COMMA);
		Expression();
		match(COMMA);
		Expression();
		match(RPIXEL);
	}
		
	//PixelExpression ::= IDENTIFIER PixelSelector
	public void PixelExpression() throws SyntaxException {
		match(IDENTIFIER);
		PixelSelector();
	}
	
	//UnaryExpression ::= + UnaryExpression | - UnaryExpression | UnaryExpressionNotPlusMinus
	public void UnaryExpression() throws SyntaxException {
		if(isKind(OP_PLUS) || isKind(OP_MINUS)) {
			consume();
			UnaryExpression();
		}
		else 
		{
			UnaryExpressionNotPlusMinus();
		}
		
	}
	
	//UnaryExpressionNotPlusMinus ::=  ! UnaryExpression  | Primary 
	public void UnaryExpressionNotPlusMinus() throws SyntaxException
	{
		if(isKind(OP_EXCLAMATION))
		{
			consume();
			UnaryExpression();
		}
		else if(isKind(firstPrimary))
		{
			Primary();
		}
		else
		{
			throw new SyntaxException(t,"Syntax Error"); //TODO  give a better error message!
		}
	}
	
	//PowerExpression := UnaryExpression  (** PowerExpression | ε)
	public void PowerExpression() throws SyntaxException
	{
		UnaryExpression();
		if(isKind(OP_POWER))
		{
			consume();
			PowerExpression();
		}
		else
		{
			
		}	
	}
	
	//MultExpression := PowerExpression ( ( * | /  | % ) PowerExpression )*
	public void MultExpression() throws SyntaxException
	{
		PowerExpression();
		while(isKind(OP_TIMES)|| isKind(OP_DIV) || isKind(OP_MOD))
		{
			consume();
			PowerExpression();			
		}
	}

	//AddExpression ::= MultExpression   (  ( + | - ) MultExpression )*
	public void AddExpression() throws SyntaxException
	{
		MultExpression();
		while(isKind(OP_PLUS)||isKind(OP_MINUS))
		{
			consume();
			MultExpression();
		}
	}

	//RelExpression ::= AddExpression (  (<  | > |  <=  | >= )   AddExpression)*
	public void RelExpression() throws SyntaxException
	{
		AddExpression();
		while(isKind(OP_LT)|| isKind(OP_GT)|| isKind(OP_LE)||isKind(OP_GE))
		{
			consume();
			AddExpression();
		}
	}

	//EqExpression ::=  RelExpression  (  (== | != )  RelExpression )*
	public void EqExpression() throws SyntaxException
	{
		RelExpression();
		while(isKind(OP_EQ)|| isKind(OP_NEQ))
		{
			consume();
			RelExpression();
		}
	}

	//AndExpression ::=  EqExpression ( & EqExpression )*
	public void AndExpression() throws SyntaxException
	{
		EqExpression();
		while(isKind(OP_AND))
		{
			consume();
			EqExpression();
			
		}
	}
	
	//OrExpression  ::=  AndExpression   (  |  AndExpression ) *
	public void OrExpression() throws SyntaxException
	{
		AndExpression();
		while(isKind(OP_OR))
		{
			consume();
			AndExpression();
		}
	}

	//Expression ::=  OrExpression  ?  Expression  :  Expression  |   OrExpression
	public void Expression() throws SyntaxException {
		if(isKind(firstOrExpression))
		{
			OrExpression();
			if(isKind(OP_QUESTION))
			{
				consume();
				Expression();
				match(OP_COLON);
				Expression();				
			}
			else
			{
				
			}
		}
		else
		{
			throw new SyntaxException(t,"Syntax Error"); //TODO  give a better error message!
		}
		
	}
		
	//Primary ::= INTEGER_LITERAL | BOOLEAN_LITERAL | FLOAT_LITERAL | ( Expression ) | FunctionApplication  | IDENTIFIER | PixelExpression | PredefinedName | PixelConstructor
	public void Primary() throws SyntaxException
	{
		
		//terminal cases -- Primary ::= INTEGER_LITERAL | BOOLEAN_LITERAL | FLOAT_LITERAL
		if(isKind(INTEGER_LITERAL)|| isKind(BOOLEAN_LITERAL) || isKind(FLOAT_LITERAL)||  isKind(firstPredefinedName))
		{
			consume();
		}
		//for Expession --   Primary ::= ( Expression ) 
		else if(isKind(LPAREN))
		{
			match(LPAREN);
			Expression();
			match(RPAREN);
		}		
		//for Funcition application
		else if(isKind(firstFunctionName))
		{
			functionApplication();
		}		
		//for identifier and pixelExpression
		else if(isKind(IDENTIFIER))
		{
			consume();
			if(isKind(LSQUARE))
			{
				PixelSelector();
			}
			else
			{
				
			}
			}
		else if(isKind(LPIXEL))
		{
			PixelConstructor();
		}
		else
		{
			throw new SyntaxException(t,"Syntax Error"); //TODO  give a better error message!
		}
		
				
	}

	//FunctionApplication ::= FunctionName ( Expression )  | FunctionName  [ Expression , Expression ] 
      public void functionApplication() throws SyntaxException
	{
	if(isKind(firstFunctionName))
	{
		consume();
		if(isKind(LPAREN))
		{
			match(LPAREN);
			Expression();
			match(RPAREN);
			
			
		}
		else if(isKind(LSQUARE))
		{
			match(LSQUARE);
			Expression();
			match(COMMA);
			Expression();
			match(RSQUARE);			
		}
		else {
			throw new SyntaxException(t,"Syntax Error"); //TODO  give a better error message!
			
		}		
	}		
	}

      //StatementSleep ::=  sleep Expression
      public void StatementSleep() throws SyntaxException
      {
    	 
    		  match(KW_sleep);
    		  Expression();
      }
    	  
      //StatementShow ::=  show Expression
      public void StatementShow() throws SyntaxException
      {
    	  
    		  match(KW_show);
    		  Expression();
    	  
      }

      //StatementIf ::=  if ( Expression ) Block 
      public void StatementIf() throws SyntaxException

      {
    	  
    		  match(KW_if);
    		  match(LPAREN);
    		  Expression();
    		  match(RPAREN);
    		  block();    		  
    	
      }
	
      //StatementWhile ::=  while (Expression ) Block
      public void StatementWhile() throws SyntaxException
      {
    	      match(KW_while);    
    		  match(LPAREN);
    		  Expression();
    		  match(RPAREN);
    		  block();      	  
      }
 
     //LHS ::=  IDENTIFIER | IDENTIFIER PixelSelector | Color ( IDENTIFIER PixelSelector )
      public void LHS() throws SyntaxException
      {
    	  if(isKind(IDENTIFIER))
    	  {
    		  consume();
    		  if(isKind(LSQUARE))
    		  {
    			  PixelSelector(); 
    		  }
    		  else
    		  {
    			  
    		  }
    	  }
    	  else if (isKind(firstColor))
    	  {
    		  consume();
    		  match(LPAREN);
    		  match(IDENTIFIER);
    		  PixelSelector();
    		  match(RPAREN);   		  
    	  }
    	  else
    	  {
    		  throw new SyntaxException(t,"Syntax Error"); //TODO  give a better error message!
    	  }
      }
      
      //StatementAssignment ::=  LHS := Expression 
      public void StatementAssignment() throws SyntaxException
      {
    	  LHS();
    	  match(OP_ASSIGN);
    	  Expression();
      }

      //StatementWrite ::= write IDENTIFIER to IDENTIFIER
      public void StatementWrite() throws SyntaxException
      {
    	  match(KW_write);
    	  match(IDENTIFIER);
    	  match(KW_to);
    	  match(IDENTIFIER);   	  
      }

      //StatementInput ::= input IDENTIFIER from @ Expression
      public void StatementInput() throws SyntaxException
      {
    	  match(KW_input);
    	  match(IDENTIFIER);
    	  match(KW_from);
    	  match(OP_AT);
    	  Expression();
      }
      
     //Statement ::= StatementInput | StatementWrite | StatementAssignment | StatementWhile | StatementIf | StatementShow | StatementSleep	
      public void statement() throws SyntaxException {
		if(isKind(KW_input))
		{
			
			StatementInput();
		}
		else if(isKind(KW_while))
		{
			
			StatementWhile();
		}
		else if(isKind(KW_if))
		{
			
			StatementIf();
		}
		else if(isKind(KW_show))
		{
			
			StatementShow();
		}
		else if(isKind(KW_sleep))
		{
			
			StatementSleep();
		}
		else if(isKind(KW_write))
		{
			
			StatementWrite();
		}
		else if(isKind(firstColor)||isKind(IDENTIFIER))
		{
			StatementAssignment();
		}
		
		else
		{
			throw new SyntaxException(t,"Syntax Error"); //TODO  give a better error message!
		}
		
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
		throw new SyntaxException(t,"Syntax Error"); //TODO  give a better error message!
	}


	private Token consume() throws SyntaxException {
		Token tmp = t;
		if (isKind( EOF)) {
			throw new SyntaxException(t,"Syntax Error"); //TODO  give a better error message!  
			//Note that EOF should be matched by the matchEOF method which is called only in parse().  
			//Anywhere else is an error. */
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
		throw new SyntaxException(t,"Syntax Error"); //TODO  give a better error message!
	}
	

}

