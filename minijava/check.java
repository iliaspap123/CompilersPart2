import syntaxtree.*;
import visitor.GJDepthFirst;
import java.util.HashMap;
import java.util.Map;



public class check extends GJDepthFirst<String, Map> {

  public HashMap<String,ClassForm> ClassTypes;
  ClassForm classCur;
  String currentClass;
  /**
   * f0 -> "class"
   * f1 -> Identifier()Map
   * f2 -> "{"
   * f3 -> ( VarDeclaration() )*
   * f4 -> ( MethodDeclaration() )*
   * f5 -> "}"
   */
  public String visit(ClassDeclaration n, Map argu) {

    ClassTypes = (HashMap) argu;

     String className = n.f1.accept(this, argu);
     currentClass = className;
     ClassForm classF = (ClassForm) argu.get(className);
     classCur = classF;
     //n.f3.accept(this, argu);
     //System.out.println("HA" + classF.Methods);

     HashMap all_vars = new HashMap();
     // all_vars.putAll(classF.Methods.Vars);
     // all_vars.putAll(classF.Methods.Arguments);
     // all_vars.putAll(classF.ClassVars);
      n.f4.accept(this,classF.Methods);
     return "ok";
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

      // ClassForm classF = ClassTypes.get(currentClass);
      // System.out.println(classF.ClassVars);
      //n.f1.accept(this, argu);
      String funct = n.f2.accept(this, null);
      //n.f3.accept(this, argu);
      //HashMap<String,String> vars = (HashMap) argu.get(funct);
      //System.out.println(funct + " " + vars);
      //n.f4.accept(this, argu);
      //n.f5.accept(this, argu);
      //n.f6.accept(this, argu);
      //n.f7.accept(this, argu);
      MethodForm meth = (MethodForm) argu.get(funct);
      HashMap all_vars = new HashMap();
      all_vars.putAll(meth.Arguments);
      all_vars.putAll(meth.Vars);
      all_vars.putAll(classCur.ClassVars);

      //all_vars.putAll(classF.ClassVars);

      n.f8.accept(this, all_vars);
      //n.f9.accept(this, argu);
      n.f10.accept(this, all_vars);
      //n.f11.accept(this, argu);
      //n.f12.accept(this, argu);
      return "ok2";
   }



    /**
     * f0 -> Block()
     *       | AssignmentStatement()
     *       | ArrayAssignmentStatement()
     *       | IfStatement()
     *       | WhileStatement()
     *       | PrintStatement()
     */
    public String visit(Statement n, Map argu) {
       return n.f0.accept(this, argu);
    }



   /**
    * f0 -> Identifier()
    * f1 -> "="
    * f2 -> Expression()
    * f3 -> ";"
    */
   public String visit(AssignmentStatement n, Map argu) {

      String ident = n.f0.accept(this, argu);
      //n.f1.accept(this, argu);
      String expr = n.f2.accept(this, argu);
      //n.f3.accept(this, argu);

      if(argu.get(ident) != expr) {
        System.out.println(argu.get(ident)+" error "+expr);
      }
      else {
        System.out.println("ok "+expr);
      }
      return "ko";
   }




  /**
   * f0 -> AndExpression()
   *       | CompareExpression()
   *       | PlusExpression()
   *       | MinusExpression()
   *       | TimesExpression()
   *       | ArrayLookup()
   *       | ArrayLength()
   *       | MessageSend()
   *       | Clause()
   */
  public String visit(Expression n, Map argu) {
     return n.f0.accept(this, argu);
  }

  /**
   * f0 -> Clause()
   * f1 -> "&&"
   * f2 -> Clause()
   */
  public String visit(AndExpression n, Map argu) {
     //R _ret=null;
     //n.f1.accept(this, argu);
     if(n.f0.accept(this, argu) != "boolean" || n.f2.accept(this, argu) != "boolean") {
       System.out.println("error &&");
     }
     return "boolean";
  }

  /**
   * f0 -> PrimaryExpression()
   * f1 -> "<"
   * f2 -> PrimaryExpression()
   */
  public String visit(CompareExpression n, Map argu) {
     //String type;
     //n.f1.accept(this, argu);
     if(n.f0.accept(this, argu) != "int" ||  n.f2.accept(this, argu) != "int") {
       System.out.println("error <");
     }
     return "boolean";
  }


 /**
  * f0 -> PrimaryExpression()
  * f1 -> "+"
  * f2 -> PrimaryExpression()
  */
 public String visit(PlusExpression n, Map argu) {
    if(n.f0.accept(this, argu) != "int" ||  n.f2.accept(this, argu) != "int") {
     System.out.println("error +");
    }
    return "int";
 }


  /**
   * f0 -> PrimaryExpression()
   * f1 -> "-"
   * f2 -> PrimaryExpression()
   */
  public String visit(MinusExpression n, Map argu) {
    if(n.f0.accept(this, argu) != "int" ||  n.f2.accept(this, argu) != "int") {
     System.out.println("error -");
    }
    return "int";
  }


  /**
   * f0 -> IntegerLiteral()
   *       | TrueLiteral()
   *       | FalseLiteral()
   *       | Identifier()
   *       | ThisExpression()
   *       | ArrayAllocationExpression()
   *       | AllocationExpression()
   *       | BracketExpression()
   */
  public String visit(PrimaryExpression n, Map argu) {
     String type = n.f0.accept(this, argu);
     if(type != "int" && type != "int[]" && type != "boolean") {
       type = (String) argu.get(type);
       //System.out.println("tyoe is "+type);
     }
     return type;
  }

  /**
   * f0 -> NotExpression()
   *       | PrimaryExpression()
   */
  public String visit(Clause n, Map argu) {
     return n.f0.accept(this, argu);
  }


  /**
   * f0 -> "!"
   * f1 -> Clause()
   */
  public String visit(NotExpression n, Map argu) {
     //R _ret=null;
     //n.f0.accept(this, argu);
     if(n.f1.accept(this, argu) != "boolean") {
       System.out.println("error !");
     }
     return "boolean";
  }

  /**
   * f0 -> "("
   * f1 -> Expression()
   * f2 -> ")"
   */
  public String visit(BracketExpression n, Map argu) {
     //n.f0.accept(this, argu);
     //n.f2.accept(this, argu);
     return n.f1.accept(this, argu);
  }


   /**
    * f0 -> "new"
    * f1 -> "int"
    * f2 -> "["
    * f3 -> Expression()
    * f4 -> "]"
    */
   public String visit(ArrayAllocationExpression n, Map argu) {
      //n.f0.accept(this, argu);
      //n.f1.accept(this, argu);
      //n.f2.accept(this, argu);
      if(n.f3.accept(this, argu) != "int" ) {
          System.out.println("in array "+n.f3.accept(this, argu));
      }
      //n.f4.accept(this, argu);
      return "int[]";
   }


   /**
    * f0 -> PrimaryExpression()
    * f1 -> "["
    * f2 -> PrimaryExpression()
    * f3 -> "]"
    */
   public String visit(ArrayLookup n, Map argu) {
      //n.f1.accept(this, argu);
      //n.f3.accept(this, argu);
      String type1 = n.f0.accept(this, argu);
      String type2 = n.f2.accept(this, argu);

      //if(n.f0.accept(this, argu) != "int[]" ||  n.f2.accept(this, argu) != "int") {
      if(!type1.equals("int[]") || !type2.equals("int")) {

       System.out.println("error ArrayLookup " );
      }
      return "int";
   }



   /**
    * f0 -> <INTEGER_LITERAL>
    */
   public String visit(IntegerLiteral n, Map argu) {
      return "int";
   }

   /**
    * f0 -> "true"
    */
   public String visit(TrueLiteral n, Map argu) {
      return "boolean";
   }

   /**
    * f0 -> "false"
    */
   public String visit(FalseLiteral n, ClassForm argu) {
      return "boolean";
   }

   /**
   * f0 -> <IDENTIFIER>
   */
   public String visit(Identifier n, Map argu) {
     //System.out.println(n.f0.toString());
     return n.f0.toString();
   }




  //
  // public check(HashMap<String,ClassForm> oldTypes) {
  //   //System.out.println("OOOOOOK"+oldTypes);
  //   ClassTypes = new HashMap<String,ClassForm>(oldTypes);
  //   for(String keys : ClassTypes.keySet()) {
  //     System.out.println("\t" + keys);
  //     ClassForm M = ClassTypes.get(keys);
  //     System.out.println("\t" + M.ClassVars);
  //     System.out.println("\t" + M.Methods);
  //   }
  //  }

}
