package com.example.plpa.project;


import static com.example.plpa.project.DbConstants.*;
import static com.example.plpa.project.PLPA_CLAP.PREF_FILENAME;
import static com.example.plpa.project.PLPA_CLAP.PREF_CALL;
import static com.example.plpa.project.PLPA_CLAP.ID_CHECK;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.channels.FileChannel;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.HttpEntity;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Base64;

//import com.example.plpa.project.PLPA_CLAP.EventReceiver;
//import com.example.plpa.project.PLPA_CLAP.exPhoneCallListener;

//import com.example.plpa.project.PLPA_CLAP.EventReceiver;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import android.app.AlertDialog;
import android.app.Service;
import android.app.AlertDialog.Builder;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.preference.PreferenceManager;
import android.provider.Browser;
import android.provider.CallLog;
import android.provider.ContactsContract;
import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;
import android.telephony.SmsMessage;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Toast;



public class LogService extends Service implements LocationListener,SensorEventListener{

	public static SensorManager sensorManager=null;
	private Sensor sensor;
	public static LocationManager locationManager;
	public static LinkedList<Location> locList;
	
	private String[] experimentIdList;
	private String[] experimentNameList;
	private String[] experimentDescriptionList;
	private String[] experimentScriptList;	
	private String[] clientdeviceIdList;
	private String[] authcodeList;
	
	public static int expid = 0;
	public static int fgroup = 0;
	//public static int delgroup = 0;
	private static final String TAG = "Mytest";
	public static Handler mThreadHandler;
	public static HandlerThread mThread;
	public static DBHelper dbhelper = null;
	public static String sqlCase = "";
    public String Strvalue;
	public float ax,ay,az,ox,oy,oz,mx,my,mz,px,temp,li,pres,speed,accuracy,bearing;
	public double latitude,longitude,altitude;
	public String date= "";
	public String todayTime;
	public String rssi="";
	public String ssid ="";
	public String screen ="";
	public String extmedia ="";
	public String lan ="";
	public String country ="";
	public String msgfrom ="";
	public String msgBody ="";
	public String pkgname ="";
	public String apkact ="";
	public String powact ="";
	public String language ="";
	public String callstate ="";   //call
	public String callnumber ="";  //call
	public String receivenumber ="";  //call
	public String callcontact ="";  //call
	public String title = "";
	public String url = "";
	public String contents = "";
	public int incomingcountgap,outcomingcountgap,inalltimegap,outalltimegap,incomingcount,outcomingcount;
	public long INTERVAL,AXINTERVAL,AYINTERVAL,AZINTERVAL,OXINTERVAL,OYINTERVAL,OZINTERVAL,MXINTERVAL,MYINTERVAL,MZINTERVAL,
	PXINTERVAL,LIINTERVAL,TEMPINTERVAL,PRESINTERVAL,LONGITUDEINTERVAL,LATITUDEINTERVAL,SPEEDINTERVAL,BEARINGINTERVAL,ALTITUDEINTERVAL,ACCURACYINTERVAL,
	CALLINTIMESINTERVAL,CALLOUTTIMESINTERVAL,CALLINALLTIMEINTERVAL,CALLOUTALLTIMESINTERVAL,MSGTIMESINTERVAL,RSSIINTERVAL,SSIDINTERVAL,GSMRSSIINTERVAL,GSMSPNINTERVAL,
	CALLNUMBERINTERVAL,RECEIVERNUMBERINTERVAL,POWERINTERVAL;
	static final String mSMSAction = "android.provider.Telephony.SMS_RECEIVED";
	public long time = new Date().getTime();
	public EventReceiver EReceiver,ScreenReceiver,MediaReceiver,PackageReceiver,PowerconnectReceiver,LocaleReceiver;
	public EventReceiver WifiReceiver;
	public PEventReceiver PReceiver;
	
	//private static final String PREF_FILENAME = "project.plpa.example.com";
	public boolean Bacc,Bori,Bmagn,Bpres,Bli,Bpx,Btemp,Bwifi,Bbrowser,Bloc,Bcall,Bsms,Blocale,Bpow,Bapp,Bmedia,Bscreen,Bgsm,Bcallact,Bpower = false;
	public boolean btax,btay,btaz,btox,btoy,btoz,btmx,btmy,btmz,btpx,bttemp,btlight,btpressure,btlongitude,btlatitude,btspeed,btaltitude,btaccuracy,btbearing = false;
	public String axbigop,axlessop,aybigop,azbigop,oxbigop,oybigop,ozbigop,mxbigop,mybigop,mzbigop,pxbigop,tempbigop,lightbigop,pressurebigop,speedbigop,altitudebigop,bearingbigop,accuracybigop,longitudebigop,latitudebigop,
	aylessop,azlessop,oxlessop,oylessop,ozlessop,mxlessop,mylessop,mzlessop,pxlessop,templessop,lightlessop,pressurelessop,speedlessop,altitudelessop,bearinglessop,accuracylessop,longitudelessop,latitudelessop;
	public String axbigvalue,axlessvalue,aybigvalue,azbigvalue,oxbigvalue,oybigvalue,ozbigvalue,mxbigvalue,mybigvalue,mzbigvalue,pxbigvalue,tempbigvalue,lightbigvalue,pressurebigvalue,speedbigvalue,altitudebigvalue,bearingbigvalue,accuracybigvalue,longitudebigvalue,latitudebigvalue,
	aylessvalue,azlessvalue,oxlessvalue,oylessvalue,ozlessvalue,mxlessvalue,mylessvalue,mzlessvalue,pxlessvalue,templessvalue,lightlessvalue,pressurelessvalue,speedlessvalue,altitudelessvalue,bearinglessvalue,accuracylessvalue,longitudelessvalue,latitudelessvalue;
	public boolean fupdate = false;
	public boolean fpowerdown = false;
	public boolean tax;
	public boolean tay;
	public boolean taz;
	public boolean tox;
	public boolean toy;
	public boolean toz;
	public boolean tmx;
	public boolean tmy;
	public boolean tmz;
	public boolean tpx;
	public boolean ttemp;
	public boolean tlight;
	public boolean tpressure;
	public boolean tlongitude;
	public boolean tlatitude;
	public boolean tspeed;
	public boolean taltitude;
	public boolean taccuracy;
	public boolean trssi;
	public boolean tssid;
	public boolean tscreen;
	public boolean textmedia;
	public boolean tapkact;
	public boolean tpkgname;
	public boolean tpow;
	public boolean tlan;
	public boolean tcountry;
	public boolean tmsgtimes;
	public boolean tmsgfrom;
	public boolean tmsgbody;
	public boolean tcallstate;
	public boolean tcallnumber;
	public boolean treceivenumber;
	public boolean tcalltime;
	public boolean treceivetime;
	public boolean tcallcontact;
	public boolean ttitle;
	public boolean turl;
	public boolean tbearing;
	public boolean tgsmrssi;
	public boolean tgsmspn;
	public boolean tpowerstatus;
	public boolean tincomingcount,toutcomingcount,tinalltime,toutalltime,tcallin,tcallout = false;
	public int gsmrssi;
	public String gsmspn;
	private Map<String, String> mapParser;
	private static String QUEUE_NAME = "clap";
	private static String AMQPHOST = "140.119.221.34";
	private static String AMQPVHOST = "clap";
	private static String AMQPUSER = "clap";
	private static String AMQPPASSWORD = "clap@nccu";
	//public static String AccountMachine; // Android ID
	public static String exid;
	public boolean screenupload = false;
    public boolean chageupload = false;
	public int h = 0;
	public int msgtimes = 0;
	public int calltimesgap,msgtimesgap = 0;
	public int phonetimes = 0;
	private int intLevel;
	private int intScale;
	public int batint = 0;
	public String ClicentDeviceID;
	public static String deviceId;
	private boolean uploadstatus = false;
	private boolean checkdb = false;
	public long dbsize;
//	public boolean checktest = false;
	public int totaldbCount = 0;
	public static final int INCOMING_TYPE = 1;
	public static final int OUTGOING_TYPE = 2;
	public static final int MISSED_TYPE = 3;
	private boolean freupload = false;
	private String DeviceOs;
	private String Device;
	private String AuthCode;
	private String nowtest;
	private String nowtestdescript;
	private String testdescript;
	public static final String HIPPO_SERVICE_IDENTIFIER = "HIPPO_ON_SERVICE_001";
	public static final String POWER_CHECKER = "powercheck";
	public String back = "";
	public boolean agreeTest;
	public int SizeLimit;
	public int Timelimit;
	public int calltime;
	public int receivetime;
	public String nowtime;
	public boolean Bbook = false;
	
	public void onCreate(Bundle savedInstanceState){
		
		Log.v(TAG,"Log onCreate");
	
	}
	
	 
	@SuppressWarnings("deprecation")
	@Override
	public void onStart(Intent intent, int startId){
		
		
		EReceiver = new EventReceiver();
		IntentFilter filter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
    	getApplication().getApplicationContext().registerReceiver(EReceiver, filter);  //電量初始畫
		
		Log.v(TAG,"Log onStart");
		
		  SimpleDateFormat sdf =  new SimpleDateFormat("yyyy/MM/dd");
           java.util.Date today = new Date();
        todayTime = String.valueOf(sdf.format(today));
       Log.v("TAG","todayTime"+todayTime);
		
		sqlCase = "select * from clap ";
		//registerScreenBroadcastReceiver();
		
		getPref();        //取得頗析後的參數                                
		if (screenupload){
			registerScreenBroadcastReceiver();  //螢幕關閉待機時上傳事件註冊
			}
			if (chageupload){ 
				registerPowerconnectBroadcastReceiver();   //充電時上傳事件註冊
				}
			Log.v(TAG,"screenupload"+String.valueOf(screenupload));
			Log.v(TAG,"chageupload"+String.valueOf(chageupload));
		
			
		logdetermine(); 
		checkstart();
		/*
		SharedPreferences settings = getSharedPreferences(PREF_FILENAME,0);
		checktest = settings.getBoolean("checktest",checktest);
		if (checktest){
			 openDatabase();
			   
			   Log.v("TAG","logdetermine startRecording");
			   startRecording();
		}else{
			
		}     */
		
		super.onStart(intent, startId);
		
	}
	     private void startRecording(){
	    	
	    	 if(tax){
	    		 //getting Accelerometer record from mobile
	    		mThread = new HandlerThread("processAccax");
	 			mThread.start();
	 			mThreadHandler = new Handler(mThread.getLooper());
	 			mThreadHandler.post(r1);
	    	 }else{
	    		 
	    	 }
	 		
	    	 if(tay){
		 			//getting Accelerometer record from mobile
		 			mThread = new HandlerThread("processAccay");
		 			mThread.start();
		 			mThreadHandler = new Handler(mThread.getLooper());
		 			mThreadHandler.post(r2);
		 		}else{
		 			//mThread.quit();
		 		}
	    	 
	    	 if(taz){
		 			//getting Accelerometer record from mobile
		 			mThread = new HandlerThread("processAccaz");
		 			mThread.start();
		 			mThreadHandler = new Handler(mThread.getLooper());
		 			mThreadHandler.post(r3);
		 		}else{
		 			//mThread.quit();
		 		}      
	 		if(tox){
	 			//getting ORIENTATION record from mobile
	 		mThread = new HandlerThread("processOriox");
	 		mThread.start();
	 		mThreadHandler = new Handler(mThread.getLooper());
	 		mThreadHandler.post(r4);
	 		}else{
	 			//mThread.quit();
	 		}

	 		if(toy){
	 			//getting ORIENTATION record from mobile
		 		mThread = new HandlerThread("processOrioy");
		 		mThread.start();
		 		mThreadHandler = new Handler(mThread.getLooper());
		 		mThreadHandler.post(r5);
		 		}else{
		 			//mThread.quit();
		 		}
	 		
	 		if(toz){
	 			//getting ORIENTATION record from mobile
		 		mThread = new HandlerThread("processOrioz");
		 		mThread.start();
		 		mThreadHandler = new Handler(mThread.getLooper());
		 		mThreadHandler.post(r6);
		 		}else{
		 			//mThread.quit();
		 		}
	 		
	 		if(tmx){
		 		//getting MAGNETIC_FIELD record from mobile
		     	mThread = new HandlerThread("processMagnmx");
		 		mThread.start();
		 		mThreadHandler = new Handler(mThread.getLooper());
		 		mThreadHandler.post(r7);		
		 		}else{
		 			//mThread.quit();
		 		}
	 		
	 		if(tmy){
		 		//getting MAGNETIC_FIELD record from mobile
		     	mThread = new HandlerThread("processMagnmy");
		 		mThread.start();
		 		mThreadHandler = new Handler(mThread.getLooper());
		 		mThreadHandler.post(r8);		
		 		}else{
		 			//mThread.quit();
		 		}
	 		
	 		if(tmz){
		 		//getting MAGNETIC_FIELD record from mobile
		     	mThread = new HandlerThread("processMagnmz");
		 		mThread.start();
		 		mThreadHandler = new Handler(mThread.getLooper());
		 		mThreadHandler.post(r9);		
		 		}else{
		 			//mThread.quit();
		 		}
	 			 		
	 		if(Bli){
	 		//getting LIGHT record from mobile
	     	mThread = new HandlerThread("processLi");
	 		mThread.start();
	 		mThreadHandler = new Handler(mThread.getLooper());
	 		mThreadHandler.post(r10);
	 		}else{
	 			//mThread.quit();
	 		}
	 		
	 		if(Bpres){
	 		//getting PRESSURE record from mobile
	 		mThread = new HandlerThread("processPressure");
	 		mThread.start();
	 		mThreadHandler = new Handler(mThread.getLooper());
	 		mThreadHandler.post(r11);
	 		}else{
	 			//mThread.quit();
	 		}
	 		
	 		if(Bpx){
	 		//getting PROXIMITY record from mobile
	    	    mThread = new HandlerThread("processProx");
	 		mThread.start();
	 		mThreadHandler = new Handler(mThread.getLooper());
	 		mThreadHandler.post(r12);
	 		}else{
	 			//mThread.quit();
	 		}
	 		
	 		if(Btemp){
	 		//getting TEMPERATURE record from mobile
	     	mThread = new HandlerThread("processTemp");
	 		mThread.start();
	 		mThreadHandler = new Handler(mThread.getLooper());
	 		mThreadHandler.post(r13);   
	 		}else{
	 			//mThread.quit();
	 		}
	 		
	 		if(trssi){
	 		//getting WIFI record from mobile
	 		mThread = new HandlerThread("processWifirssi");
	 		mThread.start();
	 		mThreadHandler = new Handler(mThread.getLooper());
	 		mThreadHandler.post(r14);
	 		}else{
	 			//mThread.quit();
	 		}
	 	    /*	
	 		if(tssid){
	 			//getting WIFI record from mobile
	 		mThread = new HandlerThread("processWifissid");
	 		mThread.start();
	 		mThreadHandler = new Handler(mThread.getLooper());
	 		mThreadHandler.post(r15);
	 		}else{
	 			//mThread.quit();
	 		}    */
	 		
	 		/*
	 		if(ttitle){
	 			//getting BROWSER record from mobile
	 		mThread = new HandlerThread("processBrowsertitle");
	 		mThread.start();
	 		mThreadHandler = new Handler(mThread.getLooper());
	 		mThreadHandler.post(r16);
	 		}else{
	 			//mThread.quit();
	 		}
	 		
	 		if(turl){
	 			//getting BROWSER record from mobile
	 		mThread = new HandlerThread("processBrowserurl");
	 		mThread.start();
	 		mThreadHandler = new Handler(mThread.getLooper());
	 		mThreadHandler.post(r17);
	 		}else{
	 			//mThread.quit();
	 		}              */  
	 		
	 	// getting result counts from database
	 		mThread = new HandlerThread("counting");
	 		mThread.start();
	 		mThreadHandler = new Handler();
	 		mThreadHandler.post(r18);
	 	
	 	
	 		if(tlongitude){
	 			//getting GPS record from mobile
	 		mThread = new HandlerThread("processGpslongitude");
	 		mThread.start();
	 		mThreadHandler = new Handler(mThread.getLooper());
	 		mThreadHandler.post(r20);
	 		}else{
	 			//mThread.quit();
	 		}
	 		
	 		if(tlatitude){
	 			//getting GPS record from mobile
	 		mThread = new HandlerThread("processGpslatitude");
	 		mThread.start();
	 		mThreadHandler = new Handler(mThread.getLooper());
	 		mThreadHandler.post(r21);
	 		}else{
	 			//mThread.quit();
	 		}   
	 			 		
	 		if(tspeed){
	 			//getting GPS record from mobile
	 		mThread = new HandlerThread("processGpsspeed");
	 		mThread.start();
	 		mThreadHandler = new Handler(mThread.getLooper());
	 		mThreadHandler.post(r22);
	 		}else{
	 			//mThread.quit();
	 		}
	 		
	 		if(taccuracy){
	 			//getting GPS record from mobile
	 		mThread = new HandlerThread("processGpsaccuracy");
	 		mThread.start();
	 		mThreadHandler = new Handler(mThread.getLooper());
	 		mThreadHandler.post(r23);
	 		}else{
	 			//mThread.quit();
	 		}
	 		
	 		if(tbearing){
	 			//getting GPS record from mobile
	 		mThread = new HandlerThread("processGpsbearing");
	 		mThread.start();
	 		mThreadHandler = new Handler(mThread.getLooper());
	 		mThreadHandler.post(r24);
	 		}else{
	 			//mThread.quit();
	 		}
	 		
	 		if(taltitude){
	 			//getting GPS record from mobile
	 		mThread = new HandlerThread("processGpsaltitude");
	 		mThread.start();
	 		mThreadHandler = new Handler(mThread.getLooper());
	 		mThreadHandler.post(r25);
	 		}else{
	 			//mThread.quit();
	 		}
	 		
	 		if (freupload==false){
	 		mThread = new HandlerThread("uploadcheck");
	 		mThread.start();
	 		mThreadHandler = new Handler(mThread.getLooper());
	 		mThreadHandler.post(r26);}else{
	 			
	 		}  
            /*
	 		if(fpowerdown==false){
		 		mThread = new HandlerThread("powercheck");
		 		mThread.start();
		 		mThreadHandler = new Handler(mThread.getLooper());
		 		mThreadHandler.post(r27);}else{
	 			 		
	 		}      */
	 		
	 		if (tincomingcount){
		 		mThread = new HandlerThread("incomingcount");
		 		mThread.start();
		 		mThreadHandler = new Handler(mThread.getLooper());
		 		mThreadHandler.post(r28);}else{
	 			
	 		}
	 		
	 		if (toutcomingcount){
		 		mThread = new HandlerThread("outcomingcount");
		 		mThread.start();
		 		mThreadHandler = new Handler(mThread.getLooper());
		 		mThreadHandler.post(r29);}else{
	 			 		
	 		}
	 		
	 		if (tinalltime){
		 		mThread = new HandlerThread("inalltime");
		 		mThread.start();
		 		mThreadHandler = new Handler(mThread.getLooper());
		 		mThreadHandler.post(r30);}else{
	 			 		
	 		}
	 		
	 		if (toutalltime){
		 		mThread = new HandlerThread("outalltime");
		 		mThread.start();
		 		mThreadHandler = new Handler(mThread.getLooper());
		 		mThreadHandler.post(r31);}else{
	 			 		
	 		}
	     
	     if (tmsgtimes){
		 		mThread = new HandlerThread("msgtimes");
		 		mThread.start();
		 		mThreadHandler = new Handler(mThread.getLooper());
		 		mThreadHandler.post(r32);}else{
	 			 		
	 		}
	     
	     if(tgsmrssi){
		 		//getting GSM RSSI record from mobile
		 		mThread = new HandlerThread("processGsmrssi");
		 		mThread.start();
		 		mThreadHandler = new Handler(mThread.getLooper());
		 		mThreadHandler.post(r33);
		 		}else{
		 			//mThread.quit();
		 		}
	     
	     if(tgsmspn){
		 		//getting GSM SPN record from mobile
		 		mThread = new HandlerThread("processGsmspn");
		 		mThread.start();
		 		mThreadHandler = new Handler(mThread.getLooper());
		 		mThreadHandler.post(r34);
		 		}else{
		 			//mThread.quit();
		 		}
	     /*
	     if (tcallin){
		 		mThread = new HandlerThread("callnumber");
		 		mThread.start();
		 		mThreadHandler = new Handler(mThread.getLooper());
		 		mThreadHandler.post(r35);}else{	 			 		
	 		}
	     
	     if (tcallout){
		 		mThread = new HandlerThread("receivenumber");
		 		mThread.start();
		 		mThreadHandler = new Handler(mThread.getLooper());
		 		mThreadHandler.post(r36);}else{	 			 		
	 		}
	     */
	     if (tpowerstatus){
		 		mThread = new HandlerThread("powerstatus");
		 		mThread.start();
		 		mThreadHandler = new Handler(mThread.getLooper());
		 		mThreadHandler.post(r37);}else{	 			 		
	 		}
	     
        }
         //重新開機時，判斷有無參加過實驗
	    
	     private void checkstart(){
	     if((Bacc==false)&&(Bori==false)&&(Bmagn==false)&&(Bpres==false)&&(Bli==false)&&(Bpx==false)&&(Btemp==false)&&(Bwifi==false)&&(Bbrowser==false)&&(Bloc==false)&&(Bscreen==false)&&(Bmedia==false)&&(Bapp==false)&&(Bpow==false)&&(Blocale==false)&&(Bsms==false)&&(Bcall==false)&&(Bcallact==false)&&(Bgsm==false))
		   {
			   //沒參加實驗，不做任何動作	    	
	    	 
		   }else{
			   //已參加過實驗，開啟資料表繼續實驗紀錄
			   Log.v("TAG","op database");
			   openDatabase();
			   
			   Log.v("TAG","logdetermine startRecording");
			   startRecording();
		   }
	     }    
	     //行為資訊感測註冊
		private void logdetermine(){
		   /*
		   if(Bscreen==true){
			   registerScreenBroadcastReceiver();
		   }
		   if(Bpow==true){
			   registerPowerconnectBroadcastReceiver();
		   }
		   */
		/*	if (Bacc==true){
				   processAcc();
			}     */
		   if(Bmedia==true){
			   registerMediaBroadcastReceiver();
		   }
		   if(Bapp==true){
			   registerPackageBroadcastReceiver();
		   }
		  
		   if(Blocale==true){
			   registerLocaleChangedBroadcastReceiver();
		   }
		   
		   if(Bgsm==true){
			   processGsm();
		   }
	/*	   if(tcallnumber==true|treceivenumber==true|tcalltime==true|treceivetime==true){
			   
		   }     */
		   Log.v(TAG,"finish logdetermine()");
		   }   
		//@Override
		//結束後端服務，呼叫endprocess()
		public void onDestroy(){
		     Log.v(TAG,"onDestroy");
		     endprocess();
		super.onDestroy();
	}  
	//關閉個感應器的註冊，停止所有執行緒
	public void endprocess(){
    try {
			
			if (sensorManager != null && accelerationListener != null) {
				sensorManager.unregisterListener(accelerationListener);
			}		
			
			if (sensorManager != null && orientationListener != null) {
				sensorManager.unregisterListener(orientationListener);
			}
			
			if (sensorManager != null && magneticListener != null) {
				sensorManager.unregisterListener(magneticListener);
			}
		/*	if (locationManager != null && myLocationListener != null) {
				locationManager.removeUpdates(myLocationListener);
			}   */
			
			if (sensorManager != null && proximityListener != null) {
				sensorManager.unregisterListener(proximityListener);
			}
			
			if (sensorManager != null && temperatureListener != null) {
				sensorManager.unregisterListener(temperatureListener);
			}
			
			if (sensorManager != null && pressureListener != null) {
				sensorManager.unregisterListener(pressureListener);
			}
			
			if (sensorManager != null && lightListener != null) {
				sensorManager.unregisterListener(lightListener);		
			}         

			mThread.interrupt();
			mThread = null;
			mThreadHandler.removeCallbacks(r1);
			mThreadHandler.removeCallbacks(r2);
			mThreadHandler.removeCallbacks(r3);
			mThreadHandler.removeCallbacks(r4);
			mThreadHandler.removeCallbacks(r5);
			mThreadHandler.removeCallbacks(r6);
			mThreadHandler.removeCallbacks(r7);
			mThreadHandler.removeCallbacks(r8);
			mThreadHandler.removeCallbacks(r9);
			mThreadHandler.removeCallbacks(r10);
			mThreadHandler.removeCallbacks(r11);
			mThreadHandler.removeCallbacks(r12);
			mThreadHandler.removeCallbacks(r13);
			mThreadHandler.removeCallbacks(r14);
			//mThreadHandler.removeCallbacks(r15);
			//mThreadHandler.removeCallbacks(r16);
			//mThreadHandler.removeCallbacks(r17);   
			mThreadHandler.removeCallbacks(r18);
			//mThreadHandler.removeCallbacks(r19);
			mThreadHandler.removeCallbacks(r20);
			mThreadHandler.removeCallbacks(r21);
			mThreadHandler.removeCallbacks(r22);
			mThreadHandler.removeCallbacks(r23);
			mThreadHandler.removeCallbacks(r24);
			mThreadHandler.removeCallbacks(r25);   
			mThreadHandler.removeCallbacks(r26);
			mThreadHandler.removeCallbacks(r27);
			mThreadHandler.removeCallbacks(r28);
			mThreadHandler.removeCallbacks(r29);   
			mThreadHandler.removeCallbacks(r30);
			mThreadHandler.removeCallbacks(r31);
			mThreadHandler.removeCallbacks(r32);
			mThreadHandler.removeCallbacks(r33);
			mThreadHandler.removeCallbacks(r34);
		//	mThreadHandler.removeCallbacks(r35);
		//	mThreadHandler.removeCallbacks(r36);
			mThreadHandler.removeCallbacks(r37);
			mThreadHandler = null; 				

			closeDatabase();     //關閉SQLite資料庫

		} catch (Exception e) {

		}
	}
	//建立資料表
	private void openDatabase() {
		dbhelper = new DBHelper(this);
		Log.v(TAG,"opdb");
	}
	//關閉資料表
	private static void closeDatabase() {
		dbhelper.close();
	}
	//刪除資料表資料	
	private void deleteTable(){
		SQLiteDatabase db = dbhelper.getWritableDatabase();
		dbhelper.deleteTable(db);
		fgroup = 0;
		Log.v(TAG,"deleteTable");
			}
	/*
	private void deleteGroup(int delgroup){
	SQLiteDatabase db = dbhelper.getWritableDatabase();
	dbhelper.deleteGroup(db,delgroup);
	Log.v(TAG,"deleteGroup");
	}    
	*/
	//從實驗參數檔XML檔案取得頗析後的參數
	private void getPref(){               
		SharedPreferences settings = getSharedPreferences(PREF_FILENAME,0);
		
		Bacc = settings.getBoolean("Bacc",Bacc);
    	Bori = settings.getBoolean("Bori",Bmagn);
    	Bmagn = settings.getBoolean("Bmagn",Bmagn);
    	Bpres = settings.getBoolean("Bpres",Bli);
    	Btemp = settings.getBoolean("Btemp",Btemp);
    	Bwifi = settings.getBoolean("Bwifi",Bwifi);
    	Bli =settings.getBoolean("Bli",Bli);
    	Bpx =  settings.getBoolean("Bpx",Bpx);
    	Bbrowser = settings.getBoolean("Bbrowser",Bbrowser);
    	Bloc = settings.getBoolean("Bloc",Bloc);    	
    	Bgsm = settings.getBoolean("Bgsm",Bgsm);
    	MSGTIMESINTERVAL = settings.getLong("msgtimesinterval",MSGTIMESINTERVAL);
    	CALLINTIMESINTERVAL = settings.getLong("callintimesinterval",CALLINTIMESINTERVAL);
    	CALLOUTTIMESINTERVAL = settings.getLong("callouttimesinterval",CALLOUTTIMESINTERVAL);
    	CALLINALLTIMEINTERVAL = settings.getLong("callinalltimeinterval",CALLINALLTIMEINTERVAL);
    	CALLOUTALLTIMESINTERVAL = settings.getLong("calloutalltimeinterval",CALLOUTALLTIMESINTERVAL);
    	CALLNUMBERINTERVAL = settings.getLong("callnumberinterval",CALLNUMBERINTERVAL);             //7/30增加
    	RECEIVERNUMBERINTERVAL = settings.getLong("receivernumberinterval",RECEIVERNUMBERINTERVAL);     //7/30增加    	
    	AXINTERVAL = settings.getLong("axinterval",AXINTERVAL);
    	AYINTERVAL = settings.getLong("ayinterval",AYINTERVAL);
    	AZINTERVAL = settings.getLong("azinterval",AZINTERVAL);
    	OXINTERVAL = settings.getLong("oxinterval",OXINTERVAL);
    	OYINTERVAL = settings.getLong("oyinterval",OYINTERVAL);
    	OZINTERVAL = settings.getLong("ozinterval",OZINTERVAL);
    	MXINTERVAL = settings.getLong("mxinterval",MXINTERVAL);
    	MYINTERVAL = settings.getLong("myinterval",MYINTERVAL);
    	MZINTERVAL = settings.getLong("myinterval",MZINTERVAL);
    	PXINTERVAL = settings.getLong("pxinterval",PXINTERVAL);
    	LIINTERVAL = settings.getLong("liinterval",LIINTERVAL);
    	TEMPINTERVAL = settings.getLong("tempinterval",TEMPINTERVAL);
    	PRESINTERVAL = settings.getLong("presinterval",PRESINTERVAL);
    	LONGITUDEINTERVAL = settings.getLong("longitudeinterval",LONGITUDEINTERVAL);
    	LATITUDEINTERVAL = settings.getLong("latitudeinterval",LATITUDEINTERVAL);
    	SPEEDINTERVAL = settings.getLong("speedinterval",SPEEDINTERVAL);
    	BEARINGINTERVAL = settings.getLong("bearinginterval",BEARINGINTERVAL);
    	ALTITUDEINTERVAL = settings.getLong("altitudeinterval",ALTITUDEINTERVAL);
    	ACCURACYINTERVAL = settings.getLong("accuracyinterval",ACCURACYINTERVAL);
    	RSSIINTERVAL = settings.getLong("rssiinterval",RSSIINTERVAL);
    	SSIDINTERVAL = settings.getLong("ssidinterval",SSIDINTERVAL);
    	GSMRSSIINTERVAL = settings.getLong("gsmrssiinterval",GSMRSSIINTERVAL);
    	GSMSPNINTERVAL = settings.getLong("gsmspninterval",GSMSPNINTERVAL);
    	POWERINTERVAL = settings.getLong("powerinterval",POWERINTERVAL);        //7/30增加
    	Timelimit = settings.getInt("timelimit",Timelimit);
    	exid = settings.getString("experimentId",exid);
    	Bcallact = settings.getBoolean("Bcallact",Bcallact);
    	Bcall = settings.getBoolean("Bcall",Bcall);
    	Bsms = settings.getBoolean("Bsms",Bsms);
    	Bgsm = settings.getBoolean("Bgsm",Bgsm);
    	Blocale = settings.getBoolean("Blocale",Blocale);
    	Bpow = settings.getBoolean("Bpow",Bpow);
    	Bapp = settings.getBoolean("Bapp",Bapp);
    	Bmedia = settings.getBoolean("Bmedia",Bmedia);
    	Bscreen = settings.getBoolean("Bscreen",Bscreen);
    	tax = settings.getBoolean("tax",tax);
    	tay = settings.getBoolean("tay",tay);
    	taz = settings.getBoolean("taz",taz);
    	tox = settings.getBoolean("tox",tox);
    	toy = settings.getBoolean("toy",toy);
    	toz = settings.getBoolean("toz",toz);
    	tmx = settings.getBoolean("tmx",tmx);
    	tmy = settings.getBoolean("tmy",tmy);
    	tmz = settings.getBoolean("tmz",tmz);
    	tpx= settings.getBoolean("tpx",tpx);
    	ttemp = settings.getBoolean("ttemp",ttemp);
    	tlight = settings.getBoolean("tlight",tlight);
    	tpressure = settings.getBoolean("tpressure",tpressure);
    	tlongitude = settings.getBoolean("tlongitude",tlongitude);
    	tlatitude = settings.getBoolean("tlatitude",tlatitude);
    	tspeed = settings.getBoolean("tspeed",tspeed);
    	tbearing = settings.getBoolean("tbearing",tbearing);
    	taltitude = settings.getBoolean("taltitude",taltitude);
    	taccuracy = settings.getBoolean("taccuracy",taccuracy);
    	trssi= settings.getBoolean("trssi",trssi);
    	tssid= settings.getBoolean("tssid",tssid);
    	tscreen = settings.getBoolean("tscreen",tscreen);
    	textmedia = settings.getBoolean("textmedia",textmedia);
    	tapkact = settings.getBoolean("tapkact",tapkact);
    	tpkgname = settings.getBoolean("tpkgname",tpkgname);
    	tpow = settings.getBoolean("tpow",tpow);
    	tlan = settings.getBoolean("tlan",tlan);
    	tcountry = settings.getBoolean("tcountry",tcountry);
    	tmsgtimes = settings.getBoolean("tmsgtimes",tmsgtimes);
    	tincomingcount = settings.getBoolean("tincomingcount",tincomingcount);
    	toutcomingcount = settings.getBoolean("toutcomingcount",toutcomingcount);
    	tinalltime = settings.getBoolean("tinalltime",tinalltime);
    	toutalltime = settings.getBoolean("toutalltime",toutalltime);
    	tcallin = settings.getBoolean("tcallin",tcallin);               //7/30增加
    	tcallout = settings.getBoolean("tcallout",tcallout);
    	tgsmrssi = settings.getBoolean("tgsmrssi",tgsmrssi);
    	tgsmspn = settings.getBoolean("tgsmspn",tgsmspn);
    	tpowerstatus = settings.getBoolean("tpowerstatus",tpowerstatus);   //7/30增加
    	//tmsgfrom = settings.getBoolean("tmsgfrom",tmsgfrom);
    	//tmsgbody = settings.getBoolean("tmsgbody",tmsgbody);
    	tcallstate = settings.getBoolean("tcallstate",tcallstate);
    	tcallnumber = settings.getBoolean("tcallnumber",tcallnumber);
    	treceivenumber = settings.getBoolean("treceivenumber",treceivenumber);
    	tcalltime = settings.getBoolean("tcalltime",tcalltime);
    	treceivetime = settings.getBoolean("treceivetime",treceivetime);
    	tcallcontact = settings.getBoolean("tcallcontact",tcallcontact);
    	ttitle = settings.getBoolean("ttitle",ttitle);
    	turl = settings.getBoolean("turl",turl);
    	btax = settings.getBoolean("btax",btax);
    	btay = settings.getBoolean("btay",btay);
    	btaz = settings.getBoolean("btaz",btaz);
    	btox = settings.getBoolean("btox",btox);
    	btoy = settings.getBoolean("btoy",btoy);
    	btoz = settings.getBoolean("btoz",btoz);
    	btmx = settings.getBoolean("btmx",btmx);
    	btmy = settings.getBoolean("btmy",btmy);
    	btmz = settings.getBoolean("btmz",btmz);
    	btpx = settings.getBoolean("btpx",btpx);
    	bttemp = settings.getBoolean("bttemp",bttemp);
    	btlight = settings.getBoolean("btlight",btlight);
    	btpressure = settings.getBoolean("btpressure",btpressure);
    	btlongitude = settings.getBoolean("btlongitude",btlongitude);
    	btlatitude = settings.getBoolean("btlatitude",btlatitude);
    	btspeed = settings.getBoolean("btspeed",btspeed);
    	btbearing = settings.getBoolean("btbearing",btbearing);
    	btaltitude = settings.getBoolean("btaltitude",btaltitude);
    	btaccuracy = settings.getBoolean("btaccuracy",btaccuracy);
    	axbigop = settings.getString("axbigop",axbigop);
    	axlessop = settings.getString("axlessop",axlessop);
    	aybigop = settings.getString("aybigop",aybigop);
    	aylessop = settings.getString("aylessop",aylessop);
    	azbigop = settings.getString("azbigop",azbigop);
    	azlessop = settings.getString("azlessop",azlessop);
    	oxbigop = settings.getString("oxbigop",oxbigop);
    	oxlessop = settings.getString("oxlessop",oxlessop);
    	oybigop = settings.getString("oybigop",oybigop);
    	oylessop = settings.getString("oylessop",oylessop);
    	ozbigop = settings.getString("ozbigop",ozbigop);
    	ozlessop = settings.getString("ozlessop",ozlessop);
    	mxbigop = settings.getString("mxbigop",mxbigop);
    	mxlessop = settings.getString("mxlessop",mxlessop);
    	mybigop = settings.getString("mybigop",mybigop);
    	mylessop = settings.getString("mylessop",mylessop);
    	mzbigop = settings.getString("mzbigop",mzbigop);
    	mzlessop = settings.getString("mzlessop",mzlessop);
    	tempbigop = settings.getString("tempbigop",tempbigop);
    	templessop = settings.getString("templessop",templessop);
    	lightbigop = settings.getString("lightbigop",lightbigop);
    	lightlessop = settings.getString("lightlessop",lightlessop);
    	pressurebigop = settings.getString("pressurebigop",pressurebigop);
    	pressurelessop = settings.getString("pressurelessop",pressurelessop);
    	speedbigop = settings.getString("speedbigop",speedbigop);
    	speedlessop = settings.getString("speedlessop",speedlessop);
    	altitudebigop = settings.getString("altitudebigop",altitudebigop);
    	altitudelessop = settings.getString("altitudelessop",altitudelessop);
    	bearingbigop = settings.getString("bearingbigop",bearingbigop);
    	bearinglessop = settings.getString("bearinglessop",bearinglessop);
    	accuracybigop = settings.getString("accuracybigop",accuracybigop);
    	accuracylessop = settings.getString("accuracylessop",accuracylessop);
    	longitudebigop = settings.getString("longitudebigop",longitudebigop);
    	longitudelessop = settings.getString("longitudelessop",longitudelessop);
    	latitudebigop = settings.getString("latitudebigop",latitudebigop);
    	latitudelessop = settings.getString("latitudelessop",latitudelessop);    	
    	
    	axbigvalue = settings.getString("axbigvalue",axbigvalue);
    	axlessvalue = settings.getString("axlessvalue",axlessvalue);
    	aybigvalue = settings.getString("aybigvalue",aybigvalue);
    	aylessvalue = settings.getString("aylessvalue",aylessvalue);
    	azbigvalue = settings.getString("azbigvalue",azbigvalue);
    	azlessvalue = settings.getString("azlessvalue",azlessvalue);
    	oxbigvalue = settings.getString("oxbigvalue",oxbigvalue);
    	oxlessvalue = settings.getString("oxlessvalue",oxlessvalue);
    	oybigvalue = settings.getString("oybigvalue",oybigvalue);
    	oylessvalue = settings.getString("oylessvalue",oylessvalue);
    	ozbigvalue = settings.getString("ozbigvalue",ozbigvalue);
    	ozlessvalue = settings.getString("ozlessvalue",ozlessvalue);
    	mxbigvalue = settings.getString("mxbigvalue",mxbigvalue);
    	mxlessvalue = settings.getString("mxlessvalue",mxlessvalue);
    	mybigvalue = settings.getString("mybigvalue",mybigvalue);
    	mylessvalue = settings.getString("mylessvalue",mylessvalue);
    	mzbigvalue = settings.getString("mzbigvalue",mzbigvalue);
    	mzlessvalue = settings.getString("mzlessvalue",mzlessvalue);
    	tempbigvalue = settings.getString("tempbigvalue",tempbigvalue);
    	templessvalue = settings.getString("templessvalue",templessvalue);
    	lightbigvalue = settings.getString("lightbigvalue",lightbigvalue);
    	lightlessvalue = settings.getString("lightlessvalue",lightlessvalue);
    	pressurebigvalue = settings.getString("pressurebigvalue",pressurebigvalue);
    	pressurelessvalue = settings.getString("pressurelessvalue",pressurelessvalue);
    	speedbigvalue = settings.getString("speedbigvalue",speedbigvalue);
    	speedlessvalue = settings.getString("speedlessvalue",speedlessvalue);
    	altitudebigvalue = settings.getString("altitudebigvalue",altitudebigvalue);
    	altitudelessvalue = settings.getString("altitudelessvalue",altitudelessvalue);
    	bearingbigvalue = settings.getString("bearingbigvalue",bearingbigvalue);
    	bearinglessvalue = settings.getString("bearinglessvalue",bearinglessvalue);
    	accuracybigvalue = settings.getString("accuracybigvalue",accuracybigvalue);
    	accuracylessvalue = settings.getString("accuracylessvalue",accuracylessvalue);
    	longitudebigvalue = settings.getString("longitudebigvalue",longitudebigvalue);
    	longitudelessvalue = settings.getString("longitudelessvalue",longitudelessvalue);
    	latitudebigvalue = settings.getString("latitudebigvalue",latitudebigvalue);
    	latitudelessvalue = settings.getString("latitudelessvalue",latitudelessvalue);
    	
    	screenupload = settings.getBoolean("screenupload",screenupload);
    	chageupload = settings.getBoolean("chageupload",chageupload);
    	h = settings.getInt("h",h);
    	deviceId = settings.getString("deviceId",ClicentDeviceID);
    	DeviceOs = settings.getString("deviceOs",DeviceOs);
    	Device = settings.getString("deviceType",Device);
    	nowtest= settings.getString("nowexperiment",contents);
    	nowtestdescript = settings.getString("nowtestdescript",testdescript);
    	back = settings.getString("back",back);
    	SizeLimit = settings.getInt("SizeLimit",SizeLimit);
    	SharedPreferences sets = getSharedPreferences(ID_CHECK,0);
    	AuthCode = sets.getString("authCode",AuthCode);
    	Log.v(TAG,"gref back"+back);
	}
	//確認現在電量
	private void Checkpower(){
		EReceiver = new EventReceiver();
		IntentFilter filter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
    	getApplication().getApplicationContext().registerReceiver(EReceiver, filter);
		if(batint<=20){                 //若低於總電量20%
			Log.v(TAG,"run powercheck");
			fpowerdown = true;
			StopRecord();               //停止實驗進行
			registerRePowerconnectBroadcastReceiver();  //註冊充電事件廣播監聽器，當充電時重新啟動實驗紀錄
			//  Intent i = new Intent(POWER_CHECKER);
	           /* 隞叫endBroadcast�撱�閮 */
	       //   sendBroadcast(i);
		}
		
	}
	
	//停止實驗進行，但不將頁面轉回前端PLPA_CLAP activity
	private void StopRecord() {
		// TODO Auto-generated method stub
try {
			mThread.interrupt();
			mThread = null;
			mThreadHandler.removeCallbacks(r1);
			mThreadHandler.removeCallbacks(r2);
			mThreadHandler.removeCallbacks(r3);
			mThreadHandler.removeCallbacks(r4);
			mThreadHandler.removeCallbacks(r5);
			mThreadHandler.removeCallbacks(r6);
			mThreadHandler.removeCallbacks(r7);
			mThreadHandler.removeCallbacks(r8);
			mThreadHandler.removeCallbacks(r9);
			mThreadHandler.removeCallbacks(r10);
			mThreadHandler.removeCallbacks(r11);
			mThreadHandler.removeCallbacks(r12);
			mThreadHandler.removeCallbacks(r13);
			mThreadHandler.removeCallbacks(r14);
			mThreadHandler.removeCallbacks(r18);
			mThreadHandler.removeCallbacks(r20);
			mThreadHandler.removeCallbacks(r21);
			mThreadHandler.removeCallbacks(r22);
			mThreadHandler.removeCallbacks(r23);
			mThreadHandler.removeCallbacks(r24);
			mThreadHandler.removeCallbacks(r25);   
			mThreadHandler.removeCallbacks(r26);
			mThreadHandler.removeCallbacks(r27);
			mThreadHandler.removeCallbacks(r28);
			mThreadHandler.removeCallbacks(r29);   
			mThreadHandler.removeCallbacks(r30);
			mThreadHandler.removeCallbacks(r31);
			mThreadHandler.removeCallbacks(r32);
			mThreadHandler.removeCallbacks(r33);
			mThreadHandler.removeCallbacks(r34);		
			mThreadHandler.removeCallbacks(r37);
			mThreadHandler = null; 
						
			if (ScreenReceiver != null) {
				getApplication().getApplicationContext().unregisterReceiver(ScreenReceiver);
			}		
			if (MediaReceiver != null) {
				getApplication().getApplicationContext().unregisterReceiver(MediaReceiver);
			}	
			if (PackageReceiver != null) {
				getApplication().getApplicationContext().unregisterReceiver(PackageReceiver);
			}	
			if (PowerconnectReceiver != null) {
				getApplication().getApplicationContext().unregisterReceiver(PowerconnectReceiver);
			}	
			if (LocaleReceiver != null) {
				getApplication().getApplicationContext().unregisterReceiver(LocaleReceiver);
			}	
			closeDatabase();
			Log.v(TAG,"power down STOP Record");
		} catch (Exception e) {

		}
	}

	//註冊充電事件監聽器
    public void registerRePowerconnectBroadcastReceiver() {
		
		{
			PReceiver = new PEventReceiver();
			Log.v(TAG,"registerRePowerconnectBroadcastReceiver");
			IntentFilter filter = new IntentFilter(Intent.ACTION_POWER_CONNECTED);	      	       
			getApplication().getApplicationContext().registerReceiver(PReceiver, filter);
		}
	}

    //當充電時重新啟動實驗紀錄
    public class PEventReceiver extends BroadcastReceiver{
			
			public void onReceive(Context context, Intent intent) {
				// TODO Auto-generated method stub
				System.out.println("onReceive ");
				if (intent.getAction().equals(Intent.ACTION_POWER_CONNECTED)){
					logdetermine();
					checkstart();
				}
			}

			
    }
    //判斷實驗有無更新
	private void CheckExperimentUpdate() {
		//REST
		AsyncTask<String, Void, String> RESTtext;
		RESTtext = new readREST().execute(" http://140.119.221.34/ws/le/getall/Android/"+DeviceOs+"/"+Device+"/"+deviceId+"/"+AuthCode);
		System.out.println(" http://140.119.221.34/ws/le/getall/Android/"+DeviceOs+"/"+Device+"/"+deviceId+"/"+AuthCode);
		try {
			
			JSONObject jObj = new JSONObject(RESTtext.get());	
			JSONArray experiments = jObj.getJSONArray("experiment");
			int count = experiments.length();
			experimentIdList = new String[count];	
			experimentNameList = new String[count];
			experimentDescriptionList = new String[count];
			experimentScriptList = new String[count];
			
			
			for(int i = 0; i < experiments.length(); i++){
				JSONObject experiment = experiments.getJSONObject(i);
				experimentIdList[i] = experiment.getString("experimentId");
				experimentNameList[i] = experiment.getString("name");
				experimentDescriptionList[i] = experiment.getString("description");
				experimentScriptList[i] = experiment.getString("script");
				if (exid==experimentIdList[i]){
					Log.v(TAG,"test match");
					CheckChange(exid,experimentScriptList[i],experimentDescriptionList[i]);
				}
			}
			
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	    //比較新舊實驗描述檔和設定檔
        private void CheckChange(String string1, String string2, String string3) {
		// TODO Auto-generated method stub
        if ((nowtest.equals(string2))&&(nowtestdescript.equals(string3))){
			Log.v(TAG,"CheckChange NOCHANGE");
		}else{
			Log.v(TAG,"CheckChange NEWCHANGE");
			deleteTable();
			
			fupdate = true;
			   Intent i = new Intent(HIPPO_SERVICE_IDENTIFIER);
		          i.putExtra("testID", string1);
		          i.putExtra("testScript", string2);
		          i.putExtra("testDescript", string3);		         
		          sendBroadcast(i);          //將新的實驗資訊送至UpdateCheck BroadcastReceiver
                     }
			
        }
        //上傳實驗紀錄
		private void uploadDSL() {
        
			try {
		Log.v(TAG,"uploadDSL");		
		// do nothing if database is empty
		SQLiteDatabase database = dbhelper.getReadableDatabase();
		Cursor cursor = database.rawQuery("select * from clap", null);   //選取SQLite全部資料
		
		Log.v(TAG,"havedata");
		// generate a backup database file into SDcard for test
		backupDBtoSD();		
		
			
		
		if (cursor.getCount() == 0) {
			Toast.makeText(getApplicationContext(),
					"Database is empty, try to start capturing",
					Toast.LENGTH_SHORT).show();
			return;
		}
		
		// uploading JSON data to server
		mThread = new HandlerThread("uploadAMQP");
		mThread.start();
		mThreadHandler = new Handler(mThread.getLooper());
		mThreadHandler.post(r19);	
			} catch (Exception e) {

			}
		
	}
        
        //上傳時確認電池電量和SQLite資料庫資料筆數
        private boolean uploadcheck() {
			// TODO Auto-generated method stub
        	EReceiver = new EventReceiver();
        	IntentFilter filter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        	getApplication().getApplicationContext().registerReceiver(EReceiver, filter);
            //if �餅�閮�憭扳20%,
        	if ((batint > 20)&& (getResultCount()>SizeLimit)){
        		uploadstatus = true;        		
        	}else{
        		uploadstatus = false;
        	}
            Log.v(TAG,"uploadcheck"+getResultCount()+"power"+String.valueOf(batint));
        	return uploadstatus;
		}
		            
		
        //限制條件比較
		public boolean detectWhereCheck() {
			// TODO Auto-generated method stub
        	//Log.v(TAG,"delectWhereCheck");
        	agreeTest = true;
        	//SENSOR限制條件
        	if (btax){
            	 float axbigcomparevalue = Float.parseFloat(axbigvalue);
            	 float axlesscomparevalue = Float.parseFloat(axlessvalue);
            	 if(axbigop.equals(">")){
            		 if(ax<=axbigcomparevalue){
	     					agreeTest = false;
	     					Log.v(TAG,"against ax big limit"+ax);
	     				}
	     			}
	            	 if(axbigop.equals(">=")){
		     				if(ax<axbigcomparevalue){
		     					agreeTest = false;
		     					Log.v(TAG,"against ax big limit"+ax);		     					
		     				}
		     			}	 
	            	 if(axbigop.equals("=")){
		     				if(ax!=axbigcomparevalue){
		     					agreeTest = false;
		     					Log.v(TAG,"against ax big limit"+ax);	     					
		     				}
		     			}	 
	            	 if(axlessop.equals("<=")){
		     				if(ax>axlesscomparevalue){
		     					agreeTest = false;
		     					Log.v(TAG,"against ax less limit"+ax);		     					
		     				}
		     			}
	            	 if(axlessop.equals("<")){
		     				if(ax>=axlesscomparevalue){
		     					agreeTest = false;
		     					Log.v(TAG,"against ax less limit"+ax);		     					
		     				}
        	           }
        		
        	}
        	
        	if (btay){
       		 
           	float aybigcomparevalue = Float.parseFloat(aybigvalue);
       	   float aylesscomparevalue = Float.parseFloat(aylessvalue);
       	 if(aybigop.equals(">")){
       		 
    				if(ay<=aybigcomparevalue){
    					agreeTest = false;     					
    				}
    			}
           	 if(aybigop.equals(">=")){
	     				if(ay<aybigcomparevalue){
	     					agreeTest = false;     							     					
	     				}
	     			}	 
           	 if(aybigop.equals("=")){
	     				if(ay!=aybigcomparevalue){
	     					agreeTest = false;
	     						     					
	     				}
	     			}	 
           	 if(aylessop.equals("<=")){
	     				if(ay>aylesscomparevalue){
	     					agreeTest = false;     							     					
	     				}
	     			}
           	 if(aylessop.equals("<")){
	     				if(ay>=aylesscomparevalue){
	     					agreeTest = false;	     						     					
	     				}
   	           }
       	}
        	
        	if (btaz){
        	        			
        		float azbigcomparevalue = Float.parseFloat(azbigvalue);
           	   float azlesscomparevalue = Float.parseFloat(azlessvalue);
           	 if(azbigop.equals(">")){
           		 
	     				if(az<=azbigcomparevalue){
	     					agreeTest = false;     					
	     				}
	     			}
	            	 if(azbigop.equals(">=")){
		     				if(az<azbigcomparevalue){
		     					agreeTest = false;     							     					
		     				}
		     			}	 
	            	 if(azbigop.equals("=")){
		     				if(az!=azbigcomparevalue){
		     					agreeTest = false;
		     						     					
		     				}
		     			}	 
	            	 if(azlessop.equals("<=")){
		     				if(az>azlesscomparevalue){
		     					agreeTest = false;     							     					
		     				}
		     			}
	            	 if(azlessop.equals("<")){
		     				if(az>=azlesscomparevalue){
		     					agreeTest = false;	     						     					
		     				}
       	           }
          	}
        	
        	if (btmx){
        		float mxbigcomparevalue = Float.parseFloat(mxbigvalue);
           	 float mxlesscomparevalue = Float.parseFloat(mxlessvalue);
           	 if(mxbigop.equals(">")){
           		 
	     				if(mx<=mxbigcomparevalue){
	     					agreeTest = false;     					
	     				}
	     			}
	            	 if(mxbigop.equals(">=")){
		     				if(mx<mxbigcomparevalue){
		     					agreeTest = false;     							     					
		     				}
		     			}	 
	            	 if(mxbigop.equals("=")){
		     				if(mx!=mxbigcomparevalue){
		     					agreeTest = false;
		     						     					
		     				}
		     			}	 
	            	 if(mxlessop.equals("<=")){
		     				if(mx>mxlesscomparevalue){
		     					agreeTest = false;     							     					
		     				}
		     			}
	            	 if(mxlessop.equals("<")){
		     				if(mx>=mxlesscomparevalue){
		     					agreeTest = false;	     						     					
		     				}
       	           }
       	}
       	
       	if (btmy){
       	 float mybigcomparevalue = Float.parseFloat(mybigvalue);
    	 float mylesscomparevalue = Float.parseFloat(mylessvalue);
    	 if(mybigop.equals(">")){
    		 
 				if(my<=mybigcomparevalue){
 					agreeTest = false;     					
 				}
 			}
        	 if(mybigop.equals(">=")){
     				if(my<mybigcomparevalue){
     					agreeTest = false;     							     					
     				}
     			}	 
        	 if(mybigop.equals("=")){
     				if(my!=mybigcomparevalue){
     					agreeTest = false;
     						     					
     				}
     			}	 
        	 if(mylessop.equals("<=")){
     				if(my>mylesscomparevalue){
     					agreeTest = false;     							     					
     				}
     			}
        	 if(mylessop.equals("<")){
     				if(my>=mylesscomparevalue){
     					agreeTest = false;	     						     					
     				}
	           }
      	}
       	
       	if (btmz){
       		float mzbigcomparevalue = Float.parseFloat(mzbigvalue);
       	 float mzlesscomparevalue = Float.parseFloat(mzlessvalue);
       	 if(mzbigop.equals(">")){
       		 
    				if(mz<=mzbigcomparevalue){
    					agreeTest = false;     					
    				}
    			}
           	 if(mzbigop.equals(">=")){
	     				if(mz<mzbigcomparevalue){
	     					agreeTest = false;     							     					
	     				}
	     			}	 
           	 if(mzbigop.equals("=")){
	     				if(mz!=mzbigcomparevalue){
	     					agreeTest = false;
	     						     					
	     				}
	     			}	 
           	 if(mzlessop.equals("<=")){
	     				if(mz>mzlesscomparevalue){
	     					agreeTest = false;     							     					
	     				}
	     			}
           	 if(mzlessop.equals("<")){
	     				if(mz>=mzlesscomparevalue){
	     					agreeTest = false;	     						     					
	     				}
   	           }
       	}
  	            	 
  	            	if (btox){
  	            		float oxbigcomparevalue = Float.parseFloat(oxbigvalue);
  	             	 float oxlesscomparevalue = Float.parseFloat(oxlessvalue);
  	             	 if(oxbigop.equals(">")){
  	             		 
  	 	     				if(ox<=oxbigcomparevalue){
  	 	     					agreeTest = false;     					
  	 	     				}
  	 	     			}
  	 	            	 if(oxbigop.equals(">=")){
  	 		     				if(ox<oxbigcomparevalue){
  	 		     					agreeTest = false;     							     					
  	 		     				}
  	 		     			}	 
  	 	            	 if(oxbigop.equals("=")){
  	 		     				if(ox!=oxbigcomparevalue){
  	 		     					agreeTest = false;  	 		     						    					
  	 		     				}
  	 		     			}	 
  	 	            	 if(oxlessop.equals("<=")){
  	 		     				if(ox>oxlesscomparevalue){
  	 		     					agreeTest = false;     							     					
  	 		     				}
  	 		     			}
  	 	            	 if(oxlessop.equals("<")){
  	 		     				if(ox>=oxlesscomparevalue){
  	 		     					agreeTest = false;	     						     					
  	 		     				}
  	         	           }  	            		
  	            	}
  	            	
  	            	if (btoy){
  	            		float oybigcomparevalue = Float.parseFloat(oybigvalue);
  	             	 float oylesscomparevalue = Float.parseFloat(oylessvalue);
  	             	 if(oybigop.equals(">")){
  	             		 
  	 	     				if(oy<=oybigcomparevalue){
  	 	     					agreeTest = false;     					
  	 	     				}
  	 	     			}
  	 	            	 if(oybigop.equals(">=")){
  	 		     				if(oy<oybigcomparevalue){
  	 		     					agreeTest = false;     							     					
  	 		     				}
  	 		     			}	 
  	 	            	 if(oybigop.equals("=")){
  	 		     				if(oy!=oybigcomparevalue){
  	 		     					agreeTest = false;
  	 		     						     					
  	 		     				}
  	 		     			}	 
  	 	            	 if(oylessop.equals("<=")){
  	 		     				if(oy>oylesscomparevalue){
  	 		     					agreeTest = false;     							     					
  	 		     				}
  	 		     			}
  	 	            	 if(oylessop.equals("<")){
  	 		     				if(oy>=oylesscomparevalue){
  	 		     					agreeTest = false;	     						     					
  	 		     				}
  	         	           }
  	           	}
  	            	
  	            	if (btoz){         	
  	            		float ozbigcomparevalue = Float.parseFloat(ozbigvalue);
  	             	 float ozlesscomparevalue = Float.parseFloat(ozlessvalue);
  	             	 if(ozbigop.equals(">")){
  	             		 
  	 	     				if(oz<=ozbigcomparevalue){
  	 	     					agreeTest = false;     					
  	 	     				}
  	 	     			}
  	 	            	 if(ozbigop.equals(">=")){
  	 		     				if(oz<ozbigcomparevalue){
  	 		     					agreeTest = false;     							     					
  	 		     				}
  	 		     			}	 
  	 	            	 if(ozbigop.equals("=")){
  	 		     				if(oz!=ozbigcomparevalue){
  	 		     					agreeTest = false;		
  	 		     				}
  	 		     			}	 
  	 	            	 if(ozlessop.equals("<=")){
  	 		     				if(oz>ozlesscomparevalue){
  	 		     					agreeTest = false;     							     					
  	 		     				}
  	 		     			}
  	 	            	 if(ozlessop.equals("<")){
  	 		     				if(oz>=ozlesscomparevalue){
  	 		     					agreeTest = false;	     						     					
  	 		     				}
  	         	           }
         	              }
  	            	
  	            	if (btlight){         	
  	            		 float lightbigcomparevalue = Float.parseFloat(lightbigvalue);
  	              	 float lightlesscomparevalue = Float.parseFloat(lightlessvalue);
  	              	 if(lightbigop.equals(">")){
  	              		 
  	  	     				if(li<=lightbigcomparevalue){
  	  	     					agreeTest = false;     					
  	  	     				}
  	  	     			}
  	  	            	 if(lightbigop.equals(">=")){
  	  		     				if(li<lightbigcomparevalue){
  	  		     					agreeTest = false;     							     					
  	  		     				}
  	  		     			}	 
  	  	            	 if(lightbigop.equals("=")){
  	  		     				if(li!=lightbigcomparevalue){
  	  		     					agreeTest = false;
  	  		     						     					
  	  		     				}
  	  		     			}	 
  	  	            	 if(lightlessop.equals("<=")){
  	  		     				if(li>lightlesscomparevalue){
  	  		     					agreeTest = false;     							     					
  	  		     				}
  	  		     			}
  	  	            	 if(lightlessop.equals("<")){
  	  		     				if(li>=lightlesscomparevalue){
  	  		     					agreeTest = false;	     						     					
  	  		     				}
  	          	           }
    	              }
  	            	
  	            	if (btpx){         	
            			
  	            		 float pxbigcomparevalue = Float.parseFloat(pxbigvalue);
  	              	 float pxlesscomparevalue = Float.parseFloat(pxlessvalue);
  	              	 if(pxbigop.equals(">")){
  	              		 
  	  	     				if(px<=pxbigcomparevalue){
  	  	     					agreeTest = false;     					
  	  	     				}
  	  	     			}
  	  	            	 if(pxbigop.equals(">=")){
  	  		     				if(px<pxbigcomparevalue){
  	  		     					agreeTest = false;     							     					
  	  		     				}
  	  		     			}	 
  	  	            	 if(pxbigop.equals("=")){
  	  		     				if(px!=pxbigcomparevalue){
  	  		     					agreeTest = false;
  	  		     						     					
  	  		     				}
  	  		     			}	 
  	  	            	 if(pxlessop.equals("<=")){
  	  		     				if(px>pxlesscomparevalue){
  	  		     					agreeTest = false;     							     					
  	  		     				}
  	  		     			}
  	  	            	 if(pxlessop.equals("<")){
  	  		     				if(px>=pxlesscomparevalue){
  	  		     					agreeTest = false;	     						     					
  	  		     				}
  	          	           }
	              }
  	            	
  	            	if (bttemp){         	
  	            		float tempbigcomparevalue = Float.parseFloat(tempbigvalue);
  	             	    float templesscomparevalue = Float.parseFloat(templessvalue);
  	             	 if(tempbigop.equals(">")){
  	             		 
  	 	     				if(temp<=tempbigcomparevalue){
  	 	     					agreeTest = false;     					
  	 	     				}
  	 	     			}
  	 	            	 if(tempbigop.equals(">=")){
  	 		     				if(temp<tempbigcomparevalue){
  	 		     					agreeTest = false;     							     					
  	 		     				}
  	 		     			}	 
  	 	            	 if(tempbigop.equals("=")){
  	 		     				if(temp!=tempbigcomparevalue){
  	 		     					agreeTest = false;
  	 		     						     					
  	 		     				}
  	 		     			}	 
  	 	            	 if(templessop.equals("<=")){
  	 		     				if(temp>templesscomparevalue){
  	 		     					agreeTest = false;     							     					
  	 		     				}
  	 		     			}
  	 	            	 if(templessop.equals("<")){
  	 		     				if(temp>=templesscomparevalue){
  	 		     					agreeTest = false;	     						     					
  	 		     				}
  	         	           }
	              }
  	          
  	            	if (btpressure){ 
  	            		float pressurebigcomparevalue = Float.parseFloat(pressurebigvalue);
  	            	 float pressurelesscomparevalue = Float.parseFloat(pressurelessvalue);
  	            	 if(pressurebigop.equals(">")){
  	            		 
  		     				if(pres<=pressurebigcomparevalue){
  		     					agreeTest = false;     					
  		     				}
  		     			}
  		            	 if(pressurebigop.equals(">=")){
  			     				if(pres<pressurebigcomparevalue){
  			     					agreeTest = false;     							     					
  			     				}
  			     			}	 
  		            	 if(pressurebigop.equals("=")){
  			     				if(pres!=pressurebigcomparevalue){
  			     					agreeTest = false;
  			     						     					
  			     				}
  			     			}	 
  		            	 if(pressurelessop.equals("<=")){
  			     				if(pres>pressurelesscomparevalue){
  			     					agreeTest = false;     							     					
  			     				}
  			     			}
  		            	 if(pressurelessop.equals("<")){
  			     				if(pres>=pressurelesscomparevalue){
  			     					agreeTest = false;	     						     					
  			     				}
  	        	           }
	              }
  	              
  	            	//gps限制條件
  	            	   	if (btlongitude){         	
  	            		 double longitudebigcomparevalue = Double.parseDouble(longitudebigvalue);
  		           		  double longitudelesscomparevalue = Double.parseDouble(longitudelessvalue);
  		             	 if(longitudebigop.equals(">")){
  		             		 
  		 	     				if(longitude<=longitudebigcomparevalue){
  		 	     					agreeTest = false;     					
  		 	     				}
  		 	     			}
  		 	            	 if(longitudebigop.equals(">=")){
  		 		     				if(longitude<longitudebigcomparevalue){
  		 		     					agreeTest = false;     							     					
  		 		     				}
  		 		     			}	 
  		 	            	 if(longitudebigop.equals("=")){
  		 		     				if(longitude!=longitudebigcomparevalue){
  		 		     					agreeTest = false;  		 		     						     					
  		 		     				}
  		 		     			}	 
  		 	            	 if(longitudelessop.equals("<=")){
  		 		     				if(longitude>longitudelesscomparevalue){
  		 		     					agreeTest = false;     							     					
  		 		     				}
  		 		     			}
  		 	            	 if(longitudelessop.equals("<")){
  		 		     				if(longitude>=longitudelesscomparevalue){
  		 		     					agreeTest = false;	     						     					
  		 		     				}
  		         	           }
  		              }	
  	            	
  	            	if (btlatitude){         	
  	            		double latitudebigcomparevalue = Double.parseDouble(latitudebigvalue);
  	           		  double latitudelesscomparevalue = Double.parseDouble(latitudelessvalue);
  	             	 if(latitudebigop.equals(">")){
  	             		 
  	 	     				if(latitude<=latitudebigcomparevalue){
  	 	     					agreeTest = false;     					
  	 	     				}
  	 	     			}
  	 	            	 if(latitudebigop.equals(">=")){
  	 		     				if(latitude<latitudebigcomparevalue){
  	 		     					agreeTest = false;     							     					
  	 		     				}
  	 		     			}	 
  	 	            	 if(latitudebigop.equals("=")){
  	 		     				if(latitude!=latitudebigcomparevalue){
  	 		     					agreeTest = false;
  	 		     						     					
  	 		     				}
  	 		     			}	 
  	 	            	 if(latitudelessop.equals("<=")){
  	 		     				if(latitude>latitudelesscomparevalue){
  	 		     					agreeTest = false;     							     					
  	 		     				}
  	 		     			}
  	 	            	 if(latitudelessop.equals("<")){
  	 		     				if(latitude>=latitudelesscomparevalue){
  	 		     					agreeTest = false;	     						     					
  	 		     				}
  	         	           }
 		              }	
  	            	
  	            	if (btaltitude){   
	           		  double altitudebigcomparevalue = Double.parseDouble(altitudebigvalue);
	           		  double altitudelesscomparevalue = Double.parseDouble(altitudelessvalue);
	             	 if(altitudebigop.equals(">")){
	             		 
	 	     				if(altitude<=altitudebigcomparevalue){
	 	     					agreeTest = false;     					
	 	     				}
	 	     			}
	 	            	 if(altitudebigop.equals(">=")){
	 		     				if(altitude<altitudebigcomparevalue){
	 		     					agreeTest = false;     							     					
	 		     				}
	 		     			}	 
	 	            	 if(altitudebigop.equals("=")){
	 		     				if(altitude!=altitudebigcomparevalue){
	 		     					agreeTest = false;
	 		     						     					
	 		     				}
	 		     			}	 
	 	            	 if(altitudelessop.equals("<=")){
	 		     				if(altitude>altitudelesscomparevalue){
	 		     					agreeTest = false;     							     					
	 		     				}
	 		     			}
	 	            	 if(altitudelessop.equals("<")){
	 		     				if(altitude>=altitudelesscomparevalue){
	 		     					agreeTest = false;	     						     					
	 		     				}
	         	           }
		              }	
  	            	 
  	            	if (btbearing){        	
            			
  	            		float bearingbigcomparevalue = Float.parseFloat(bearingbigvalue);
  	             	 float bearinglesscomparevalue = Float.parseFloat(bearinglessvalue);
  	             	 if(bearingbigop.equals(">")){
  	             		 
  	 	     				if(bearing<=bearingbigcomparevalue){
  	 	     					agreeTest = false;     					
  	 	     				}
  	 	     			}
  	 	            	 if(bearingbigop.equals(">=")){
  	 		     				if(bearing<bearingbigcomparevalue){
  	 		     					agreeTest = false;     							     					
  	 		     				}
  	 		     			}	 
  	 	            	 if(bearingbigop.equals("=")){
  	 		     				if(bearing!=bearingbigcomparevalue){
  	 		     					agreeTest = false;
  	 		     						     					
  	 		     				}
  	 		     			}	 
  	 	            	 if(bearinglessop.equals("<=")){
  	 		     				if(bearing>bearinglesscomparevalue){
  	 		     					agreeTest = false;     							     					
  	 		     				}
  	 		     			}
  	 	            	 if(bearinglessop.equals("<")){
  	 		     				if(bearing>=bearinglesscomparevalue){
  	 		     					agreeTest = false;	     						     					
  	 		     				}
  	         	           }
		              }	
  	            	
                 if (btaccuracy){        	
            			
                	 float accuracybigcomparevalue = Float.parseFloat(accuracybigvalue);
                	 float accuracylesscomparevalue = Float.parseFloat(accuracylessvalue);
                	 if(accuracybigop.equals(">")){
                		 
    	     				if(accuracy<=accuracybigcomparevalue){
    	     					agreeTest = false;     					
    	     				}
    	     			}
    	            	 if(accuracybigop.equals(">=")){
    		     				if(accuracy<accuracybigcomparevalue){
    		     					agreeTest = false;     							     					
    		     				}
    		     			}	 
    	            	 if(accuracybigop.equals("=")){
    		     				if(accuracy!=accuracybigcomparevalue){
    		     					agreeTest = false;
    		     						     					
    		     				}
    		     			}	 
    	            	 if(accuracylessop.equals("<=")){
    		     				if(accuracy>accuracylesscomparevalue){
    		     					agreeTest = false;     							     					
    		     				}
    		     			}
    	            	 if(accuracylessop.equals("<")){
    		     				if(accuracy>=accuracylesscomparevalue){
    		     					agreeTest = false;	     						     					
    		     				}
            	           }
		              }	
  	            	
                 if (btspeed){        	
                	 float speedbigcomparevalue = Float.parseFloat(speedbigvalue);
                	 float speedlesscomparevalue = Float.parseFloat(speedlessvalue);
                	 if(speedbigop.equals(">")){
                		 
    	     				if(speed<=speedbigcomparevalue){
    	     					agreeTest = false;     					
    	     				}
    	     			}
    	            	 if(speedbigop.equals(">=")){
    		     				if(speed<speedbigcomparevalue){
    		     					agreeTest = false;     							     					
    		     				}
    		     			}	 
    	            	 if(speedbigop.equals("=")){
    		     				if(speed!=speedbigcomparevalue){
    		     					agreeTest = false;
    		     						     					
    		     				}
    		     			}	 
    	            	 if(speedlessop.equals("<=")){
    		     				if(speed>speedlesscomparevalue){
    		     					agreeTest = false;     							     					
    		     				}
    		     			}
    	            	 if(speedlessop.equals("<")){
    		     				if(speed>=speedlesscomparevalue){
    		     					agreeTest = false;	     						     					
    		     				}
            	           }
		              }	
                  
        	Log.v(TAG,"agreeTest"+String.valueOf(agreeTest));
			return agreeTest;
		}
		
        	//取得上傳格式
		private String getResultJSON() throws JSONException {

    		// for multiple grammar patterns
    		int checkflag = 0;

    		String stringResultJSON = "";
    		Log.v(TAG,"getResultJSON");
    		stringResultJSON = queryData(sqlCase);		
    		return stringResultJSON;
    	}
              //將SQLite資料轉換成實驗紀錄檔ELF
    	private String queryData(String sql) throws JSONException {
    		Log.v(TAG,"queryData");
    		List<ResultData> eachRowData = null;
    		Log.v(TAG,sql);
    		SQLiteDatabase database = dbhelper.getReadableDatabase();
    		Cursor cursor = database.rawQuery(sql, null);

    		JSONObject bodyJSON = null;
    		String stringJSON = "";
    		   		
    		int exidIndex = 0;
    		int datetimeIndex = 0;
    		int itemIndex = 0;
    		int attributeIndex = 0;
    		int valueIndex = 0;
    		int deviceidIndex = 0;
    		int fgroupIndex = 0;
            int dbcount = cursor.getCount();
            String itemif = "";
            String attributeif = "";
            int groupif = 0;
    		System.out.println("cursor count: " + cursor.getCount());
            
    		if (cursor.getCount() > 0) {
    			
    			eachRowData = new ArrayList<ResultData>();
    			datetimeIndex = cursor.getColumnIndex("dateime");
    			exidIndex = cursor.getColumnIndex("exid");
    			deviceidIndex = cursor.getColumnIndex("clientdeviceid");
    		
    			for (int i = 0; i < 3; i++) {
    				try {
    					itemIndex = cursor.getColumnIndex("item");  //2
    					attributeIndex = cursor.getColumnIndex("attribute");  //3
    					valueIndex = cursor.getColumnIndex("value");  //4
    					
    				} catch (Exception e) {
    				}
    			}
    		}  
    		// flexible query patterns
    		
    		cursor.moveToFirst();
    		while (cursor.moveToNext()){   		
    		ResultData each = new ResultData();
    		attributeif = cursor.getString(attributeIndex);				 
    	    	  	    		Log.v(TAG,attributeif);
    	    	  	    		Strvalue = cursor.getString(valueIndex);
    	    	  	    		
    	    				 if (attributeif.equals("latitude")){
    	     					 Log.v(TAG,"attributeif==latitude");
    	         			     each.setLatitude(Strvalue);
    	    				 }
                              if (attributeif.equals("ax")){
    	    					 Log.v(TAG,cursor.getString(valueIndex));
    	        			     each.setAX(Strvalue);
    	    				 }
                              if (attributeif.equals("ay")){
    	    					 Log.v(TAG,cursor.getString(valueIndex));
    	        			     each.setAY(Strvalue);
    	    				 }
                              if (attributeif.equals("az")){
    	    					 Log.v(TAG,cursor.getString(valueIndex));
    	        			     each.setAZ(Strvalue);
    	    				 }
                              if (attributeif.equals("ox")){
    	    					
    	        			     each.setOX(cursor.getString(valueIndex));
    	    				 }
                              if (attributeif.equals("oy")){
    	    					
    	        			     each.setOY(cursor.getString(valueIndex));
    	    				 }
                              if (attributeif.equals("oz")){
    	    					
    	        			     each.setOZ(cursor.getString(valueIndex));
    	    				 }
                              if (attributeif.equals("mx")){
    	    					
    	        			     each.setMX(cursor.getString(valueIndex));
    	    				 }
                              if (attributeif.equals("my")){
    	    					
    	        			     each.setMY(cursor.getString(valueIndex));
    	    				 }
    	    				 if (attributeif.equals("mz")){
    	    					
    	        			     each.setMZ(cursor.getString(valueIndex));
    	    				 }
    	    				 if (attributeif.equals("li")){
    	    					
    	        			     each.setLI(cursor.getString(valueIndex));
    	    				 }
    	    				 if (attributeif.equals("pres")){
    	    					
    	        			     each.setPRES(cursor.getString(valueIndex));
    	    				 }
    	    				 if (attributeif.equals("px")){
    	    				
    	        			     each.setPX(cursor.getString(valueIndex));
    	    				 }
    	    				 if (attributeif.equals("temp")){
    	    					
    	        			     each.setTEMP(cursor.getString(valueIndex));
    	    				 }
    	    				 if (attributeif.equals("rssi")){
    	    					each.setRSSI(cursor.getString(valueIndex));
    	    				 }
    	    				 if (attributeif.equals("ssid")){
    	    					 
    	        			     each.setSSID(cursor.getString(valueIndex));
    	    				 }
    	    				 if (attributeif.equals("screen")){
    	    					
    	        			     each.setSCREEN(cursor.getString(valueIndex));
    	    				 }
    	    				 if (attributeif.equals("extmedia")){
    	    					 
    	        			     each.setEXTMEDIA(cursor.getString(valueIndex));
    	    				 }
    	    				 if (attributeif.equals("apkact")){
    	    					 
    	        			     each.setAPKACT(cursor.getString(valueIndex));
    	    				 }
    	    				 if (attributeif.equals("pkgname")){
    	    					
    	        			     each.setPKGNAME(cursor.getString(valueIndex));
    	    				 }
    	    				 if (attributeif.equals("powact")){
    	    					 
    	        			     each.setPOWER(cursor.getString(valueIndex));
    	    				 }
    	    				  if (attributeif.equals("powerstatus")){
    	    					 
    	        		          each.setPOWERSTATUS(cursor.getString(valueIndex));
    	    				 }
    	    				 if (attributeif.equals("language")){
    	    					  each.setLANGUAGE(cursor.getString(valueIndex));
    	    				 }
    	    				 if (attributeif.equals("country")){
    	    					 
    	        			     each.setCOUNTRY(cursor.getString(valueIndex));
    	    				 }
    	    				 if (attributeif.equals("times")){
    	    					 
    	        			     each.setMSGTIMES(cursor.getString(valueIndex));
    	    				 }
    	    				 if (attributeif.equals("gsmrssi")){
    	    					 
    	        			     each.setGSMRSSI(cursor.getString(valueIndex));
    	    				 }
    	    				 if (attributeif.equals("gsmspn")){
    	    					
    	        			     each.setGSMSPN(cursor.getString(valueIndex));
    	    				 }
    	    				 /*
    	    				 if (attributeif.equals("msgfrom")){
    	    					 //each.setItem("sms");
    	        			     each.setMSGFROM(cursor.getString(valueIndex));
    	    				 }
    	    				 if (attributeif.equals("msgBody")){
    	    					// each.setItem("sms");
    	        			     each.setMSGBODY(cursor.getString(valueIndex));
    	    				 }       */
    	    				 if (attributeif.equals("incomingcount")){
    	    					 //each.setItem("call");
    	        			     each.setCALLINCOUNT(cursor.getString(valueIndex));
    	    				 }
    	    				 if (attributeif.equals("outcomingcount")){
    	    					 //each.setItem("call");
    	        			     each.setCALLOUTCOUNT(cursor.getString(valueIndex));
    	    				 }
    	    				 if (attributeif.equals("callinalltime")){
    	    					// each.setItem("call");
    	        			     each.setCALLINALL(cursor.getString(valueIndex));
    	    				 }
    	    				 if (attributeif.equals("calloutalltime")){
     	    					// each.setItem("call");
     	        			     each.setCALLOUTALL(cursor.getString(valueIndex));
     	    				 }
    	    				 if (attributeif.equals("title")){
    	    					// each.setItem("browser");
    	        			     each.setBROWSERTITLE(cursor.getString(valueIndex));
    	    				 }
    	    				 if (attributeif.equals("url")){
    	    					 //each.setItem("browser");
    	        			     each.setBROWSERURL(cursor.getString(valueIndex));
    	    				 }
    	    				 if (attributeif.equals("longitude")){
    	    					// each.setItem("gps");
    	    					 Log.v(TAG,"attributeif==longitude");
    	        			     each.setLongitude(cursor.getString(valueIndex));
    	    				 }    	    				 
    	    				 if (attributeif.equals("accuracy")){
    	    					// each.setItem("gps");
    	        			     each.setAccuracy(cursor.getString(valueIndex));
    	    				 }
    	    				 if (attributeif.equals("speed")){
    	    					// each.setItem("gps");
    	        			     each.setSpeed(cursor.getString(valueIndex));
    	    				 }
    	    				 if (attributeif.equals("bearing")){
    	    					// each.setItem("gps");
    	        			     each.setBearing(cursor.getString(valueIndex));
    	    				 }
    	    				 if (attributeif.equals("altitude")){
    	    					//each.setItem("gps");
    	        			     each.setAltitude(cursor.getString(valueIndex));
    	    				 }
    	    				 if (attributeif.equals("callnumber")){
     	    					//each.setItem("gps");
     	        			     each.setCALLNUMBER(cursor.getString(valueIndex));
     	    				 }
    	    				 if (attributeif.equals("receivenumber")){
      	    					//each.setItem("gps");
      	        			     each.setRECEIVENUMBER(cursor.getString(valueIndex));
      	    				 }
    	    				 if (attributeif.equals("calltime")){
      	    					//each.setItem("gps");
      	        			     each.setCALLTIME(cursor.getString(valueIndex));
      	    				 }
     	    				 if (attributeif.equals("receivetime")){
       	    					//each.setItem("gps");
       	        			     each.setRECEIVETIME(cursor.getString(valueIndex));
       	    				 }
     	    				 
    	    				 
    	    				  each.setDatetime(cursor.getString(datetimeIndex));
    	           			  each.setExId(cursor.getString(exidIndex));
    	           			  each.setDeviceId(cursor.getString(deviceidIndex));
    	           			  eachRowData.add(each);
    	    				// Log.v(TAG,"do second");
    				
    			                  	}
    	                       
    			
    		
    				//讀取arrayList，並將資料轉換成上傳JSON格式
    			ResultData rd = null;
        		
        		// read arrayList based on checkflag
        		Iterator<ResultData> iter = eachRowData.iterator();
        		bodyJSON = new JSONObject();    			
    			JSONArray logs = new JSONArray();
        		//JSONObject logs = new JSONObject();
    			bodyJSON.put("logs", logs);
    			
        		while (iter.hasNext()) {
        			
        				rd = new ResultData();
        				rd = iter.next();
        				
        				bodyJSON.put("experimentid", rd.getExId());
        				bodyJSON.put("clientdeviceid", rd.getDeviceId());
        				    JSONObject log = new JSONObject();
        					JSONObject sensor = new JSONObject();
        					JSONObject acc = new JSONObject();
        					JSONObject ori = new JSONObject();
        					JSONObject magn = new JSONObject();
        					JSONObject li = new JSONObject();
        					JSONObject pres = new JSONObject();
        					JSONObject temp = new JSONObject();
        					JSONObject px = new JSONObject();				
        					JSONObject gps = new JSONObject(); 
        					JSONObject wifi = new JSONObject();
        					JSONObject screen = new JSONObject();
        					JSONObject extmedia = new JSONObject();
        					JSONObject pow = new JSONObject();
        					JSONObject local = new JSONObject();
        					JSONObject sms = new JSONObject();
        					JSONObject call = new JSONObject();
        					JSONObject browser = new JSONObject();
        					JSONObject pkg = new JSONObject();
        					JSONObject gsm = new JSONObject();
        					JSONObject callact = new JSONObject();
        					
        					//log.get("log");
        					
        					logs.put(log);
        					//logs.put(log); 	
        					
        					//bodyJSON.put("logs", log);
        					
        					if(Bacc==true|Bori==true|Bmagn==true|Bpres==true|Btemp==true|Bli==true|Bpx==true|Bwifi==true|Bbrowser==true|Bcall==true|Bsms==true|Blocale==true|Bpow==true|Bapp==true|Bmedia==true|Bscreen==true|Bloc==true){
        					log.put("sensor", sensor);
        					}
        					/*
        					if(Bwifi==true|Bbrowser==true|Bcall==true|Bsms==true|Blocale==true|Bpow==true|Bapp==true|Bmedia==true|Bscreen==true){
        					log.put("behavior", behavior);
        					}      */
        					if(Bacc==true){
        					sensor.put("acc", acc);
        					}
        					if(taz==true){
        					acc.put("az", rd.getAZ());
        					}
        					if(tay==true){
        					acc.put("ay", rd.getAY());
        					}
        					if(tax==true){
        					acc.put("ax", rd.getAX());
        					}
        					
        					if(Bori==true){
        					sensor.put("ori", ori);
        					}
        					if(toz==true){
        					ori.put("oz", rd.getOZ());
        					}
        					if(toy==true){
        					ori.put("oy", rd.getOY());
        					}
        					if(tox==true){
        					ori.put("ox", rd.getOX());
        					}
        					
        					if(Bmagn==true){
        					sensor.put("magn", magn);
        					}
        					if(tmz==true){
        					magn.put("mz", rd.getMZ());
        					}
        					if(tmy==true){
        					magn.put("my", rd.getMY());
        					}
        					if(tmz==true){
        					magn.put("mx", rd.getMX());
        					}
        					
        					if(Bpx==true){
        					sensor.put("px", px);
        					}
        					px.put("px", rd.getPX());
        					
        					if(Bli==true){
        					sensor.put("li", li);
        					}
        					li.put("li", rd.getLI());
        					
        					if(Btemp==true){
        					sensor.put("temp", temp);
        					}
        					temp.put("celsius", rd.getTEMP());
        					
        					if(Bpres==true){
        					sensor.put("pres", pres);
        					}
        					pres.put("pres", rd.getPRES());

        					if(Bloc==true){
        						sensor.put("gps", gps);
        					}
        					if(tlongitude==true){
        					gps.put("longitude", rd.getLongitude());
        					}
        					if(tlatitude==true){
        					gps.put("latitude", rd.getLatitude());}
        					if(taccuracy==true){
        					gps.put("accuracy", rd.getAccuracy());}
        					if(tspeed==true){
        					gps.put("speed", rd.getSpeed());}
        					if(tbearing==true){
        					gps.put("bearing", rd.getBearing());}
        					if(taltitude==true){
        					gps.put("altitude", rd.getAltitude());}
        					
        					if(Bwifi==true){
        						sensor.put("wifi",wifi);
        					}
        					if(trssi==true){
        					wifi.put("rssi", rd.getRSSI());
        					}
        					if(tssid==true){
        					wifi.put("ssid", rd.getSSID());}
        					
        					if(Bscreen==true){
        						sensor.put("screen",screen);
        					}
        					screen.put("screen", rd.getSCREEN());
        					
        					if(Bmedia==true){
        						sensor.put("extmedia",extmedia);
        					}
        					extmedia.put("extmedia", rd.getEXTMEDIA());
        					
        					if(Bapp==true){
        						sensor.put("pkg",pkg);
        					}
        					if(tpkgname==true){
        					pkg.put("pkgname", rd.getPKGNAME());}
        					if(tapkact==true){
        					pkg.put("apkact", rd.getAPKACT());}
        					
        					if(Bpow==true){
        						sensor.put("pow",pow);
        					}
        					if(tpow==true){
        					pow.put("powact", rd.getPOWER());
        					}
        					if(tpowerstatus==true){
        						pow.put("powerstatus", rd.getPOWERSTATUS());
            				}		
        					
        					if(Blocale==true){
        						sensor.put("local",local);
        					}
        					if(tlan==true){
        					local.put("lan", rd.getLANGUAGE());}
        					if(tcountry==true){
        					local.put("country", rd.getCOUNTRY());}
        					
        					if(Bgsm==true){
        						sensor.put("gsm",gsm);
        					}
        					if(tgsmrssi==true){
        					gsm.put("gsmrssi", rd.getGSMRSSI());}
        					if(tcountry==true){
        					gsm.put("gsmspn", rd.getGSMSPN());}
        					
        					if(Bsms==true){
        						sensor.put("sms",sms);
        					}
        					//sms.put("msgFrom", rd.getMSGFROM());
        					//sms.put("msgbody", rd.getMSGBODY());
        					sms.put("times", rd.getMSGTIMES());
        					
        					if(Bcall==true){
        						sensor.put("call",call);
        					}
        					if(tincomingcount==true){
        					call.put("incomingcount", rd.getCALLINCOUNT());
        					}
        					if(toutcomingcount==true){
        					call.put("outcomingcount", rd.getCALLOUTCOUNT());
        					}
        					if(tinalltime==true){
        					call.put("callinalltime", rd.getCALLINALL());
        					}
        					if(toutalltime==true){
            					call.put("calloutalltime", rd.getCALLOUTALL());
            					}
        					
        					if(Bbrowser==true){
        						sensor.put("browser",browser);
        					}
        					if(turl==true){
        					browser.put("url", rd.getBROWSERURL());}
        					if(ttitle==true){
        					browser.put("title", rd.getBROWSERTITLE());}
        					
        					if(Bcallact==true){
        						sensor.put("callact",callact);
        					}
        					if(tcallnumber==true){
        						callact.put("callnumber", rd.getCALLNUMBER());
            					}
            					if(treceivenumber==true){
            						callact.put("receivenumber", rd.getRECEIVENUMBER());
            					}
            					if(tcalltime==true){
            						callact.put("calltime", rd.getCALLTIME());
            					}
            					if(treceivetime==true){
            						callact.put("receivetime", rd.getRECEIVETIME());
                			}        					
            					
        					log.put("time", rd.getDatetime());    
        					
        					
        		}
        			
        				// text appends more than one record
        				stringJSON += "," + bodyJSON.toString(); 		 
    				
    		System.out.println("return JSON string: "+ stringJSON.substring(1, stringJSON.length()));
    		// omit the first comma in order to string concatenation
    		return stringJSON.substring(1, stringJSON.length());
    	          
    	}
    	
    	
    	
		
		// a backup to SDcard
    	@SuppressWarnings("resource")
    	private void backupDBtoSD() {
    		File sd = Environment.getExternalStorageDirectory();
    		File data = Environment.getDataDirectory();

    		String currentDBPath = "//data//com.example.plap.project//databases//clap.db";
    		String backupDBPath = "clap.db";

    		File currentDB = new File(data, currentDBPath);
    		File backupDB = new File(sd, backupDBPath);

    		FileChannel src;
    		try {
    			src = new FileInputStream(currentDB).getChannel();
    			FileChannel dst = new FileOutputStream(backupDB).getChannel();

    			try {
    				dst.transferFrom(src, 0, src.size());
    				src.close();
    				dst.close();
    			} catch (IOException e) {
    			}

    		} catch (FileNotFoundException e) {
    		}
    	//	Toast.makeText(getApplicationContext(),
    		//		"Generating a backup to SDCard", Toast.LENGTH_SHORT).show();
    	}
    	
    	protected String getASCIIContentFromEntity(HttpEntity entity)
    			throws IllegalStateException, IOException {
    		InputStream in = entity.getContent();
    		StringBuffer out = new StringBuffer();
    		int n = 1;
    		while (n > 0) {
    			byte[] b = new byte[4096];
    			n = in.read(b);
    			if (n > 0)
    				out.append(new String(b, 0, n));
    		}
    		return out.toString();
    	}    	
    	
        //註冊GPS監聽器
    	private void processLoc() {

    		locList = new LinkedList<Location>();
    		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
    		// Location location =
    		// locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
    		Log.v(TAG,"locexe");
    		try {
    			if(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){  //如果位於室外，透過GPS_PROVIDER 衛星定位
    				
    				locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0,myLocationListener);    //.GPS_PROVIDER,最少時間,最短距離,this
    			          
    			}else if(locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)){   //如果位於室內，透過NETWORK_PROVIDER 網路定位
    				
    				locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,0,0,myLocationListener);
    		}
    			//locationManager.requestLocationUpdates(
    				//	LocationManager.NETWORK_PROVIDER, 0, 0, myLocationListener);
    		} catch (Exception e) {

    		}

    	}
    	
    	//取得行動業者名稱
    	private void processGsm() {

    		MyPhoneStateListener MyListener   = new MyPhoneStateListener();
    		TelephonyManager Tel = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
            Tel.listen(MyListener,PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);
            gsmspn = Tel.getNetworkOperatorName();
    	}

    	public Location getLocation() {
    		Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
    		if(location == null) 
    			location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
    	    return location;
    	}
    	//取得受測者GPS相關資訊
    	private LocationListener myLocationListener = new LocationListener() {

    		public void onLocationChanged(Location location) {
    			
    			Location myloc = getLocation();
    			latitude = myloc.getLatitude();
        		longitude = myloc.getLongitude();
        		speed = myloc.getSpeed();
        		accuracy = myloc.getAccuracy();
        		bearing  = myloc.getBearing();
        		altitude = myloc.getAltitude();
    			
    		}
	
    		public void onProviderDisabled(String provider) {
    		}

    		public void onProviderEnabled(String provider) {
    		}

    		public void onStatusChanged(String provider, int status, Bundle extras) {
    		}
    	};    
    	
    		    		
    		
    		 //r1 註冊加速度感應器
    	     private void processAcc() {
    			sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
    		    sensorManager.registerListener(accelerationListener, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);
    			Log.v(TAG,"processacc");
    		}
    		//r4 註冊方向感應器
    		@SuppressWarnings("deprecation")
    		private void processOri() {
    			sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);    		
    			sensorManager.registerListener(orientationListener, sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION), SensorManager.SENSOR_DELAY_NORMAL);
    			Log.v(TAG,"processOri");
    		}
    		
    		//r5註冊磁性感應器
    		private void processMagn() {
   				sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
   	            sensorManager.registerListener(magneticListener, sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD), SensorManager.SENSOR_DELAY_NORMAL);   
    			Log.v(TAG,"processMagn");
    			}
    		
    		//r6註冊亮度感應器
    	    private void processLi(){
    	   	   	sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
    		   	sensorManager.registerListener(lightListener, sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT), SensorManager.SENSOR_DELAY_NORMAL); 
    			   		   }
    	    //r7註冊壓力感應器
    	    private void processPressure(){
    		   	sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
    			sensorManager.registerListener(pressureListener, sensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE), SensorManager.SENSOR_DELAY_NORMAL); 
    		  		   }         
    		 //r8註冊遠近感應器
    		   private void processProx(){
    		     	sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
    			   	sensorManager.registerListener(proximityListener, sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY), SensorManager.SENSOR_DELAY_NORMAL); 
    					    }
    		   
    		   //r9註冊溫度感應器	     		
    		   @SuppressWarnings("deprecation")
    			private void processTemp(){
    		    sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
    			sensorManager.registerListener(temperatureListener, sensorManager.getDefaultSensor(Sensor.TYPE_TEMPERATURE), SensorManager.SENSOR_DELAY_NORMAL); 
    		   		   }  
    		  //開啟各屬性監聽器，取得感應器數值        
    		@SuppressWarnings("deprecation")
    		private SensorEventListener accelerationListener = new SensorEventListener() {
    			
    			public void onAccuracyChanged(Sensor sensor, int acc) {
    			}
    			public void onSensorChanged(SensorEvent event) {
    				Log.v(TAG,"accelerationListener start");
    				ax = event.values[0];
    				ay = event.values[1];
    	 			az=  event.values[2];
    	 			
    					}	
    			
    		};
    		
    		
    		private SensorEventListener orientationListener = new SensorEventListener() {
    			public void onAccuracyChanged(Sensor sensor, int ori) {
    			}

    			public void onSensorChanged(SensorEvent event) {
    				
    				ox = event.values[0];
    	 			oy = event.values[1];
    	 			oz=  event.values[2];
    	 			
    						
    					}	
    		};
    		
    		private SensorEventListener magneticListener = new SensorEventListener() {
    			public void onAccuracyChanged(Sensor sensor, int magn) {
    			}

    			public void onSensorChanged(SensorEvent event) {
    				
    				mx = event.values[0];
    	 			my = event.values[1];
    	 			mz=  event.values[2];
    	 			
    						
    					}	
    		};
    		
    		private SensorEventListener lightListener = new SensorEventListener() {
    			@Override
    			public void onAccuracyChanged(Sensor sensor, int accuracy) {
    			}
    	 
    			@Override
    			public void onSensorChanged(SensorEvent event) {
    				
    				li = event.values[0];
    				
    							}
    		};
    		
    		private SensorEventListener pressureListener = new SensorEventListener() {
    			@Override
    			public void onAccuracyChanged(Sensor sensor, int accuracy) {
    			}
    	 
    			@Override
    			public void onSensorChanged(SensorEvent event) {
    				
    				pres = event.values[0];
    				
    							}
    		};
    		
    		private SensorEventListener proximityListener = new SensorEventListener() {
    				@Override
    				public void onAccuracyChanged(Sensor sensor, int accuracy) {
    				}
    		 
    				@Override
    				public void onSensorChanged(SensorEvent event) {
    					
    					px = event.values[0];
    					
    								}
    			};
    			
    			private SensorEventListener temperatureListener = new SensorEventListener() {
    				@Override
    				public void onAccuracyChanged(Sensor sensor, int accuracy) {
    				}
    		 
    				@Override
    				public void onSensorChanged(SensorEvent event) {
    					
    					temp = event.values[0];
    					
    								}
    			};
    			
    			//各屬性執行緒
    			private Runnable r1 = new Runnable() {
    				
    				public void run() {
    					
    					if (tax){
    					processAcc();    //註冊加速度感應器，新增監聽器	
    					insertRecord("acc","ax",String.valueOf(ax));}    	//寫入資料庫數值				
    					sensorManager.unregisterListener(accelerationListener);  //取消加速度感應器註冊
    					mThreadHandler.postDelayed(this, AXINTERVAL);   //停止一段時間，視研究員選擇間隔而定
    				}
    			};
    			
    			private Runnable r2 = new Runnable() {
    				public void run() {
    					
    					if(tay){
    						processAcc();	
    					insertRecord("acc","ay",String.valueOf(ay));
    					}
    					sensorManager.unregisterListener(accelerationListener);
    					mThreadHandler.postDelayed(this, AYINTERVAL);    					
    				}
    			};
    			
    			
    			private Runnable r3 = new Runnable() {
    				public void run() {
    					
    					if(taz){
    						processAcc();
    					insertRecord("acc","az",String.valueOf(az));	
    					}
    					sensorManager.unregisterListener(accelerationListener);
    					mThreadHandler.postDelayed(this, AZINTERVAL);  				
    				}
    			};    
    			
    			private Runnable r4 = new Runnable() {
    				public void run() {
    					
    					if (tox){
    						processOri();
    					insertRecord("ori","ox",String.valueOf(ox));}
    					sensorManager.unregisterListener(orientationListener);
    					mThreadHandler.postDelayed(this, OXINTERVAL);
    				}
    			};
    			
    			private Runnable r5 = new Runnable() {
    				public void run() {
    					
    					if(toy){
    						processOri();
        				insertRecord("ori","oy",String.valueOf(oy));
        				}
    					sensorManager.unregisterListener(orientationListener);
    					mThreadHandler.postDelayed(this, OYINTERVAL);
    				}
    			};
    			
    			private Runnable r6 = new Runnable() {
    				public void run() {
    					
    					if(toz){
    						processOri();
        				insertRecord("ori","oz",String.valueOf(oz));}
    					sensorManager.unregisterListener(orientationListener);
        				mThreadHandler.postDelayed(this, OZINTERVAL);
    					    				}
    			};
    			
    			private Runnable r7 = new Runnable() {
    				public void run() {
    					
    					if (tmx){
    						processMagn();
    						insertRecord("magn","mx",String.valueOf(mx));}
    						sensorManager.unregisterListener(magneticListener);
    						mThreadHandler.postDelayed(this, MXINTERVAL);
    					
    				}
    			};
    			
    			private Runnable r8 = new Runnable() {
    				public void run() {
    					
    					if(tmy){
    						processMagn();
            				insertRecord("magn","my",String.valueOf(my));}
    					    sensorManager.unregisterListener(magneticListener);
            				mThreadHandler.postDelayed(this, MYINTERVAL);
    					
    				}
    			};
    			
    			private Runnable r9 = new Runnable() {
    				public void run() {
    					
    					if(tmz){
    						processMagn();
            				insertRecord("magn","mz",String.valueOf(mz));}
    					 sensorManager.unregisterListener(magneticListener);
            				mThreadHandler.postDelayed(this, MZINTERVAL);
    					
    				}
    			};   
    			
    			private Runnable r10 = new Runnable() {
    				public void run() {
    					
    					if (tlight){
    						processLi();
    						insertRecord("li","li",String.valueOf(li));
    					}
    					sensorManager.unregisterListener(lightListener);
    					mThreadHandler.postDelayed(this, LIINTERVAL);
    				}
    			};
    			
    			private Runnable r11 = new Runnable() {
    				public void run() {
    					
    					if (tpressure){
    						processPressure();
    						insertRecord("pres","pres",String.valueOf(pres));
    					}
    					sensorManager.unregisterListener(pressureListener);
    					mThreadHandler.postDelayed(this, PRESINTERVAL);
    				}
    			};    
    				
    			private Runnable r12 = new Runnable() {
    				public void run() {
    					
    					if (tpx){
    						processProx();
    					insertRecord("px","px",String.valueOf(px));
    					}
    					sensorManager.unregisterListener(proximityListener);
    					mThreadHandler.postDelayed(this, PXINTERVAL);
    				}
    			};
    			
    			private Runnable r13 = new Runnable() {
    				public void run() {
    					
    					if (ttemp){
    						processTemp();
    						insertRecord("temp","temp",String.valueOf(temp));
        					}
    					sensorManager.unregisterListener(temperatureListener);
    					mThreadHandler.postDelayed(this, TEMPINTERVAL);
    				}
    			};
    			
    			private Runnable r14 = new Runnable() {
    				public void run() {
    					getWifiStrength();
    					//trssi
    					
    					if (Bwifi){
    						insertRecord("wifi","rssi",rssi);
    						insertRecord("wifi","ssid",ssid);
            				}else{
            					
            				}
    					mThreadHandler.postDelayed(this, RSSIINTERVAL);
    				}
    			};
    			
    		/*	private  Runnable r15 = new Runnable() {
    				public void run() {
    					getWifiStrength();
    					 if(tssid){
            				insertRecord("wifi","ssid",ssid);
            				}else{
            					
            				}
    					mThreadHandler.postDelayed(this, SSIDINTERVAL);
    				}
    			};   */
    			/*
    			private  Runnable r16 = new Runnable() {
    				public void run() {
    					//bookget();
    					if (ttitle){
    						insertRecord("browser","title",title);
            				}else {
            					
            				}
    					mThreadHandler.postDelayed(this, INTERVAL);
    				}
    			};  
    			
    			
    			private  Runnable r17 = new Runnable() {
    				public void run() {
    					//bookget();
    					if (turl){
    						\insertRecord("browser","url",url);
            				}else {
            					
            				}
    					mThreadHandler.postDelayed(this, INTERVAL);
    				}
    			};     */
    			             
    			private Runnable r18 = new Runnable() {
    				public void run() {
    					getResultCount();
    					mThreadHandler.postDelayed(this, 10000);
    				}
    			};     				
    			//上傳執行緒
    			private Runnable r19 = new Runnable() {
    				public void run() {
    					String message = null;
						try {
							message = getResultJSON();
						} catch (JSONException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
    					ConnectionFactory factory = new ConnectionFactory();
    					factory.setHost(AMQPHOST);
    					factory.setVirtualHost(AMQPVHOST);
    					factory.setUsername(AMQPUSER);
    					factory.setPassword(AMQPPASSWORD);
    					Connection connection = null;
    					Channel channel = null;
    		            Log.v(TAG,"nontry");
    					try {
    						connection = factory.newConnection();
    						channel = connection.createChannel();
    						
    						
    						
    						//BASE64Encoder base64encoder = new BASE64Encoder();
    						byte[] base64enc = Base64.encode(message.getBytes(),Base64.DEFAULT);
    						//String base64enc = base64encoder.encode(message.getBytes());
    						channel.queueDeclare(QUEUE_NAME, false, false, false, null);
    						//channel.basicPublish("", QUEUE_NAME, null, message.getBytes()); //傳送加密資料至rabbitmq駐列
    						channel.basicPublish("", QUEUE_NAME, null, base64enc);
    						//.getBytes()
    						//channel.basicPublish("", QUEUE_NAME, null, message.getBytes());

    						channel.close();
    						connection.close();
    						 Log.v(TAG,"try");
    					} catch (Exception e) {
    						System.out.println(e.getMessage());
    					}
    					System.out.println("Thread r19 is done");
    					deleteTable();
    					CheckExperimentUpdate();
    				}
    			};   
    			
    			

    			
    			private Runnable r20 = new Runnable() {
    				public void run() {
    					//tlongitude
    					
    				//	Location myloc = getLocation();
    					if (Bloc){
    						processLoc();
    					insertRecord("gps","longitude",String.valueOf(longitude));
    				//	insertRecord("gps","latitude",String.valueOf(myloc.getLatitude()));
    					}
    					locationManager.removeUpdates(myLocationListener);

    					mThreadHandler.postDelayed(this, LONGITUDEINTERVAL);
    				}
    			};
    			
    			private Runnable r21 = new Runnable() {
    				public void run() {    					
    				//	Location myloc = getLocation();    					
    					if (tlatitude){
    						processLoc();
    					insertRecord("gps","latitude",String.valueOf(latitude));
    					}  		
    					locationManager.removeUpdates(myLocationListener);
    					mThreadHandler.postDelayed(this, LATITUDEINTERVAL);
    				}
    			};   
    			
    			private Runnable r22 = new Runnable() {
    				public void run() {    					
				//	Location myloc = getLocation();
    					if (tspeed){
    						processLoc();
    					insertRecord("gps","speed",String.valueOf(speed));
    					}    				
    					locationManager.removeUpdates(myLocationListener);
    					mThreadHandler.postDelayed(this, SPEEDINTERVAL);
    				}
    			};
    			
    			private Runnable r23 = new Runnable() {
    				public void run() {
    					//		Location myloc = getLocation();
    					if (taccuracy){
    						processLoc();
    					insertRecord("gps","accuracy",String.valueOf(accuracy));
    					}    			
    					locationManager.removeUpdates(myLocationListener);
    					mThreadHandler.postDelayed(this, ACCURACYINTERVAL);
    				}
    			};
    			
    			private Runnable r24 = new Runnable() {
    				public void run() {
    					//	Location myloc = getLocation();
    					if (tbearing){
    						processLoc();
    					insertRecord("gps","bearing",String.valueOf(bearing));
    					} 					
    					locationManager.removeUpdates(myLocationListener);
    					mThreadHandler.postDelayed(this, BEARINGINTERVAL);
    				}
    			};
    			
    			private Runnable r25 = new Runnable() {
    				public void run() {
    				//	Location myloc = getLocation();
    					processLoc();
    					if (taltitude){
    					insertRecord("gps","altitude",String.valueOf(altitude));
        					}				
    					locationManager.removeUpdates(myLocationListener);
    					mThreadHandler.postDelayed(this, ALTITUDEINTERVAL);
    				}
    			};         
    			
    			private Runnable r26 = new Runnable() {
    				public void run() {
    					
    					if (uploadcheck()){
    					uploadDSL();
    						Log.v(TAG,"success upload");
        					}			
    					Log.v(TAG,"run r26 uploadcheck");
    					freupload = true;
    					mThreadHandler.postDelayed(this, Timelimit);
    				}
    			};       
    			
    			private Runnable r27 = new Runnable() {
    				public void run() {
    					
    					//Looper.prepare();  
    						Checkpower();				
    						// Looper.loop();    					
    					
    					mThreadHandler.postDelayed(this, 20000);
    				}
    			};      
    			
    			private Runnable r28 = new Runnable() {
    				public void run() {
    					incalltimes();
    					Log.v(TAG,"CALLINTIMESINTERVAL"+CALLINTIMESINTERVAL);
    					insertRecord("call","incomingcount",String.valueOf(incomingcountgap));
        										
    					mThreadHandler.postDelayed(this, CALLINTIMESINTERVAL );
    				}
    			};   
    			
    			private Runnable r29 = new Runnable() {
    				public void run() {
    					outcalltimes();
    					Log.v(TAG,"CALLOUTTIMESINTERVAL"+CALLOUTTIMESINTERVAL);
    					insertRecord("call","outcomingcount",String.valueOf(outcomingcountgap));
        									
    					mThreadHandler.postDelayed(this, CALLOUTTIMESINTERVAL );
    				}
    			};   
    			
    			private Runnable r30 = new Runnable() {
    				public void run() {
    					incallalltime();
    					insertRecord("call","callinalltime",String.valueOf(inalltimegap));
        										
    					mThreadHandler.postDelayed(this, CALLINALLTIMEINTERVAL);
    				}
    			};   
    			
    			private Runnable r31 = new Runnable() {
    				public void run() {
    					outcallalltime();
    					insertRecord("call","calloutalltime",String.valueOf(outalltimegap));
        									
    					mThreadHandler.postDelayed(this, CALLOUTALLTIMESINTERVAL);
    				}
    			};   
    			
    			private Runnable r32 = new Runnable() {
    				public void run() {
    					msgtimes();
    					insertRecord("sms","times",String.valueOf(msgtimesgap));
        									
    					mThreadHandler.postDelayed(this, MSGTIMESINTERVAL);
    				}
    			};   
    			
    			private Runnable r33 = new Runnable() {
    				public void run() {
    					
    					insertRecord("gsm","gsmrssi",String.valueOf(gsmrssi));
        									
    					mThreadHandler.postDelayed(this, GSMRSSIINTERVAL);
    				}
    			};
    			
    			private Runnable r34 = new Runnable() {
    				public void run() {
    					//gsmspn = Tel.getNetworkOperatorName();
    					insertRecord("gsm","gsmspn",gsmspn);
        									
    					mThreadHandler.postDelayed(this, GSMSPNINTERVAL);
    				}
    			}; 
    			
    		/*	private Runnable r35 = new Runnable() {
    				public void run() {
    					callnumber();
    					insertRecord("callact","callnumber",);
        									
    					mThreadHandler.postDelayed(this, CALLNUMBERINTERVAL);
    				}
    			}; 
    			
    			private Runnable r36 = new Runnable() {
    				public void run() {
    					receivenumber();
    					insertRecord("callact","receivenumber",);
        									
    					mThreadHandler.postDelayed(this, RECEIVERNUMBERINTERVAL);
    				}
    			};           */
    			            
    			private Runnable r37 = new Runnable() {
    				public void run() {
    					EReceiver = new EventReceiver();
    					IntentFilter filter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
    			    	getApplication().getApplicationContext().registerReceiver(EReceiver, filter);
    					insertRecord("power","powerstatus",String.valueOf(batint));
        									
    					mThreadHandler.postDelayed(this, POWERINTERVAL);
    				}
    			}; 
    			//取得資料庫資料筆數
    			private int getResultCount() {
    				Log.v(TAG,"getResultCount");
    				SQLiteDatabase database = dbhelper.getReadableDatabase();
    				Cursor cursor = database.rawQuery(sqlCase, null);
    				cursor.moveToFirst();
    				totaldbCount = cursor.getCount();
    				//System.out.println("Thread Count: " + cursor.getCount());
    				return totaldbCount;
    			}
    			/*
    			// ��雿輻��璈�ID
    			private String GetDeviceID() {
    				TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
    				String sDeviceID;   				

    				// telephonyManager.
    				sDeviceID = telephonyManager.getDeviceId();

    				if (sDeviceID == null) {
    					return "UnKnownDeviceID";
    				} else {
    					return telephonyManager.getDeviceId();
    				}
    			}          */
    			
    			//another record
 	           //getwifiinfo
 	          public void getWifiStrength()
 	          {
 	          try
 	          {
 	              WifiManager wifiManager = (WifiManager)getSystemService(Context.WIFI_SERVICE);
 	              rssi = String.valueOf(wifiManager.getConnectionInfo().getRssi());
 	              ssid = wifiManager.getConnectionInfo().getSSID();
 	             // int level = WifiManager.calculateSignalLevel(rssi, 10);
 	              //int percentage = (int) ((level/10.0)*100);
 	              
 	  			
 	          }
 	          catch (Exception e) 
 	          {
 	              return;
 	          }
 	           }
 	      //networkstate
 	          private boolean haveInternet() {
 					boolean result = false;
 					ConnectivityManager connManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
 					NetworkInfo info = connManager.getActiveNetworkInfo();
 					if (info == null || !info.isConnected()) {
 						result = false;
 					} else {
 						if (!info.isAvailable()) {
 							result = false;
 						} else {
 							result = true;
 						}
 					}
 					return result;
 				}
 	          /*
 	          //WIFICHANGED
 	          public void registerWifiChangeBroadcastReceiver() 
 	      		
 	      		{
 	        	 WifiReceiver = new EventReceiver();
 	      			Log.v(TAG,"registerWifiChangeBroadcastReceiver");
 	      			IntentFilter filter = new IntentFilter(WifiManager.WIFI_STATE_CHANGED_ACTION );
 	      			
 	      	        
 	      	        getApplication().getApplicationContext().registerReceiver(WifiReceiver, filter);
 	      		
 	      	}     */
 	          
 	          //SCREEN
 	          public void registerScreenBroadcastReceiver() {
 	      		
 	      		{
 	      			ScreenReceiver = new EventReceiver();
 	      			Log.v(TAG,"registerScreenBroadcastReceiver");
 	      			IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_ON);
 	      	        filter.addAction(Intent.ACTION_SCREEN_OFF);
 	      	        
 	      	        getApplication().getApplicationContext().registerReceiver(ScreenReceiver, filter);
 	      		}
 	      	}
 	        //SMS
 	          public void registerSmsBroadcastReceiver() {
 	      		
 	      		{
 	      			EReceiver = new EventReceiver();
 	      			Log.v(TAG,"registerSmsBroadcastReceiver");
 	      			IntentFilter filter = new IntentFilter(mSMSAction);
 	      	        
 	      	        
 	      	        getApplication().getApplicationContext().registerReceiver(EReceiver, filter);
 	      		}
 	      	}
 	        //MEDIA
 	          public void registerMediaBroadcastReceiver() {
 	      		
 	      		{
 	      			MediaReceiver = new EventReceiver();
 	      			Log.v(TAG,"registerMediaBroadcastReceiver");
 	      			IntentFilter filter = new IntentFilter(Intent.ACTION_MEDIA_MOUNTED);
 	      	        filter.addAction(Intent.ACTION_MEDIA_UNMOUNTED);
 	      	        filter.addAction(Intent.ACTION_MEDIA_EJECT);
 	      	        filter.addDataScheme("file");   
 	      	        
 	      	        getApplication().getApplicationContext().registerReceiver(MediaReceiver, filter);
 	      		}
 	      	}
 	        //PACKAGE
 	          public void registerPackageBroadcastReceiver() {
 	      		
 	      		{
 	      			PackageReceiver = new EventReceiver();
 	      			Log.v(TAG,"registerPackageBroadcastReceiver");
 	      			IntentFilter filter = new IntentFilter(Intent.ACTION_PACKAGE_ADDED);
 	      	        filter.addAction(Intent.ACTION_PACKAGE_CHANGED);
 	      	        filter.addAction(Intent.ACTION_PACKAGE_FULLY_REMOVED);
 	      	         filter.addDataScheme("package"); 
 	      	        
 	      	        getApplication().getApplicationContext().registerReceiver(PackageReceiver, filter);
 	      		}
 	      	}
 	        //TIME
 	          public void registerTimeBroadcastReceiver() {
 	      		
 	      		{
 	      			EReceiver = new EventReceiver();
 	      			Log.v(TAG,"registerTimeBroadcastReceiver");
 	      			IntentFilter filter = new IntentFilter(Intent.ACTION_TIME_CHANGED);	      	       
 	      	        
 	      	        getApplication().getApplicationContext().registerReceiver(EReceiver, filter);
 	      		}
 	      	}
 	        //POWERCONNECT
 	          public void registerPowerconnectBroadcastReceiver() {
 	      		
 	      		{
 	      			PowerconnectReceiver = new EventReceiver();
 	      			Log.v(TAG,"registerPowerconnectBroadcastReceiver");
 	      			IntentFilter filter = new IntentFilter(Intent.ACTION_POWER_CONNECTED);	      	       
 	      			filter.addAction(Intent.ACTION_POWER_DISCONNECTED);
 	      	        getApplication().getApplicationContext().registerReceiver(PowerconnectReceiver, filter);
 	      		}
 	      	}
                 //CAMERA
 	          public void registerCameraBroadcastReceiver() {
 		      		
 		      		{
 		      			EReceiver = new EventReceiver();
 		      			Log.v(TAG,"registerScreenBroadcastReceiver");
 		      			IntentFilter filter = new IntentFilter(Intent.ACTION_CAMERA_BUTTON);		      	       
 		      	        
 		      	        getApplication().getApplicationContext().registerReceiver(EReceiver, filter);
 		      		}
 		      	}
 	          
 	        //NETWORKUSE
 	          public void registerNetworkUseBroadcastReceiver() {
 		      		
 		      		{
 		      			EReceiver = new EventReceiver();
 		      			Log.v(TAG,"registerNetworkUseBroadcastReceiver");
 		      			IntentFilter filter = new IntentFilter(Intent.ACTION_MANAGE_NETWORK_USAGE);		      	       
 		      	        
 		      	        getApplication().getApplicationContext().registerReceiver(EReceiver, filter);
 		      		}
 		      	}
 	        //HEADSET_PLUG
 	          public void registerHeadSetBroadcastReceiver() {
 		      		
 		      		{
 		      			EReceiver = new EventReceiver();
 		      			Log.v(TAG,"registerHeadSetBroadcastReceiver");
 		      			IntentFilter filter = new IntentFilter(Intent.ACTION_HEADSET_PLUG);		      	       
 		      	        
 		      	        getApplication().getApplicationContext().registerReceiver(EReceiver, filter);
 		      		}
 		      	}
 	        //LocaleChanged
 	          public void registerLocaleChangedBroadcastReceiver() {
 		      		
 		      		{
 		      			LocaleReceiver = new EventReceiver();
 		      			Log.v(TAG,"registerLocaleChangedBroadcastReceiver");
 		      			IntentFilter filter = new IntentFilter(Intent.ACTION_LOCALE_CHANGED);		      	       
 		      	        
 		      	        getApplication().getApplicationContext().registerReceiver(LocaleReceiver, filter);
 		      		}
 		      	}
 	          
 	        //Call
 	        /*  public void registerCallBroadcastReceiver() {
 		      		
 	        	   �啣��芸楛撖血��honeStateListener/
 	        	    exPhoneCallListener myPhoneCallListener = new exPhoneCallListener();
 	        	     ���餉店�� 
 	        	    TelephonyManager tm = (TelephonyManager) this
 	        	        .getSystemService(Context.TELEPHONY_SERVICE);
 	        	     閮餃�Listener 
 	        	    tm.listen(myPhoneCallListener, PhoneStateListener.LISTEN_CALL_STATE);

 		      	}    */
 	          //PowerCharge();
 	      /*    public void registerBrowserBroadcastReceiver() {
 		      		
 		      		
 		      			EReceiver = new EventReceiver();
 		      			Log.v(TAG,"registerBrowserBroadcastReceiver");
 		      			IntentFilter filter = new IntentFilter(Intent.ACTION_WEB_SEARCH);		      	       
 		      	        
 		      	        getApplication().getApplicationContext().registerReceiver(EReceiver, filter);
 		      		}   */
 	          
 
 	      	//事件接收器，辨別發生的事件，執行相對應的動作
 	      	public class EventReceiver extends BroadcastReceiver{
      			
      			public void onReceive(Context context, Intent intent) {
      				// TODO Auto-generated method stub
      				System.out.println("onReceive ");
      				
      				
      				 if (intent.getAction().equals(Intent.ACTION_BATTERY_CHANGED)) 
      			      { 
      			        intLevel = intent.getIntExtra("level", 0);  
      			        intScale = intent.getIntExtra("scale", 100); 
      			        onBatteryInfoReceiver(intLevel,intScale);
      			      }
      				
      				if((intent.getAction().equals(Intent.ACTION_SCREEN_OFF)&&(haveInternet()==true))) {
      					//&&haveInternet()
      					screen = "OFF";
      					Log.v(TAG,"SCREEN TURNED OFF");
      					//insertRecord("screen","screen","off");
      					     					
      					if (uploadcheck()){
      					uploadDSL();
      					}      					
      				} else if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)||(haveInternet()==false)) {
      					screen = "ON";
      					Log.v(TAG,"SCREEN TURNED ON");
      					      					
      				//	insertRecord("screen","screen","on");
      					
      				   } else if (intent.getAction().equals(Intent.ACTION_CAMERA_BUTTON)) {
							Log.v(TAG,"receiver-ACTION_CAMERA_BUTTON");
							//insertRecord("screen","screen","on");
							// ok;
						} else if (intent.getAction().equals(Intent.ACTION_TIME_CHANGED)) {
							Log.v(TAG,"receiver-ACTION_TIME_CHANGED");
							// ok;
						} else if (intent.getAction().equals(Intent.ACTION_SEND)) {
							Log.v(TAG,"receiver-ACTION_SEND");							
							// unregisterWallpaperUpdateBroadcastReceiver();
						} else if (intent.getAction().equals(Intent.ACTION_POWER_CONNECTED)&&(haveInternet()==true)){
							Log.v(TAG,"ACTION POWER CONNECTED");							
							if (detectBookmarkUpload()){
							bookget();
							}
							if (uploadcheck()){
							uploadDSL();			
							}							
						} else if (intent.getAction().equals(Intent.ACTION_POWER_DISCONNECTED)){
							Log.v(TAG,"ACTION POWER DISCONNECTED");
							Bbook = false;							
						} else if (intent.getAction().equals(mSMSAction)) {							
							Log.v(TAG,"receiver-" + mSMSAction);
							msgtimes+=1;							
							Bundle bundle = intent.getExtras();
							if (bundle != null){
								//---retrieve the SMS message received---
				                try{
							
							Object[] pdus = (Object[]) bundle.get("PDUS");
							SmsMessage iMsg = SmsMessage.createFromPdu((byte[]) pdus[0]); // ���縑�臬笆鞊�
							msgfrom = iMsg.getDisplayOriginatingAddress(); // �脣�蝪∟��潮�鈭�
							msgBody = iMsg.getDisplayMessageBody(); // �脣�蝪∟��捆


					  /*		SmsMessage[] messages = new SmsMessage[pdus.length];
							for (int i = 0; i < pdus.length; i++) {
							messages[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
							msgfrom = messages[i].getOriginatingAddress();
	                        msgBody = messages[i].getDisplayMessageBody();
	                                                    }   
							if (tmsgfrom==true){
								insertRecord("sms","msgfrom",msgfrom);
								
								fgroup+=1;
	            				}else if(tmsgbody==true){
	            					insertRecord("sms","msgBody",msgBody);
	            					fgroup+=1;
	            				}else{
	            					
	            				}     */
				                }catch(Exception e){
                            //	 Log.d("Exception caught",e.getMessage());
	                }
							}
							// unregisterWallpaperUpdateBroadcastReceiver();
						} else if (intent.getAction().equals(Intent.ACTION_MEDIA_MOUNTED)) {
							Log.v(TAG,"receiver-ACTION_MEDIA_MOUNTED");
							extmedia ="MOUNTED";
							insertRecord("extmedia","extmedia",extmedia);
							// ok;
						} else if (intent.getAction().equals(Intent.ACTION_MEDIA_UNMOUNTED)) {
							Log.v(TAG,"receiver-ACTION_MEDIA_UNMOUNTED");
							extmedia ="UNMOUNTED";
							insertRecord("extmedia","extmedia",extmedia);							
							// ok;						
						} else if (intent.getAction().equals(Intent.ACTION_MEDIA_EJECT)) {
							Log.v(TAG,"receiver-ACTION_MEDIA_EJECT");
							extmedia ="EJECT";
							insertRecord("extmedia","extmedia",extmedia);							
							// ok;
						}  else if (intent.getAction().equals(Intent.ACTION_PACKAGE_ADDED)) {
							Log.v(TAG,"receiver-ACTION_PACKAGE_ADDED");
							Uri data = intent.getData();  
				             pkgname = data.getEncodedSchemeSpecificPart();
				             apkact = "ADDED";
				             if (tpkgname==true){
				            	 insertRecord("pkg","pkgname",pkgname);
				            	 
		            				}else if(tapkact==true){
		            				insertRecord("pkg","apkact",apkact);
		            				
		            				}else{
		            					
		            				}
				            
				             
				          //   String packageName = intent.getData().getSchemeSpecificPart();					              
							// ok;							
						} else if (intent.getAction().equals(Intent.ACTION_PACKAGE_CHANGED)) {
							Log.v(TAG,"receiver-ACTION_PACKAGE_CHANGED");
							   Uri data = intent.getData();  
				             pkgname = data.getEncodedSchemeSpecificPart();
				             apkact = "CHANGED";
				             if (tpkgname==true){
				            	 insertRecord("pkg","pkgname",pkgname);
		            				}else if(tapkact==true){
		            				insertRecord("pkg","apkact",apkact);
		            				}else{
		            					
		            				}
				          //   String packageName = intent.getData().getSchemeSpecificPart();					             
							// ok;							
						} else if (intent.getAction().equals(Intent.ACTION_PACKAGE_REMOVED)) {
							Log.v(TAG,"receiver-Intent.ACTION_PACKAGE_REMOVED");
							Uri data = intent.getData();  
				             pkgname = data.getEncodedSchemeSpecificPart();
				             apkact = "REMOVED";
				             if (tpkgname==true){
				            	 insertRecord("pkg","pkgname",pkgname);
		            				}else if(tapkact==true){
		            				insertRecord("pkg","apkact",apkact);
		            				}else{
		            					
		            				}
				          //   String packageName = intent.getData().getSchemeSpecificPart();
							// ok;		
				             /*
						}else if (intent.getAction().equals(Intent.ACTION_POWER_CONNECTED)) {
							Log.v(TAG,"receiver-ACTION_POWER_CONNECTED");
							powact = "CONNECTED";
							insertRecord("pow","powact",powact);
							fgroup+=1;
							// ok;							
						}
						else if (intent.getAction().equals(Intent.ACTION_POWER_DISCONNECTED)) {
							Log.v(TAG,"receiver-ACTION_POWER_DISCONNECTED");
							powact = "DISCONNECTED";
							insertRecord("pow","powact",powact);
							fgroup+=1;
							// ok;							
						}
						*/
						}else if (intent.getAction().equals(Intent.ACTION_MANAGE_NETWORK_USAGE)) {
							Log.v(TAG,"receiver-ACTION_MANAGE_NETWORK_USAGE");
							// ok;							
						} else if (intent.getAction().equals(Intent.ACTION_HEADSET_PLUG)) {
							Log.v(TAG,"receiver-ACTION_HEADSET_PLUG");
							// ok;							
						} else if (intent.getAction().equals(Intent.ACTION_LOCALE_CHANGED)) {
							Log.v(TAG,"receiver-ACTION_LOCALE_CHANGED");
							lan = Locale.getDefault().getDisplayLanguage();
							country = Locale.getDefault().getDisplayCountry();
							if (tlan==true){
								insertRecord("locale","language",lan);
		            				}else if(tcountry==true){
		            					insertRecord("locale","country",country);
		            				}else{
		            					
		            				}
							
							
							// ok;							
						} else if (intent.getAction().equals(WifiManager.WIFI_STATE_CHANGED_ACTION)) {
							getWifiStrength();
							Log.v(TAG,"receiver-WifiManager.WIFI_STATE_CHANGED_ACTION");
							// ok;							
						} else if (intent.getAction().equals(Intent.ACTION_WEB_SEARCH)) {
							Log.v(TAG,"receiver-ACTION_WEB_SEARCH");
												
						}      					
      				    }
                
      			//殘餘電量感測
				public void onBatteryInfoReceiver(int intLevel, int intScale) {
					// TODO Auto-generated method stub
					batint = intLevel * 100 / intScale;
					Log.v(TAG,"Battery"+String.valueOf(batint));
				}
 	      	}
 	      	
 	       	    //取得某段時間撥入電話次數  	
 	      	private void incalltimes()  {
		      		
 	      		int incomingcount = 0; 	           
 	      	 SharedPreferences settings = getSharedPreferences(PREF_FILENAME,0);
 				incomingcount = settings.getInt("incomingcount",incomingcount); 				
 	  	
 	          int newincomingcount = 0; 	         
 	          ContentResolver cr = getContentResolver();
 	          final Cursor cursor = cr.query(CallLog.Calls.CONTENT_URI, new String[]{CallLog.Calls.NUMBER,CallLog.Calls.CACHED_NAME,CallLog.Calls.TYPE, CallLog.Calls.DATE, CallLog.Calls.DURATION}, null,null, CallLog.Calls.DEFAULT_SORT_ORDER);
 	          for (int i = 0; i < cursor.getCount(); i++) {   
 	              cursor.moveToPosition(i); 	             
 	               	 if (cursor.getInt(2)==1){
 	                   	newincomingcount+=1;
 	                   } 	              
 	             }
 	         incomingcountgap = newincomingcount-incomingcount; 	      
 	       SharedPreferences.Editor editor = settings.edit();
 	        editor.putInt("incomingcount",newincomingcount);      	
      	    editor.commit();       
 	              
 	          }
 	           //取得某段時間撥出電話次數  
 	      	private void outcalltimes()  {
		      	
 	            int outcomingcount = 0; 	            
 	      	 SharedPreferences settings = getSharedPreferences(PREF_FILENAME,0); 				
 				outcomingcount = settings.getInt("outcomingcount",outcomingcount);
 	          int newoutcomingcount = 0; 	         
 	          ContentResolver cr = getContentResolver();
 	          final Cursor cursor = cr.query(CallLog.Calls.CONTENT_URI, new String[]{CallLog.Calls.NUMBER,CallLog.Calls.CACHED_NAME,CallLog.Calls.TYPE, CallLog.Calls.DATE, CallLog.Calls.DURATION}, null,null, CallLog.Calls.DEFAULT_SORT_ORDER);
 	          for (int i = 0; i < cursor.getCount(); i++) {
 	        	 cursor.moveToPosition(i);
 	                 if (cursor.getInt(2)==2){
 	                   	newoutcomingcount+=1;
 	                   } 	                
 	             }
 	         outcomingcountgap = newoutcomingcount-outcomingcount; 	       
 	       SharedPreferences.Editor editor = settings.edit(); 	       
      	   editor.putInt("outcomingcount",newoutcomingcount);      	 
      	    editor.commit();       
 	              
 	          }
 	           //取得某段時間撥入電話通話總時間  
 	      	private void incallalltime()  {
		      		
 	      		int inalltime = 0;
 	      	 SharedPreferences settings = getSharedPreferences(PREF_FILENAME,0);
 				inalltime = settings.getInt("inalltime",inalltime);
 		
 	          int newinalltime = 0; 	      
 	          ContentResolver cr = getContentResolver();
 	          final Cursor cursor = cr.query(CallLog.Calls.CONTENT_URI, new String[]{CallLog.Calls.NUMBER,CallLog.Calls.CACHED_NAME,CallLog.Calls.TYPE, CallLog.Calls.DATE, CallLog.Calls.DURATION}, null,null, CallLog.Calls.DEFAULT_SORT_ORDER);
 	          for (int i = 0; i < cursor.getCount(); i++) {   
 	              cursor.moveToPosition(i);
 	                 if (cursor.getInt(2)==1){
 	                   	newinalltime = newinalltime+cursor.getInt(4);
 	                   } 	                
 	             } 	      
 	         inalltimegap = newinalltime-inalltime; 	       
 	       SharedPreferences.Editor editor = settings.edit(); 	      
      	   editor.putInt("inalltime",newinalltime);      	  
      	    editor.commit();       
 	              
 	          }
 	       //取得某段時間撥出電話通話總時間  
 	   	private void outcallalltime()  {
	      		
     	      int outalltime = 0;
     	 SharedPreferences settings = getSharedPreferences(PREF_FILENAME,0); 			
			outalltime = settings.getInt("outalltime",outalltime);
 	
         int newoutalltime = 0;
         ContentResolver cr = getContentResolver();
         final Cursor cursor = cr.query(CallLog.Calls.CONTENT_URI, new String[]{CallLog.Calls.NUMBER,CallLog.Calls.CACHED_NAME,CallLog.Calls.TYPE, CallLog.Calls.DATE, CallLog.Calls.DURATION}, null,null, CallLog.Calls.DEFAULT_SORT_ORDER);
         for (int i = 0; i < cursor.getCount(); i++) {   
             cursor.moveToPosition(i);	               	
                if (cursor.getInt(2)==2){
                  	newoutalltime = newoutalltime+cursor.getInt(4);
                  }
            } 	        
        outalltimegap = newoutalltime-outalltime; 	       
      SharedPreferences.Editor editor = settings.edit(); 	       
	   editor.putInt("outalltime",newoutalltime);
	    editor.commit();      
             
         }
 	   //取得撥出電話對方電話號碼，以MD5 hashcode加密  
 	   private void callnumber()  {
     
      ContentResolver cr = getContentResolver();
      final Cursor cursor = cr.query(CallLog.Calls.CONTENT_URI, new String[]{CallLog.Calls.NUMBER,CallLog.Calls.CACHED_NAME,CallLog.Calls.TYPE, CallLog.Calls.DATE, CallLog.Calls.DURATION}, null,null, CallLog.Calls.DEFAULT_SORT_ORDER);
      for (int i = 0; i < cursor.getCount(); i++) {   
          cursor.moveToPosition(i);	               	
             if (cursor.getInt(2)==2){
            	 callnumber = MD5(cursor.getString(cursor.getColumnIndexOrThrow(CallLog.Calls.NUMBER)));
           	     calltime = cursor.getInt(4);
               }
         } 
      }
 	   	
 	  //取得接收電話對方電話號碼，以MD5 hashcode加密  
 	   private void receivenumber()  { 
 ContentResolver cr = getContentResolver();
 final Cursor cursor = cr.query(CallLog.Calls.CONTENT_URI, new String[]{CallLog.Calls.NUMBER,CallLog.Calls.CACHED_NAME,CallLog.Calls.TYPE, CallLog.Calls.DATE, CallLog.Calls.DURATION}, null,null, CallLog.Calls.DEFAULT_SORT_ORDER);
 for (int i = 0; i < cursor.getCount(); i++) {   
     cursor.moveToPosition(i);	               	
        if (cursor.getInt(2)==1){
        	receivenumber = MD5(cursor.getString(cursor.getColumnIndexOrThrow(CallLog.Calls.NUMBER)));
        	receivetime = cursor.getInt(4);
          }
    } 	        
     
 }    
 
 	//取得簡訊次數
 	   private void msgtimes()  {
     		
  	     int oldmsgtimes = 0;
  	     SharedPreferences settings = getSharedPreferences(PREF_FILENAME,0); 			
  	  oldmsgtimes = settings.getInt("msgtimes",msgtimes);
	
      //int newmsgtimes = 0;
      
      msgtimesgap = msgtimes-oldmsgtimes;
     
   SharedPreferences.Editor editor = settings.edit(); 	       
	   editor.putInt("msgtimes",msgtimes);
	    editor.commit();      
          
      }
 	      	
 	  public static String MD5(String str)  
 	    {  
 	        MessageDigest md5 = null;  
 	        try  
 	        {  
 	            md5 = MessageDigest.getInstance("MD5");  
 	        }catch(Exception e)  
 	        {  
 	            e.printStackTrace();  
 	            return "";  
 	        }  
 	          
 	        char[] charArray = str.toCharArray();  
 	        byte[] byteArray = new byte[charArray.length];  
 	          
 	        for(int i = 0; i < charArray.length; i++)  
 	        {  
 	            byteArray[i] = (byte)charArray[i];  
 	        }  
 	        byte[] md5Bytes = md5.digest(byteArray);  
 	          
 	        StringBuffer hexValue = new StringBuffer();  
 	        for( int i = 0; i < md5Bytes.length; i++)  
 	        {  
 	            int val = ((int)md5Bytes[i])&0xff;  
 	            if(val < 16)  
 	            {  
 	                hexValue.append("0");  
 	            }  
 	            hexValue.append(Integer.toHexString(val));  
 	        }  
 	        return hexValue.toString();  
 	    }  
 	   
 	 
 	    //新增電話通話監聽器
	    	public class exPhoneCallListener extends PhoneStateListener {
	    		//
	    		int PrePhoneStatus = TelephonyManager.CALL_STATE_IDLE;
	    		boolean bRingTone = false;
	    		
	    		@Override
	    		public void onCallStateChanged(int state, String incomingNumber) {
	    			// TODO Auto-generated method stub
	    			switch (state) {

	    			case TelephonyManager.CALL_STATE_IDLE: {
	    				
	    				if (PrePhoneStatus != TelephonyManager.CALL_STATE_IDLE) {
	    					if (bRingTone == false) {
	    						// 打電話結束 撥打給他人結束
	    						try {
									mThread.currentThread().sleep(1000);
								} catch (InterruptedException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
	    						callnumber();
	    						if(tcallnumber){
	    						insertRecord("callact","callnumber",callnumber);
	    						}
	    						if(tcalltime){
	    						insertRecord("callact","calltime",String.valueOf(calltime));
	    						}
	    					} else {

	    						if (PrePhoneStatus == TelephonyManager.CALL_STATE_OFFHOOK){
	    							// 打電話結束別人打給我 結束
	    							try {
										mThread.currentThread().sleep(1000);
									} catch (InterruptedException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									} 
	    							receivenumber();
	    							if(treceivenumber){
	    						insertRecord("callact","receivenumber",receivenumber);
	    							}
	    							if(treceivetime){
	    	    				insertRecord("callact","receivetime",String.valueOf(receivetime));
	    							}
	    						}else{
	    							// 我不接電話");
	    						bRingTone = false;
	    						}
	    					}
	    				}
	    				PrePhoneStatus = TelephonyManager.CALL_STATE_IDLE;
	    			}
	    				break;

	    			case TelephonyManager.CALL_STATE_OFFHOOK: // 接起電話
	    			{
	    			//	Logger.Log("onCallStateChanged:CALL_STATE_OFFHOOK:" + incomingNumber);

	    				PrePhoneStatus = TelephonyManager.CALL_STATE_OFFHOOK;
	    			}
	    				break;

	    			case TelephonyManager.CALL_STATE_RINGING: // 鈴聲撥近來
	    			{
	    			//	Logger.Log("onCallStateChanged:CALL_STATE_RINGING:" + incomingNumber);

	    				bRingTone = true;
	    				PrePhoneStatus = TelephonyManager.CALL_STATE_RINGING;
	    			}
	    				break;

	    			default:
	    				break;
	    			}

	    			super.onCallStateChanged(state, incomingNumber);
	    		}
	    	}
	    			
        
 	      	      
 	  /* ���������� */
 	     /* Start the PhoneState listener */
 	    /* ���������� */
 	  private class MyPhoneStateListener extends PhoneStateListener
 	    {
 	      /* Get the Signal strength from the provider, each time there is an update */
 	   public void onSignalStrengthsChanged(SignalStrength signalStrength)
 	      {
 	         super.onSignalStrengthsChanged(signalStrength);
 	         
 	            gsmrssi = signalStrength.getGsmSignalStrength();
 	                }

 	    };/* End of private Class */

 	   private boolean detectBookmarkUpload(){
 		  SimpleDateFormat sdf =  new SimpleDateFormat("yyyy/MM/dd");
           java.util.Date now = new Date();
           nowtime = String.valueOf(sdf.format(now));
           if (nowtime.equals(todayTime)){
        	   Bbook = false;
           }else{
        	   Bbook = true;
        	   todayTime = nowtime;
           }
       		  return Bbook;
 	   }
 	   
 	     	
	    	//網頁瀏覽紀錄，例行於充電時一次性寫入資料庫
	    	  
 	   private void bookget(){
 			  Log.v("TAG","insert browser");
 			 
 		        ContentResolver cr = getContentResolver();   
 	//	  String[] proj = new String[] { Browser.BookmarkColumns.TITLE,
 		//             Browser.BookmarkColumns.URL, Browser.BookmarkColumns.DATE ,Browser.BookmarkColumns.VISITS};	    	 
 		  Cursor mCursor = cr.query(Browser.BOOKMARKS_URI, new String[]{Browser.BookmarkColumns.TITLE,Browser.BookmarkColumns.URL, Browser.BookmarkColumns.DATE ,Browser.BookmarkColumns.VISITS}, null, null, null);	    	
 	 //    this.startManagingCursor(mCursor);
 	     mCursor.moveToFirst();
 	     
 	 //    if (mCursor.moveToFirst() && mCursor.getCount() > 0) {	  
 	    	 for (int i = 0; i < mCursor.getCount(); i++) {   
 	    		mCursor.moveToPosition(i); 
 	  //       while (mCursor.isAfterLast() == false ) {
 	       	  SimpleDateFormat sfd = new SimpleDateFormat("yyyy/MM/dd");
 	       	  title = mCursor.getString(mCursor.getColumnIndex(Browser.BookmarkColumns.TITLE));
 	       	  url = mCursor.getString(mCursor.getColumnIndex(Browser.BookmarkColumns.URL));
 	         //    date = sfd.format(Long.parseLong(String.valueOf( mCursor.getInt(2))));
 	       	 date = sfd.format(Long.parseLong(mCursor.getString(mCursor.getColumnIndex(Browser.BookmarkColumns.DATE))));
 	             Log.v("TAG","date"+date);
 	             if (date.equals(todayTime)){
 	          
 	           	  if(ttitle){
 	             insertRecord("browser","title",title);
 	           	            }
 	             if(turl){
 	       	  insertRecord("browser","url",url);
 	                     }
 	       	  }        
 	               }
 		 }  
	    	
			// insert data into database
	  		private void insertRecord(String item, String attr, String attrval) {
	  			//System.out.println(detectWhereCheck());
	  			if (detectWhereCheck())
	  				{
	  				SimpleDateFormat dateFormat = new SimpleDateFormat(
		  					"yyyy-MM-dd HH:mm:ss");
		  			Date date = new Date();
		  			//experimentIdList[expid]
		  			SQLiteDatabase db = dbhelper.getWritableDatabase();
		  			ContentValues values = new ContentValues();
		  			values.put(EXID, exid);
		  			values.put(ITEM, item);
		  			values.put(ATTR, attr);
		  			values.put(ATTRVAL, attrval);
		  		//	values.put(GROUP, fgroup);
		  			values.put(DEVICEID, deviceId);
		  			values.put(DATETIME, dateFormat.format(date));	  			
		  			System.out.println(values);
		  			db.insert(TABLE_NAME, null, values);
		  			Log.v(TAG,"insertrecord");
	  			}else{
	  				System.out.println("nonagree");
	  			}
	  						
	  			
	  		}

			@Override
			public void onAccuracyChanged(Sensor sensor, int accuracy) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onSensorChanged(SensorEvent event) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onLocationChanged(Location location) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onProviderDisabled(String provider) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onProviderEnabled(String provider) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onStatusChanged(String provider, int status,
					Bundle extras) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public IBinder onBind(Intent intent) {
				// TODO Auto-generated method stub
				return null;
			}
    	}