import java.util.Map;
import java.util.HashMap;

public class ClassForm {
  String Isimpliments;
  public HashMap<String,String> ClassVars = new HashMap();
  public HashMap<String,MethodForm> Methods = new HashMap();
}

class MethodForm {
  String Type;
  HashMap<String,String> Arguments = new HashMap();
  HashMap<String,String> Vars = new HashMap();
}
