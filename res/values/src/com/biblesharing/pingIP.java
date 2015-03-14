package com.biblesharing;

import java.io.IOException;

public class pingIP {
	
	public boolean isConnection() {
		
		Process p1 = null;
		try {
			p1 = java.lang.Runtime.getRuntime().exec("ping -c 1 219.84.100.114");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		int returnVal = 0;
		try {
			returnVal = p1.waitFor();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		boolean reachable = (returnVal==0);
		
		return reachable;
	}

}




