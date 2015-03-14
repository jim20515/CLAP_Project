class ErrorCall {
	//object method
    int data;  // object variable
    public void objectMethod() {
        data = 10;
    }
    
    //class method
    public static void classMethod() {
        //objectMethod(); // Compile Error
        //data = 10; // Compile Error
    }
}
public class test2 {

	/**
	 * class method can't access object method
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
