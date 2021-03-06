package com.smsTest;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.location.LocationListener;
import android.os.BatteryManager;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.smsutils.MD5;
import com.smsutils.SMSSender;
import com.smsutils.SMSSenderOnlyException;
import com.smsutils.Sender;

public class UpdatePositionTask implements LocationListener {

	Context context;
	Sender sender;
	double latitude=52.380656;
	double longitude=9.745458;
	String message="";
	IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);

	LocationManagerHandler locationHandler;
	TelephonyManager telManager;
	String secret;
	
	public UpdatePositionTask(Context context, String number, String secret, int delay) {
		this.context=context;
		this.secret = secret;
		this.sender = new SMSSender(number);
		this.telManager = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
		this.locationHandler = LocationManagerHandler.getInstance(context, this, delay);
		
		Location location = locationHandler.getLastKnownLocation();
		
		if(location!=null)
			this.onLocationChanged(location);
		else {
			this.message = "no_lock";
			this.send();
		}
		
	}
	
	public void setSecret(String secret) {
		this.secret = secret;
	}
	
	public void setNumber(String number) throws SMSSenderOnlyException {
		if(this.sender instanceof SMSSender)
			((SMSSender)this.sender).setNumber(number);
		else
			throw new SMSSenderOnlyException();
	}

	public void send() {
		
		Intent batteryStatus = context.registerReceiver(null, ifilter);
		int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
		int scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
		int health = batteryStatus.getIntExtra(BatteryManager.EXTRA_HEALTH, -1);
		float batteryPct = (level / (float)scale);
		
		String msg = this.latitude+":"+this.longitude+":"+ System.currentTimeMillis() +":" + batteryPct +":"+health+":"+this.message;
		msg += ":" + MD5.hash(msg+this.secret);
		//Log.d("l�nge", ""+msg.length());
		this.sender.send(msg);
		this.message="";
	}
	
	public void onLocationChanged(Location location) {
		
		this.latitude = location.getLatitude();
		this.longitude = location.getLongitude();
		//Log.d("location","changed!");
		
		if(outOfRange(location)) {
			this.message="range";
			Log.e("range","Moved out of range!");
			locationHandler.unregisterListener();
		} else {
			locationHandler.scheduleListener();
		}
		this.send();
		
	}

	public boolean outOfRange(Location location) {
		//Log.d("network",telManager.getNetworkCountryIso());
	    return !telManager.getNetworkCountryIso().equals("de");
	    /*
		return ((location.getLatitude() > 51.0768337) &&
				(location.getLatitude() < 53.7286946) &&
				(location.getLongitude() > 7.6229597) &&
				(location.getLongitude() < 13.7725857));
		*/
	}
	
	@Override
	public void onProviderDisabled(String provider) {
//		Log.d("provider disabled", provider);
	}

	@Override
	public void onProviderEnabled(String provider) {
//		Log.d("provider enabled", provider);
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
//		Log.d("status changed", provider);
//		Log.d("status", ""+status);
	}
	
}
