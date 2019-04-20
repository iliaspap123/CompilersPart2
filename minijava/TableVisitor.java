import syntaxtree.*;
import visitor.GJDepthFirst;
import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList; // import the ArrayList class



public class TableVisitor extends GJDepthFirst<String, Map> {

  public HashMap<String,ArrayList<String>> Table = new HashMap();
  //public HashMap<String,Map<String,String>> ClassTypes = new HashMap();

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

        //argu = new HashMap();
        n.f0.accept(this, argu);
        String className = n.f1.accept(this, argu);
        n.f2.accept(this, argu);

        //Map<String,String> vars = new HashMap();
        ClassForm elem = new ClassForm();
        ClassTypes.put(className,elem);

        //String varDecl = n.f3.accept(this, argu);
        String varDecl = "";
        if(n.f3.present()) {
          varDecl = n.f3.accept(this, elem.ClassVars);
        }
        else {
          varDecl = "hi";
        }

        //ClassTypes.put(className,vars);
        //n.f4.accept(this, argu);



        //Map<String,String> methods = new HashMap();
        //String varDecl = n.f3.accept(this, argu);
        if(n.f4.present()) {
          varDecl = n.f4.accept(this, elem.Methods);
        }
        else {
          varDecl = "hi";
        }
        //ClassTypes.put(className,vars);

        n.f5.accept(this, argu);

        //System.out.println(className + " -  " + methods);
        //ClassTypes.put(className,"none");
        return className;
     }

     //
     // /**
     //  * f0 -> "class"
     //  * f1 -> Identifier()
     //  * f2 -> "extends"
     //  * f3 -> Identifier()
     //  * f4 -> "{"
     //  * f5 -> ( VarDeclaration() )*
     //  * f6 -> ( MethodDeclaration() )*
     //  * f7 -> "}"
     //  */
     // public String visit(ClassExtendsDeclaration n, String argu) {
     //
     //    String className = n.f0.accept(this, argu);
     //    n.f1.accept(this, argu);
     //    n.f2.accept(this, argu);
     //    n.f3.accept(this, argu);
     //    n.f4.accept(this, argu);
     //    String superClass = "";
     //    if(n.f5.present()) {
     //      superClass = n.f5.accept(this, argu);
     //    }
     //    else {
     //      superClass = "hi";
     //    }
     //    n.f6.accept(this, argu);
     //    n.f7.accept(this, argu);
     //    //System.out.println(className + " + " + superClass);
     //
     //    //ClassTypes.put(className,superClass);
     //
     //    return className;
     // }


     /**
      * f0 -> "public"
      * f1 -> Type()
      * f2 -> Identifier()
      * f3 -> "("
      * f4 -> ( FormalParameterList() )?
      * f5 -> ")"
      * f6 -> "{"
      * f7 -> ( VarDeclaration() )*
      * f8 -> ( Statement() )*
      * f9 -> "return"
      * f10 -> Expression()
      * f11 -> ";"
      * f12 -> "}"
      */
     public String visit(MethodDeclaration n, Map argu) {

        //ClassForm method = ClassTypes.get(argu);
        n.f0.accept(this, argu);
        String type = n.f1.accept(this, argu);
        String funct = n.f2.accept(this, argu);

        //System.out.println(type+funct+ argu);
        Map<String,String> vars = new HashMap();
        n.f3.accept(this, vars);
        n.f4.accept(this, vars);
        n.f5.accept(this, vars);
        n.f6.accept(this, vars);
        String varDecl = n.f7.accept(this, vars);
        n.f8.accept(this, vars);
        n.f9.accept(this, vars);
        n.f10.accept(this, vars);
        n.f11.accept(this, vars);
        n.f12.accept(this, vars);
        argu.put(funct,vars);
        return "OK";
     }



  /**
   * f0 -> Type()
   * f1 -> Identifier()
   * f2 -> ";"
   */
  public String visit(VarDeclaration n, Map argu) {
     String Type = n.f0.accept(this, argu);
     String Ident = n.f1.accept(this, argu);
     n.f2.accept(this, argu);
     argu.put(Ident,Type);
    // argu.put(Ident,Type);
     //System.out.println(Type + " " + Ident);

     // ArrayList<String> list;
     // if(Table.containsKey(Type)) {
     //   list = Table.get(Type);
     //   list.add(Ident);
     //   //System.out.println("list1 " + list);
     //
     // }
     // else {
     //   list = new ArrayList<String>();
     //   list.add(Ident);
     //   Table.put(Type,list);
     //   //System.out.println("list2 " + list);
     // }

     // System.out.println("list2 " + list);
     // System.out.println("list");
     return Type + " " + Ident;
  }


  /**
   * f0 -> ArrayType()
   *       | BooleanType()
   *       | IntegerType()
   *       | Identifier()
   */
  public String visit(Type n, Map argu) {
     return n.f0.accept(this, argu);
  }


  /**
  * f0 -> <IDENTIFIER>
  */
  public String visit(Identifier n, Map argu) {
    //System.out.println(n.f0.toString());
    return n.f0.toString();
  }

  /**
   * f0 -> "boolean"
   */
  public String visit(BooleanType n, Map argu) {
     return n.f0.toString();
  }


   /**
    * f0 -> "int"
    */
   public String visit(IntegerType n, Map argu) {
      return n.f0.toString();
   }

   /**
    * f0 -> "int"
    * f1 -> "["
    * f2 -> "]"
    */
   public String visit(ArrayType n, Map argu) {
      return n.f0.toString()+n.f1.toString()+n.f2.toString();
   }
  //
  // public Integer visit(IntegerLiteral n, Integer argu) {
  //    //System.out.println("hi");
  //    System.out.println(Integer.parseInt(n.f0.toString()));
  //    return Integer.parseInt(n.f0.toString());
  // }



  // /**
  //  * f0 -> MainClass()
  //  * f1 -> ( TypeDeclaration() )
  //  * f2 -> <EOF>
  //  */
  // public String visit(Goal n, Interger argu) {
  //    R _ret=null;
  //    n.f0.accept(this, argu);
  //    n.f1.accept(this, argu);
  //    n.f2.accept(this, argu);
  //    return _ret;
  // }
  //
  // /**
  //  * f0 -> ClassDeclaration()
  //  *       | ClassExtendsDeclaration()
  //  */
  // public String visit(TypeDeclaration n, Integer argu) {
  //    return n.f0.accept(this, argu);
  // }

}
