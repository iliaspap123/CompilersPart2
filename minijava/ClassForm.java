import java.util.Map;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.ArrayList;
class ClassInfo {
  int offset_var;
  int offset_meth;
  public HashMap<String,Integer> ClassVarInfo = new HashMap();
  public HashMap<String,Integer> MethodInfo = new HashMap();

  public void MyAddMethod(String name,String type) {
    MethodInfo.put(name,offset_meth);
    this.offset_meth += 8;
  }
  public void MyAddVar(String name,String type) {
    this.ClassVarInfo.put(name,offset_var);
    if(type.equals("int")) {
      this.offset_var += 4;
    }
    else if(type.equals("boolean")) {
      this.offset_var += 1;
    }
    else {
      this.offset_var += 8;
    }
  }
}


public class ClassForm extends ClassInfo{
  String Isimpliments;
  public HashMap<String,String> ClassVars = new HashMap();
  public HashMap<String,MethodForm> Methods = new HashMap();
}

class MethodForm {
  String Type;
  LinkedHashMap<String,String> Arguments = new LinkedHashMap();
  HashMap<String,String> Vars = new HashMap();
}
