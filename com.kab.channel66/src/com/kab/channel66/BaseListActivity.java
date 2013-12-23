package com.kab.channel66;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class BaseListActivity extends ListActivity {

	@Override
	public void onResume()
	{
		super.onResume();
		
		if(!isOnline(this.getApplicationContext()))
		{
			new AlertDialog.Builder(this)
		    .setTitle("Data not available")
		    .setMessage("Appliaction needs data connection")
		    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
		        public void onClick(DialogInterface dialog, int which) { 
		            // continue with delete
		        	finish();
		        	return;
		        }
		     }).show();
		}
	}
	 public boolean isOnline(Context context) { 
		    ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);    
		    NetworkInfo netInfo = cm.getActiveNetworkInfo();    
		    return netInfo != null && netInfo.isConnected();
		}
}
