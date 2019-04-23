import syntaxtree.*;
import visitor.GJDepthFirst;
import java.util.HashMap;
import java.util.Map;



public class check extends GJDepthFirst<String, Map> {

  public HashMap<String,ClassForm> ClassTypes = new HashMap();

  /**
   * f0 -> "class"
   * f1 -> Identifier()
   * f2 -> "{"
   * f3 -> ( VarDeclaration() )*
   * f4 -> ( MethodDeclaration() )*
   * f5 -> "}"
   */
  public String visit(ClassDeclaration n, Map argu) {
     n.f0.accept(this, argu);
     n.f1.accept(this, argu);
     n.f2.accept(this, argu);
     n.f3.accept(this, argu);
     n.f4.accept(this, argu);
     n.f5.accept(this, argu);

     return "ok";
  }

  public check(HashMap<String,ClassForm> oldTypes) {
    //System.out.println("OOOOOOK"+oldTypes);
    ClassTypes = oldTypes;
    for(String keys : ClassTypes.keySet()) {
      System.out.println("\t" + keys);
      ClassForm M = ClassTypes.get(keys);
      System.out.println("\t" + M.ClassVars);
      System.out.println("\t" + M.Methods);
    }
   }

}
