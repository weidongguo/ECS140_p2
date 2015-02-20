/* *** This file is given as part of the programming assignment. *** */
import java.util.ArrayList;
import java.util.Stack;

public class Parser {


    // tok is global to all these parsing methods;
    // scan just calls the scanner's scan method and saves the result in tok.
    private Token tok; // the current token

    public static final int PROGRAM = 0, BLOCK = 1, DECLARATIONS = 2, 
      STATEMENT_LIST = 3, STATEMENT = 4, ASSIGNMENT = 5, PRINT = 6, IF = 7, DO = 8, 
      FA = 9, GUARDED_COMMANDS = 10, GUARDED_COMMAND = 11, COMMANDS = 12, 
      EXPRESSION = 13, SIMPLE = 14, TERM  = 15, FACTOR = 16, RELOP = 17, ADDOP = 18,
      MULTOP = 19, ASSIGNED_ON = 0, USED_ON = 1;
    
    private Stack<myArrayList> symbolTable;
    private myArrayList currentList;//holds current block's var info
    private Stack<myArrayList> varRef; 
    private int depth;
    private int indexForList;

    private void scan() {
        tok = scanner.scan();
    }

    private Scan scanner;
    Parser(Scan scanner) {
        this.scanner = scanner;
        scan(); // in Scan(), tok = scanner.scan(); so we can use tok after calling scan
        symbolTable = new Stack<myArrayList>(); 
        varRef = new Stack<myArrayList>();
        depth = 0;
        indexForList = 0;
        program();
        if( tok.kind != TK.EOF ) { 
            parse_error("junk after logical end of program");
        }
        if(currentList != null) 
          varRef.push(currentList); //the top most block

        printTable();
    }

    private void gen(String code) {
      System.out.print(code);
    }
    private void genln(String code) {
      System.out.println(code);
    }
    private void genln() {
      System.out.println();
    }
    private void genVar(String var) {
      System.out.print("x_" + var);
    }

    private void program() {
        genln("#include <stdio.h>");
        genln("main() {");
        block();
        gen("\n}");
    }

    private void block() {
        if( is(TK.VAR) )
          declarations();
        
        statement_list();
    }

    private void declarations() {
        boolean firstOne = true;
        mustbe(TK.VAR);
        gen("int ");
        while( is(TK.ID) ) { //keep consuming tokens for variable declaration
          int dist = inSymbolTable(); 
          if( dist == 0) { //redeclaration within the same block
            System.err.println("variable "+tok.string+" is redeclared on line " + tok.lineNumber);
          }
          else { //either not found(-1) variable in current block or found in outer block for dist >= 1
            add2List(); //Variable already declared from outer blocks
                      //but Variable hiding is legal
            if(firstOne) {
              genVar(tok.string + "= -12345");
              firstOne=false;
            }
            else
              gen(", "+ "x_" + tok.string +" = -12345");
          }
          scan();
        }
        genln(";");
        mustbe(TK.RAV);
    }

    private void statement_list() {
      while(first(STATEMENT))
        statement();
    }
    
    private void statement() {
      if(first(ASSIGNMENT))
        assignment();
      else if(first(PRINT))
        print();
      else if(first(IF))
        _if();
      else if(first(DO))
        _do();
      else if(first(FA))
        fa();
    }

    private void assignment() {
      if( inSymbolTable() == -1 )
        undeclaredVar();
      modifyEntry(ASSIGNED_ON);
      
      genVar(tok.string);
      mustbe(TK.ID);
      gen(" = ");
      mustbe(TK.ASSIGN);
      expression();
      genln(";");
    }

    private void print() {
      mustbe(TK.PRINT);//mustbe() call scan() at the end
      gen("printf(\"%d\\n\", ");
      expression();
      genln(");");
    }

    private void _if() {
      gen("if( ");
      mustbe(TK.IF);
      guarded_commands(false); //not doLoop, just normal if-else
      mustbe(TK.FI);
    }

    private void _do() {
      genln("while(1) { ");
      gen("if( ");
      mustbe(TK.DO);
      guarded_commands(true); //doLoop = true;
      mustbe(TK.OD);
      gen("break;\n}");
    }

    private void fa() {
      boolean st = false;
      gen("for( ");
      mustbe(TK.FA);
      if( inSymbolTable() == -1 )
        undeclaredVar();
      modifyEntry(ASSIGNED_ON); // assignment line
      String var = tok.string; 
      genVar(var + " = "); 
      mustbe(TK.ID);
      mustbe(TK.ASSIGN);
      expression();
      gen("; ");
      mustbe(TK.TO);
      genVar(var + " <= ");
      expression();
      gen("; " + "x_" + var + "++)");
      if( is(TK.ST) ) {
        st = true; 
        mustbe(TK.ST);
        gen("{\nif(");
        expression();
        gen(")");
      }
      commands(false);//false for normal block, not the do loop block
      mustbe(TK.AF);
      if(st)
        gen("\n}");
    }
    
    private void guarded_commands(boolean doLoop) {
      guarded_command(doLoop);
      while( is(TK.BOX) ) { // can have many [] (it's else if in C)
        gen("\nelse if( "); 
        mustbe(TK.BOX);
        guarded_command(doLoop);
      }

      if( is(TK.ELSE) ){
        gen("\nelse");
        mustbe(TK.ELSE);
        commands(doLoop); 
      }
     
       
    }

    private void guarded_command(boolean doLoop) {
      expression();
      gen(" )");
      commands(doLoop);

    }

    private void commands(boolean doLoop) {
      mustbe(TK.ARROW);
      if(currentList == null){
        //if null, then have to allocate space. otherwise seg fault
        //However, we can reuse this allocated space when it gets popped
        //back to currentList at the end of each block.
      currentList = new myArrayList(indexForList++);  
      } 

      symbolTable.push(currentList);
      currentList = null;
      depth++;
      
      gen(" {\n"); 

      block();
      if(doLoop)
        gen("continue;");
      genln("}\n");
      if(currentList != null) {
        varRef.push(currentList);
      }
      currentList = symbolTable.pop();
      depth--;


    }


    private void expression() {
      simple();
      if(first(RELOP)) {//only one relation operator in an expr
        relop();
        simple();
      }
    }
    
    private void simple() {
      term();
      while(first(ADDOP)) {//can have many addition operator in an expr
        addop();
        term();
      }
    }

    private void term() {
      factor();
      while(first(MULTOP)) {//can have many mult operation in an expr
        multop();
        factor();
      }
    }

    private void factor()
    { 
      if( is(TK.LPAREN) ) {
        mustbe(TK.LPAREN);
        gen("(");
        expression();
        mustbe(TK.RPAREN);
        gen(")");
      }
      else if( is(TK.ID) ) {
        if( inSymbolTable() == -1)
          undeclaredVar();
        modifyEntry(USED_ON);
        gen("x_" + tok.string);
        mustbe(TK.ID);
      }
      else if( is(TK.NUM) ) {
        gen(tok.string); //num
        mustbe(TK.NUM);
      }
      else {
        //mustbe(TK.ERROR);
        parse_error("factor");
      }
    }

    private void relop() {
      if( is(TK.EQ) ) {
        gen(" == "); 
        mustbe(TK.EQ);
      }
      else if( is(TK.LT) ) {
        gen(" < "); 
        mustbe(TK.LT);
      }
      else if( is(TK.GT) ) {
        gen(" > ");
        mustbe(TK.GT);
      }
      else if( is(TK.NE) ) {
        gen(" != ");
        mustbe(TK.NE);
      }
      else if( is(TK.LE) ) {
        gen(" <= "); 
        mustbe(TK.LE);
      }
      else if( is(TK.GE) ) {
        gen(" >= "); 
        mustbe(TK.GE);
      }
      else
        parse_error("Relational Operator not found");

    }

    private void addop() { 
      if( is(TK.PLUS) ) {
        gen(" + "); 
        mustbe(TK.PLUS);
      }
      else if( is(TK.MINUS) ) {
        gen(" - "); 
        mustbe(TK.MINUS);
      }
      else
        parse_error("Addition Operator not found");
    } 

    private void multop() {
      if( is(TK.TIMES) ) {
        gen(" * "); 
        mustbe(TK.TIMES);
      }
      else if( is(TK.DIVIDE) ) {
        gen(" / ");
        mustbe(TK.DIVIDE);
      }
      else
        parse_error("Multiplication Operator not found");
    }
    // you'll need to add a bunch of methods here

    // is current token what we want?
    private boolean is(TK tk) {
        return tk == tok.kind;
    }

    // ensure current token is tk and skip over it.
    private void mustbe(TK tk) {
        if( ! is(tk) ) {
            System.err.println( "mustbe: want " + tk + ", got " +
                                    tok);
            parse_error( "missing token (mustbe)" ); //will call System.exit(1) inside
        }
        scan();
    }

    private void parse_error(String msg) {
        System.err.println( "can't parse: line "
                            + tok.lineNumber + " " + msg );
        System.exit(1);
    }

    private boolean first(int index) {
        firstSet fs = new firstSet(); 
        return fs.searchTable(index, tok.kind);
        // return the whether the Current Token has the desired TYPE(kind)
    }

    private int inSymbolTable() {
      if(currentList != null)
        for(symbolTableEntry entry : currentList)
          if( entry.strcmp(tok.string) )
            return 0;//found within the same block
      
      // go check if there are some on the stack (from outer blocks);
      if(symbolTable.empty())
        return -1;
      int block_size = symbolTable.size();
      for(int bl = block_size - 1; bl >=0 ; bl-- ) {//each block
        for(int entry = 0; entry < symbolTable.get(bl).size(); entry++){//each entry
          if( symbolTable.get(bl).get(entry).strcmp(tok.string) )
            return block_size - bl + 1; //declaration's distance away from current block

        }
      }
      return -1;
    }

    private void add2List() {
      if(currentList == null)
        currentList = new myArrayList(indexForList++);
     
      symbolTableEntry entry = new symbolTableEntry(tok.string, tok.lineNumber, depth);
      currentList.add(entry);
      
    }
    
    private void undeclaredVar() {
     System.err.println("undeclared variable "+tok.string+" on line "+tok.lineNumber);
     System.exit(1); 
    }
    
    private void printTable() {
      Stack<myArrayList> sortedVarRef = sortVarRef(); 
      
      for( myArrayList list : sortedVarRef ) { //top down from the stack
        for(symbolTableEntry entry: list)
          System.err.println(entry);
      }
    }

    private void modifyEntry(int option) {
      boolean foundInCurrentList = false;
      if(currentList != null) { 
        for( symbolTableEntry entry : currentList) {
          if(entry.strcmp(tok.string)) {
            if(option == 0)
              entry.appendToAssignedLines(tok.lineNumber);
            else if(option == 1)
              entry.appendToUsedLines(tok.lineNumber);
            foundInCurrentList = true;
            break;
          }
        } 
      }
      if(!foundInCurrentList) {
        //System.out.println("NOT FOUND >>>>>>>>>>>>>>\n");
        for( int i = symbolTable.size()-1; i >= 0; i--) { //from top of the stack
          
          /*for(symbolTableEntry entry: symbolTable.get(i)) {
            if(entry.strcmp(tok.string)) {
              entry.appendToAssignedLines(tok.lineNumber);
            }
          }*/

          symbolTableEntry entry = searchCurrentList(symbolTable.get(i));
          if(entry != null) {
            if(option == 0)
              entry.appendToAssignedLines(tok.lineNumber); 
            if(option == 1)
              entry.appendToUsedLines(tok.lineNumber);
          }
        }
      }

    }

    private symbolTableEntry searchCurrentList(myArrayList list) {
      for( symbolTableEntry entry : list) {
        if(entry.strcmp(tok.string)) 
          return entry; //return reference to
      }
      return null;
    }

    private Stack<myArrayList> sortVarRef() { 
      Stack<myArrayList> sortedVarRef = new Stack<myArrayList>();
      for(int i = 0; i < varRef.size(); i++)
        for(myArrayList list : varRef) {
          if(list.index == i)
            sortedVarRef.push(list);
        }
      return sortedVarRef;
    }
}
