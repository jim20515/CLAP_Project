package com.example.plpa.project;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;


public class UpdateCheck extends BroadcastReceiver {
	
	public static final String HIPPO_SERVICE_IDENTIFIER = "HIPPO_ON_SERVICE_001";
	private static final String TAG = "Mytest";
	@Override
	public void onReceive(Context context, Intent intent) {

		 if(intent.getAction().toString().equals(HIPPO_SERVICE_IDENTIFIER))
		    {
		      /* 以Bundle物件解開傳來的參數 */
		      Bundle mBundle01 = intent.getExtras();
		      String strParam1="";
		      String strParam2="";
		      String strParam3="";
		      /* 若Bundle物件不為空值，取出參數 */
		      if (mBundle01 != null)
		      {
		        /* 將取出的STR_PARAM01參數，存放於strParam1字串中 */
		        strParam1 = mBundle01.getString("testID");
		        strParam2 = mBundle01.getString("testScript");
		        strParam3 = mBundle01.getString("testDescript");
		        Log.v(TAG,"UpdateCheck"+strParam1+strParam2+strParam3);
		      }
		      
		      /* 呼叫母體Activity，喚醒原主程式 */
		      Intent mRunPackageIntent = new Intent(context, PLPA_CLAP.class); 
		      mRunPackageIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		      if(strParam1!="")
		      {
		        /* 重新實驗ID回傳 */
		        mRunPackageIntent.putExtra("OriginTestId", strParam1);
		        mRunPackageIntent.putExtra("NewTestScript", strParam2);
		        mRunPackageIntent.putExtra("NewTestDescript", strParam3);
		      }
		      else
		      {
		        mRunPackageIntent.putExtra("OriginTestId", "From Service notification...");
		      }
		      context.startActivity(mRunPackageIntent);
		    }
		  } 

		}

