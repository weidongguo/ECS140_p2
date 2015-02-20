public class testEnum {
  public static void main(String args[]) {
    System.out.println("Weidong");
    
    TK tk = TK.IF ; 
    System.out.println(tk);

    for(TK p : TK.values() )
      System.out.println(p);
    System.out.println();
    System.out.println(Character.isLetter(' '));
    System.out.println(Character.isDigit(' '));
    System.out.println(String.valueOf('a').getClass());
    System.out.println(new Character('a').getClass());
  }

}
