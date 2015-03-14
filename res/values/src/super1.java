class kk{
	
}
class pp1 extends kk{
	
	public String toString() {
        return super.toString();
    }
}
class yy extends pp1{
	
	public String ok(){
		return "test";
	}
}
public class super1{

	public static void main(String[] args) throws ClassNotFoundException {
		pp1 yy1 = new pp1();
		Class c = yy1.getClass();

		java.lang.reflect.Method[] ff = c.getMethods() ;
		for(java.lang.reflect.Method aa: ff){
			System.out.println(aa);
		}
		
		System.out.println("-----------");
		
		 Class ccc = Class.forName("java.lang.Class");
		 java.lang.reflect.Method m[] = ccc.getMethods() ;
         for (int i = 0; i < m.length; i++)
         System.out.println(m[i].toString());
         
         
         System.out.println("--------");
         Class cd = yy.class;
         java.lang.reflect.Method[] methods = cd.getDeclaredMethods();
         
         for (java.lang.reflect.Method method : methods) {
             System.out.println(method.getName());
         }
	}

}
