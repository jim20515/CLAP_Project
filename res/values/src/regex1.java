import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class regex1 {
   public static void main(String[] args) 
   throws Exception {
	  
      Matcher m = Pattern.compile("(?i)(book)").matcher("this is a Book");
      
      
      while (m.find())
         System.out.println(m.group());
      	
   }
} 