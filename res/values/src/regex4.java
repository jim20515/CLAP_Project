import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class regex4 {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		regex4 a = new regex4();
		System.out.println("test validation: " + a.IsValidDate("2012-12-15"));

	}
	boolean IsValidDate(String inputString)
	{            
		Matcher m = Pattern.compile("^(19|20)\\d\\d[-/.](0[1-9]|1[012])[-/.](0[1-9]|[12][0-9]|3[01])$").matcher(inputString);
		
		while (m.find())
			System.out.println("test: " + m.group());
		 
	    if (m.matches())
	    	
	    {
	        int year = Integer.parseInt(m.group(1));
	        int month = Integer.parseInt(m.group(2));
	        int day = Integer.parseInt(m.group(3));
	        if (day == 31 && (month == 4 || month == 6 || month == 9 || month == 11)) 
	        {
	            return false;
	        } 
	        else if (day >= 30 && month == 2) 
	        {
	            return false;
	        } 
	        else if (month == 2 && day == 29 
	            && !(year % 4 == 0 && (year % 100 != 0 || year % 400 == 0))) 
	        {
	            return false; 
	        } 
	        else 
	        {
	            return true; 
	        }
	    }
	    return false;        
	}

}
