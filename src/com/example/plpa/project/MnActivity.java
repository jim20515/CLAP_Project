package com.example.plpa.project;


import android.app.Activity;
import android.content.Intent;
//import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

public class MnActivity extends Activity {

	private static final String TAG = "Mytest";
	  public TextView myTextView1;
	  public TextView myTextView2;
	  public CheckBox myCheckBox;
	  public Button myButton;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_mn);
		Log.v(TAG,"Mn");
		
		myTextView1 = (TextView) findViewById(R.id.myTextView1);
	    myTextView2 = (TextView) findViewById(R.id.myTextView2);
	    myCheckBox = (CheckBox) findViewById(R.id.myCheckBox);
	    myButton = (Button) findViewById(R.id.myButton);
		
	    myCheckBox.setChecked(false);
	    myButton.setEnabled(false); 
	    myCheckBox.setOnClickListener(new CheckBox.OnClickListener()
	    {
	        @Override
	        public void onClick(View v)
	        {
	          // TODO Auto-generated method stub
	          if(myCheckBox.isChecked())
	          {
	            myButton.setEnabled(true); 
	            myTextView2.setText("");
	          }
	          else
	          {
	            myButton.setEnabled(false);
	            myTextView1.setText(R.string.text1);
	            /*�bTextView2����ܥX"�ФĿ�ڦP�N"*/
	            myTextView2.setText(R.string.no);          
	          }
	        }
	      });
	        
	      myButton.setOnClickListener(new Button.OnClickListener()
	      {
	        @Override
	        public void onClick(View v)
	        {
	          // TODO Auto-generated method stub
	          if(myCheckBox.isChecked())        
	          {
	            myTextView1.setText(R.string.ok);
	            Intent intent = new Intent();
	      	  intent.setClass(MnActivity.this, PLPA_CLAP.class);
	      	  
	      	  /* 呼叫一個新的Activity */
	      	  startActivity(intent);
	      	  /* 關閉原本的Activity */
	      	  MnActivity.this.finish();

	           // jumpToLayout2();
	          }else
	          {      
	          }
	        }
	      });
	      
	  }
	}
	        
	      