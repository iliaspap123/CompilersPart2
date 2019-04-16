import syntaxtree.*;
import visitor.GJDepthFirst;
import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList; // import the ArrayList class

public class TableVisitor extends GJDepthFirst<String, Integer> {

  public HashMap<String,ArrayList<String>> Table = new HashMap();

  /**
   * f0 -> Type()
   * f1 -> Identifier()
   * f2 -> ";"
   */
  public String visit(VarDeclaration n, Integer argu) {
     String Type = n.f0.accept(this, argu);
     String Ident = n.f1.accept(this, argu);
     n.f2.accept(this, argu);
     System.out.println(Type + " " + Ident);

     ArrayList<String> list;
     if(Table.containsKey(Type)) {
       list = Table.get(Type);
       list.add(Ident);
       //System.out.println("list1 " + list);

     }
     else {
       list = new ArrayList<String>();
       list.add(Ident);
       Table.put(Type,list);
       //System.out.println("list2 " + list);
     }
     //System.out.println("list");
     return Ident;
  }


  /**
   * f0 -> ArrayType()
   *       | BooleanType()
   *       | IntegerType()
   *       | Identifier()
   */
  public String visit(Type n, int argu) {
     return n.f0.accept(this, argu);
  }


  /**
  * f0 -> <IDENTIFIER>
  */
  public String visit(Identifier n, Integer argu) {
    //System.out.println(n.f0.toString());
    return n.f0.toString();
  }

  /**
   * f0 -> "boolean"
   */
  public String visit(BooleanType n, Integer argu) {
     return n.f0.toString();
  }


   /**
    * f0 -> "int"
    */
   public String visit(IntegerType n, Integer argu) {
      return n.f0.toString();
   }

   /**
    * f0 -> "int"
    * f1 -> "["
    * f2 -> "]"
    */
   public String visit(ArrayType n, Integer argu) {
      return n.f0.toString()+n.f1.toString()+n.f2.toString();
   }
  //
  // public Integer visit(IntegerLiteral n, Integer argu) {
  //    //System.out.println("hi");
  //    System.out.println(Integer.parseInt(n.f0.toString()));
  //    return Integer.parseInt(n.f0.toString());
  // }

}
