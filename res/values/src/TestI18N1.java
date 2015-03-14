

import java.text.NumberFormat;
import java.util.*;
import java.text.*;

public class TestI18N1 {
    private Locale curLocale;

    public TestI18N1(String language, String country){
        curLocale = new Locale(language, country);

    }

    public String getMessage1(String id){
        ResourceBundle messages =
        ResourceBundle.getBundle("MessagesBundle", curLocale);

        return messages.getString(id);

    }

    public String getNumberDisplay1(double num){
        NumberFormat nf = NumberFormat.getNumberInstance(curLocale);

        return String.valueOf(nf.format(num));

    }

    public String getCurrencyDisplay1(double num) {
        NumberFormat cf = NumberFormat.getCurrencyInstance(curLocale);

        return String.valueOf(cf.format(num));

    }

    public String getDateDisplay1(Date date){
        DateFormat df = DateFormat.getDateInstance(

        DateFormat.FULL,curLocale);

        return df.format(date);

    }

    public static void main(String[] args) {
        TestI18N1 ap = new TestI18N1("en","US"); //USA

        System.out.println(ap.getMessage1("msgid1"));

        System.out.println(ap.getMessage1("msgid2"));

        System.out.println(ap.getMessage1("msgid3"));

        System.out.println(ap.getNumberDisplay1(100));

        System.out.println(ap.getNumberDisplay1(10.22));

        System.out.println(ap.getCurrencyDisplay1(10223.22));

        System.out.println(ap.getCurrencyDisplay1(1022));

        System.out.println(ap.getDateDisplay1(new Date()));


        ap = new TestI18N1("zh","TW"); //Taiwan

        System.out.println(ap.getMessage1("msgid1"));

        System.out.println(ap.getMessage1("msgid2"));

        System.out.println(ap.getMessage1("msgid3"));

        System.out.println(ap.getNumberDisplay1(100));

        System.out.println(ap.getNumberDisplay1(10.22));

        System.out.println(ap.getCurrencyDisplay1(10223.22));

        System.out.println(ap.getCurrencyDisplay1(1022));

        System.out.println(ap.getDateDisplay1(new Date()));

    }
}

