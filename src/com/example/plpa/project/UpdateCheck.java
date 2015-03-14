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
		      /* �HBundle����Ѷ}�ǨӪ��Ѽ� */
		      Bundle mBundle01 = intent.getExtras();
		      String strParam1="";
		      String strParam2="";
		      String strParam3="";
		      /* �YBundle���󤣬��ŭȡA���X�Ѽ� */
		      if (mBundle01 != null)
		      {
		        /* �N���X��STR_PARAM01�ѼơA�s���strParam1�r�ꤤ */
		        strParam1 = mBundle01.getString("testID");
		        strParam2 = mBundle01.getString("testScript");
		        strParam3 = mBundle01.getString("testDescript");
		        Log.v(TAG,"UpdateCheck"+strParam1+strParam2+strParam3);
		      }
		      
		      /* �I�s����Activity�A�����D�{�� */
		      Intent mRunPackageIntent = new Intent(context, PLPA_CLAP.class); 
		      mRunPackageIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		      if(strParam1!="")
		      {
		        /* ���s����ID�^�� */
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

