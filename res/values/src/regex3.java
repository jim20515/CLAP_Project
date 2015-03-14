
import java.io.*;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
 
public class regex3 {
    public static void main(String[] args) {
    	
        File file = new File("src/test.txt");
        
        StringBuilder contents = new StringBuilder();
        BufferedReader reader = null;
 
        try {
            reader = new BufferedReader(new FileReader(file));
            String text = null;
 
            // repeat until all lines is read
            while ((text = reader.readLine()) != null) {
                contents.append(text).append(System.getProperty("line.separator"));
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        
        String DSLString = contents.toString().replace(",","").replace(" ", "");
        
        if(IsValidDSL(DSLString) == true) {
        	System.out.println("it's valid");
        	AccTokenizer(DSLString);
        	locTokenizer(DSLString);
        	speedTokenizer(DSLString);
        	intervalTokenizer(DSLString);
        } else {
        	System.out.println("it's invalid");
        }
        
    }
    static boolean IsValidDSL(String inputString) {            
    	Pattern p = Pattern.compile("^\\blog\\s*(acc.[x|y|z]\\s*){0,3}|(gps.[longitute|latitude]*\\s*){0,2}(where\\s*)(gps.speed[>|=|<]+\\d+\\s*)(every\\s*){1,}(\\d+)$", Pattern.MULTILINE|Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(inputString);
        
		while(m.find()) {
			if(m.group(0).length() != 0) {
				return true;
			}
		} 
	    return false;        
	}
    static void AccTokenizer(String inputString) {
    	Pattern p = Pattern.compile("(acc.[x|y|z]\\s*)", Pattern.MULTILINE|Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(inputString.replace(",","").replace(" ", ""));
        
        Vector<String> vv = new Vector<String>();
        
		while(m.find()) {
			vv.add(m.group(1).replace("\n", "").replace("\r", ""));
			//System.out.println(m.group(1));
		} 
		for(String ss: vv){
			System.out.println("acc -> " + ss);
		}
    }
    static void locTokenizer(String inputString) {
    	Pattern p = Pattern.compile("(gps.longitute|gps.latitude\\s*)", Pattern.MULTILINE|Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(inputString.replace(",","").replace(" ", ""));
        
        Vector<String> vv = new Vector<String>();
        
		while(m.find()) {
			vv.add(m.group(1).replace("\n", "").replace("\r", ""));
			//System.out.println(m.group(1));
		} 
		for(String ss: vv){
			System.out.println("gps -> " + ss);
		}
    }
    static void speedTokenizer(String inputString) {
    	
    	Pattern p = Pattern.compile("(gps.speed[>|=|<]+\\d+)", Pattern.MULTILINE|Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(inputString.replace(",","").replace(" ", ""));
  
		while(m.find()) {
			
			Pattern p1 = Pattern.compile("[>|=|<]+", Pattern.MULTILINE|Pattern.CASE_INSENSITIVE);
	        Matcher m1 = p1.matcher(m.group(1));
	        
	        while(m1.find()) {
	        	System.out.println("operator -> " + m1.group(0));
	        }
	        
	        Pattern p2 = Pattern.compile("\\d+", Pattern.MULTILINE|Pattern.CASE_INSENSITIVE);
	        Matcher m2 = p2.matcher(m.group(1));
	        
	        while(m2.find()) {
	        	System.out.println("speed -> " + m2.group(0));
	        }
		} 	
    }
    static void intervalTokenizer(String inputString) {
    	
    	Pattern p = Pattern.compile("(every\\s*\\d+)", Pattern.MULTILINE|Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(inputString.replace(",","").replace(" ", ""));
        
        while(m.find()) {
        	//System.out.println(m.group(0));
        	Pattern p1 = Pattern.compile("\\d+", Pattern.MULTILINE|Pattern.CASE_INSENSITIVE);
	        Matcher m1 = p1.matcher(m.group(1));
	        
	        while(m1.find()) {
	        	System.out.println("interval -> " + m1.group(0));
	        }
        }
    }
}