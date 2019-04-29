import syntaxtree.*;
import visitor.GJDepthFirst;
import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList; // import the ArrayList class



public class TableVisitor extends GJDepthFirst<String, Map> {

  //public HashMap<String,ArrayList<String>> Table = new HashMap();
  //public HashMap<String,Map<String,String>> ClassTypes = new HashMap();

  public HashMap<String,ClassForm> ClassTypes = new HashMap();
  String currentClass;
     /**
      * f0 -> "class"
      * f1 -> Identifier()
      * f2 -> "{"
      * f3 -> ( VarDeclaration() )*
      * f4 -> ( MethodDeclaration() )*
      * f5 -> "}"
      */
     public String visit(ClassDeclaration n, Map argu) {

        //n.f0.accept(this, argu);
        String className = n.f1.accept(this, argu);
        currentClass = className;
        //n.f2.accept(this, argu);

        ClassForm elem = new ClassForm();
        ClassTypes.put(className,elem);

        //String varDecl = n.f3.accept(this, argu);
        if(n.f3.present()) {
          n.f3.accept(this, elem.ClassVars);
        }

        if(n.f4.present()) {
          n.f4.accept(this, elem.Methods);
        }
        n.f5.accept(this, argu);
        //System.out.println(className + " -  " + methods);
        return className;
     }


     /**
      * f0 -> "class"
      * f1 -> Identifier()
      * f2 -> "extends"
      * f3 -> Identifier()
      * f4 -> "{"
      * f5 -> ( VarDeclaration() )*
      * f6 -> ( MethodDeclaration() )*
      * f7 -> "}"
      */
     public String visit(ClassExtendsDeclaration n, Map argu) {

        //n.f0.accept(this, argu);

        String className = n.f1.accept(this, argu);


        ClassForm elem = new ClassForm();
        ClassTypes.put(className,elem);


        //n.f2.accept(this, argu);
        elem.Isimpliments = n.f3.accept(this, argu);
        //n.f4.accept(this, argu);
        if(n.f5.present()) {
          n.f5.accept(this, elem.ClassVars);
        }
        if(n.f5.present()) {
          n.f6.accept(this, elem.Methods);
        }
        //n.f7.accept(this, argu);
        //System.out.println(className + " + " + superClass);

        //ClassTypes.put(className,superClass);

        return className;
     }


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

        MethodForm meth = new MethodForm();
        //ClassForm method = ClassTypes.get(argu);
        //n.f0.accept(this, argu);
        //ClassForm classF = (ClassForm) argu.get(currentClass);
        meth.Type = n.f1.accept(this, null);
        String funct = n.f2.accept(this, null);

        //System.out.println(type+funct+ argu);
        //n.f3.accept(this, vars);
        n.f4.accept(this, meth.Arguments);
        //n.f5.accept(this, vars);
        //n.f6.accept(this, vars);
        //Map<String,String> vars = new HashMap();
        n.f7.accept(this, meth.Vars);
        //n.f8.accept(this, vars);
        //n.f9.accept(this, vars);
        //n.f10.accept(this, vars);
        //n.f11.accept(this, vars);
        //n.f12.accept(this, vars);
        //argu.put(funct,vars);
        //classF.Methods.Vars = (HashMap) vars;
        argu.put(funct,meth);
        return "OK";
     }

 /**
  * f0 -> FormalParameter()
  * f1 -> FormalParameterTail()
  */
 public String visit(FormalParameterList n, Map argu) {
    n.f0.accept(this, argu);
    n.f1.accept(this, argu);
    return null;
 }

 /**
  * f0 -> Type()
  * f1 -> Identifier()
  */
 public String visit(FormalParameter n, Map argu) {
    String type = n.f0.accept(this, argu);
    String ident = n.f1.accept(this, argu);
    argu.put(ident,type);
    return type+ident;
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
      // /**
      //  * f0 -> "class"
      //  * f1 -> Identifier()
      //  * f2 -> "{"
      //  * f3 -> "public"
      //  * f4 -> "static"
      //  * f5 -> "void"
      //  * f6 -> "main"
      //  * f7 -> "("
      //  * f8 -> "String"
      //  * f9 -> "["
      //  * f10 -> "]"
      //  * f11 -> Identifier()
      //  * f12 -> ")"
      //  * f13 -> "{"
      //  * f14 -> ( VarDeclaration() )*
      //  * f15 -> ( Statement() )*
      //  * f16 -> "}"
      //  * f17 -> "}"
      //  */
      // public String visit(MainClass n, Map argu) {
      //    n.f0.accept(this, argu);
      //    String className = n.f1.accept(this, argu);
      //
      //    ClassForm elem = new ClassForm();
      //    ClassTypes.put(className,elem);
      //
      //    n.f2.accept(this, argu);
      //    n.f3.accept(this, argu);
      //    n.f4.accept(this, argu);
      //    n.f5.accept(this, argu);
      //    n.f6.accept(this, argu);
      //    n.f7.accept(this, argu);
      //    n.f8.accept(this, argu);
      //    n.f9.accept(this, argu);
      //    n.f10.accept(this, argu);
      //    n.f11.accept(this, argu);
      //    n.f12.accept(this, argu);
      //    n.f13.accept(this, argu);
      //    HashMap<String,String> vars = new HashMap();
      //    n.f14.accept(this, vars);
      //    elem.Methods.put("main",vars);
      //    n.f15.accept(this, argu);
      //    n.f16.accept(this, argu);
      //    n.f17.accept(this, argu);
      //    //System.out.println(className+" "+name+" "+arg);
      //    return className;
      // }
}
