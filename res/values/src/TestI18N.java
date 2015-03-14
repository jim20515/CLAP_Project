import java.io.UnsupportedEncodingException;
import java.util.*;


public class TestI18N {
    private Locale curLocale;

    public TestI18N(String language, String country) {
    	curLocale = new Locale(language, country);
    }

    public String getMessage(String id) {
    	ResourceBundle messages = ResourceBundle.getBundle("MessagesBundle",
				curLocale);
        return messages.getString(id);
    }

    public static void main(String[] args) throws UnsupportedEncodingException {
        TestI18N ap = new TestI18N("en", "US"); // USA
        System.out.println(ap.getMessage("msgid1"));
        System.out.println(ap.getMessage("msgid2"));
        System.out.println(ap.getMessage("msgid3"));
      

        ap = new TestI18N("zh", "TW"); // Taiwan
        System.out.println(ap.getMessage("msgid1"));
        System.out.println(ap.getMessage("msgid2"));
        System.out.println(ap.getMessage("msgid3"));
        
        String text = java.net.URLEncoder.encode("ªL", "UTF-8");
        System.out.println(text);
        
        String text1 = java.net.URLDecoder.decode("%E6%9E%97", "ISO-8859-1");
        System.out.println(text1);
    }
}