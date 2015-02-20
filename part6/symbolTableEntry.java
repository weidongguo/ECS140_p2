import java.util.ArrayList;

public class symbolTableEntry {
  private String var;
  private int line_declared;
  private int depth;
  private String lineNumberAssigned;
  private String lineNumberUsed;
  private ArrayList<Integer> assigned_lines;
  private ArrayList<Integer> assigned_counts;
  private ArrayList<Integer> used_lines;
  private ArrayList<Integer> used_counts;


  public String decl_info() {
    return ("declared on line"+line_declared+" at nesting depth "+depth);
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

    assigned_lines = new ArrayList<Integer>(); 
    assigned_counts = new ArrayList<Integer>();
    used_lines = new ArrayList<Integer>();
    used_counts = new ArrayList<Integer>();
  }

  private String assigned() {
    if(lineNumberAssigned == "")
      return "never assigned";
    return "assigned to on:" + lineNumberAssigned;
  }

  private String used() {
    if(lineNumberUsed == "")
      return "never used";
    return "used on:" + lineNumberUsed;

  }

  public String toString() { 
    lineNumberAssigned = "";
    for( int i = 0; i < assigned_lines.size(); i++) {
      if(assigned_counts.get(i) == 1)
        lineNumberAssigned = lineNumberAssigned + " " + assigned_lines.get(i);
      else {
        lineNumberAssigned = lineNumberAssigned +" "+ assigned_lines.get(i) +"(" +
                         assigned_counts.get(i) + ")";
      }
    }

    lineNumberUsed = "";
    for(int i= 0 ; i < used_lines.size(); i++) {
      if(used_counts.get(i) == 1)
        lineNumberUsed = lineNumberUsed + " " + used_lines.get(i);
      else {
        lineNumberUsed = lineNumberUsed + " " + used_lines.get(i) + "(" + 
                         used_counts.get(i) + ")";
      }
    }
    return var+"\n  declared on line "+line_declared+" at nesting depth "+depth +
         "\n  " + assigned() + "\n  " + used(); 
  }

  public void appendToAssignedLines(int lineNumber){
    int index = assigned_lines.indexOf(lineNumber);
    if( index == -1) {
      assigned_lines.add(lineNumber);
      assigned_counts.add(1);
    }
    else
      assigned_counts.set(index, assigned_counts.get(index) + 1);//autoboxing!

  }

  public void appendToUsedLines(int lineNumber) {
    int index = used_lines.indexOf(lineNumber);
    if( index == -1) {
      used_lines.add(lineNumber);
      used_counts.add(1);
    }
    else
      used_counts.set(index, used_counts.get(index) + 1 );

  }
}
