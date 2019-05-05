import syntaxtree.*;
import visitor.GJDepthFirst;
import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.Arrays;

public class check extends GJDepthFirst<String, Map> {

  public HashMap<String,ClassForm> ClassTypes;
  ClassForm classCur;
  String currentClass;
  ArrayList<String> temp_args;

  /**
   * f0 -> "class"
   * f1 -> Identifier()
   * f2 -> "{"
   * f3 -> ( VarDeclaration() )*
   * f4 -> ( MethodDeclaration() )*
   * f5 -> "}"
   */
  public String visit(ClassDeclaration n, Map argu) throws Exception {

     ClassTypes = (HashMap) argu;

     String className = n.f1.accept(this, argu);
     currentClass = className;

     ClassForm classF = (ClassForm) argu.get(className);
     classCur = classF;
     if(n.f4.present()){
       n.f4.accept(this,classF.Methods); //accept methods
     }
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
  public String visit(ClassExtendsDeclaration n, Map argu) throws Exception {

     ClassTypes = (HashMap) argu;

     String className = n.f1.accept(this, argu);
     currentClass = className;

     ClassForm classF = (ClassForm) argu.get(className);
     classCur = classF;

     if(n.f6.present()) {
       n.f6.accept(this,classF.Methods);
     }

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
   public String visit(MethodDeclaration n, Map argu) throws Exception {
      String Type = n.f1.accept(this, null);
      String funct = n.f2.accept(this, null);
      MethodForm meth = (MethodForm) argu.get(funct);
      HashMap all_vars = new HashMap(); //
      if(meth != null ) {
        for(String key : meth.Arguments.keySet()) { // for all methods
          if(meth.Vars.containsKey(key)) {
            String Message = "variable "+key+ " is already defined in method";
            throw new MyException(currentClass,funct,Message);
          }
        }
        all_vars.putAll(meth.Arguments);
        all_vars.putAll(meth.Vars);
      }

      check_overload(currentClass,funct);

      if(n.f8.present()) {
        n.f8.accept(this, all_vars);
      }

      String return_type = n.f10.accept(this, all_vars);
      if(return_type.startsWith("class ") || return_type.startsWith("this ") ) {
        String[] parts = return_type.split(" ");
        return_type = parts[1];
      }
      if(!Type.equals(return_type) && !checkIfSuperclass(Type,return_type)) {
        String Message = "error: return type doesn't comply";
        throw new MyException(currentClass,funct,Message);
      }
      return funct;
   }

    /**
     * f0 -> "{"
     * f1 -> ( Statement() )*
     * f2 -> "}"
     */
    public String visit(Block n, Map argu) throws Exception {
       return n.f1.accept(this, argu);
    }

    /**
     * f0 -> Identifier()
     * f1 -> "["
     * f2 -> Expression()
     * f3 -> "]"
     * f4 -> "="
     * f5 -> Expression()
     * f6 -> ";"
     */
    public String visit(ArrayAssignmentStatement n, Map argu) throws Exception {
       String ident = n.f0.accept(this, argu);
       String type;
       if((type = (String) argu.get(ident)) == null) {
         if((type = check_var(ident,currentClass)) == null) {
           String Message = "error: variable "+type+" isn't declared" ;
           throw new MyException(currentClass,null,Message);
         }
       }
       String int_type2 = n.f2.accept(this, argu);
       if(int_type2.startsWith("this ") || int_type2.startsWith("class ")) {
         String[] parts = int_type2.split(" ");
         int_type2 = parts[1];
       }
       String int_type3 = n.f5.accept(this, argu);
       if(int_type3.startsWith("this ") || int_type3.startsWith("class ")) {
         String[] parts = int_type3.split(" ");
         int_type3 = parts[1];
       }
       if(!type.equals("int[]") || !int_type2.equals("int") || !int_type3.equals("int")) {
         String Message = "error: ArrayAssignmentStatement expecting int[int]";
         throw new MyException(currentClass,null,Message);

       }
       return null;
    }


     /**
      * f0 -> "if"
      * f1 -> "("
      * f2 -> Expression()
      * f3 -> ")"
      * f4 -> Statement()
      * f5 -> "else"
      * f6 -> Statement()
      */
     public String visit(IfStatement n, Map argu) throws Exception {
        String type = n.f2.accept(this, argu);
        if(type.startsWith("this ") || type.startsWith("class ")) {
          String[] parts = type.split(" ");
          type = parts[1];
        }

        if(!type.equals("boolean")) {
          String Message = "error: IfStatement expected boolean";
          throw new MyException(currentClass,null,Message);
        }
        n.f4.accept(this, argu);
        n.f6.accept(this, argu);
        return "if";
     }

     /**
      * f0 -> "while"
      * f1 -> "("
      * f2 -> Expression()
      * f3 -> ")"
      * f4 -> Statement()
      */
     public String visit(WhileStatement n, Map argu) throws Exception {
        String type = n.f2.accept(this, argu);
        if(type.startsWith("this ") || type.startsWith("class ")) {
          String[] parts = type.split(" ");
          type = parts[1];
        }
        if(!type.equals("boolean")) { //!type.startsWith("int") &&
          String Message = "error: WhileStatement expected boolean";
          throw new MyException(currentClass,null,Message);
        }
        n.f4.accept(this, argu);
        return "while";
     }

     /**
      * f0 -> "System.out.println"
      * f1 -> "("
      * f2 -> Expression()
      * f3 -> ")"
      * f4 -> ";"
      */
     public String visit(PrintStatement n, Map argu) throws Exception {
        String type = n.f2.accept(this, argu);
        if(type.startsWith("class ") || type.startsWith("this ")) {
          String[] parts = type.split(" ");
          type = parts[1];
        }
        if(!type.equals("boolean") && !type.equals("int")) {
          String Message = "error: in print PrintStatement";
          throw new MyException(currentClass,null,Message);
        }
        return "print";
     }

   /**
    * f0 -> Identifier()
    * f1 -> "="
    * f2 -> Expression()
    * f3 -> ";"
    */
   public String visit(AssignmentStatement n, Map argu) throws Exception {

      String ident = n.f0.accept(this, argu);
      String type;
      if((type = (String) argu.get(ident)) == null) {
        if((type = check_var(ident,currentClass)) == null) {
          String Message = "error: variable "+type+"isn't defined";
          throw new MyException(currentClass,null,Message);
        }
      }
      String expr = n.f2.accept(this, argu);
      if(expr.startsWith("class ") || expr.startsWith("this ") ) {
        String[] parts = expr.split(" ");
        expr = parts[1];
      }

      if( !type.equals(expr) && !checkIfSuperclass(type,expr)) {
        String Message = "error: "+argu.get(ident)+" doesn't comply with "+expr;
        throw new MyException(currentClass,null,Message);

      }
      return "AssignmentStatement";
   }


  /**
   * f0 -> PrimaryExpression()
   * f1 -> "."
   * f2 -> "length"
   */
  public String visit(ArrayLength n, Map argu) throws Exception {
     String type = n.f0.accept(this, argu);
     if(type.startsWith("this ") || type.startsWith("class ")) {
       String[] parts = type.split(" ");
       type = parts[1];
     }
     if(!type.equals("int[]")) {
       String Message = "error: expecting type int[] in ArrayLength";
       throw new MyException(currentClass,null,Message);
     }
     return "int";
  }

  /**
   * f0 -> PrimaryExpression()
   * f1 -> "*"
   * f2 -> PrimaryExpression()
   */
  public String visit(TimesExpression n, Map argu) throws Exception {
     String type1 = n.f0.accept(this, argu);
     if(type1.startsWith("this ") || type1.startsWith("class ")) {
       String[] parts = type1.split(" ");
       type1 = parts[1];
     }
     String type2 = n.f2.accept(this, argu);
     if(type2.startsWith("this ") || type2.startsWith("class ")) {
       String[] parts = type2.split(" ");
       type2 = parts[1];
     }
     if(!type1.equals("int") || !type2.equals("int")) {
       String Message = "error: expecting type int in TimesExpression";
       throw new MyException(currentClass,null,Message);
     }
     return "int";
  }


  /**
   * f0 -> Clause()
   * f1 -> "&&"
   * f2 -> Clause()
   */
  public String visit(AndExpression n, Map argu) throws Exception {
     String type1 = n.f0.accept(this, argu);
     String type2 = n.f2.accept(this, argu);
     if( !type1.equals("boolean") || !type2.equals("boolean")) {
       String Message = "error: expecting type boolean in AndExpression";
       throw new MyException(currentClass,null,Message);
     }
     return "boolean";
  }

  /**
   * f0 -> PrimaryExpression()
   * f1 -> "<"
   * f2 -> PrimaryExpression()
   */
  public String visit(CompareExpression n, Map argu) throws Exception {
     String type1 = n.f0.accept(this, argu);
     if(type1.startsWith("this ") || type1.startsWith("class ")) {
       String[] parts = type1.split(" ");
       type1 = parts[1];
     }
     String type2 = n.f2.accept(this, argu);
     if(type2.startsWith("this ") || type2.startsWith("class ")) {
       String[] parts = type2.split(" ");
       type2 = parts[1];
     }
     if( !type1.equals("int") ||  !type2.equals("int")) {
       String Message = "error: expecting type int in CompareExpression";
       throw new MyException(currentClass,null,Message);
     }
     return "boolean";
  }


 /**
  * f0 -> PrimaryExpression()
  * f1 -> "+"
  * f2 -> PrimaryExpression()
  */
 public String visit(PlusExpression n, Map argu) throws Exception {
   String type1 = n.f0.accept(this, argu);
   if(type1.startsWith("this ") || type1.startsWith("class ")) {
     String[] parts = type1.split(" ");
     type1 = parts[1];
   }
   String type2 = n.f2.accept(this, argu);
   if(type2.startsWith("this ") || type2.startsWith("class ")) {
     String[] parts = type2.split(" ");
     type2 = parts[1];
   }
   if( !type1.equals("int") ||  !type2.equals("int")) {
     String Message = "error: expecting type int in PlusExpression";
     throw new MyException(currentClass,null,Message);
   }
    return "int";
 }


  /**
   * f0 -> PrimaryExpression()
   * f1 -> "-"
   * f2 -> PrimaryExpression()
   */
  public String visit(MinusExpression n, Map argu) throws Exception {
    String type1 = n.f0.accept(this, argu);
    if(type1.startsWith("this ") || type1.startsWith("class ")) {
      String[] parts = type1.split(" ");
      type1 = parts[1];
    }
    String type2 = n.f2.accept(this, argu);
    if(type2.startsWith("this ") || type2.startsWith("class ")) {
      String[] parts = type2.split(" ");
      type2 = parts[1];
    }
    if( !type1.equals("int") ||  !type2.equals("int")) {
      String Message = "error: expecting type int in MinusExpression";
      throw new MyException(currentClass,null,Message);
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
  public String visit(PrimaryExpression n, Map argu) throws Exception {

     String type = n.f0.accept(this, argu);

     if(type != "int" && type != "int[]" && type != "boolean" && !type.startsWith("this ") && !type.startsWith("class ")) {

       String type2;
       if((type2 = (String) argu.get(type)) == null) {

         if((type2 = check_var(type,currentClass)) == null) {
           String Message = "error: Identifier "+type+" doesn't exist";
           throw new MyException(currentClass,null,Message);
         }
       }
       type = type2;

     }

     return type;
  }

  /**
   * f0 -> PrimaryExpression()
   * f1 -> "."
   * f2 -> Identifier()
   * f3 -> "("
   * f4 -> ( ExpressionList() )?
   * f5 -> ")"
   */
  public String visit(MessageSend n, Map argu) throws Exception {
     String className = n.f0.accept(this, argu);
     if(className.startsWith("this ") || className.startsWith("class ")) {
       String[] parts = className.split(" ");
       className = parts[1];
     }
     if(!ClassTypes.containsKey(className)){
       String type1;
       if((type1 = (String) argu.get(className)) == null) {
         if((type1 = check_var(className,currentClass)) == null) {
           String Message = "error: Identifier "+className+" doesn't exist";
           throw new MyException(currentClass,null,Message);
         }
       }
       className = type1;
     }
     String methName = n.f2.accept(this, argu);
     temp_args = new ArrayList<String>();
     n.f4.accept(this, argu);

     String type = check_method_Call(methName,className,temp_args);
     temp_args.clear();

     return "class " + type;
  }

  /**
   * f0 -> Expression()
   * f1 -> ExpressionTail()
   */
  public String visit(ExpressionList n, Map argu) throws Exception {
     String type = n.f0.accept(this, argu);
     if(type.startsWith("class ") || type.startsWith("this ")) {
       String[] parts = type.split(" ");
       type = parts[1];
     }
     temp_args.add(type);
     n.f1.accept(this, argu);
     return type;
  }


  /**
   * f0 -> ","
   * f1 -> Expression()
   */
  public String visit(ExpressionTerm n, Map argu) throws Exception {
     String type = n.f1.accept(this, argu);
     if(type.startsWith("class ") || type.startsWith("this ")) {
       String[] parts = type.split(" ");
       type = parts[1];
     }
     temp_args.add(type);
     return null;
  }

  /**
   * f0 -> "new"
   * f1 -> Identifier()
   * f2 -> "("
   * f3 -> ")"
   */
  public String visit(AllocationExpression n, Map argu) throws Exception {
     String objectName = n.f1.accept(this, argu);
     if(!ClassTypes.containsKey(objectName)) {
       System.out.println("error: object does not exit" + objectName);
     }
     return "class "+objectName;
  }

  /**
  * f0 -> "!"
  * f1 -> Clause()
  */
  public String visit(NotExpression n, Map argu) throws Exception {
    String type = n.f1.accept(this, argu);
    if(type.startsWith("class ")) {
      String[] parts = type.split(" ");
      type = parts[1];
    }
    if(!type.equals("boolean")) {
      String Message = "error: expected boolean in NotExpression";
      throw new MyException(currentClass,null,Message);
    }
    return "boolean";
  }

  /**
   * f0 -> "("
   * f1 -> Expression()
   * f2 -> ")"
   */
  public String visit(BracketExpression n, Map argu) throws Exception {
     return n.f1.accept(this, argu);
  }


   /**
    * f0 -> "new"
    * f1 -> "int"
    * f2 -> "["
    * f3 -> Expression()
    * f4 -> "]"
    */
   public String visit(ArrayAllocationExpression n, Map argu) throws Exception {
      String type = n.f3.accept(this, argu);
      if( !type.equals("int") ) {
          String Message = "error: expected int in ArrayAllocationExpression";
          throw new MyException(currentClass,null,Message);
      }
      return "int[]";
   }


   /**
    * f0 -> PrimaryExpression()
    * f1 -> "["
    * f2 -> PrimaryExpression()
    * f3 -> "]"
    */
   public String visit(ArrayLookup n, Map argu) throws Exception {
      String type1 = n.f0.accept(this, argu);
      if(type1.startsWith("this ") || type1.startsWith("class ")) {
        String[] parts = type1.split(" ");
        type1 = parts[1];
      }
      String type2 = n.f2.accept(this, argu);
      if(type2.startsWith("this ") || type2.startsWith("class ")) {
        String[] parts = type2.split(" ");
        type2 = parts[1];
      }
      if(!type1.equals("int[]") || !type2.equals("int")) {
       String Message = "error: expecting int[int] in ArrayLookup";
       throw new MyException(currentClass,null,Message);
      }

      return "int";
   }



   /**
    * f0 -> <INTEGER_LITERAL>
    */
   public String visit(IntegerLiteral n, Map argu) throws Exception {
      return "int";
   }

   /**
    * f0 -> "true"
    */
   public String visit(TrueLiteral n, Map argu) throws Exception {
      return "boolean";
   }

   /**
    * f0 -> "false"
    */
   public String visit(FalseLiteral n, Map argu) throws Exception {
      return "boolean";
   }

   /**
   * f0 -> <IDENTIFIER>
   */
   public String visit(Identifier n, Map argu) throws Exception {
     return n.f0.toString();
   }

   /**
    * f0 -> "this"
    */
   public String visit(ThisExpression n, Map argu) throws Exception {
       return "this " + currentClass;
   }


  String check_var(String var,String className) {
    /* check if var is definied */
    ClassForm classF = ClassTypes.get(className);
    if(classF.ClassVars.containsKey(var)) { //it definied in currentClass
      return classF.ClassVars.get(var);
    }

    String superClass = classF.Isimpliments;
    while(superClass != null) { //check in all super classes
      classF = ClassTypes.get(superClass);

      if(classF.ClassVars.get(var) != null) {
        return classF.ClassVars.get(var);
      }

      superClass = classF.Isimpliments;
    }
    return null;
  }


  /**
   * f0 -> "class"
   * f1 -> Identifier()
   * f2 -> "{"
   * f3 -> "public"
   * f4 -> "static"
   * f5 -> "void"
   * f6 -> "main"
   * f7 -> "("
   * f8 -> "String"
   * f9 -> "["
   * f10 -> "]"
   * f11 -> Identifier()
   * f12 -> ")"
   * f13 -> "{"
   * f14 -> ( VarDeclaration() )*
   * f15 -> ( Statement() )*
   * f16 -> "}"
   * f17 -> "}"
   */
  public String visit(MainClass n, Map argu) throws Exception {
     ClassTypes = (HashMap) argu;
     String className = n.f1.accept(this, argu);
     currentClass = className;

     ClassForm classF = (ClassForm) argu.get(className);
     classCur = classF;

     MethodForm meth= classF.Methods.get("main");

     HashMap all_vars = new HashMap();
     for(String key : meth.Arguments.keySet()) {
       if(meth.Vars.containsKey(key)) {
         String Message = "error: variable "+key+ " is already defined in method";
         throw new MyException(currentClass,"main",Message);
       }
     }
     all_vars.putAll(meth.Arguments);
     all_vars.putAll(meth.Vars);

     n.f15.accept(this, all_vars);
     return "main";
  }

  /**
   * f0 -> "int"
   * f1 -> "["
   * f2 -> "]"
   */
  public String visit(ArrayType n, Map argu) throws Exception {
     return "int[]";
  }

  /**
   * f0 -> "boolean"
   */
  public String visit(BooleanType n, Map argu) throws Exception {
     return n.f0.toString();
  }

  /**
   * f0 -> "int"
   */
  public String visit(IntegerType n, Map argu) throws Exception {
     return n.f0.toString();
  }


  void check_overload(String classA,String funct) throws Exception {
    /* check function for overload */
    ClassForm classF = ClassTypes.get(classA);
    MethodForm cur_meth = classF.Methods.get(funct);

    String superClass = classF.Isimpliments;
    while(superClass != null) {
      classF = ClassTypes.get(superClass);
      if(classF.Methods.containsKey(funct) ) { // function is declared in classF
        MethodForm meth = classF.Methods.get(funct);
        if(!meth.Arguments.keySet().equals(cur_meth.Arguments.keySet())) { // if functions have different args throw an error
          String Message = "error: minijava doesn't support overload";
          throw new MyException(classA,null,Message);
        }

      }
      superClass = classF.Isimpliments;
    }
  }


  Boolean checkIfSuperclass(String classA,String classB) throws Exception {
    /* if classA is superClass of classB return true */
    if(!ClassTypes.containsKey(classB)) {
      return false;
    }
    ClassForm classF = ClassTypes.get(classB);
    String superClass = classF.Isimpliments;
    while(superClass != null) {
      if(superClass.equals(classA)) {
        return true;
      }
      classF = ClassTypes.get(superClass);
      superClass = classF.Isimpliments;
    }
    return false;
  }

  String check_method_Call(String meth,String className,ArrayList args) throws Exception {
    /* check if method is declared returns type or null */
    ClassForm classF = ClassTypes.get(className);
    if(classF.Methods.get(meth) != null) {
      MethodForm M = classF.Methods.get(meth);

      if(M.Arguments.size() != args.size()) {
        System.out.println("error: method has different num of args");
        String Message = "error";
        throw new MyException(currentClass,meth,Message);
      }

      int i = 0;
      for(String keys : M.Arguments.keySet()) {
        String type = M.Arguments.get(keys);
        String arg = (String) args.get(i);
        if(!type.equals(arg) &&  !checkIfSuperclass(type,arg)) {
          System.out.println(type + " not equal "+ args.get(i));
          String Message = "error: not right arguments";
          throw new MyException(currentClass,meth,Message);
        }
        i++;
      }
      return M.Type;
    }

    String superClass = classF.Isimpliments;
    while(superClass != null) {
      classF = ClassTypes.get(superClass);

      if(classF.Methods.get(meth) != null) {
        MethodForm M = classF.Methods.get(meth);
        if(M.Arguments.size() != args.size() ) {
            String Message = "error: method has different num of args";
            throw new MyException(currentClass,meth,Message);
        }
        int i = 0;
        for(String keys : M.Arguments.keySet()) {
          String type = M.Arguments.get(keys);
          String arg = (String) args.get(i);
          if(!type.equals(arg) &&  !checkIfSuperclass(type,arg)) {
            String Message = "error: not right arguments";
            throw new MyException(currentClass,meth,Message);
          }
          i++;
        }
        return M.Type;
      }

      superClass = classF.Isimpliments;
    }
    return null;
  }

}
