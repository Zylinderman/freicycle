package com.smsTest;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class SendSMSActivity extends Activity {
 
	Button buttonSend;
	Button buttonChange;
	EditText numberEdit;
	EditText bikeKeyEdit;
	SharedPreferences sharedPref;
	UpdatePositionTask updatePositionTask;
 
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		this.sharedPref = getPreferences(Context.MODE_PRIVATE);
		this.numberEdit = (EditText) findViewById(R.id.actNumber);
		this.bikeKeyEdit = (EditText) findViewById(R.id.actBikeKey);
		this.buttonSend = (Button)findViewById(R.id.sendButton);
		this.buttonSend.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				SendSMSActivity.this.updatePositionTask.run();
			}
		});
		
		this.buttonChange = (Button) findViewById(R.id.changeButton);
		this.buttonChange.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				SendSMSActivity.this.updateEdits();
			}
		});
		//Timer updatePositionTimer = new Timer();
		
		String number = sharedPref.getString(getString(R.string.actual_number),numberEdit.getText().toString());
		String bikeKey = sharedPref.getString(getString(R.string.actual_bike_key), bikeKeyEdit.getText().toString());
		
		 this.updatePositionTask = new UpdatePositionTask(this, number, bikeKey);
		//updatePositionTimer.scheduleAtFixedRate(updatePositionTask, 0, 60*1000);
		
	}
	
	
	public void updateEdits() {
		SharedPreferences.Editor editor = sharedPref.edit();
		
		String number = numberEdit.getText().toString();
		String bikeKey = bikeKeyEdit.getText().toString();
		
		editor.putString(getString(R.string.actual_number), number);
		editor.putString(getString(R.string.actual_bike_key), bikeKey);
		
		editor.commit();
		
		this.updatePositionTask.setNumber(number);
		this.updatePositionTask.setSecret(bikeKey);
	}
}