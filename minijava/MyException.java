
public class MyException extends Exception {
  String ClassName;
  String MethodName;
  String Message;

  public MyException(String myClass, String myMeth, String myMess) {
    ClassName = myClass;
    MethodName = myMeth;
    Message = myMess;
  }

  public String getMessage() {
    //System.err.println("In class: " + ClassName + " In Method: " + MethodName + " with message: " + Message);
    String Myerror = "In class: " + ClassName + " In Method: " + MethodName + " with message: " + Message;
    return Myerror;
  }


}
