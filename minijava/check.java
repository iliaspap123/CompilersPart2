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
     //System.out.println("cur is "+currentClass);


     ClassForm classF = (ClassForm) argu.get(className);
     classCur = classF;
     n.f4.accept(this,classF.Methods);
     return "ok";
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

     ClassTypes = (HashMap) argu;

     String className = n.f1.accept(this, argu);
     currentClass = className;
     //System.out.println("cur is "+currentClass);
     ClassForm classF = (ClassForm) argu.get(className);
     classCur = classF;

     n.f6.accept(this,classF.Methods);

     //n.f7.accept(this, argu);
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

      String funct = n.f2.accept(this, null);
      MethodForm meth = (MethodForm) argu.get(funct);
      HashMap all_vars = new HashMap();
      if(meth != null ) {
        all_vars.putAll(meth.Arguments);
        all_vars.putAll(meth.Vars);
      }
      all_vars.putAll(classCur.ClassVars);

      n.f8.accept(this, all_vars);
      n.f10.accept(this, all_vars);
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
     * f0 -> "{"
     * f1 -> ( Statement() )*
     * f2 -> "}"
     */
    public String visit(Block n, Map argu) {
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
    public String visit(ArrayAssignmentStatement n, Map argu) {
       String ident = n.f0.accept(this, argu);
       String type;
       if((type = (String) argu.get(ident)) == null) {
         if((type = check_var(ident,currentClass)) == null) {
           System.out.println("peta error");
         }
       }
       String int_type2 = n.f2.accept(this, argu);
       String int_type3 = n.f5.accept(this, argu);
       if(!type.equals("int[]") || !int_type2.equals("int") || !int_type3.equals("int")) {
         System.out.println("error2 ArrayAssignmentStatement"+type+int_type2+int_type3);
       }
       return "ok";
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
     public String visit(IfStatement n, Map argu) {
        String type = n.f2.accept(this, argu);
        if(type.startsWith("class")) {
          String[] parts = type.split(" ");
          type = parts[1];
        }
        if(!type.startsWith("int") && !type.startsWith("boolean")) {
          System.out.println("if error "+ type);
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
     public String visit(WhileStatement n, Map argu) {
        String type = n.f2.accept(this, argu);
        if(type.startsWith("class")) {
          String[] parts = type.split(" ");
          type = parts[1];
        }
        if(!type.startsWith("int") && !type.startsWith("boolean")) {
          System.out.println("error while");
        }
        n.f3.accept(this, argu);
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
     public String visit(PrintStatement n, Map argu) {
        n.f2.accept(this, argu);
        return "print";
     }

   /**
    * f0 -> Identifier()
    * f1 -> "="
    * f2 -> Expression()
    * f3 -> ";"
    */
   public String visit(AssignmentStatement n, Map argu) {

      String ident = n.f0.accept(this, argu);
      String type;
      if((type = (String) argu.get(ident)) == null) {
        if((type = check_var(ident,currentClass)) == null) {
          System.out.println("peta error");
        }
      }
      String expr = n.f2.accept(this, argu);
      if(expr.startsWith("class") ) {
        String[] parts = expr.split(" ");
        expr = parts[1];
      }
      //System.out.println(type+" proerror "+expr);

      if( !type.equals(expr)) {
        System.out.println("AssignmentStatement: " +argu.get(ident)+" error "+expr);

      }
      else {
        //System.out.println("ok "+expr);
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
    //System.out.println("Expression " + argu);
     return n.f0.accept(this, argu);
  }

  /**
   * f0 -> PrimaryExpression()
   * f1 -> "."
   * f2 -> "length"
   */
  public String visit(ArrayLength n, Map argu) {
     n.f0.accept(this, argu);
     n.f1.accept(this, argu);
     n.f2.accept(this, argu);
     return "int";
  }

  /**
   * f0 -> PrimaryExpression()
   * f1 -> "*"
   * f2 -> PrimaryExpression()
   */
  public String visit(TimesExpression n, Map argu) {
     String type = n.f0.accept(this, argu);
     //n.f1.accept(this, argu);
     n.f2.accept(this, argu);
     return type;
  }


  /**
   * f0 -> Clause()
   * f1 -> "&&"
   * f2 -> Clause()
   */
  public String visit(AndExpression n, Map argu) {
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
    //System.out.println("PrimaryExpression1: "+argu);

     String type = n.f0.accept(this, argu);
     //System.out.println("PrimaryExpression: "+type);
     //System.out.println("PrimaryExpression2: "+argu);

     if(type!=null && type != "int" && type != "int[]" && type != "boolean" && !type.startsWith("this") && !type.startsWith("class")) {
       //type = (String) argu.get(type);
       //System.out.println(argu + " --- " + type);

       String type2;
       if((type2 = (String) argu.get(type)) == null) {
         //System.out.println(type+" peta error "+argu);

         if((type2 = check_var(type,currentClass)) == null) {
           System.out.println(type+" peta error "+type2);
         }
       }
       type = type2;
       //System.out.println("argu is "+argu);
       //System.out.println("tyoe is "+type);
     }
     if(type.startsWith("this") || type.startsWith("class")) {
       String[] parts = type.split(" ");
       type = parts[1];
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
  public String visit(MessageSend n, Map argu) {
     //System.out.println("MessageSend " + argu);
     String className = n.f0.accept(this, argu);
     if(className==null || className.equals("this")) {
       className = currentClass;
       //System.out.println("ok "+ currentClass);
     }
     else if(!ClassTypes.containsKey(className)){
       String type1;
       if((type1 = (String) argu.get(className)) == null) {
         if((type1 = check_var(className,currentClass)) == null) {
           System.out.println("peta error MessageSend");
         }
       }
       className = type1;
     }
     //System.out.println(className);
     String methName = n.f2.accept(this, argu);
     temp_args = new ArrayList<String>();
     n.f4.accept(this, argu);
     // for (String i : temp_args) {
     //  System.out.print(i);
     // }
     // System.out.println(" ]");
     //System.out.println("temp " + temp_args);
     String type = check_method_Call(methName,className,temp_args);
     temp_args.clear();
     //System.out.println("check meth is " + type);
     return "class " + type;
  }

  /**
   * f0 -> Expression()
   * f1 -> ExpressionTail()
   */
  public String visit(ExpressionList n, Map argu) {
     String type = n.f0.accept(this, argu);
     // System.out.println("type: "+type);
     temp_args.add(type);
     n.f1.accept(this, argu);
     return type;
  }

  /**
   * f0 -> ( ExpressionTerm() )*
   */
  public String visit(ExpressionTail n, Map argu) {
     return n.f0.accept(this, argu);
  }

  /**
   * f0 -> ","
   * f1 -> Expression()
   */
  public String visit(ExpressionTerm n, Map argu) {
     String type = n.f1.accept(this, argu);
     temp_args.add(type);
     return "ok" ;
  }

  /**
   * f0 -> "new"
   * f1 -> Identifier()
   * f2 -> "("
   * f3 -> ")"
   */
  public String visit(AllocationExpression n, Map argu) {
     //System.out.println("AllocationExpression");
     String objectName = n.f1.accept(this, argu);
     //System.out.println(objectName);
     if(!ClassTypes.containsKey(objectName)) {
       System.out.println("object does not exit" + objectName);
     }
     return "class "+objectName;
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
    String type = n.f1.accept(this, argu);
    if(type.startsWith("class")) {
      String[] parts = type.split(" ");
      type = parts[1];
    }
    if(!type.startsWith("int") && !type.startsWith("boolean")) {
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
     //System.out.println("BracketExpression");
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
      //System.out.println("ArrayAllocationExpression");
      if(n.f3.accept(this, argu) != "int" ) {
          System.out.println("error in array "+n.f3.accept(this, argu));
      }
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
      //System.out.println("INTEGER_LITERAL");
      return "int";
   }

   /**
    * f0 -> "true"
    */
   public String visit(TrueLiteral n, Map argu) {
     //System.out.println("TrueLiteral");

      return "boolean";
   }

   /**
    * f0 -> "false"
    */
   public String visit(FalseLiteral n, Map argu) {
     //System.out.println("TrueLiteral");

      return "boolean";
   }

   /**
   * f0 -> <IDENTIFIER>
   */
   public String visit(Identifier n, Map argu) {
     //System.out.println("Identifier");
     //System.out.println(n.f0.toString());
     return n.f0.toString();
   }

   /**
    * f0 -> "this"
    */
   public String visit(ThisExpression n, Map argu) {
       //System.out.println("ThisExpression");
       return "this " + currentClass;
      //return n.f0.toString();
   }


  String check_var(String var,String className) {
    //System.out.println("var " +var +" --- className "+ className);
    if(className == "main") {
      return null;
    }
    //System.out.println("funct: I will check in super_classes of "+ className);
    ClassForm classF = ClassTypes.get(className);
    if(classF.ClassVars.get(var) != null) {
      //System.out.println("found it " + var);
      return classF.ClassVars.get(var);
    }

    String superClass = classF.Isimpliments;
    while(superClass != null) {
      //System.out.println("check in superClass: "+superClass );
      classF = ClassTypes.get(superClass);

      if(classF.ClassVars.get(var) != null) {
        //System.out.println("found it " + var);
        return classF.ClassVars.get(var);
      }

      superClass = classF.Isimpliments;
    }
    return null;
  }

  /**
   * f0 -> MainClass()
   * f1 -> ( TypeDeclaration() )*
   * f2 -> <EOF>
   */
  public String visit(Goal n, Map argu) {
     ClassTypes = (HashMap) argu;
     n.f0.accept(this, argu);
     n.f1.accept(this, argu);
     n.f2.accept(this, argu);
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
  public String visit(MainClass n, Map argu) {
     currentClass = "main";
     // n.f0.accept(this, argu);
     // n.f1.accept(this, argu);
     // n.f2.accept(this, argu);
     // n.f3.accept(this, argu);
     // n.f4.accept(this, argu);
     // n.f5.accept(this, argu);
     // n.f6.accept(this, argu);
     // n.f7.accept(this, argu);
     // n.f8.accept(this, argu);
     // n.f9.accept(this, argu);
     // n.f10.accept(this, argu);
     // n.f11.accept(this, argu);
     // n.f12.accept(this, argu);
     // n.f13.accept(this, argu);
     // n.f14.accept(this, argu);
     // n.f15.accept(this, argu);
     // n.f16.accept(this, argu);
     // n.f17.accept(this, argu);

     return "main";
  }


  String check_method_Call(String meth,String className,ArrayList args) {
    //System.out.println(ClassTypes);
    //System.out.println(className);
    if(className == "main") {
      return "main";
    }
    ClassForm classF = ClassTypes.get(className);
    if(classF.Methods.get(meth) != null) {
      //System.out.println("found meth it " + meth);
      MethodForm M = classF.Methods.get(meth);
      //System.out.println(M.Arguments.values​() + "  " + args);

      if(M.Arguments.size() != args.size()) {
        System.out.println("method has different num of args");
      }

      int i = 0;
      for(String keys : M.Arguments.keySet()) {
        String type = M.Arguments.get(keys);
        if(!type.equals(args.get(i))) {
          System.out.println(type + " not equal "+ args.get(i));
        }
        i++;
      }
      return M.Type;
    }

    //System.out.println("funct: I will check in super_classes of "+ className);
    String superClass = classF.Isimpliments;
    while(superClass != null) {
      //System.out.println("check in superClass: "+superClass );
      classF = ClassTypes.get(superClass);

      if(classF.Methods.get(meth) != null) {
        //System.out.println("found it " + meth);
        MethodForm M = classF.Methods.get(meth);
        System.out.println(M.Arguments.values​() + "  " + args);
        if(M.Arguments.size() != args.size()) {
          System.out.println("method has different num of args");
        }
        int i = 0;
        for(String keys : M.Arguments.keySet()) {
          String type = M.Arguments.get(keys);
          if(!type.equals(args.get(i))) {
            System.out.println(type+" not equal "+args.get(i));
          }
          i++;
        }
        return M.Type;
      }

      superClass = classF.Isimpliments;
    }
    return null;
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
