package com.example.plpa.project;

import static com.example.plpa.project.DbConstants.*;
import static com.example.plpa.project.PLPA_CLAP.PREF_FILENAME;
//import static com.example.plpa.project.LogService.endprocess;

/*
 import java.io.BufferedReader;
 import java.io.FileReader;
 import java.io.FilenameFilter;
 */

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.channels.FileChannel;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Locale;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.HttpEntity;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.ContentObserver;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorListener;
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
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.provider.Browser;
import android.provider.CallLog;
import android.provider.ContactsContract;
import android.telephony.PhoneStateListener;
import android.telephony.SmsMessage;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.plpa.project.LogService.EventReceiver;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

public class PLPA_CLAP extends Activity implements OnClickListener,
		SensorEventListener {

	private Button btnLoadDSL = null;
	private Button btnStart = null;
	private Button btnEnd = null;
	// private Button btnUpload = null;
	// private Button btnTruncateDB = null;
	private TextView txvDSLMsg = null;
	private TextView txvDBCount = null;

	private boolean validDSL = false;

	private String[] experimentIdList;
	private String[] experimentNameList;
	private String[] experimentDescriptionList;
	private String[] experimentScriptList;
	private Map<String, String> mapParser;
	private String[] clientdeviceIdList;
	private String[] authcodeList;

	private static String QUEUE_NAME = "clap";
	private static String AMQPHOST = "140.119.221.34";
	private static String AMQPVHOST = "clap";
	private static String AMQPUSER = "clap";
	private static String AMQPPASSWORD = "clap@nccu";

	private SensorManager sensorManager = null;
	private Sensor sensor;
	private LocationManager locationManager;
	private LinkedList<Location> locList;

	private Handler mThreadHandler;
	private HandlerThread mThread;
	private DBHelper dbhelper = null;
	private String sqlCase = "";
	public String collectSen;
	public String ax, ay, az;
	public String ox, oy, oz;
	public String mx, my, mz;
	public String px;
	public String temp;
	public String li;
	public String pres;
	public String rssi = "";
	public String ssid = "";
	public String screen = "";
	public String extmedia = "";
	public String lan = "";
	public String country = "";
	public String msgfrom = "";
	public String msgBody = "";
	public String pkgname = "";
	public String apkact = "";
	public String powact = "";
	public String callstate = ""; // call
	public String callnumber = ""; // call
	public String callcontact = ""; // call
	public String title = "";
	public String url = "";
	public String contents = "";
	public String testdescript = "";
	public String exid = "";
	public String powerdown = "";
	public EventReceiver EReceiver;
	private static long INTERVAL, AXINTERVAL, AYINTERVAL, AZINTERVAL,
			OXINTERVAL, OYINTERVAL, OZINTERVAL, MXINTERVAL, MYINTERVAL,
			MZINTERVAL, PXINTERVAL, LIINTERVAL, TEMPINTERVAL, PRESINTERVAL,
			LONGITUDEINTERVAL, LATITUDEINTERVAL, SPEEDINTERVAL,
			BEARINGINTERVAL, ALTITUDEINTERVAL, ACCURACYINTERVAL,
			CALLINTIMESINTERVAL, CALLOUTTIMESINTERVAL, CALLINALLTIMEINTERVAL,
			CALLOUTALLTIMESINTERVAL, MSGTIMESINTERVAL, RSSIINTERVAL,
			SSIDINTERVAL, GSMRSSIINTERVAL, GSMSPNINTERVAL, CALLNUMBERINTERVAL,
			RECEIVERNUMBERINTERVAL, POWERINTERVAL;
	private static final String TAG = "Mytest";
	static final String mSMSAction = "android.provider.Telephony.SMS_RECEIVED";
	public long time = new Date().getTime();
	// public EventReceiver EReceiver;
	public double latitude;
	public double longitude;
	public float speed;
	public float accuracy;
	public float bearing;
	public double altitude;
	private int expid = 0;
	Timer timer = new Timer(true);
	public static final String PREF_FILENAME = "projectclap"; // 已分析實驗設定參數XML檔名
	public static final String PREF_CALL = "clapcall"; // 通話記錄初始值XML檔名
	public static final int teststatus = 0;
	public static final String ID_CHECK = "checkid"; // 實驗ID參數XML檔名
	public boolean Bacc, Bori, Bmagn, Bpres, Bli, Bpx, Btemp, Bwifi, Bbrowser,
			Bloc, Bcall, Bsms, Blocale, Bpow, Bapp, Bmedia, Bscreen, Bgsm,
			Bcallact, Bpower = false;
	public boolean tax, tay, taz, tox, toy, toz, tmx, tmy, tmz, tpx, ttemp,
			tlight, tpressure, tlongitude, tlatitude, tspeed, taltitude,
			taccuracy, trssi, tssid, tscreen, textmedia, tapkact, tpkgname,
			tpow, tlan, tcountry, tmsgtimes, tcallstate, tcallnumber,
			tcallcontact, ttitle, turl, tbearing, tincomingcount,
			toutcomingcount, tinalltime, toutalltime, tgsmrssi, tgsmspn,
			tcallin, tcallout, tpowerstatus, treceivenumber, tcalltime,
			treceivetime = false;
	public boolean btax, btay, btaz, btox, btoy, btoz, btmx, btmy, btmz, btpx,
			bttemp, btlight, btpressure, btlongitude, btlatitude, btspeed,
			btaltitude, btaccuracy, btbearing = false;
	public String axbigop, axlessop, aybigop, azbigop, oxbigop, oybigop,
			ozbigop, mxbigop, mybigop, mzbigop, pxbigop, tempbigop, lightbigop,
			pressurebigop, speedbigop, altitudebigop, bearingbigop,
			accuracybigop, longitudebigop, latitudebigop, aylessop, azlessop,
			oxlessop, oylessop, ozlessop, mxlessop, mylessop, mzlessop,
			pxlessop, templessop, lightlessop, pressurelessop, speedlessop,
			altitudelessop, bearinglessop, accuracylessop, longitudelessop,
			latitudelessop;
	public String axbigvalue, axlessvalue, aybigvalue, azbigvalue, oxbigvalue,
			oybigvalue, ozbigvalue, mxbigvalue, mybigvalue, mzbigvalue,
			pxbigvalue, tempbigvalue, lightbigvalue, pressurebigvalue,
			speedbigvalue, altitudebigvalue, bearingbigvalue, accuracybigvalue,
			longitudebigvalue, latitudebigvalue, aylessvalue, azlessvalue,
			oxlessvalue, oylessvalue, ozlessvalue, mxlessvalue, mylessvalue,
			mzlessvalue, pxlessvalue, templessvalue, lightlessvalue,
			pressurelessvalue, speedlessvalue, altitudelessvalue,
			bearinglessvalue, accuracylessvalue, longitudelessvalue,
			latitudelessvalue;
	public String fore = "";
	public String back = "";
	public String uppolicy = "";
	private boolean record = false;
	public boolean screenupload = false;
	public boolean chageupload = false;
	public int h = 0;
	public boolean checktest = false;
	public String AuthCode;
	public String ClicentDeviceID;
	public String DeviceOs;
	public String Device;
	public String todayTime;
	public int msgtimes = 0;
	public int SizeLimit = 0;
	public int Timelimit = 0;

	Calendar c = Calendar.getInstance();

	// Timer timer2 = new Timer(true);

	@SuppressWarnings("deprecation")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		initView();
		SharedPreferences settings = getSharedPreferences(PREF_FILENAME, 0);
		checktest = settings.getBoolean("checktest", checktest);
		if (checktest) {
			btnLoadDSL.setEnabled(false);
			btnStart.setEnabled(false);
			btnEnd.setEnabled(true);
		} else {

			btnLoadDSL.setEnabled(true);
			btnStart.setEnabled(true);
			btnEnd.setEnabled(false);
		}
		clapcalltimes();
		loadClientId();

		// 取得使用者手機型號和OS版本
		DeviceOs = android.os.Build.VERSION.RELEASE;
		Device = android.os.Build.MODEL.replace(' ', '-');
		Log.v(TAG, "Product Model: " + Device + ",OS,"
				+ android.os.Build.VERSION.RELEASE + ";");

		// 實驗設定檔更新
		Bundle mBundle01 = this.getIntent().getExtras();
		if (mBundle01 != null) {
			exid = mBundle01.getString("OriginTestId");
			contents = mBundle01.getString("NewTestScript");
			testdescript = mBundle01.getString("NewTestDescript");
			if (exid != null) {
				StopService();
				Log.v(TAG, "refind TestId" + exid);
				reFindTest(exid, contents, testdescript); // 重新將新實驗設定檔作分析
				Log.v(TAG, "retrun PLPA_CLAP sucessful");
				txvDSLMsg.setText(testdescript);
				new AlertDialog.Builder(PLPA_CLAP.this)
						.setMessage("實驗有更新")
						.setPositiveButton(R.string.update,
								new DialogInterface.OnClickListener() { // 確認更新
									public void onClick(DialogInterface dialog,
											int whichButton) {
										testfileCheck(); // 刪除舊實驗參數，產生新實驗參數檔
										txvDSLMsg.setText(testdescript);
										startRecording();
										Log.v(TAG, "update sucessful");
									}
								})
						.setNegativeButton(R.string.str_no,
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int whichButton) {
										// finish();
									}
								}).show();
			}
		}
		/*
		 * 電力過低視窗警告 Bundle mBundle02 = this.getIntent().getExtras(); // if
		 * (mBundle02!= null) if (mBundle02!=null){ powerdown =
		 * mBundle02.getString("power"); if(powerdown.equals("powerdown")){
		 * Log.v(TAG,"powerdown"); StopService(); new AlertDialog.Builder(this)
		 * .setMessage("電力過低，請充電恢復實驗紀錄") .setPositiveButton(R.string.str_ok, new
		 * DialogInterface.OnClickListener() { public void
		 * onClick(DialogInterface dialog, int whichButton) { finish(); }
		 * }).show(); registerPowerconnectBroadcastReceiver(); } }
		 */
	}

	// POWERCONNECT
	/*
	 * public void registerPowerconnectBroadcastReceiver() {
	 * 
	 * { EReceiver = new EventReceiver();
	 * Log.v(TAG,"registerPowerconnectBroadcastReceiver"); IntentFilter filter =
	 * new IntentFilter(Intent.ACTION_POWER_CONNECTED);
	 * filter.addAction(Intent.ACTION_POWER_DISCONNECTED);
	 * getApplication().getApplicationContext().registerReceiver(EReceiver,
	 * filter); } } //connectpowerreceiver public class EventReceiver extends
	 * BroadcastReceiver{ public void onReceive(Context context, Intent intent)
	 * { if (intent.getAction().equals(Intent.ACTION_POWER_CONNECTED)){
	 * Log.v(TAG,"ACTION POWER CONNECTED"); startRecording(); } } }
	 */

	// 重新將新實驗設定檔作分析
	private void reFindTest(String exid2, String contents2, String testdescript2) {
		// TODO Auto-generated method stub
		// 如果有限定條件的狀態
		Matcher w1 = Pattern.compile("(where\\s*)").matcher(contents2);
		if (w1.find()) {
			Log.v(TAG, "refind contests where" + contents2);
			String[] tokens = contents2.split("where\\s*");
			for (String token : tokens) {
				fore = tokens[0];
				back = tokens[1];
			}
			/*
			 * AccTokenizer(fore); OriTokenizer(fore); MagnTokenizer(fore);
			 * locTokenizer(fore); ProxTokenizer(fore); TempTokenizer(fore);
			 * LightTokenizer(fore); PressureTokenizer(fore);
			 * BrowserTokenizer(fore); CallTokenizer(fore);
			 * SmsintervalTokenizer(fore); LocaleTokenizer(fore);
			 * PowTokenizer(fore); PkgTokenizer(fore); ExtmediaTokenizer(fore);
			 * ScreenTokenizer(fore); WifiTokenizer(fore);
			 */
			AccaxbkTokenizer(back);
			AccaybkTokenizer(back);
			AccazbkTokenizer(back);
			OrioxbkTokenizer(back);
			OrioybkTokenizer(back);
			OriozbkTokenizer(back);
			MagnmxbkTokenizer(back);
			MagnmybkTokenizer(back);
			MagnmzbkTokenizer(back);
			ProxbkTokenizer(back);
			TempbkTokenizer(back);
			LightbkTokenizer(back);
			PressurebkTokenizer(back);
			locspeedbkTokenizer(back);
			localtitudebkTokenizer(back);
			locbearingbkTokenizer(back);
			locaccuracybkTokenizer(back);
			loclongitudebkTokenizer(back);
			loclatitudebkTokenizer(back);
			callincountintervalTokenizer(fore);
			calloutcomingcountintervalTokenizer(fore);
			callinalltimeintervalTokenizer(fore);
			calloutalltimeintervalTokenizer(fore);
			ssidintervalTokenizer(fore);
			rssiintervalTokenizer(fore);
			axintervalTokenizer(fore);
			ayintervalTokenizer(fore);
			azintervalTokenizer(fore);
			oxintervalTokenizer(fore);
			oyintervalTokenizer(fore);
			ozintervalTokenizer(fore);
			mxintervalTokenizer(fore);
			myintervalTokenizer(fore);
			mzintervalTokenizer(fore);
			liintervalTokenizer(fore);
			pxintervalTokenizer(fore);
			tempintervalTokenizer(fore);
			presintervalTokenizer(fore);
			longitudeintervalTokenizer(fore);
			latitudeintervalTokenizer(fore);
			speedintervalTokenizer(fore);
			bearingintervalTokenizer(fore);
			altitudeintervalTokenizer(fore);
			accuracyintervalTokenizer(fore);
			GsmrssiintervalTokenizer(fore);
			GsmspnintervalTokenizer(fore);
			powerintervalTokenizer(fore);
			screenuploadbkTokenizer(back);
			charguploadbkTokenizer(back);
			limitdb(back);
			limittime(back);
			collectSen = IdentificationList(fore);
			ActAttribute(fore);
		} else {
			// 沒有限定條件的狀態
			/*
			 * AccTokenizer(contents2); OriTokenizer(contents2);
			 * MagnTokenizer(contents2); locTokenizer(contents2);
			 * ProxTokenizer(contents2); TempTokenizer(contents2);
			 * LightTokenizer(contents2); PressureTokenizer(contents2);
			 * BrowserTokenizer(contents2); CallTokenizer(contents2);
			 * SmsintervalTokenizer(contents2); LocaleTokenizer(contents2);
			 * PowTokenizer(contents2); PkgTokenizer(contents2);
			 * ExtmediaTokenizer(contents2); ScreenTokenizer(contents2);
			 * WifiTokenizer(contents);
			 */
			ssidintervalTokenizer(contents2);
			rssiintervalTokenizer(contents2);
			callincountintervalTokenizer(contents2);
			calloutcomingcountintervalTokenizer(contents2);
			callinalltimeintervalTokenizer(contents2);
			calloutalltimeintervalTokenizer(contents2);
			axintervalTokenizer(contents2);
			ayintervalTokenizer(contents2);
			azintervalTokenizer(contents2);
			oxintervalTokenizer(contents2);
			oyintervalTokenizer(contents2);
			ozintervalTokenizer(contents2);
			mxintervalTokenizer(contents2);
			myintervalTokenizer(contents2);
			mzintervalTokenizer(contents2);
			liintervalTokenizer(contents2);
			pxintervalTokenizer(contents2);
			tempintervalTokenizer(contents2);
			presintervalTokenizer(contents2);
			longitudeintervalTokenizer(contents2);
			latitudeintervalTokenizer(contents2);
			speedintervalTokenizer(contents2);
			bearingintervalTokenizer(contents2);
			altitudeintervalTokenizer(contents2);
			accuracyintervalTokenizer(contents2);
			GsmrssiintervalTokenizer(contents2);
			GsmspnintervalTokenizer(contents2);
			powerintervalTokenizer(contents2);
			screenuploadbkTokenizer(contents2);
			charguploadbkTokenizer(contents2);
			limitdb(contents2);
			limittime(contents2);
			collectSen = IdentificationList(contents2);
			ActAttribute(contents2);
		}
	}

	// 建立SQLite資料表
	private void openDatabase() {
		dbhelper = new DBHelper(this);
		Log.v(TAG, "opdb");
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

	// 起始畫面物件
	private void initView() {

		getWindow().addFlags(
				WindowManager.LayoutParams.FLAG_FULLSCREEN
						| WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

		btnLoadDSL = (Button) findViewById(R.id.button1);
		btnStart = (Button) findViewById(R.id.button2);
		btnEnd = (Button) findViewById(R.id.button3);
		txvDSLMsg = (TextView) findViewById(R.id.DSLContents);
		btnLoadDSL.setOnClickListener(this);
		btnStart.setOnClickListener(this);
		btnEnd.setOnClickListener(this);
		mapParser = new HashMap<String, String>();
	}

	// 各按鈕觸發事件
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.button1:
			loadDSL(); // 取得實驗列表
			break;
		case R.id.button2:
			startRecording(); // 重新恢復復錄
			break;
		case R.id.button3:
			StopService(); // 停止記錄
			break;
		default:
			break;
		}
	}

	// 取得實驗列表
	private void loadDSL() {
		if (haveInternet()) { // 偵測有無網路狀態
			openDatabase();
			loadExperimentList();
			NewShowCreateDialog();
		} else { // 偵測沒有網路，提醒開啟網路功能
			new AlertDialog.Builder(this)
					.setMessage("為方便實驗，請開啟無線網路功能")
					.setPositiveButton(R.string.str_ok,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int whichButton) {
									finish();
								}
							}).show();
		}
	}

	// 開始記錄
	@SuppressWarnings("deprecation")
	private void startRecording() {

		Log.v(TAG, "startRecording");
		btnLoadDSL.setEnabled(false);
		btnStart.setEnabled(false);
		btnEnd.setEnabled(true);
		Intent intent = new Intent();
		Log.v(TAG, contents);

		intent.putExtra("Bacc", Bacc);
		intent.putExtra("Bloc", Bloc);
		intent.putExtra("Bori", Bori);
		intent.putExtra("Bmagn", Bmagn);
		intent.putExtra("Bpres", Bpres);
		intent.putExtra("Bpx", Bpx);
		intent.putExtra("Btemp", Btemp);
		intent.putExtra("Bwifi", Bwifi);
		intent.putExtra("Bbrowser", Bbrowser);
		intent.putExtra("Bcall", Bcall);
		intent.putExtra("Bbrowser", Bbrowser);
		intent.putExtra("Bsms", Bsms);
		intent.putExtra("Blocale", Blocale);
		intent.putExtra("Bpow", Bpow);
		intent.putExtra("Bapp", Bapp);
		intent.putExtra("Bmedia", Bmedia);
		intent.putExtra("Bscreen", Bscreen);
		intent.putExtra("interval", INTERVAL);
		intent.putExtra("contents", contents);
		intent.setClass(PLPA_CLAP.this, LogService.class); // 轉換至後端LogService
		startService(intent);
		finish();
	}

	@Override
	protected void onStop() {
		super.onStop();
	}

	// 停止後端記錄，後端LogService轉換至PLPA_CLAP
	private void StopService() {
		checktest = false;
		SharedPreferences settings = getSharedPreferences(PREF_FILENAME, 0);
		SharedPreferences.Editor editor = settings.edit();
		editor.putBoolean("checktest", checktest);
		btnStart.setEnabled(true);
		btnLoadDSL.setEnabled(true);
		btnEnd.setEnabled(false);
		try {
			Intent i = new Intent(PLPA_CLAP.this, LogService.class);
			stopService(i);
		} catch (Exception e) {
		}
	}

	// 取得受測者ID和授權碼
	private void loadClientId() {
		// TODO Auto-generated method stub

		File f = new File(
				"/data/data/com.example.plpa.project/shared_prefs/checkid.xml");
		if (f.exists()) {
			Log.v(TAG, "SharedPreferences checkid : exist");
			SharedPreferences ckdevice = getSharedPreferences(ID_CHECK, 0);
			ClicentDeviceID = ckdevice.getString("clientdeviceid",
					ClicentDeviceID);
			AuthCode = ckdevice.getString("authCode", AuthCode);
		} else {
			Log.v(TAG, "New checkId");
			AsyncTask<String, Void, String> Idtext;
			Idtext = new readREST()
					.execute(" http://140.119.221.34/ws/rc/getid "); // 受測者ID和授權碼RESTFUL
			try {
				JSONObject jCid = new JSONObject(Idtext.get());
				int count = jCid.length();
				clientdeviceIdList = new String[count];
				authcodeList = new String[count];

				for (int i = 0; i < jCid.length(); i++) {
					clientdeviceIdList[i] = jCid.getString("clientdeviceid");
					authcodeList[i] = jCid.getString("authcode");
				}
				ClicentDeviceID = clientdeviceIdList[0];
				AuthCode = authcodeList[0];
				// ClicentDeviceID = "262";
				// AuthCode = "52bceea3e55a6e2fdee85b30efc8fa71";
				Log.v(TAG, "ClicentDeviceID" + ClicentDeviceID + "AuthCode"
						+ AuthCode);
				SharedPreferences ckdevice = getSharedPreferences(ID_CHECK, 0); // 將取得受測者ID和授權碼寫入XML檔
				SharedPreferences.Editor editor = ckdevice.edit();
				editor.putString("clientdeviceid", ClicentDeviceID);
				editor.putString("authCode", AuthCode);
				editor.commit();

			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (ExecutionException e) {
				e.printStackTrace();
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		Log.v(TAG, ClicentDeviceID);
		Log.v(TAG, AuthCode);
	}

	// 取得實驗列表
	private void loadExperimentList() {
		// REST
		AsyncTask<String, Void, String> RESTtext; // 實驗列表REST
		RESTtext = new readREST()
				.execute(" http://140.119.221.34/ws/le/getall/Android/"
						+ DeviceOs + "/" + Device + "/" + ClicentDeviceID + "/"
						+ AuthCode);
		try {

			JSONObject jObj = new JSONObject(RESTtext.get());
			JSONArray experiments = jObj.getJSONArray("experiment");
			int count = experiments.length();
			experimentIdList = new String[count]; // 實驗ID
			experimentNameList = new String[count]; // 實驗名稱
			experimentDescriptionList = new String[count]; // 實驗描述
			experimentScriptList = new String[count]; // 實驗設定檔列表

			for (int i = 0; i < experiments.length(); i++) {
				JSONObject experiment = experiments.getJSONObject(i);
				experimentIdList[i] = experiment.getString("experimentId");
				experimentNameList[i] = experiment.getString("name");
				experimentDescriptionList[i] = experiment
						.getString("description");
				experimentScriptList[i] = experiment.getString("script");
			}

		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	// 將實驗列表以選單方式提供受測者選取
	private void NewShowCreateDialog() {
		Dialog dialog = null;
		AlertDialog.Builder builder = new Builder(this);

		builder.setTitle("Choose an experiment");
		if (experimentNameList == null) {
			dialog.dismiss();
		}
		builder.setItems(experimentNameList,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {

						// SharedPreferences.delete();

						expid = which;

						System.out.println("clicking DSL number = " + which);

						contents = experimentScriptList[which];
						testdescript = experimentDescriptionList[which];
						exid = experimentIdList[which];
						// contents =
						// "log acc.ax 5 acc.ay 10 where acc.ax>0.1 uploadpolicy screenoff limit 20";
						// uploadpolicy screenoff, charging,where acc.ax>0.2
						// every 5
						// acc.ax > 0 gps.longitude gps.latitude li temp magn.mx

						Matcher w1 = Pattern.compile("(where\\s*)").matcher(
								contents);
						if (w1.find()) {
							Log.v(TAG, "first check hava Where");
							String[] tokens = contents.split("where\\s*");
							for (String token : tokens) {
								fore = tokens[0];
								back = tokens[1];
							}
							// 判斷實驗記錄記錄屬性
							collectSen = IdentificationList(fore);
							ActAttribute(fore);
							/*
							 * AccTokenizer(fore); OriTokenizer(fore);
							 * MagnTokenizer(fore); locTokenizer(fore);
							 * ProxTokenizer(fore); TempTokenizer(fore);
							 * LightTokenizer(fore); PressureTokenizer(fore);
							 * BrowserTokenizer(fore); CallTokenizer(fore);
							 * LocaleTokenizer(fore); PowTokenizer(fore);
							 * PkgTokenizer(fore); ExtmediaTokenizer(fore);
							 * ScreenTokenizer(fore);
							 */
							AccaxbkTokenizer(back);
							AccaybkTokenizer(back);
							AccazbkTokenizer(back);
							OrioxbkTokenizer(back);
							OrioybkTokenizer(back);
							OriozbkTokenizer(back);
							MagnmxbkTokenizer(back);
							MagnmybkTokenizer(back);
							MagnmzbkTokenizer(back);
							ProxbkTokenizer(back);
							TempbkTokenizer(back);
							LightbkTokenizer(back);
							PressurebkTokenizer(back);
							locspeedbkTokenizer(back);
							localtitudebkTokenizer(back);
							locbearingbkTokenizer(back);
							locaccuracybkTokenizer(back);
							loclongitudebkTokenizer(back);
							loclatitudebkTokenizer(back);
							ssidintervalTokenizer(fore);
							rssiintervalTokenizer(fore);
							callincountintervalTokenizer(fore);
							calloutcomingcountintervalTokenizer(fore);
							callinalltimeintervalTokenizer(fore);
							calloutalltimeintervalTokenizer(fore);
							SmsintervalTokenizer(fore);
							axintervalTokenizer(fore);
							ayintervalTokenizer(fore);
							azintervalTokenizer(fore);
							oxintervalTokenizer(fore);
							oyintervalTokenizer(fore);
							ozintervalTokenizer(fore);
							mxintervalTokenizer(fore);
							myintervalTokenizer(fore);
							mzintervalTokenizer(fore);
							liintervalTokenizer(fore);
							pxintervalTokenizer(fore);
							tempintervalTokenizer(fore);
							presintervalTokenizer(fore);
							longitudeintervalTokenizer(fore);
							latitudeintervalTokenizer(fore);
							speedintervalTokenizer(fore);
							bearingintervalTokenizer(fore);
							altitudeintervalTokenizer(fore);
							accuracyintervalTokenizer(fore);
							powerintervalTokenizer(fore);
							screenuploadbkTokenizer(back);
							charguploadbkTokenizer(back);
							limitdb(back);
							limittime(back);
							GsmrssiintervalTokenizer(fore);
							GsmspnintervalTokenizer(fore);
						} else {
							Log.v(TAG, "first check no Where");
							collectSen = IdentificationList(contents);
							ActAttribute(contents);
							/*
							 * AccTokenizer(contents); OriTokenizer(contents);
							 * MagnTokenizer(contents); locTokenizer(contents);
							 * ProxTokenizer(contents); TempTokenizer(contents);
							 * LightTokenizer(contents);
							 * PressureTokenizer(contents);
							 * BrowserTokenizer(contents);
							 * CallTokenizer(contents);
							 * LocaleTokenizer(contents);
							 * PowTokenizer(contents); PkgTokenizer(contents);
							 * ExtmediaTokenizer(contents);
							 * ScreenTokenizer(contents);
							 * WifiTokenizer(contents);
							 */
							ssidintervalTokenizer(contents);
							rssiintervalTokenizer(contents);
							callincountintervalTokenizer(contents);
							calloutcomingcountintervalTokenizer(contents);
							callinalltimeintervalTokenizer(contents);
							calloutalltimeintervalTokenizer(contents);
							SmsintervalTokenizer(contents);
							GsmrssiintervalTokenizer(contents);
							GsmspnintervalTokenizer(contents);
							axintervalTokenizer(contents);
							ayintervalTokenizer(contents);
							azintervalTokenizer(contents);
							oxintervalTokenizer(contents);
							oyintervalTokenizer(contents);
							ozintervalTokenizer(contents);
							mxintervalTokenizer(contents);
							myintervalTokenizer(contents);
							mzintervalTokenizer(contents);
							liintervalTokenizer(contents);
							pxintervalTokenizer(contents);
							tempintervalTokenizer(contents);
							presintervalTokenizer(contents);
							longitudeintervalTokenizer(contents);
							latitudeintervalTokenizer(contents);
							speedintervalTokenizer(contents);
							bearingintervalTokenizer(contents);
							altitudeintervalTokenizer(contents);
							accuracyintervalTokenizer(contents);
							powerintervalTokenizer(contents);
							screenuploadbkTokenizer(contents);
							charguploadbkTokenizer(contents);
							limitdb(contents);
							limittime(contents);
						}

						String normalText = contents; // 提醒使用者此實驗記錄屬性
						// Log.v(TAG,contents);
						CharSequence strDialogBody = getString(R.string.str_alert_body);

						new AlertDialog.Builder(PLPA_CLAP.this)
								.setMessage(collectSen)
								.setPositiveButton(R.string.str_ok,
										new DialogInterface.OnClickListener() {
											public void onClick(
													DialogInterface dialog,
													int whichButton) // 確認後開始感測
											{
												checktest = true;
												testfileCheck();
												startRecording();
												dialog.dismiss();
											}

										})
								.setNegativeButton(R.string.str_no,
										new DialogInterface.OnClickListener() {
											public void onClick(
													DialogInterface dialog,
													int whichButton) // 不進行此實驗，取消關閉視窗
											{
												dialog.dismiss();
											}
										}).show();
						String DSLString = contents.replace(",", "").replace(
								" ", "");

						// String DSLString =
						// "log gps.longitude, gps.latitude, acc.ax, acc.ay, acc.az, wifi.rssi, browser.title, call.state, sms.msgfrom, locale.lan, extmedia where gps.speed > 0 every 5";
						Log.v(TAG, DSLString);
						if (true) {
							// IsValidDSL(DSLString)
							validDSL = true;
							Toast.makeText(getApplicationContext(),
									"DSL is valid", Toast.LENGTH_SHORT).show();
							txvDSLMsg.setText(testdescript);

						} else {
							// validDSL = false;
							Toast.makeText(getApplicationContext(),
									"DSL is invalid", Toast.LENGTH_LONG).show();
							txvDSLMsg.setText("DSL is invalid...\n"
									+ normalText);
						}
					}
				})
				.setNegativeButton(R.string.str_no,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int whichButton) {

								dialog.dismiss();
							}
						}).show();
	}

	// 實驗參數寫入projectclap.xml檔
	private void testfileCheck() {
		// TODO Auto-generated method stub
		File f = new File(
				"/data/data/com.example.plpa.project/shared_prefs/projectclap.xml");
		if (f.exists()) {
			Log.v(TAG, "SharedPreferences projectclap : exist");
			f.delete();
		} else {
			Log.v(TAG, "New preferences");
		}

		SharedPreferences settings = getSharedPreferences(PREF_FILENAME, 0);
		SharedPreferences.Editor editor = settings.edit();
		editor.putBoolean("Bacc", Bacc);
		editor.putBoolean("Bori", Bori);
		editor.putBoolean("Bmagn", Bmagn);
		editor.putBoolean("Bpres", Bpres);
		editor.putBoolean("Btemp", Btemp);
		editor.putBoolean("Bwifi", Bwifi);
		editor.putBoolean("Bli", Bli);
		editor.putBoolean("Bpx", Bpx);
		editor.putBoolean("Bbrowser", Bbrowser);
		editor.putBoolean("Bloc", Bloc);
		editor.putBoolean("Bcall", Bcall);
		editor.putBoolean("Bcallact", Bcallact);
		editor.putBoolean("Bsms", Bsms);
		editor.putBoolean("Blocale", Blocale);
		editor.putBoolean("Bpow", Bpow);
		editor.putBoolean("Bapp", Bapp);
		editor.putBoolean("Bmedia", Bmedia);
		editor.putBoolean("Bscreen", Bscreen);
		editor.putBoolean("Bgsm", Bgsm);
		editor.putLong("callintimesinterval", CALLINTIMESINTERVAL);
		editor.putLong("callouttimesinterval", CALLOUTTIMESINTERVAL);
		editor.putLong("callinalltimeinterval", CALLINALLTIMEINTERVAL);
		editor.putLong("calloutalltimeinterval", CALLOUTALLTIMESINTERVAL);
		editor.putLong("callnumberinterval", CALLNUMBERINTERVAL); // 7/30增加
		editor.putLong("receivernumberinterval", RECEIVERNUMBERINTERVAL); // 7/30增加
		editor.putLong("msgtimesinterval", MSGTIMESINTERVAL);
		editor.putLong("axinterval", AXINTERVAL);
		editor.putLong("ayinterval", AYINTERVAL);
		editor.putLong("azinterval", AZINTERVAL);
		editor.putLong("oxinterval", OXINTERVAL);
		editor.putLong("oyinterval", OYINTERVAL);
		editor.putLong("ozinterval", OZINTERVAL);
		editor.putLong("mxinterval", MXINTERVAL);
		editor.putLong("myinterval", MYINTERVAL);
		editor.putLong("myinterval", MZINTERVAL);
		editor.putLong("pxinterval", PXINTERVAL);
		editor.putLong("liinterval", LIINTERVAL);
		editor.putLong("tempinterval", TEMPINTERVAL);
		editor.putLong("presinterval", PRESINTERVAL);
		editor.putLong("longitudeinterval", LONGITUDEINTERVAL);
		editor.putLong("latitudeinterval", LATITUDEINTERVAL);
		editor.putLong("speedinterval", SPEEDINTERVAL);
		editor.putLong("bearinginterval", BEARINGINTERVAL);
		editor.putLong("altitudeinterval", ALTITUDEINTERVAL);
		editor.putLong("accuracyinterval", ACCURACYINTERVAL);
		editor.putLong("rssiinterval", RSSIINTERVAL);
		editor.putLong("ssidinterval", SSIDINTERVAL);
		editor.putLong("gsmrssiinterval", GSMRSSIINTERVAL);
		editor.putLong("gsmspninterval", GSMSPNINTERVAL);
		editor.putLong("powerinterval", POWERINTERVAL); // 7/30增加
		editor.putString("experimentId", exid);
		editor.putBoolean("tax", tax);
		editor.putBoolean("tay", tay);
		editor.putBoolean("taz", taz);
		editor.putBoolean("tox", tox);
		editor.putBoolean("toy", toy);
		editor.putBoolean("toz", toz);
		editor.putBoolean("tmx", tmx);
		editor.putBoolean("tmy", tmy);
		editor.putBoolean("tmz", tmz);
		editor.putBoolean("tpx", tpx);
		editor.putBoolean("ttemp", ttemp);
		editor.putBoolean("tlight", tlight);
		editor.putBoolean("tpressure", tpressure);
		editor.putBoolean("tlongitude", tlongitude);
		editor.putBoolean("tlatitude", tlatitude);
		editor.putBoolean("tspeed", tspeed);
		editor.putBoolean("tbearing", tbearing);
		editor.putBoolean("taltitude", taltitude);
		editor.putBoolean("taccuracy", taccuracy);
		editor.putBoolean("trssi", trssi);
		editor.putBoolean("tssid", tssid);
		editor.putBoolean("tscreen", tscreen);
		editor.putBoolean("textmedia", textmedia);
		editor.putBoolean("tapkact", tapkact);
		editor.putBoolean("tpkgname", tpkgname);
		editor.putBoolean("tpow", tpow);
		editor.putBoolean("tlan", tlan);
		editor.putBoolean("tcountry", tcountry);
		editor.putBoolean("tmsgtimes", tmsgtimes);
		editor.putBoolean("tincomingcount", tincomingcount);
		editor.putBoolean("toutcomingcount", toutcomingcount);
		editor.putBoolean("tinalltime", tinalltime);
		editor.putBoolean("toutalltime", toutalltime);
		editor.putBoolean("tcallin", tcallin); // 7/30增加
		editor.putBoolean("tcallout", tcallout); // 7/30增加
		// editor.putBoolean("tmsgbody",tmsgbody);
		editor.putBoolean("tcallstate", tcallstate);
		editor.putBoolean("tcallnumber", tcallnumber); // 7/31增加
		editor.putBoolean("treceivenumber", treceivenumber); // 7/31增加
		editor.putBoolean("tcalltime", tcalltime); // 7/31增加
		editor.putBoolean("treceivetime", treceivetime); // 7/31增加
		editor.putBoolean("tcallcontact", tcallcontact);
		editor.putBoolean("ttitle", ttitle);
		editor.putBoolean("turl", turl);
		editor.putBoolean("tgsmrssi", tgsmrssi);
		editor.putBoolean("tgsmspn", tgsmspn);
		editor.putBoolean("tpowerstatus", tpowerstatus); // 7/30增加
		editor.putBoolean("btax", btax);
		editor.putBoolean("btay", btay);
		editor.putBoolean("btaz", btaz);
		editor.putBoolean("btox", btox);
		editor.putBoolean("btoy", btoy);
		editor.putBoolean("btoz", btoz);
		editor.putBoolean("btmx", btmx);
		editor.putBoolean("btmy", btmy);
		editor.putBoolean("btmz", btmz);
		editor.putBoolean("btpx", btpx);
		editor.putBoolean("bttemp", bttemp);
		editor.putBoolean("btlight", btlight);
		editor.putBoolean("btpressure", btpressure);
		editor.putBoolean("btlongitude", btlongitude);
		editor.putBoolean("btlatitude", btlatitude);
		editor.putBoolean("btspeed", btspeed);
		editor.putBoolean("btbearing", btbearing);
		editor.putBoolean("btaltitude", btaltitude);
		editor.putBoolean("btaccuracy", btaccuracy);
		editor.putBoolean("Bpower", Bpower);
		editor.putString("axbigop", axbigop);
		editor.putString("aybigop", aybigop);
		editor.putString("azbigop", azbigop);
		editor.putString("oxbigop", oxbigop);
		editor.putString("oybigop", oybigop);
		editor.putString("ozbigop", ozbigop);
		editor.putString("mxbigop", mxbigop);
		editor.putString("mybigop", mybigop);
		editor.putString("mzbigop", mzbigop);
		editor.putString("pxbigop", pxbigop);
		editor.putString("tempbigop", tempbigop);
		editor.putString("lightbigop", lightbigop);
		editor.putString("pressurebigop", pressurebigop);
		editor.putString("speedbigop", speedbigop);
		editor.putString("altitudebigop", altitudebigop);
		editor.putString("bearingbigop", bearingbigop);
		editor.putString("accuracybigop", accuracybigop);
		editor.putString("longitudebigop", longitudebigop);
		editor.putString("latitudebigop", latitudebigop);
		editor.putString("axlessop", axlessop);
		editor.putString("aylessop", aylessop);
		editor.putString("azlessop", azlessop);
		editor.putString("oxlessop", oxlessop);
		editor.putString("oylessop", oylessop);
		editor.putString("ozlessop", ozlessop);
		editor.putString("mxlessop", mxlessop);
		editor.putString("mylessop", mylessop);
		editor.putString("mzlessop", mzlessop);
		editor.putString("pxlessop", pxlessop);
		editor.putString("templessop", templessop);
		editor.putString("lightlessop", lightlessop);
		editor.putString("pressurelessop", pressurelessop);
		editor.putString("speedlessop", speedlessop);
		editor.putString("altitudelessop", altitudelessop);
		editor.putString("bearinglessop", bearinglessop);
		editor.putString("accuracylessop", accuracylessop);
		editor.putString("longitudelessop", longitudelessop);
		editor.putString("latitudelessop", latitudelessop);
		editor.putString("axbigvalue", axbigvalue);
		editor.putString("aybigvalue", aybigvalue);
		editor.putString("azbigvalue", azbigvalue);
		editor.putString("oxbigvalue", oxbigvalue);
		editor.putString("oybigvalue", oybigvalue);
		editor.putString("ozbigvalue", ozbigvalue);
		editor.putString("mxbigvalue", mxbigvalue);
		editor.putString("mybigvalue", mybigvalue);
		editor.putString("mzbigvalue", mzbigvalue);
		editor.putString("pxbigvalue", pxbigvalue);
		editor.putString("tempbigvalue", tempbigvalue);
		editor.putString("lightbigvalue", lightbigvalue);
		editor.putString("pressurebigvalue", pressurebigvalue);
		editor.putString("speedbigvalue", speedbigvalue);
		editor.putString("altitudebigvalue", altitudebigvalue);
		editor.putString("bearingbigvalue", bearingbigvalue);
		editor.putString("accuracybigvalue", accuracybigvalue);
		editor.putString("longitudebigvalue", longitudebigvalue);
		editor.putString("latitudebigvalue", latitudebigvalue);
		editor.putString("axlessvalue", axlessvalue);
		editor.putString("aylessvalue", aylessvalue);
		editor.putString("azlessvalue", azlessvalue);
		editor.putString("oxlessvalue", oxlessvalue);
		editor.putString("oylessvalue", oylessvalue);
		editor.putString("ozlessvalue", ozlessvalue);
		editor.putString("mxlessvalue", mxlessvalue);
		editor.putString("mylessvalue", mylessvalue);
		editor.putString("mzlessvalue", mzlessvalue);
		editor.putString("pxlessvalue", pxlessvalue);
		editor.putString("templessvalue", templessvalue);
		editor.putString("lightlessvalue", lightlessvalue);
		editor.putString("pressurelessvalue", pressurelessvalue);
		editor.putString("speedlessvalue", speedlessvalue);
		editor.putString("altitudelessvalue", altitudelessvalue);
		editor.putString("bearinglessvalue", bearinglessvalue);
		editor.putString("accuracylessvalue", accuracylessvalue);
		editor.putString("longitudelessvalue", longitudelessvalue);
		editor.putString("latitudelessvalue", latitudelessvalue);
		editor.putBoolean("screenupload", screenupload);
		editor.putBoolean("chageupload", chageupload);
		editor.putBoolean("checktest", checktest);
		editor.putString("deviceId", ClicentDeviceID);
		editor.putString("deviceOs", DeviceOs);
		editor.putString("deviceType", Device);
		editor.putString("back", back);
		editor.putInt("h", h);
		editor.putString("nowexperiment", contents);
		editor.putString("nowtestdescript", testdescript);
		editor.putInt("SizeLimit", SizeLimit);
		editor.putInt("timelimit", Timelimit);
		editor.commit();
	}

	// 判斷實驗設定檔包含哪些屬性
	private void ActAttribute(String inputString) {
		Log.v(TAG, "ActAttribute");
		Matcher a1 = Pattern.compile("(acc.ax\\s*)").matcher(inputString);
		Matcher a2 = Pattern.compile("(acc.ay\\s*)").matcher(inputString);
		Matcher a3 = Pattern.compile("(acc.az\\s*)").matcher(inputString);
		Matcher a4 = Pattern.compile("(ori.ox\\s*)").matcher(inputString);
		Matcher a5 = Pattern.compile("(ori.oy\\s*)").matcher(inputString);
		Matcher a6 = Pattern.compile("(ori.oz\\s*)").matcher(inputString);
		Matcher a7 = Pattern.compile("(magn.mx\\s*)").matcher(inputString);
		Matcher a8 = Pattern.compile("(magn.my\\s*)").matcher(inputString);
		Matcher a9 = Pattern.compile("(magn.mz\\s*)").matcher(inputString);
		Matcher a10 = Pattern.compile("(px\\s*)").matcher(inputString);
		Matcher a11 = Pattern.compile("(temp.celsius\\s*)")
				.matcher(inputString);
		Matcher a12 = Pattern.compile("(light\\s*)").matcher(inputString);
		Matcher a13 = Pattern.compile("(pressure\\s*)").matcher(inputString);
		Matcher a14 = Pattern.compile("(gps.longitude\\s*)").matcher(
				inputString);
		Matcher a15 = Pattern.compile("(gps.latitude\\s*)")
				.matcher(inputString);
		Matcher a16 = Pattern.compile("(gps.speed\\s*)").matcher(inputString);
		Matcher a17 = Pattern.compile("(gps.bearing\\s*)").matcher(inputString);
		Matcher a18 = Pattern.compile("(gps.altitude\\s*)")
				.matcher(inputString);
		Matcher a19 = Pattern.compile("(gps.accuracy\\s*)")
				.matcher(inputString);
		Matcher a20 = Pattern.compile("(wifi.rssi\\s*)").matcher(inputString);
		Matcher a21 = Pattern.compile("(wifi.ssid\\s*)").matcher(inputString);
		Matcher a22 = Pattern.compile("(screen\\s*)").matcher(inputString);
		Matcher a23 = Pattern.compile("(extmedia\\s*)").matcher(inputString);
		Matcher a24 = Pattern.compile("(pkg.apkact\\s*)").matcher(inputString);
		Matcher a25 = Pattern.compile("(pkg.pkgname\\s*)").matcher(inputString);
		Matcher a26 = Pattern.compile("(pow.powact\\s*)").matcher(inputString);
		Matcher a27 = Pattern.compile("(locale.lan\\s*)").matcher(inputString);
		Matcher a28 = Pattern.compile("(locale.country\\s*)").matcher(
				inputString);
		Matcher a29 = Pattern.compile("(sms.times\\s*)").matcher(inputString);
		Matcher a30 = Pattern.compile("(call.incomingcount\\s*)").matcher(
				inputString);
		Matcher a31 = Pattern.compile("(call.outcomingcount\\s*)").matcher(
				inputString);
		Matcher a32 = Pattern.compile("(call.callinalltime\\s*)").matcher(
				inputString);
		Matcher a33 = Pattern.compile("(call.calloutalltimet\\s*)").matcher(
				inputString);
		Matcher a34 = Pattern.compile("(browser.title\\s*)").matcher(
				inputString);
		Matcher a35 = Pattern.compile("(browser.url\\s*)").matcher(inputString);
		Matcher a36 = Pattern.compile("(gsm.gsmrssi\\s*)").matcher(inputString);
		Matcher a37 = Pattern.compile("(gsm.gsmspn\\s*)").matcher(inputString);
		Matcher a38 = Pattern.compile("(callact.callnumber\\s*)").matcher(
				inputString);
		Matcher a39 = Pattern.compile("(callact.receivenumber\\s*)").matcher(
				inputString);
		Matcher a40 = Pattern.compile("(callact.calltime\\s*)").matcher(
				inputString);
		Matcher a41 = Pattern.compile("(callact.receivetime\\s*)").matcher(
				inputString);
		// Matcher a42 =
		// Pattern.compile("(pow.powerstatus\\s*)").matcher(inputString);

		while (a1.find()) {
			tax = true;
			break;
		}
		Log.v(TAG, String.valueOf(tax));
		while (a2.find()) {
			tay = true;
			break;
		}
		while (a3.find()) {
			taz = true;
			break;
		}
		while (a4.find()) {
			tox = true;
			break;
		}
		while (a5.find()) {
			toy = true;
			break;
		}
		while (a6.find()) {
			toz = true;
			break;
		}
		while (a7.find()) {
			tmx = true;
			break;
		}
		while (a8.find()) {
			tmy = true;
			break;
		}
		while (a9.find()) {
			tmz = true;
			break;
		}
		while (a10.find()) {
			tpx = true;
			break;
		}
		while (a11.find()) {
			ttemp = true;
			break;
		}
		while (a12.find()) {
			tlight = true;
			break;
		}
		while (a13.find()) {
			tpressure = true;
			break;
		}
		while (a14.find()) {
			tlongitude = true;
			break;
		}
		while (a15.find()) {
			tlatitude = true;
			break;
		}
		while (a16.find()) {
			tspeed = true;
			break;
		}
		while (a17.find()) {
			tbearing = true;
			break;
		}
		while (a18.find()) {
			taltitude = true;
			break;
		}
		while (a19.find()) {
			taccuracy = true;
			break;
		}
		while (a20.find()) {
			trssi = true;
			break;
		}
		while (a21.find()) {
			tssid = true;
			break;
		}
		while (a22.find()) {
			tscreen = true;
			break;
		}
		while (a23.find()) {
			textmedia = true;
			break;
		}
		while (a24.find()) {
			tapkact = true;
			break;
		}
		while (a25.find()) {
			tpkgname = true;
			break;
		}
		while (a26.find()) {
			tpow = true;
			break;
		}
		while (a27.find()) {
			tlan = true;
			break;
		}
		while (a28.find()) {
			tcountry = true;
			break;
		}
		while (a29.find()) {
			tmsgtimes = true;
			break;
		}
		while (a30.find()) {
			tincomingcount = true;
			break;
		}
		while (a31.find()) {
			toutcomingcount = true;
			break;
		}
		while (a32.find()) {
			tinalltime = true;
			break;
		}
		while (a33.find()) {
			toutalltime = true;
			break;
		}
		while (a34.find()) {
			ttitle = true;
			break;
		}
		while (a35.find()) {
			turl = true;
			break;
		}
		while (a36.find()) {
			tgsmrssi = true;
			break;
		}
		while (a37.find()) {
			tgsmspn = true;
			break;
		}
		while (a38.find()) {
			tcallnumber = true;
			break;
		}
		while (a39.find()) {
			treceivenumber = true;
			break;
		}
		while (a40.find()) {
			tcalltime = true;
			break;
		}
		while (a41.find()) {
			treceivetime = true;
			break;
		}
		/*
		 * while(a42.find()) { tpowerstatus = true; break;}
		 */

	}

	// 提醒受測者擷取項目
	private String IdentificationList(String inputString) {
		Log.v(TAG, "IdentificationList");
		Matcher m1 = Pattern.compile("(acc.[ax|ay|az]*\\s*)").matcher(
				inputString);
		Matcher m2 = Pattern.compile("(ori.[ox|oy|oz]*\\s*)").matcher(
				inputString);
		Matcher m3 = Pattern.compile("(magn.[mx|my|mz]*\\s*)").matcher(
				inputString);
		Matcher m4 = Pattern.compile("(px\\s*)").matcher(inputString);
		Matcher m5 = Pattern.compile("(temp.celsius\\s*)").matcher(inputString);
		Matcher m6 = Pattern.compile("(light\\s*)").matcher(inputString);
		Matcher m7 = Pattern.compile("(pressure\\s*)").matcher(inputString);
		Matcher m8 = Pattern
				.compile(
						"(gps.[longitude|latitude|speed|accuracy|bearing|altitude]*\\s*)")
				.matcher(inputString);
		Matcher m9 = Pattern.compile("(wifi.[rssi|ssid]\\s*)").matcher(
				inputString);
		Matcher m10 = Pattern.compile("(screen\\s*)").matcher(inputString);
		Matcher m11 = Pattern.compile("(extmedia\\s*)").matcher(inputString);
		Matcher m12 = Pattern.compile("(pkg.[apkact|pkgname]\\s*)").matcher(
				inputString);
		Matcher m13 = Pattern.compile("(pow.[powact|powerstatus]\\s*)")
				.matcher(inputString);
		Matcher m14 = Pattern.compile("(locale.[lan|country]\\s*)").matcher(
				inputString);
		Matcher m15 = Pattern.compile("(sms.[times]\\s*)").matcher(inputString);
		Matcher m16 = Pattern
				.compile(
						"(call.[incomingcount|outcomingcount|callinalltime|calloutalltime]\\s*)")
				.matcher(inputString);
		Matcher m17 = Pattern.compile("(browser.[title|url]\\s*)").matcher(
				inputString);
		Matcher m18 = Pattern.compile("(gsm.[gsmrssi|gsmspn]\\s*)").matcher(
				inputString);
		Matcher m19 = Pattern
				.compile(
						"(callact.[callnumber|receivenumber|calltime|receivetime]\\s*)")
				.matcher(inputString);

		ArrayList t1 = new ArrayList();
		String test = "本實驗將擷取:";

		while (m1.find()) {
			test += " 加速度感應器 ";
			Bacc = true;
			break;
		}
		Log.v(TAG, test);
		while (m2.find()) {
			test += " 方向感應器 ";
			Bori = true;
			break;
		}
		while (m3.find()) {
			test += " 磁性感應器 ";
			Bmagn = true;
			break;
		}
		while (m4.find()) {
			test += " 距離感應器 ";
			Bpx = true;
			break;
		}
		while (m5.find()) {
			test += " 溫度感應器 ";
			Btemp = true;
			break;
		}
		while (m6.find()) {
			test += " 亮度感應器 ";
			Bli = true;
			break;
		}
		while (m7.find()) {
			test += " 壓力感應器 ";
			Bpres = true;
			break;
		}
		while (m8.find()) {
			test += " GPS資訊 ";
			Bloc = true;
			break;
		}
		while (m9.find()) {
			test += " WiFI資訊 ";
			Bwifi = true;
			break;
		}
		while (m10.find()) {
			// test+=" SCREEN鞈� ";
			// Bscreen = true;
			// registerScreenBroadcastReceiver();
			break;
		}
		while (m11.find()) {
			test += " 外接裝置 ";
			Bmedia = true;
			// registerMediaBroadcastReceiver();
			break;
		}
		while (m12.find()) {
			test += " APP狀態資訊 ";
			Bapp = true;
			// registerPackageBroadcastReceiver();
			break;
		}
		while (m13.find()) {
			test += " 電源資訊 ";
			Bpow = true;
			// registerPowerconnectBroadcastReceiver();
			break;
		}
		while (m14.find()) {
			test += " 語言地區 ";
			Blocale = true;
			// registerLocaleChangedBroadcastReceiver();
			break;
		}
		while (m15.find()) {
			test += " 簡訊資訊  ";
			Bsms = true;
			// registerLocaleChangedBroadcastReceiver();
			break;
		}
		while (m16.find()) {
			test += " 通話資訊 ";
			Bcall = true;
			// registerCallBroadcastReceiver();
			break;
		}
		while (m17.find()) {
			test += " 網頁瀏覽紀錄 ";
			Bbrowser = true;
			break;
		}
		while (m18.find()) {
			test += " GSM資訊 ";
			Bgsm = true;
			break;
		}
		while (m19.find()) {
			test += " 電話資訊 ";
			Bcallact = true;
			break;
		}

		Log.v(TAG, test);

		return test;
	}

	// 判斷實驗設定檔是否符合規範
	private boolean IsValidDSL(String inputString) {
		Pattern p = Pattern
				.compile(
						"^\\b(log\\s*)(acc.ax|acc.ay|acc.az\\s*|ori.ox|ori.oy|ori.oz\\s*|magn.mx|magn.my|magn.mz\\s*|px\\s*|pres\\s*|li\\s*|celsius\\s*|gps.longitude|gps.latitude|gps.accuracy|gps.bearing|gps.altitude\\s*|browser.title|browser.url\\s*|call.incomingcount|call.outcomingcount|call.callinalltime|call.calloutalltime\\s*|sms.times\\s*|locale.lan|locale.country\\s*|pow\\s*|pkg\\s*|extmedia\\s*|screen\\s*|wifi.rssi|wifi.ssid\\s*)+(where\\s*)(gps.speed[>=|>|=|<|<=]+\\d+\\s*|acc.ax[>=|>|=|<|<=]+\\d*\\.\\d+|\\d+\\s*)(every\\s*){1,}(\\d+)$",
						Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);
		Matcher m = p.matcher(inputString);
		while (m.find()) {
			System.out.println("IsValidDSL test: " + m.group(0));
			if (m.group(0).length() != 0) {
				return true;
			}
		}
		return false;
	}

	// 限定條件比較參數剖析
	private void AccaxbkTokenizer(String inputString) {
		System.out.println("accax bk check: " + back);
		Pattern p = Pattern.compile("(acc.ax[>=|>|=|<|<=]+\\d+\\.\\d+)",
				Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);
		Matcher m = p.matcher(inputString.replace(",", "").replace(" ", ""));
		while (m.find()) {
			mapParser.put("acc", m.group(0));
			System.out.println("accax attribute: " + m.group(0));
			btax = true;
			Pattern b1 = Pattern.compile("[>=|>|=]+", Pattern.CASE_INSENSITIVE
					| Pattern.MULTILINE);
			Matcher m1 = b1.matcher(m.group(0).replace(",", "")
					.replace(" ", ""));

			while (m1.find()) {
				axbigop = m1.group(0);
				Pattern b2 = Pattern.compile("\\d+\\.\\d+",
						Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);
				Matcher m2 = b2.matcher(m.group(0).replace(",", "")
						.replace(" ", ""));
				while (m2.find()) {
					axbigvalue = m2.group(0);
				}
			}

			Pattern l1 = Pattern.compile("[<|<=]+", Pattern.CASE_INSENSITIVE
					| Pattern.MULTILINE);
			Matcher m3 = l1.matcher(m.group(0).replace(",", "")
					.replace(" ", ""));

			while (m3.find()) {
				axlessop = m3.group(0);
				Pattern l2 = Pattern.compile("\\d+\\.\\d+",
						Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);
				Matcher m4 = l2.matcher(m.group(0).replace(",", "")
						.replace(" ", ""));
				while (m4.find()) {
					axlessvalue = m4.group(0);
				}
			}
		}
		System.out.println("accax attribute: axvalue" + axlessop + axlessvalue);
		System.out.println("accax attribute: axvalue" + axbigop + axbigvalue);
	}

	private void AccaybkTokenizer(String inputString) {

		Pattern p = Pattern.compile("(acc.ay[>=|>|=|<|<=]+\\d+\\.\\d+)",
				Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);
		Matcher m = p.matcher(inputString.replace(",", "").replace(" ", ""));
		while (m.find()) {
			mapParser.put("acc", m.group(0));
			System.out.println("accay attribute: " + m.group(0));
			btay = true;
			Pattern b1 = Pattern.compile("[>=|>|=]+", Pattern.CASE_INSENSITIVE
					| Pattern.MULTILINE);
			Matcher m1 = b1.matcher(m.group(0).replace(",", "")
					.replace(" ", ""));

			while (m1.find()) {
				aybigop = m1.group(0);
				Pattern b2 = Pattern.compile("\\d+\\.\\d+",
						Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);
				Matcher m2 = b2.matcher(m.group(0).replace(",", "")
						.replace(" ", ""));
				while (m2.find()) {
					aybigvalue = m2.group(0);
				}
			}

			Pattern l1 = Pattern.compile("[<|<=]+", Pattern.CASE_INSENSITIVE
					| Pattern.MULTILINE);
			Matcher m3 = l1.matcher(m.group(0).replace(",", "")
					.replace(" ", ""));

			while (m3.find()) {
				aylessop = m3.group(0);
				Pattern l2 = Pattern.compile("\\d+\\.\\d+",
						Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);
				Matcher m4 = l2.matcher(m.group(0).replace(",", "")
						.replace(" ", ""));
				while (m4.find()) {
					aylessvalue = m4.group(0);
				}
			}
		}
	}

	private void AccazbkTokenizer(String inputString) {

		Pattern p = Pattern.compile("(acc.az[>=|>|=|<|<=]+\\d+\\.\\d+)",
				Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);
		Matcher m = p.matcher(inputString.replace(",", "").replace(" ", ""));
		while (m.find()) {
			mapParser.put("acc", m.group(0));
			System.out.println("accaz attribute: " + m.group(0));
			btaz = true;
			Pattern b1 = Pattern.compile("[>=|>|=]+", Pattern.CASE_INSENSITIVE
					| Pattern.MULTILINE);
			Matcher m1 = b1.matcher(m.group(0).replace(",", "")
					.replace(" ", ""));

			while (m1.find()) {
				azbigop = m1.group(0);
				Pattern b2 = Pattern.compile("\\d+\\.\\d+",
						Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);
				Matcher m2 = b2.matcher(m.group(0).replace(",", "")
						.replace(" ", ""));
				while (m2.find()) {
					azbigvalue = m2.group(0);
				}
			}

			Pattern l1 = Pattern.compile("[<|<=]+", Pattern.CASE_INSENSITIVE
					| Pattern.MULTILINE);
			Matcher m3 = l1.matcher(m.group(0).replace(",", "")
					.replace(" ", ""));

			while (m3.find()) {
				azlessop = m3.group(0);
				Pattern l2 = Pattern.compile("\\d+\\.\\d+",
						Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);
				Matcher m4 = l2.matcher(m.group(0).replace(",", "")
						.replace(" ", ""));
				while (m4.find()) {
					azlessvalue = m4.group(0);
				}
			}
		}
	}

	private void OrioxbkTokenizer(String inputString) {

		Pattern p = Pattern.compile("(ori.ox[>=|>|=|<|<=]+\\d+\\.\\d+)",
				Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);
		Matcher m = p.matcher(inputString.replace(",", "").replace(" ", ""));
		while (m.find()) {
			mapParser.put("ori", m.group(0));

			btox = true;
			Pattern b1 = Pattern.compile("[>=|>|=]+", Pattern.CASE_INSENSITIVE
					| Pattern.MULTILINE);
			Matcher m1 = b1.matcher(m.group(0).replace(",", "")
					.replace(" ", ""));

			while (m1.find()) {
				oxbigop = m1.group(0);
				Pattern b2 = Pattern.compile("\\d+\\.\\d+",
						Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);
				Matcher m2 = b2.matcher(m.group(0).replace(",", "")
						.replace(" ", ""));
				while (m2.find()) {
					oxbigvalue = m2.group(0);
				}
			}

			Pattern l1 = Pattern.compile("[<|<=]+", Pattern.CASE_INSENSITIVE
					| Pattern.MULTILINE);
			Matcher m3 = l1.matcher(m.group(0).replace(",", "")
					.replace(" ", ""));

			while (m3.find()) {
				oxlessop = m3.group(0);
				Pattern l2 = Pattern.compile("\\d+\\.\\d+",
						Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);
				Matcher m4 = l2.matcher(m.group(0).replace(",", "")
						.replace(" ", ""));
				while (m4.find()) {
					oxlessvalue = m4.group(0);
				}
			}
		}
	}

	private void OrioybkTokenizer(String inputString) {

		Pattern p = Pattern.compile("(ori.oy[>=|>|=|<|<=]+\\d+\\.\\d+)",
				Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);
		Matcher m = p.matcher(inputString.replace(",", "").replace(" ", ""));
		while (m.find()) {
			btoy = true;
			Pattern b1 = Pattern.compile("[>=|>|=]+", Pattern.CASE_INSENSITIVE
					| Pattern.MULTILINE);
			Matcher m1 = b1.matcher(m.group(0).replace(",", "")
					.replace(" ", ""));

			while (m1.find()) {
				oybigop = m1.group(0);
				Pattern b2 = Pattern.compile("\\d+\\.\\d+",
						Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);
				Matcher m2 = b2.matcher(m.group(0).replace(",", "")
						.replace(" ", ""));
				while (m2.find()) {
					oybigvalue = m2.group(0);
				}
			}

			Pattern l1 = Pattern.compile("[<|<=]+", Pattern.CASE_INSENSITIVE
					| Pattern.MULTILINE);
			Matcher m3 = l1.matcher(m.group(0).replace(",", "")
					.replace(" ", ""));

			while (m3.find()) {
				oylessop = m3.group(0);
				Pattern l2 = Pattern.compile("\\d+\\.\\d+",
						Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);
				Matcher m4 = l2.matcher(m.group(0).replace(",", "")
						.replace(" ", ""));
				while (m4.find()) {
					oylessvalue = m4.group(0);
				}
			}
		}
	}

	private void OriozbkTokenizer(String inputString) {

		Pattern p = Pattern.compile("(ori.oy[>=|>|=|<|<=]+\\d+\\.\\d+)",
				Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);
		Matcher m = p.matcher(inputString.replace(",", "").replace(" ", ""));
		while (m.find()) {
			btoz = true;
			Pattern b1 = Pattern.compile("[>=|>|=]+", Pattern.CASE_INSENSITIVE
					| Pattern.MULTILINE);
			Matcher m1 = b1.matcher(m.group(0).replace(",", "")
					.replace(" ", ""));

			while (m1.find()) {
				ozbigop = m1.group(0);
				Pattern b2 = Pattern.compile("\\d+\\.\\d+",
						Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);
				Matcher m2 = b2.matcher(m.group(0).replace(",", "")
						.replace(" ", ""));
				while (m2.find()) {
					ozbigvalue = m2.group(0);
				}
			}

			Pattern l1 = Pattern.compile("[<|<=]+", Pattern.CASE_INSENSITIVE
					| Pattern.MULTILINE);
			Matcher m3 = l1.matcher(m.group(0).replace(",", "")
					.replace(" ", ""));

			while (m3.find()) {
				ozlessop = m3.group(0);
				Pattern l2 = Pattern.compile("\\d+\\.\\d+",
						Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);
				Matcher m4 = l2.matcher(m.group(0).replace(",", "")
						.replace(" ", ""));
				while (m4.find()) {
					ozlessvalue = m4.group(0);
				}
			}
		}
	}

	private void MagnmxbkTokenizer(String inputString) {

		Pattern p = Pattern.compile("(magn.mx[>=|>|=|<|<=]+\\d+\\.\\d+)",
				Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);
		Matcher m = p.matcher(inputString.replace(",", "").replace(" ", ""));
		while (m.find()) {
			btmx = true;
			Pattern b1 = Pattern.compile("[>=|>|=]+", Pattern.CASE_INSENSITIVE
					| Pattern.MULTILINE);
			Matcher m1 = b1.matcher(m.group(0).replace(",", "")
					.replace(" ", ""));

			while (m1.find()) {
				mxbigop = m1.group(0);
				Pattern b2 = Pattern.compile("\\d+\\.\\d+",
						Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);
				Matcher m2 = b2.matcher(m.group(0).replace(",", "")
						.replace(" ", ""));
				while (m2.find()) {
					mxbigvalue = m2.group(0);
				}
			}

			Pattern l1 = Pattern.compile("[<|<=]+", Pattern.CASE_INSENSITIVE
					| Pattern.MULTILINE);
			Matcher m3 = l1.matcher(m.group(0).replace(",", "")
					.replace(" ", ""));

			while (m3.find()) {
				mxlessop = m3.group(0);
				Pattern l2 = Pattern.compile("\\d+\\.\\d+",
						Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);
				Matcher m4 = l2.matcher(m.group(0).replace(",", "")
						.replace(" ", ""));
				while (m4.find()) {
					mxlessvalue = m4.group(0);
				}
			}
		}
	}

	private void MagnmybkTokenizer(String inputString) {

		Pattern p = Pattern.compile("(magn.my[>=|>|=|<|<=]+\\d+\\.\\d+)",
				Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);
		Matcher m = p.matcher(inputString.replace(",", "").replace(" ", ""));
		while (m.find()) {
			btmy = true;
			Pattern b1 = Pattern.compile("[>=|>|=]+", Pattern.CASE_INSENSITIVE
					| Pattern.MULTILINE);
			Matcher m1 = b1.matcher(m.group(0).replace(",", "")
					.replace(" ", ""));

			while (m1.find()) {
				mybigop = m1.group(0);
				Pattern b2 = Pattern.compile("\\d+\\.\\d+",
						Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);
				Matcher m2 = b2.matcher(m.group(0).replace(",", "")
						.replace(" ", ""));
				while (m2.find()) {
					mybigvalue = m2.group(0);
				}
			}

			Pattern l1 = Pattern.compile("[<|<=]+", Pattern.CASE_INSENSITIVE
					| Pattern.MULTILINE);
			Matcher m3 = l1.matcher(m.group(0).replace(",", "")
					.replace(" ", ""));

			while (m3.find()) {
				mylessop = m3.group(0);
				Pattern l2 = Pattern.compile("\\d+\\.\\d+",
						Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);
				Matcher m4 = l2.matcher(m.group(0).replace(",", "")
						.replace(" ", ""));
				while (m4.find()) {
					mylessvalue = m4.group(0);
				}
			}
		}
	}

	private void MagnmzbkTokenizer(String inputString) {

		Pattern p = Pattern.compile("(magn.my[>=|>|=|<|<=]+\\d+\\.\\d+)",
				Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);
		Matcher m = p.matcher(inputString.replace(",", "").replace(" ", ""));
		while (m.find()) {
			btmz = true;
			Pattern b1 = Pattern.compile("[>=|>|=]+", Pattern.CASE_INSENSITIVE
					| Pattern.MULTILINE);
			Matcher m1 = b1.matcher(m.group(0).replace(",", "")
					.replace(" ", ""));

			while (m1.find()) {
				mzbigop = m1.group(0);
				Pattern b2 = Pattern.compile("\\d+\\.\\d+",
						Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);
				Matcher m2 = b2.matcher(m.group(0).replace(",", "")
						.replace(" ", ""));
				while (m2.find()) {
					mzbigvalue = m2.group(0);
				}
			}

			Pattern l1 = Pattern.compile("[<|<=]+", Pattern.CASE_INSENSITIVE
					| Pattern.MULTILINE);
			Matcher m3 = l1.matcher(m.group(0).replace(",", "")
					.replace(" ", ""));

			while (m3.find()) {
				mzlessop = m3.group(0);
				Pattern l2 = Pattern.compile("\\d+\\.\\d+",
						Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);
				Matcher m4 = l2.matcher(m.group(0).replace(",", "")
						.replace(" ", ""));
				while (m4.find()) {
					mzlessvalue = m4.group(0);
				}
			}
		}
	}

	private void ProxbkTokenizer(String inputString) {

		Pattern p = Pattern.compile("(px[>=|>|=|<|<=]+\\d+\\.\\d+)",
				Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);
		Matcher m = p.matcher(inputString.replace(",", "").replace(" ", ""));
		while (m.find()) {
			btpx = true;
			Pattern b1 = Pattern.compile("[>=|>|=]+", Pattern.CASE_INSENSITIVE
					| Pattern.MULTILINE);
			Matcher m1 = b1.matcher(m.group(0).replace(",", "")
					.replace(" ", ""));

			while (m1.find()) {
				pxbigop = m1.group(0);
				Pattern b2 = Pattern.compile("\\d+\\.\\d+",
						Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);
				Matcher m2 = b2.matcher(m.group(0).replace(",", "")
						.replace(" ", ""));
				while (m2.find()) {
					pxbigvalue = m2.group(0);
				}
			}

			Pattern l1 = Pattern.compile("[<|<=]+", Pattern.CASE_INSENSITIVE
					| Pattern.MULTILINE);
			Matcher m3 = l1.matcher(m.group(0).replace(",", "")
					.replace(" ", ""));

			while (m3.find()) {
				pxlessop = m3.group(0);
				Pattern l2 = Pattern.compile("\\d+\\.\\d+",
						Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);
				Matcher m4 = l2.matcher(m.group(0).replace(",", "")
						.replace(" ", ""));
				while (m4.find()) {
					pxlessvalue = m4.group(0);
				}
			}
		}
	}

	private void PressurebkTokenizer(String inputString) {

		Pattern p = Pattern.compile("(pres[>=|>|=|<|<=]+\\d+\\.\\d+)",
				Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);
		Matcher m = p.matcher(inputString.replace(",", "").replace(" ", ""));
		while (m.find()) {
			btpressure = true;
			Pattern b1 = Pattern.compile("[>=|>|=]+", Pattern.CASE_INSENSITIVE
					| Pattern.MULTILINE);
			Matcher m1 = b1.matcher(m.group(0).replace(",", "")
					.replace(" ", ""));

			while (m1.find()) {
				pressurebigop = m1.group(0);
				Pattern b2 = Pattern.compile("\\d+\\.\\d+",
						Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);
				Matcher m2 = b2.matcher(m.group(0).replace(",", "")
						.replace(" ", ""));
				while (m2.find()) {
					pressurebigvalue = m2.group(0);
				}
			}

			Pattern l1 = Pattern.compile("[<|<=]+", Pattern.CASE_INSENSITIVE
					| Pattern.MULTILINE);
			Matcher m3 = l1.matcher(m.group(0).replace(",", "")
					.replace(" ", ""));

			while (m3.find()) {
				pressurelessop = m3.group(0);
				Pattern l2 = Pattern.compile("\\d+\\.\\d+",
						Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);
				Matcher m4 = l2.matcher(m.group(0).replace(",", "")
						.replace(" ", ""));
				while (m4.find()) {
					pressurelessvalue = m4.group(0);
				}
			}
		}
	}

	private void LightbkTokenizer(String inputString) {

		Pattern p = Pattern.compile("(li[>=|>|=|<|<=]+\\d+\\.\\d+)",
				Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);
		Matcher m = p.matcher(inputString.replace(",", "").replace(" ", ""));
		while (m.find()) {
			btlight = true;
			Pattern b1 = Pattern.compile("[>=|>|=]+", Pattern.CASE_INSENSITIVE
					| Pattern.MULTILINE);
			Matcher m1 = b1.matcher(m.group(0).replace(",", "")
					.replace(" ", ""));

			while (m1.find()) {
				lightbigop = m1.group(0);
				Pattern b2 = Pattern.compile("\\d+\\.\\d+",
						Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);
				Matcher m2 = b2.matcher(m.group(0).replace(",", "")
						.replace(" ", ""));
				while (m2.find()) {
					lightbigvalue = m2.group(0);
				}
			}

			Pattern l1 = Pattern.compile("[<|<=]+", Pattern.CASE_INSENSITIVE
					| Pattern.MULTILINE);
			Matcher m3 = l1.matcher(m.group(0).replace(",", "")
					.replace(" ", ""));

			while (m3.find()) {
				lightlessop = m3.group(0);
				Pattern l2 = Pattern.compile("\\d+\\.\\d+",
						Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);
				Matcher m4 = l2.matcher(m.group(0).replace(",", "")
						.replace(" ", ""));
				while (m4.find()) {
					lightlessvalue = m4.group(0);
				}
			}
		}
	}

	private void TempbkTokenizer(String inputString) {

		Pattern p = Pattern.compile("(celsius[>=|>|=|<|<=]+\\d+\\.\\d+)",
				Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);
		Matcher m = p.matcher(inputString.replace(",", "").replace(" ", ""));
		while (m.find()) {
			bttemp = true;
			Pattern b1 = Pattern.compile("[>=|>|=]+", Pattern.CASE_INSENSITIVE
					| Pattern.MULTILINE);
			Matcher m1 = b1.matcher(m.group(0).replace(",", "")
					.replace(" ", ""));

			while (m1.find()) {
				tempbigop = m1.group(0);
				Pattern b2 = Pattern.compile("\\d+\\.\\d+",
						Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);
				Matcher m2 = b2.matcher(m.group(0).replace(",", "")
						.replace(" ", ""));
				while (m2.find()) {
					tempbigvalue = m2.group(0);
				}
			}

			Pattern l1 = Pattern.compile("[<|<=]+", Pattern.CASE_INSENSITIVE
					| Pattern.MULTILINE);
			Matcher m3 = l1.matcher(m.group(0).replace(",", "")
					.replace(" ", ""));

			while (m3.find()) {
				templessop = m3.group(0);
				Pattern l2 = Pattern.compile("\\d+\\.\\d+",
						Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);
				Matcher m4 = l2.matcher(m.group(0).replace(",", "")
						.replace(" ", ""));
				while (m4.find()) {
					templessvalue = m4.group(0);
				}
			}
		}
	}

	private void loclatitudebkTokenizer(String inputString) {

		Pattern p = Pattern.compile("(gps.latitude[>=|>|=|<|<=]+\\d+\\.\\d+)",
				Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);
		Matcher m = p.matcher(inputString.replace(",", "").replace(" ", ""));
		while (m.find()) {

			btlatitude = true;
			Pattern b1 = Pattern.compile("[>=|>|=]+", Pattern.CASE_INSENSITIVE
					| Pattern.MULTILINE);
			Matcher m1 = b1.matcher(m.group(0).replace(",", "")
					.replace(" ", ""));

			while (m1.find()) {
				latitudebigop = m1.group(0);
				Pattern b2 = Pattern.compile("\\d+\\.\\d+",
						Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);
				Matcher m2 = b2.matcher(m.group(0).replace(",", "")
						.replace(" ", ""));
				while (m2.find()) {
					latitudebigvalue = m2.group(0);
				}
			}

			Pattern l1 = Pattern.compile("[<|<=]+", Pattern.CASE_INSENSITIVE
					| Pattern.MULTILINE);
			Matcher m3 = l1.matcher(m.group(0).replace(",", "")
					.replace(" ", ""));

			while (m3.find()) {
				latitudelessop = m3.group(0);
				Pattern l2 = Pattern.compile("\\d+\\.\\d+",
						Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);
				Matcher m4 = l2.matcher(m.group(0).replace(",", "")
						.replace(" ", ""));
				while (m4.find()) {
					latitudelessvalue = m4.group(0);
				}
			}
		}
	}

	private void loclongitudebkTokenizer(String inputString) {

		Pattern p = Pattern.compile("(gps.longitude[>=|>|=|<|<=]+\\d+\\.\\d+)",
				Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);
		Matcher m = p.matcher(inputString.replace(",", "").replace(" ", ""));
		while (m.find()) {
			btlatitude = true;
			Pattern b1 = Pattern.compile("[>=|>|=]+", Pattern.CASE_INSENSITIVE
					| Pattern.MULTILINE);
			Matcher m1 = b1.matcher(m.group(0).replace(",", "")
					.replace(" ", ""));

			while (m1.find()) {
				latitudebigop = m1.group(0);
				Pattern b2 = Pattern.compile("\\d+\\.\\d+",
						Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);
				Matcher m2 = b2.matcher(m.group(0).replace(",", "")
						.replace(" ", ""));
				while (m2.find()) {
					latitudebigvalue = m2.group(0);
				}
			}

			Pattern l1 = Pattern.compile("[<|<=]+", Pattern.CASE_INSENSITIVE
					| Pattern.MULTILINE);
			Matcher m3 = l1.matcher(m.group(0).replace(",", "")
					.replace(" ", ""));

			while (m3.find()) {
				latitudelessop = m3.group(0);
				Pattern l2 = Pattern.compile("\\d+\\.\\d+",
						Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);
				Matcher m4 = l2.matcher(m.group(0).replace(",", "")
						.replace(" ", ""));
				while (m4.find()) {
					latitudelessvalue = m4.group(0);
				}
			}
		}
	}

	private void locaccuracybkTokenizer(String inputString) {

		Pattern p = Pattern.compile("(gps.accuracy[>=|>|=|<|<=]+\\d+\\.\\d+)",
				Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);
		Matcher m = p.matcher(inputString.replace(",", "").replace(" ", ""));
		while (m.find()) {
			btlatitude = true;
			Pattern b1 = Pattern.compile("[>=|>|=]+", Pattern.CASE_INSENSITIVE
					| Pattern.MULTILINE);
			Matcher m1 = b1.matcher(m.group(0).replace(",", "")
					.replace(" ", ""));

			while (m1.find()) {
				latitudebigop = m1.group(0);
				Pattern b2 = Pattern.compile("\\d+\\.\\d+",
						Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);
				Matcher m2 = b2.matcher(m.group(0).replace(",", "")
						.replace(" ", ""));
				while (m2.find()) {
					latitudebigvalue = m2.group(0);
				}
			}

			Pattern l1 = Pattern.compile("[<|<=]+", Pattern.CASE_INSENSITIVE
					| Pattern.MULTILINE);
			Matcher m3 = l1.matcher(m.group(0).replace(",", "")
					.replace(" ", ""));

			while (m3.find()) {
				latitudelessop = m3.group(0);
				Pattern l2 = Pattern.compile("\\d+\\.\\d+",
						Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);
				Matcher m4 = l2.matcher(m.group(0).replace(",", "")
						.replace(" ", ""));
				while (m4.find()) {
					latitudelessvalue = m4.group(0);
				}
			}
		}
	}

	private void locbearingbkTokenizer(String inputString) {

		Pattern p = Pattern.compile("(gps.bearing[>=|>|=|<|<=]+\\d+\\.\\d+)",
				Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);
		Matcher m = p.matcher(inputString.replace(",", "").replace(" ", ""));
		while (m.find()) {
			btbearing = true;
			Pattern b1 = Pattern.compile("[>=|>|=]+", Pattern.CASE_INSENSITIVE
					| Pattern.MULTILINE);
			Matcher m1 = b1.matcher(m.group(0).replace(",", "")
					.replace(" ", ""));

			while (m1.find()) {
				bearingbigop = m1.group(0);
				Pattern b2 = Pattern.compile("\\d+\\.\\d+",
						Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);
				Matcher m2 = b2.matcher(m.group(0).replace(",", "")
						.replace(" ", ""));
				while (m2.find()) {
					bearingbigvalue = m2.group(0);
				}
			}

			Pattern l1 = Pattern.compile("[<|<=]+", Pattern.CASE_INSENSITIVE
					| Pattern.MULTILINE);
			Matcher m3 = l1.matcher(m.group(0).replace(",", "")
					.replace(" ", ""));

			while (m3.find()) {
				bearinglessop = m3.group(0);
				Pattern l2 = Pattern.compile("\\d+\\.\\d+",
						Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);
				Matcher m4 = l2.matcher(m.group(0).replace(",", "")
						.replace(" ", ""));
				while (m4.find()) {
					bearinglessvalue = m4.group(0);
				}
			}
		}
	}

	private void localtitudebkTokenizer(String inputString) {

		Pattern p = Pattern.compile("(gps.altitude[>=|>|=|<|<=]+\\d+\\.\\d+)",
				Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);
		Matcher m = p.matcher(inputString.replace(",", "").replace(" ", ""));
		while (m.find()) {
			btaltitude = true;
			Pattern b1 = Pattern.compile("[>=|>|=]+", Pattern.CASE_INSENSITIVE
					| Pattern.MULTILINE);
			Matcher m1 = b1.matcher(m.group(0).replace(",", "")
					.replace(" ", ""));

			while (m1.find()) {
				altitudebigop = m1.group(0);
				Pattern b2 = Pattern.compile("\\d+\\.\\d+",
						Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);
				Matcher m2 = b2.matcher(m.group(0).replace(",", "")
						.replace(" ", ""));
				while (m2.find()) {
					altitudebigvalue = m2.group(0);
				}
			}

			Pattern l1 = Pattern.compile("[<|<=]+", Pattern.CASE_INSENSITIVE
					| Pattern.MULTILINE);
			Matcher m3 = l1.matcher(m.group(0).replace(",", "")
					.replace(" ", ""));

			while (m3.find()) {
				altitudelessop = m3.group(0);
				Pattern l2 = Pattern.compile("\\d+\\.\\d+",
						Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);
				Matcher m4 = l2.matcher(m.group(0).replace(",", "")
						.replace(" ", ""));
				while (m4.find()) {
					altitudelessvalue = m4.group(0);
				}
			}
		}
	}

	private void locspeedbkTokenizer(String inputString) {

		Pattern p = Pattern.compile("(gps.speed[>=|>|=|<|<=]+\\d+\\.\\d+)",
				Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);
		Matcher m = p.matcher(inputString.replace(",", "").replace(" ", ""));
		while (m.find()) {
			btspeed = true;
			Pattern b1 = Pattern.compile("[>=|>|=]+", Pattern.CASE_INSENSITIVE
					| Pattern.MULTILINE);
			Matcher m1 = b1.matcher(m.group(0).replace(",", "")
					.replace(" ", ""));

			while (m1.find()) {
				speedbigop = m1.group(0);
				Pattern b2 = Pattern.compile("\\d+\\.\\d+",
						Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);
				Matcher m2 = b2.matcher(m.group(0).replace(",", "")
						.replace(" ", ""));
				while (m2.find()) {
					speedbigvalue = m2.group(0);
				}
			}

			Pattern l1 = Pattern.compile("[<|<=]+", Pattern.CASE_INSENSITIVE
					| Pattern.MULTILINE);
			Matcher m3 = l1.matcher(m.group(0).replace(",", "")
					.replace(" ", ""));

			while (m3.find()) {
				speedlessop = m3.group(0);
				Pattern l2 = Pattern.compile("\\d+\\.\\d+",
						Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);
				Matcher m4 = l2.matcher(m.group(0).replace(",", "")
						.replace(" ", ""));
				while (m4.find()) {
					speedlessvalue = m4.group(0);
				}
			}
		}
	}

	// 上船時機判定螢幕開關
	private void screenuploadbkTokenizer(String inputString) {
		// TODO Auto-generated method stub
		// uploadpolicy screenoff, charging
		Matcher m1 = Pattern.compile("(screenoff\\s*)").matcher(inputString);
		// Pattern p = Pattern.compile("(screenoff\\s*)",
		// Pattern.CASE_INSENSITIVE|Pattern.MULTILINE);
		// Matcher m = p.matcher(inputString);
		while (m1.find()) {
			screenupload = true;
		}
	}

	// 上船時機判定是否充電
	private void charguploadbkTokenizer(String inputString) {
		// TODO Auto-generated method stub
		Matcher m2 = Pattern.compile("(charging\\s*)").matcher(inputString);
		// Pattern p = Pattern.compile("(charging\\s*)",
		// Pattern.CASE_INSENSITIVE|Pattern.MULTILINE);
		// Matcher m = p.matcher(inputString);
		while (m2.find()) {
			chageupload = true;
		}
	}

	// 各屬性頻率判斷
	private void SmsintervalTokenizer(String inputString) {
		Pattern p = Pattern.compile("(sms.times\\s*\\d+)",
				Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);
		Matcher m = p.matcher(inputString.replace(",", "").replace(" ", ""));
		String ss = "";
		while (m.find()) {
			Pattern p1 = Pattern.compile("\\d+", Pattern.CASE_INSENSITIVE
					| Pattern.MULTILINE);
			Matcher m1 = p1.matcher(m.group(0).replace(",", "")
					.replace(" ", ""));
			while (m1.find()) {

				MSGTIMESINTERVAL = (Integer.parseInt(m1.group(0)) * 1000);

			}
		}
	}

	private void GsmrssiintervalTokenizer(String inputString) {
		Pattern p = Pattern.compile("(gsm.gsmrssi\\s*\\d+)",
				Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);
		Matcher m = p.matcher(inputString.replace(",", "").replace(" ", ""));
		String ss = "";
		while (m.find()) {
			tgsmrssi = true;
			Pattern p1 = Pattern.compile("\\d+", Pattern.CASE_INSENSITIVE
					| Pattern.MULTILINE);
			Matcher m1 = p1.matcher(m.group(0).replace(",", "")
					.replace(" ", ""));
			while (m1.find()) {

				GSMRSSIINTERVAL = (Integer.parseInt(m1.group(0)) * 1000);

			}
		}
	}

	private void GsmspnintervalTokenizer(String inputString) {
		Pattern p = Pattern.compile("(gsm.gsmspn\\s*\\d+)",
				Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);
		Matcher m = p.matcher(inputString.replace(",", "").replace(" ", ""));
		String ss = "";
		while (m.find()) {
			tgsmspn = true;
			Pattern p1 = Pattern.compile("\\d+", Pattern.CASE_INSENSITIVE
					| Pattern.MULTILINE);
			Matcher m1 = p1.matcher(m.group(0).replace(",", "")
					.replace(" ", ""));
			while (m1.find()) {

				GSMSPNINTERVAL = (Integer.parseInt(m1.group(0)) * 1000);
			}
		}
	}

	// interval
	private void axintervalTokenizer(String inputString) {
		Pattern p = Pattern.compile("(acc.ax\\s*\\d+)",
				Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);
		Matcher m = p.matcher(inputString.replace(",", "").replace(" ", ""));
		while (m.find()) {
			Pattern p1 = Pattern.compile("\\d+", Pattern.CASE_INSENSITIVE
					| Pattern.MULTILINE);
			Matcher m1 = p1.matcher(m.group(0).replace(",", "")
					.replace(" ", ""));
			while (m1.find()) {
				AXINTERVAL = (Integer.parseInt(m1.group(0)) * 1000);
				Log.v(TAG, "axintervalTokenizer");
			}
		}
	}

	// interval
	private void ayintervalTokenizer(String inputString) {
		Pattern p = Pattern.compile("(acc.ay\\s*\\d+)",
				Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);
		Matcher m = p.matcher(inputString.replace(",", "").replace(" ", ""));
		while (m.find()) {
			Pattern p1 = Pattern.compile("\\d+", Pattern.CASE_INSENSITIVE
					| Pattern.MULTILINE);
			Matcher m1 = p1.matcher(m.group(0).replace(",", "")
					.replace(" ", ""));
			while (m1.find()) {
				// mapParser.put("interval", String.valueOf(m1.group(0)));
				AYINTERVAL = (Integer.parseInt(m1.group(0)) * 1000);
				Log.v(TAG, "ayintervalTokenizer");

			}
		}
	}

	// interval
	private void azintervalTokenizer(String inputString) {
		Pattern p = Pattern.compile("(acc.az\\s*\\d+)",
				Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);
		Matcher m = p.matcher(inputString.replace(",", "").replace(" ", ""));
		while (m.find()) {
			Pattern p1 = Pattern.compile("\\d+", Pattern.CASE_INSENSITIVE
					| Pattern.MULTILINE);
			Matcher m1 = p1.matcher(m.group(0).replace(",", "")
					.replace(" ", ""));
			while (m1.find()) {
				AZINTERVAL = (Integer.parseInt(m1.group(0)) * 1000);
				Log.v(TAG, "azintervalTokenizer");

			}
		}
	}

	// interval
	private void oxintervalTokenizer(String inputString) {
		Pattern p = Pattern.compile("(ori.ox\\s*\\d+)",
				Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);
		Matcher m = p.matcher(inputString.replace(",", "").replace(" ", ""));
		while (m.find()) {
			Pattern p1 = Pattern.compile("\\d+", Pattern.CASE_INSENSITIVE
					| Pattern.MULTILINE);
			Matcher m1 = p1.matcher(m.group(0).replace(",", "")
					.replace(" ", ""));
			while (m1.find()) {

				OXINTERVAL = (Integer.parseInt(m1.group(0)) * 1000);
				Log.v(TAG, "oxintervalTokenizer");

			}
		}
	}

	// interval
	private void oyintervalTokenizer(String inputString) {
		Pattern p = Pattern.compile("(ori.oy\\s*\\d+)",
				Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);
		Matcher m = p.matcher(inputString.replace(",", "").replace(" ", ""));
		while (m.find()) {
			Pattern p1 = Pattern.compile("\\d+", Pattern.CASE_INSENSITIVE
					| Pattern.MULTILINE);
			Matcher m1 = p1.matcher(m.group(0).replace(",", "")
					.replace(" ", ""));
			while (m1.find()) {

				OYINTERVAL = (Integer.parseInt(m1.group(0)) * 1000);
				Log.v(TAG, "oyintervalTokenizer");

			}
		}
	}

	// interval
	private void ozintervalTokenizer(String inputString) {
		Pattern p = Pattern.compile("(ori.oz\\s*\\d+)",
				Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);
		Matcher m = p.matcher(inputString.replace(",", "").replace(" ", ""));
		while (m.find()) {
			Pattern p1 = Pattern.compile("\\d+", Pattern.CASE_INSENSITIVE
					| Pattern.MULTILINE);
			Matcher m1 = p1.matcher(m.group(0).replace(",", "")
					.replace(" ", ""));
			while (m1.find()) {

				OZINTERVAL = (Integer.parseInt(m1.group(0)) * 1000);
				Log.v(TAG, "ozintervalTokenizer");

			}
		}
	}

	// interval
	private void mxintervalTokenizer(String inputString) {
		Pattern p = Pattern.compile("(magn.mx\\s*\\d+)",
				Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);
		Matcher m = p.matcher(inputString.replace(",", "").replace(" ", ""));
		while (m.find()) {
			Pattern p1 = Pattern.compile("\\d+", Pattern.CASE_INSENSITIVE
					| Pattern.MULTILINE);
			Matcher m1 = p1.matcher(m.group(0).replace(",", "")
					.replace(" ", ""));
			while (m1.find()) {

				MXINTERVAL = (Integer.parseInt(m1.group(0)) * 1000);
				Log.v(TAG, "mxintervalTokenizer");

			}
		}
	}

	// interval
	private void myintervalTokenizer(String inputString) {
		Pattern p = Pattern.compile("(magn.my\\s*\\d+)",
				Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);
		Matcher m = p.matcher(inputString.replace(",", "").replace(" ", ""));
		while (m.find()) {
			Pattern p1 = Pattern.compile("\\d+", Pattern.CASE_INSENSITIVE
					| Pattern.MULTILINE);
			Matcher m1 = p1.matcher(m.group(0).replace(",", "")
					.replace(" ", ""));
			while (m1.find()) {

				MYINTERVAL = (Integer.parseInt(m1.group(0)) * 1000);
				Log.v(TAG, "myintervalTokenizer");

			}
		}
	}

	// interval
	private void mzintervalTokenizer(String inputString) {
		Pattern p = Pattern.compile("(magn.mz\\s*\\d+)",
				Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);
		Matcher m = p.matcher(inputString.replace(",", "").replace(" ", ""));
		while (m.find()) {
			Pattern p1 = Pattern.compile("\\d+", Pattern.CASE_INSENSITIVE
					| Pattern.MULTILINE);
			Matcher m1 = p1.matcher(m.group(0).replace(",", "")
					.replace(" ", ""));
			while (m1.find()) {

				MZINTERVAL = (Integer.parseInt(m1.group(0)) * 1000);
				Log.v(TAG, "mzintervalTokenizer");

			}
		}
	}

	// interval
	private void tempintervalTokenizer(String inputString) {
		Pattern p = Pattern.compile("(temp\\s*\\d+)", Pattern.CASE_INSENSITIVE
				| Pattern.MULTILINE);
		Matcher m = p.matcher(inputString.replace(",", "").replace(" ", ""));
		while (m.find()) {
			Pattern p1 = Pattern.compile("\\d+", Pattern.CASE_INSENSITIVE
					| Pattern.MULTILINE);
			Matcher m1 = p1.matcher(m.group(0).replace(",", "")
					.replace(" ", ""));
			while (m1.find()) {

				TEMPINTERVAL = (Integer.parseInt(m1.group(0)) * 1000);
				Log.v(TAG, "tempintervalTokenizer");

			}
		}
	}

	// interval
	private void liintervalTokenizer(String inputString) {
		Pattern p = Pattern.compile("(li\\s*\\d+)", Pattern.CASE_INSENSITIVE
				| Pattern.MULTILINE);
		Matcher m = p.matcher(inputString.replace(",", "").replace(" ", ""));
		while (m.find()) {
			Pattern p1 = Pattern.compile("\\d+", Pattern.CASE_INSENSITIVE
					| Pattern.MULTILINE);
			Matcher m1 = p1.matcher(m.group(0).replace(",", "")
					.replace(" ", ""));
			while (m1.find()) {

				LIINTERVAL = (Integer.parseInt(m1.group(0)) * 1000);
				Log.v(TAG, "liintervalTokenizer");

			}
		}
	}

	// interval
	private void pxintervalTokenizer(String inputString) {
		Pattern p = Pattern.compile("(px\\s*\\d+)", Pattern.CASE_INSENSITIVE
				| Pattern.MULTILINE);
		Matcher m = p.matcher(inputString.replace(",", "").replace(" ", ""));
		while (m.find()) {
			Pattern p1 = Pattern.compile("\\d+", Pattern.CASE_INSENSITIVE
					| Pattern.MULTILINE);
			Matcher m1 = p1.matcher(m.group(0).replace(",", "")
					.replace(" ", ""));
			while (m1.find()) {

				PXINTERVAL = (Integer.parseInt(m1.group(0)) * 1000);
				Log.v(TAG, "pxintervalTokenizer");

			}
		}
	}

	// interval
	private void presintervalTokenizer(String inputString) {
		Pattern p = Pattern.compile("(pres\\s*\\d+)", Pattern.CASE_INSENSITIVE
				| Pattern.MULTILINE);
		Matcher m = p.matcher(inputString.replace(",", "").replace(" ", ""));
		while (m.find()) {
			Pattern p1 = Pattern.compile("\\d+", Pattern.CASE_INSENSITIVE
					| Pattern.MULTILINE);
			Matcher m1 = p1.matcher(m.group(0).replace(",", "")
					.replace(" ", ""));
			while (m1.find()) {

				PRESINTERVAL = (Integer.parseInt(m1.group(0)) * 1000);
				Log.v(TAG, "presintervalTokenizer");

			}
		}
	}

	// interval
	private void longitudeintervalTokenizer(String inputString) {
		Pattern p = Pattern.compile("(gps.longitude\\s*\\d+)",
				Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);
		Matcher m = p.matcher(inputString.replace(",", "").replace(" ", ""));
		while (m.find()) {
			Pattern p1 = Pattern.compile("\\d+", Pattern.CASE_INSENSITIVE
					| Pattern.MULTILINE);
			Matcher m1 = p1.matcher(m.group(0).replace(",", "")
					.replace(" ", ""));
			while (m1.find()) {

				LONGITUDEINTERVAL = (Integer.parseInt(m1.group(0)) * 1000);
				Log.v(TAG, "longitudeintervalTokenizer");

			}
		}
	}

	// interval
	private void latitudeintervalTokenizer(String inputString) {
		Pattern p = Pattern.compile("(gps.latitude\\s*\\d+)",
				Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);
		Matcher m = p.matcher(inputString.replace(",", "").replace(" ", ""));
		while (m.find()) {
			Pattern p1 = Pattern.compile("\\d+", Pattern.CASE_INSENSITIVE
					| Pattern.MULTILINE);
			Matcher m1 = p1.matcher(m.group(0).replace(",", "")
					.replace(" ", ""));
			while (m1.find()) {
				LATITUDEINTERVAL = (Integer.parseInt(m1.group(0)) * 1000);
				Log.v(TAG, "latitudeintervalTokenizer");

			}
		}
	}

	// interval
	private void speedintervalTokenizer(String inputString) {
		Pattern p = Pattern.compile("(gps.speed\\s*\\d+)",
				Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);
		Matcher m = p.matcher(inputString.replace(",", "").replace(" ", ""));
		while (m.find()) {
			Pattern p1 = Pattern.compile("\\d+", Pattern.CASE_INSENSITIVE
					| Pattern.MULTILINE);
			Matcher m1 = p1.matcher(m.group(0).replace(",", "")
					.replace(" ", ""));
			while (m1.find()) {
				SPEEDINTERVAL = (Integer.parseInt(m1.group(0)) * 1000);
				Log.v(TAG, "speedintervalTokenizer");

			}
		}
	}

	// interval
	private void altitudeintervalTokenizer(String inputString) {
		Pattern p = Pattern.compile("(gps.altitude\\s*\\d+)",
				Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);
		Matcher m = p.matcher(inputString.replace(",", "").replace(" ", ""));
		while (m.find()) {
			Pattern p1 = Pattern.compile("\\d+", Pattern.CASE_INSENSITIVE
					| Pattern.MULTILINE);
			Matcher m1 = p1.matcher(m.group(0).replace(",", "")
					.replace(" ", ""));
			while (m1.find()) {

				ALTITUDEINTERVAL = (Integer.parseInt(m1.group(0)) * 1000);
				Log.v(TAG, "altitudeintervalTokenizer");

			}
		}
	}

	// interval
	private void bearingintervalTokenizer(String inputString) {
		Pattern p = Pattern.compile("(gps.bearing\\s*\\d+)",
				Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);
		Matcher m = p.matcher(inputString.replace(",", "").replace(" ", ""));
		while (m.find()) {
			Pattern p1 = Pattern.compile("\\d+", Pattern.CASE_INSENSITIVE
					| Pattern.MULTILINE);
			Matcher m1 = p1.matcher(m.group(0).replace(",", "")
					.replace(" ", ""));
			while (m1.find()) {

				BEARINGINTERVAL = (Integer.parseInt(m1.group(0)) * 1000);
				Log.v(TAG, "bearingintervalTokenizer");

			}
		}
	}

	// interval
	private void accuracyintervalTokenizer(String inputString) {
		Pattern p = Pattern.compile("(gps.accuracy\\s*\\d+)",
				Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);
		Matcher m = p.matcher(inputString.replace(",", "").replace(" ", ""));
		while (m.find()) {
			Pattern p1 = Pattern.compile("\\d+", Pattern.CASE_INSENSITIVE
					| Pattern.MULTILINE);
			Matcher m1 = p1.matcher(m.group(0).replace(",", "")
					.replace(" ", ""));
			while (m1.find()) {

				ACCURACYINTERVAL = (Integer.parseInt(m1.group(0)) * 1000);
				Log.v(TAG, "accuracyintervalTokenizer");

			}
		}
	}

	// interval
	private void callincountintervalTokenizer(String inputString) {
		Pattern p = Pattern.compile("(call.incomingcount\\s*\\d+)",
				Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);
		Matcher m = p.matcher(inputString.replace(",", "").replace(" ", ""));
		while (m.find()) {
			tincomingcount = true;
			Pattern p1 = Pattern.compile("\\d+", Pattern.CASE_INSENSITIVE
					| Pattern.MULTILINE);
			Matcher m1 = p1.matcher(m.group(0).replace(",", "")
					.replace(" ", ""));
			while (m1.find()) {
				CALLINTIMESINTERVAL = (Integer.parseInt(m1.group(0)) * 1000);
				Log.v(TAG, "CALLINTIMESINTERVAL" + CALLINTIMESINTERVAL);
			}
		}
	}

	// interval
	private void calloutcomingcountintervalTokenizer(String inputString) {
		Pattern p = Pattern.compile("(call.outcomingcount\\s*\\d+)",
				Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);
		Matcher m = p.matcher(inputString.replace(",", "").replace(" ", ""));
		while (m.find()) {
			toutcomingcount = true;
			Pattern p1 = Pattern.compile("\\d+", Pattern.CASE_INSENSITIVE
					| Pattern.MULTILINE);
			Matcher m1 = p1.matcher(m.group(0).replace(",", "")
					.replace(" ", ""));
			while (m1.find()) {
				CALLOUTTIMESINTERVAL = (Integer.parseInt(m1.group(0)) * 1000);
				Log.v(TAG, "CALLOUTTIMESINTERVAL" + CALLOUTTIMESINTERVAL);
			}
		}
	}

	// interval
	private void callinalltimeintervalTokenizer(String inputString) {
		Pattern p = Pattern.compile("(call.callinalltime\\s*\\d+)",
				Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);
		Matcher m = p.matcher(inputString.replace(",", "").replace(" ", ""));
		while (m.find()) {
			tinalltime = true;
			Pattern p1 = Pattern.compile("\\d+", Pattern.CASE_INSENSITIVE
					| Pattern.MULTILINE);
			Matcher m1 = p1.matcher(m.group(0).replace(",", "")
					.replace(" ", ""));
			while (m1.find()) {
				CALLINALLTIMEINTERVAL = (Integer.parseInt(m1.group(0)) * 1000);
				Log.v(TAG, "CALLINALLTIMEINTERVAL" + CALLINALLTIMEINTERVAL);
			}
		}
	}

	// interval
	private void calloutalltimeintervalTokenizer(String inputString) {
		Pattern p = Pattern.compile("(call.calloutalltime\\s*\\d+)",
				Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);
		Matcher m = p.matcher(inputString.replace(",", "").replace(" ", ""));
		while (m.find()) {
			toutalltime = true;
			Pattern p1 = Pattern.compile("\\d+", Pattern.CASE_INSENSITIVE
					| Pattern.MULTILINE);
			Matcher m1 = p1.matcher(m.group(0).replace(",", "")
					.replace(" ", ""));
			while (m1.find()) {
				CALLOUTALLTIMESINTERVAL = (Integer.parseInt(m1.group(0)) * 1000);
				Log.v(TAG, "CALLOUTALLTIMESINTERVAL" + CALLOUTALLTIMESINTERVAL);
			}
		}
	}

	// interval
	private void rssiintervalTokenizer(String inputString) {
		Pattern p = Pattern.compile("(wifi.rssi\\s*\\d+)",
				Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);
		Matcher m = p.matcher(inputString.replace(",", "").replace(" ", ""));
		while (m.find()) {
			Pattern p1 = Pattern.compile("\\d+", Pattern.CASE_INSENSITIVE
					| Pattern.MULTILINE);
			Matcher m1 = p1.matcher(m.group(0).replace(",", "")
					.replace(" ", ""));
			while (m1.find()) {
				// mapParser.put("interval", String.valueOf(m1.group(0)));
				RSSIINTERVAL = (Integer.parseInt(m1.group(0)) * 1000);
				Log.v(TAG, "rssiintervalTokenizer");

			}
		}
	}

	// interval
	private void ssidintervalTokenizer(String inputString) {
		Pattern p = Pattern.compile("(wifi.ssid\\s*\\d+)",
				Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);
		Matcher m = p.matcher(inputString.replace(",", "").replace(" ", ""));
		while (m.find()) {
			Pattern p1 = Pattern.compile("\\d+", Pattern.CASE_INSENSITIVE
					| Pattern.MULTILINE);
			Matcher m1 = p1.matcher(m.group(0).replace(",", "")
					.replace(" ", ""));
			while (m1.find()) {
				// mapParser.put("interval", String.valueOf(m1.group(0)));
				SSIDINTERVAL = (Integer.parseInt(m1.group(0)) * 1000);
				Log.v(TAG, "ssidintervalTokenizer");

			}
		}
	}

	// interval
	private void powerintervalTokenizer(String inputString) {
		Pattern p = Pattern.compile("(pow.powerstatus\\s*\\d+)",
				Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);
		Matcher m = p.matcher(inputString.replace(",", "").replace(" ", ""));
		while (m.find()) {
			tpowerstatus = true;
			Pattern p1 = Pattern.compile("\\d+", Pattern.CASE_INSENSITIVE
					| Pattern.MULTILINE);
			Matcher m1 = p1.matcher(m.group(0).replace(",", "")
					.replace(" ", ""));
			while (m1.find()) {
				// mapParser.put("interval", String.valueOf(m1.group(0)));
				POWERINTERVAL = (Integer.parseInt(m1.group(0)) * 1000);
				Log.v(TAG, "powerintervalTokenizer" + POWERINTERVAL);

			}
		}
	}

	// 限制上傳資料筆數
	private void limitdb(String inputString) {
		Pattern p = Pattern.compile("(sizelimit\\s*\\d+)",
				Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);
		Matcher m = p.matcher(inputString.replace(",", "").replace(" ", ""));
		while (m.find()) {
			Pattern p1 = Pattern.compile("\\d+", Pattern.CASE_INSENSITIVE
					| Pattern.MULTILINE);
			Matcher m1 = p1.matcher(m.group(0).replace(",", "")
					.replace(" ", ""));
			while (m1.find()) {
				SizeLimit = Integer.parseInt(m1.group(0));
				Log.v(TAG, "dbLimit");
			}
		}
	}

	// 考量使用者長時間使用，資料未上傳，定期檢查上傳頻率
	private void limittime(String inputString) {
		Pattern p = Pattern.compile("(timelimit\\s*\\d+)",
				Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);
		Matcher m = p.matcher(inputString.replace(",", "").replace(" ", ""));
		while (m.find()) {
			Pattern p1 = Pattern.compile("\\d+", Pattern.CASE_INSENSITIVE
					| Pattern.MULTILINE);
			Matcher m1 = p1.matcher(m.group(0).replace(",", "")
					.replace(" ", ""));
			while (m1.find()) {
				Timelimit = (Integer.parseInt(m1.group(0)) * 1000);
				Log.v(TAG, "Timelimit" + Timelimit);
			}
		}
	}

	// MD5 HASHCODE
	public static String MD5(String str) {
		MessageDigest md5 = null;
		try {
			md5 = MessageDigest.getInstance("MD5");
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}

		char[] charArray = str.toCharArray();
		byte[] byteArray = new byte[charArray.length];

		for (int i = 0; i < charArray.length; i++) {
			byteArray[i] = (byte) charArray[i];
		}
		byte[] md5Bytes = md5.digest(byteArray);

		StringBuffer hexValue = new StringBuffer();
		for (int i = 0; i < md5Bytes.length; i++) {
			int val = ((int) md5Bytes[i]) & 0xff;
			if (val < 16) {
				hexValue.append("0");
			}
			hexValue.append(Integer.toHexString(val));
		}
		return hexValue.toString();
	}

	// 電話撥打參數初始化
	private void clapcalltimes() {
		int incomingcount = 0;
		int outcomingcount = 0;
		int inalltime = 0;
		int outalltime = 0;
		String callnumber = "";
		// MD5("0919336386");
		ContentResolver cr = getContentResolver();
		final Cursor cursor = cr.query(CallLog.Calls.CONTENT_URI,
				new String[] { CallLog.Calls.NUMBER, CallLog.Calls.CACHED_NAME,
						CallLog.Calls.TYPE, CallLog.Calls.DATE,
						CallLog.Calls.DURATION }, null, null,
				CallLog.Calls.DEFAULT_SORT_ORDER);
		for (int i = 0; i < cursor.getCount(); i++) {
			cursor.moveToPosition(i);
			if (cursor.getInt(2) == 1) {
				incomingcount += 1;
				callnumber = String.valueOf(cursor.getInt(0));
			}

			if (cursor.getInt(2) == 2) {
				outcomingcount += 1;
				callnumber = String.valueOf(cursor.getInt(0));
			}

			if (cursor.getInt(2) == 1) {
				inalltime = inalltime + cursor.getInt(4);
			}

			if (cursor.getInt(2) == 2) {
				outalltime = outalltime + cursor.getInt(4);
			}

		}

		Log.v(TAG, "PLPA_CLAP" + "incomingcount" + incomingcount
				+ ",outcomingcount" + outcomingcount + ",inalltime" + inalltime
				+ ",outalltime" + outalltime);
		SharedPreferences settings = getSharedPreferences(PREF_FILENAME, 0);
		SharedPreferences.Editor editor = settings.edit();
		editor.putInt("incomingcount", incomingcount);
		editor.putInt("outcomingcount", outcomingcount);
		editor.putInt("inalltime", inalltime);
		editor.putInt("outalltime", outalltime);
		editor.putInt("msgtimes", msgtimes);
		editor.commit();
	}

	// 判斷有無網路狀態
	private boolean haveInternet() {
		boolean result = false;
		ConnectivityManager connManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
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

	public void onAccuracyChanged(int sensor, int accuracy) {
		// TODO Auto-generated method stub

	}

	@SuppressWarnings("deprecation")
	public void onSensorChanged(int sensor, float[] values) {

	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		// TODO Auto-generated method stub

	};

}