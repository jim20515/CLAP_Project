interface ParentInterface{  
	void myMethod();  
}  
       
interface SubInterface extends ParentInterface{  
	void anotherMethod();  
} 

class pp implements SubInterface{

	@Override
	public void myMethod() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void anotherMethod() {
		// TODO Auto-generated method stub
		
	}
	
}

public class interfaceExtend {

	public static void main(String[] args) {
	}

}
