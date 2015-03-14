package com.example.plpa.project;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

public class Powercheck extends BroadcastReceiver {
	
	public static final String POWER_CHECKER = "powercheck";
	private static final String TAG = "Mytest";
	@Override
	public void onReceive(Context context, Intent intent) {

		 if(intent.getAction().toString().equals(POWER_CHECKER))
		    {
		     		      
		      /* 呼叫母體Activity，喚醒原主程式 */
		      Intent mRunPackageIntent = new Intent(context, PLPA_CLAP.class); 
		      mRunPackageIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		     
		        mRunPackageIntent.putExtra("power", "powerdown");
		       
		      context.startActivity(mRunPackageIntent);
		    
		  } 

		}

}
