public class test {
  public static void main(String args[]) {
    TK tk =  TK.NUM;
    System.out.println(tk);
    System.err.println(TK.ERROR.toString().equals("ERROR"));
    
    System.out.println("\n");
    firstSet fs = new firstSet();
    //fs.printTable(); 
    
    System.out.println( fs.searchTable(20, TK.LPAREN) );

  }
}
