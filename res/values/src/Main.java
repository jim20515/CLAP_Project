import java.io.BufferedInputStream;
import java.util.Scanner;

public class Main {  //要用Main當class名稱
 
  public static void main(String[] args) {
	  
    Scanner in = new Scanner(new BufferedInputStream(System.in));
    
    while (in.hasNextInt()) {  
    	
      int a = in.nextInt(), b = in.nextInt(), max = 0, temp = 0;
      
      for (int i = (a < b) ? a : b; i <= a || i <= b; ++i) {
        if ((temp = cycleLength(i, 1)) > max)
          max = temp;
      }
      System.out.println(a + " " + b + " " + max);  //只需印出答案即可
    }
  }
 
  public static int cycleLength(int num, int len) {
    if (num == 1)
      return len;
    return cycleLength(((num & 1) == 1) ? (3 * num + 1) : (num >> 1), ++len);
  }
 
}