import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.PostMethod;

import java.io.BufferedReader;
import java.io.InputStreamReader;


public class httpClient {

  public static void main(String args[]) {

    HttpClient client = new HttpClient();
    BufferedReader br = null;

    PostMethod method = new PostMethod("http://219.84.100.114:8080/servlet/shareup.do");
    method.addParameter("bible_no", "F0100101,F0100102,F0100103");
    method.addParameter("note", "How are you?");
    method.addParameter("user_name", "orozcohsu@gmail.com");

    try{
      int returnCode = client.executeMethod(method);

      if(returnCode == HttpStatus.SC_NOT_IMPLEMENTED) {
        System.err.println("The Post method is not implemented by this URI");
        // still consume the response body
        System.out.println(method.getResponseBodyAsString());
      } else {
        br = new BufferedReader(new InputStreamReader(method.getResponseBodyAsStream()));
        String readLine;
        while(((readLine = br.readLine()) != null)) {
          System.err.println(readLine);
      }
      }
    } catch (Exception e) {
      System.err.println(e);
    } finally {
      method.releaseConnection();
      if(br != null) try { br.close(); } catch (Exception fe) {}
    }

  }
}

