import syntaxtree.*;
import visitor.*;
import java.io.*;
import java.util.HashMap;
import java.util.Map;



class Main {
    public static void main (String [] args){
	if(args.length < 1){
	    System.err.println("Usage: java Driver <inputFile>");
	    System.exit(1);
	}
	FileInputStream fis = null;
  for(String arg: args) {
  	try{
        System.out.println(arg);
  	    fis = new FileInputStream(arg);
  	    MiniJavaParser parser = new MiniJavaParser(fis);
  	    System.err.println("Program parsed successfully: "+arg);
  	    TableVisitor eval = new TableVisitor();
        Goal root = parser.Goal();
        root.accept(eval, null);
        check c = new check();//eval.ClassTypes
        root.accept(c, eval.ClassTypes);
        //System.out.println(" Var Elements");
        //System.out.println("\t" + eval.Table);
        //System.out.println(" Class Elements");
        // System.out.println("\t" + eval.ClassTypes);
        for(String keys : eval.ClassTypes.keySet()) {
          //System.out.println("\t" + keys);
          //System.out.println("\t" + eval.ClassTypes.get(keys));
          ClassForm M = eval.ClassTypes.get(keys);

          M.printAll();
          System.out.println("\t" + M.ClassVars);
          //System.out.println("\t" + M.Methods);
          // for(String keys2 : M.Methods.keySet()) {
          //   System.out.println("\t" + keys2);
          //   //System.out.println("\t" + eval.ClassTypes.get(keys));
          //   MethodForm meth = M.Methods.get(keys2);
          //   // for(String keys3 : meth.Arguments.keySet()) {
          //   //   System.out.println("\t" + keys3);
          //   // }
          //   System.out.println("\t" + meth.Arguments);
          //   System.out.println("\t" + meth.Vars);
          // }
        }
        //Goal root = parser.Goal();

  	}
  	catch(ParseException ex){
  	    System.err.println(ex.getMessage());
  	}
  	catch(FileNotFoundException ex){
  	    System.err.println(ex.getMessage());
  	}
    catch(Exception ex){
      System.err.println(ex.getMessage());
    }
  	finally{
  	    try{
  		if(fis != null) fis.close();
  	    }
  	    catch(IOException ex){
  		System.err.println(ex.getMessage());
  	    }
  	}
  }
    }
}
