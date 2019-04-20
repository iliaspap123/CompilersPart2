import syntaxtree.*;
import visitor.*;
import java.io.*;
import java.util.HashMap;
import java.util.Map;



class Main {
    public static void main (String [] args){
	if(args.length != 1){
	    System.err.println("Usage: java Driver <inputFile>");
	    System.exit(1);
	}
	FileInputStream fis = null;
	try{
	    fis = new FileInputStream(args[0]);
	    MiniJavaParser parser = new MiniJavaParser(fis);
	    System.err.println("Program parsed successfully.");
	    TableVisitor eval = new TableVisitor();
      Goal root = parser.Goal();
      System.out.println(root.accept(eval, null));
      System.out.println(" Var Elements");
      System.out.println("\t" + eval.Table);
      System.out.println(" Class Elements");
      // System.out.println("\t" + eval.ClassTypes);
      for(String keys : eval.ClassTypes.keySet()) {
        System.out.println("\t" + keys);
        //System.out.println("\t" + eval.ClassTypes.get(keys));
        ClassForm M = eval.ClassTypes.get(keys);
        System.out.println("\t" + M.ClassVars);
        System.out.println("\t" + M.Methods);
      }

	}
	catch(ParseException ex){
	    System.out.println(ex.getMessage());
	}
	catch(FileNotFoundException ex){
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
