package com.example.plpa.project;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;


public class StartupIntent extends BroadcastReceiver {
	
	//static final String ACTION = "android.intent.action.BOOT_COMPLETED";
	@Override
	public void onReceive(Context context, Intent intent) {

		// 監聽android.intent.action.BOOT_COMPLETED , 開機後自動把服務在背景裡執行
		//if (intent.getAction().equals(ACTION)){
		context.startService(new Intent(context, LogService.class));
            /*
			Intent mt = new Intent(context, LogService.class);
			mt.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startActivity(mt);
			}
		     */
		
	}

}
