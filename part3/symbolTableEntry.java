public class symbolTableEntry {
  private String var;
  private int line_declared;
  private int depth;
  private String line_assigned;
  private String line_used;

  public void decl_info() {
    System.err.println("declared on line"+line_declared+" at nesting depth "+depth);
  }

  public boolean strcmp(String str) {
    return var.equals(str);
  }

  public void set(String var_name, int line_number, int dist) {
    var = var_name;
    line_declared = line_number;
    depth = dist;
  }

  public symbolTableEntry(String var_name, int line_number, int dist) {
    set(var_name, line_number, dist);
  }
}
