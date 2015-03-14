package com.example.plpa.project;


import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import android.os.AsyncTask;

public class readREST extends AsyncTask<String, Void , String>{
	 
	@Override
	protected void onProgressUpdate(Void... values) {
		super.onProgressUpdate(values);
	}
	 
	@Override
	protected void onPreExecute() {
		//before doing doInBackground, it will call this method
		super.onPreExecute();
	}
	 
	@Override
	protected void onPostExecute(String result) {
		//after finishing doInBackground it will call this method
	  super.onPostExecute(result);
	}
	
	//String... strURLFile means the paramter can be empty or more as a array
	protected String doInBackground(String... strUrlFile) {
		
		URL urlRESTLocation = null;
		HttpURLConnection connREST = null;
		StringBuffer out = null;
		try {
			int iUrlCount = strUrlFile.length;
			for (int i =0 ; i < iUrlCount ; i++) {
				urlRESTLocation = new URL(strUrlFile[i]);   
				connREST = (HttpURLConnection) urlRESTLocation.openConnection();   
				connREST.setDoInput(true);
	    
			    InputStream in = connREST.getInputStream();
			    out = new StringBuffer();
	    
			    byte[] buffer = new byte[4096];
			    int n = 1;
	    
			    while ( (n = in.read(buffer)) != -1) {
			    	out.append(new String(buffer, 0, n));
			    }
			} 
	   } catch (Exception e) {
	   }
	   try {
		   Thread.sleep(1000);
	   } catch (InterruptedException e) {
		   e.printStackTrace();
	   }
	   return out.toString();
	}
}

