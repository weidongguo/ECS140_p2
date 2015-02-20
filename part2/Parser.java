/* *** This file is given as part of the programming assignment. *** */

public class Parser {


    // tok is global to all these parsing methods;
    // scan just calls the scanner's scan method and saves the result in tok.
    private Token tok; // the current token

    public static final int PROGRAM = 0, BLOCK = 1, DECLARATIONS = 2, 
      STATEMENT_LIST = 3, STATEMENT = 4, ASSIGNMENT = 5, PRINT = 6, IF = 7, DO = 8, 
      FA = 9, GUARDED_COMMANDS = 10, GUARDED_COMMAND = 11, COMMANDS = 12, 
      EXPRESSION = 13, SIMPLE = 14, TERM  = 15, FACTOR = 16, RELOP = 17, ADDOP = 18, 
      MULTOP = 19;

    private void scan() {
        tok = scanner.scan();
    }

    private Scan scanner;
    Parser(Scan scanner) {
        this.scanner = scanner;
        scan(); // in Scan(), tok = scanner.scan(); so we can use tok after calling scan
        program();
        if( tok.kind != TK.EOF ) { 
            parse_error("junk after logical end of program");
        }
    }

    private void program() {
        block();
    }

    private void block() {
        if( is(TK.VAR) )
          declarations();
        
        statement_list();
    }

    private void declarations() {
        mustbe(TK.VAR);
        while( is(TK.ID) ) { //keep consuming tokens for variable declaration
            scan();
        }
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
      mustbe(TK.ID);
      mustbe(TK.ASSIGN);
      expression();
    }

    private void print() {
      mustbe(TK.PRINT);//mustbe() call scan() at the end
      expression();
    }

    private void _if() {
      mustbe(TK.IF);
      guarded_commands();
      mustbe(TK.FI);
    }

    private void _do() {
      mustbe(TK.DO);
      guarded_commands();
      mustbe(TK.OD);
    }

    private void fa() {
      mustbe(TK.FA);
      mustbe(TK.ID);
      mustbe(TK.ASSIGN);
      expression();
      mustbe(TK.TO);
      expression();
      if( is(TK.ST) ) {
        mustbe(TK.ST);
        expression();
      }
      commands();
      mustbe(TK.AF);
    }
    
    private void guarded_commands() {
      guarded_command();
      while( is(TK.BOX) ) { // can have many [] (it's else if in C)
        mustbe(TK.BOX);
        guarded_command();
      }

      if( is(TK.ELSE) ){
        mustbe(TK.ELSE);
        commands();
      }
       
    }

    private void guarded_command() {
      expression();
      commands();

    }

    private void commands() {
      mustbe(TK.ARROW);
      block();

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
        expression();
        mustbe(TK.RPAREN);
      }
      else if( is(TK.ID) )
        mustbe(TK.ID);
      else if( is(TK.NUM) )
        mustbe(TK.NUM);
      else {
        //mustbe(TK.ERROR);
        parse_error("factor");
      }
    }

    private void relop() {
      if( is(TK.EQ) )
        mustbe(TK.EQ);
      else if( is(TK.LT) )
        mustbe(TK.LT);
      else if( is(TK.GT) )
        mustbe(TK.GT);
      else if( is(TK.NE) )
        mustbe(TK.NE);
      else if( is(TK.LE) )
        mustbe(TK.LE);
      else if( is(TK.GE) )
        mustbe(TK.GE);
      else
        parse_error("Relational Operator not found");

    }

    private void addop() { 
      if( is(TK.PLUS) )
        mustbe(TK.PLUS);
      else if( is(TK.MINUS) )
        mustbe(TK.MINUS);
      else
        parse_error("Addition Operator not found");
    } 

    private void multop() {
      if( is(TK.TIMES) )
        mustbe(TK.TIMES);
      else if( is(TK.DIVIDE) )
        mustbe(TK.DIVIDE);
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
}
