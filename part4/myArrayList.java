import java.util.ArrayList;

class myArrayList extends ArrayList<symbolTableEntry> {
  private static final long serialVersionUID = 0;
  //since ArrayList can be serialized for transimission of objects between network

  int index;
  
  public myArrayList(int i){
    index = i;
  }
}
