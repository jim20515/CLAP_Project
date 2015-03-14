package com.biblesharing;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.PostMethod;


public class postInsert {
	
	postInsert(String url, String bible_no, String note, String user_name) {
		
		 HttpClient client = new HttpClient();
		    BufferedReader br = null;
		    
		    client.getParams().setContentCharset("UTF-8");
		    
		    //PostMethod method = new PostMethod("http://219.84.100.114:8080/servlet/shareup.do");
		    PostMethod method = new PostMethod(url);
		    
		    //method.addParameter("bible_no", "F0100101,F0100102,F0100103");
		    method.addParameter("bible_no", bible_no);
		    //method.addParameter("note", "How are you?");
		    method.addParameter("note", note);
		    //method.addParameter("user_name", "orozcohsu@gmail.com");
		    method.addParameter("user_name", user_name);

		    try{
		      int returnCode = client.executeMethod(method);
		      System.out.println("returnCode:" + returnCode);
		      if(returnCode == HttpStatus.SC_NOT_IMPLEMENTED) {
		        System.err.println("The Post method is not implemented by this URI");
		        // still consume the response body
		        method.getResponseBodyAsString();
		      } else {
		    	  System.out.println("in the else");
		        br = new BufferedReader(new InputStreamReader(method.getResponseBodyAsStream()));
		        String readLine;
		        while(((readLine = br.readLine()) != null)) {
		          System.out.println("here:" + readLine);
		        }
		        System.out.println("in the end");
		      }
		    } catch (Exception e) {
		      System.err.println("err:" + e);
		    } finally {
		      method.releaseConnection();
		      if(br != null) try { br.close(); } catch (Exception fe) {}
		    }
		  }
}
