import java.util.Arrays;

public class test {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		int[] arr1 = {1, 2, 3, 4, 5}; 
	    int[] arr2 = Arrays.copyOf(arr1, arr1.length);
	    int[] arr3 = arr1;
	    
		System.out.println(arr1);
		System.out.println(arr2);
		System.out.println(arr3);
		
		String aa = new String("test");
		String bb = "test";
		
		System.out.println(aa.equals(bb));
		
		System.out.println($(10));
		
		
	}
	public static int $(int a) {
		return 10;
	}

}
