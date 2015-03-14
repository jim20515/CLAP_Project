import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class regex2 {
   public static void main(String[] args) throws Exception {
	   
	   String s = "well, well, well, look who's here...";
	   String v = "well, " +
	   		"";
	   
	   s = s.replaceAll("(?:" + Pattern.quote(v) + "){2,}", Matcher.quoteReplacement(v));
	   
	   System.out.println(s);
	   System.out.println("haaa".matches("ha{3}"));
      	
   }
} 