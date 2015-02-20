import java.io.*;


public class firstSet {
  private String[][] table; 
  private BufferedReader br;

  public firstSet() {
    table = new String[20][6];
    //table contains 20 nonterminals' first sets. In each set, non existing elements 
    //are null. e.g. {var, null, null, null ,null, null} for declaration non terminal
                                  
    try { 
      FileReader fr = new FileReader("firstSet.txt");
      br = new BufferedReader(fr);
    } catch(Exception oops) {
      System.err.println("Exception opening file\n");
      oops.printStackTrace();
      }
    int c, i = 0, j = 0; String str = new String(); 
    while( ( c = getchar() ) != -1 ) {
      if( c == ' ')
        j++;
      else if( c == '\n') {
        i++;
        j=0;
      }
      else
      {  
         if( table[i][j] == null)         
           table[i][j] = "";
        
         table[i][j] += (char)(c);
      }
         
    }
  }

  
  public void printTable() {
    for(int i = 0; i < 20; i++) {
      for(int j = 0; j < 6 ; j++)
        System.out.print( table[i][j] + " " );
      System.out.println("\n");
    }


  }

  public boolean searchTable(int index, TK tok)
  {
    if(index >= 20) // boundary check 
      return false;

    for(int j = 0; j < 6 && table[index][j] != null; ++j) 
    // 6 is the max columns we need for the table
      if( table[index][j].equals( tok.toString() ) )
      // each index represents each Non-Terminal
        return true;
    return false;
  }

  private int getchar() {
    int c = -1; 
    try {
      c = br.read();
    } catch(java.io.IOException e) {
      System.err.println("oops" );
      e.printStackTrace();
    }

    return c;
  }
}

