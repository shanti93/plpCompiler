package cop5556sp18;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import cop5556sp18.AST.Declaration;

public class symbolTable {
	
	//to keep variable and attributes
		Map<String, Map<Integer ,Declaration>> map = new HashMap<String, Map<Integer ,Declaration>>();
	//to keep track of scope number
	Stack<Integer> scopestack = new Stack<Integer>();
	
	int scope = 0;
	
	//Function to enter new scope!
	public void enterScope(){
		scopestack.push(scope++);
		//System.out.println(scope);
	}
	
	//Leaving scope
	public void leaveScope(){
		scopestack.pop();
	}
	
	//Function for lookup
	public Declaration lookup(String ident){
		int s=0;
		Declaration declar = null;
		Map<Integer ,Declaration> imap = map.get(ident);
		if (imap == null) return null;	
		//System.out.println(imap);
		for(int i=scopestack.size()-1;i>=0;i--){
			s = scopestack.get(i);
			if(imap.containsKey(s)){
				declar = imap.get(s);
				//System.out.println(s);
				break;
			}
				
		}
		return declar;
	}
	public Declaration lookkupinCurrentScope(String ident)
	{
		int x = 0;
		Declaration declaration = null;
		Map<Integer, Declaration> actualScopeMap = map.get(ident);
		
		if (actualScopeMap != null) {
			x = scopestack.get(scopestack.size() - 1);
			
			if (actualScopeMap.containsKey(x)) {
				declaration = actualScopeMap.get(x);
			}
		}
		return declaration;
	}
	
	//Function to insert into symb table
	public boolean insertintomap(String ident, Declaration declar){
		int sc = scopestack.peek();
		
		Map<Integer ,Declaration> imap;
		if(!map.containsKey(ident)){
			imap = new HashMap<Integer,Declaration>();
			imap.put(sc, declar);
			map.put(ident, imap);
			return true;
		}
		imap = map.get(ident);
		
		if(imap.containsKey(sc)){
			return false;
		}
		imap.put(sc, declar);
		map.put(ident, imap);
		return true; 
	}
	
	
	
	public void SymbolTable() {
		scopestack.push(scope++);
	}
	

}
