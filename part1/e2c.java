public class e2c {
 
    public static void main(String args[]) {
        Scan scanner = new Scan(args);
        Token t;
        do {
            t = scanner.scan(); //scanner returns a Token type 
            System.out.println(t); // Token has the instance method toString() 
        } while( t.kind != TK.EOF );     
    }
}
