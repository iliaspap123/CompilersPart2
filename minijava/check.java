import syntaxtree.*;
import visitor.GJDepthFirst;
import java.util.HashMap;
import java.util.Map;



public class check extends GJDepthFirst<String, ClassForm> {

  public HashMap<String,ClassForm> ClassTypes;

  /**
   * f0 -> "class"
   * f1 -> Identifier()
   * f2 -> "{"
   * f3 -> ( VarDeclaration() )*
   * f4 -> ( MethodDeclaration() )*
   * f5 -> "}"
   */
  public String visit(ClassDeclaration n, ClassForm argu) {



     String className = n.f1.accept(this, argu);
     ClassForm classF = (ClassForm) argu.get(className);
     //n.f3.accept(this, argu);
     //System.out.println("HA" + classF.Methods);

     HashMap all_vars = new HashMap();
     // all_vars.putAll(classF.Methods.Vars);
     // all_vars.putAll(classF.Methods.Arguments);
     // all_vars.putAll(classF.ClassVars);
      n.f4.accept(this,classF);
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
   public String visit(MethodDeclaration n, ClassForm argu) {
      //n.f1.accept(this, argu);
      //String funct = n.f2.accept(this, argu);
      //n.f3.accept(this, argu);
      //HashMap<String,String> vars = (HashMap) argu.get(funct);
      //System.out.println(funct + " " + vars);
      //n.f4.accept(this, argu);
      //n.f5.accept(this, argu);
      //n.f6.accept(this, argu);
      //n.f7.accept(this, argu);
      n.f8.accept(this, argu);
      //n.f9.accept(this, argu);
      n.f10.accept(this, argu);
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
    public String visit(Statement n, ClassForm argu) {
       return n.f0.accept(this, argu);
    }



   /**
    * f0 -> Identifier()
    * f1 -> "="
    * f2 -> Expression()
    * f3 -> ";"
    */
   public String visit(AssignmentStatement n, ClassForm argu) {

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
     * f0 -> IntegerLiteral()
     *       | TrueLiteral()
     *       | FalseLiteral()
     *       | Identifier()
     *       | ThisExpression()
     *       | ArrayAllocationExpression()
     *       | AllocationExpression()
     *       | BracketExpression()
     */
    public String visit(PrimaryExpression n, ClassForm argu) {
       return n.f0.accept(this, argu);
    }




     /**
      * f0 -> "new"
      * f1 -> "int"
      * f2 -> "["
      * f3 -> Expression()
      * f4 -> "]"
      */
     public String visit(ArrayAllocationExpression n, ClassForm argu) {
        n.f0.accept(this, argu);
        n.f1.accept(this, argu);
        n.f2.accept(this, argu);
        if(n.f3.accept(this, argu) != "int" ) {
            System.out.println("in array "+n.f3.accept(this, argu));
        }
        n.f4.accept(this, argu);
        return "int[]";
     }

   /**
    * f0 -> <INTEGER_LITERAL>
    */
   public String visit(IntegerLiteral n, ClassForm argu) {
      return "int";
   }

   /**
    * f0 -> "true"
    */
   public String visit(TrueLiteral n, ClassForm argu) {
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
   public String visit(Identifier n, ClassForm argu) {
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
