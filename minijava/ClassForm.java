import java.util.Map;
import java.util.HashMap;
import java.util.LinkedHashMap;

public class ClassForm {
  String Isimpliments;
  public HashMap<String,String> ClassVars = new HashMap();
  public HashMap<String,MethodForm> Methods = new HashMap();
}

class MethodForm {
  String Type;
  LinkedHashMap<String,String> Arguments = new LinkedHashMap();
  HashMap<String,String> Vars = new HashMap();
}
