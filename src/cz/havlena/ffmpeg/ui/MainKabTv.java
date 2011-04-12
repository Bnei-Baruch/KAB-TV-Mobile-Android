package cz.havlena.ffmpeg.ui;

import java.io.File;
import java.util.Arrays;
import java.util.Comparator;


import com.media.ffmpeg.FFMpeg;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.TabActivity;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;
import android.view.LayoutInflater;
import android.view.View;


import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;


import android.content.Intent;

import android.widget.TextView;

import kab.tv.connection.*;

public class MainKabTv extends Activity {

	private static final String TAG = "MainKabTv";
	
	int mCurrentStreamSelected;
	int bIsPlaying;
	
	private boolean				mIsPlaying =false;
	private String 			mTitle = "KAB TV 66";
	private TextView 		mTextViewLocation;
	private int	[]			mStream;
	ConnectivityReceiver    mComNotifier;
	public Intent i;
	public static  MainKabTv mSelf;

	
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//TabHost tabHost = getTabHost();	
		//LayoutInflater.from(this).inflate(R.layout.mainkabtv, tabHost.getTabContentView(), true);
		setContentView(R.layout.mainkabtv);
		mTextViewLocation = (TextView) findViewById(R.id.textview_path);
		mTextViewLocation.append("Kab 66");
		//startPlayer(/*sream url*/);
		CommunicationManager.mCurrentContext = this;
		mComNotifier = new ConnectivityReceiver();
		//registerReceiver(mComNotifier,
       //         new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
		//i = new Intent(this, ConnectivityManagerTestActivity.class);
    	
    	//startActivity(i);
		mSelf  = this;
		startPlayer(/*sream url*/);
    	
		
	}
	
	
	

	public static void checkCommunicationState(boolean status) {
		
		if(status && !mSelf.mIsPlaying)
			mSelf.startPlayer();
		else if(!status){

			AlertDialog alertDialog = new AlertDialog.Builder(mSelf).create();
			alertDialog.setTitle("Communication disconnected");
			alertDialog.setMessage("Do you want to wait or quit?");
			 alertDialog.setButton("Wait", new DialogInterface.OnClickListener() {
				     public void onClick(DialogInterface dialog, int which) {
				    	 
				     return;
				
				   } }); 
			 alertDialog.setButton2("Quit", new DialogInterface.OnClickListener() {
			     public void onClick(DialogInterface dialog, int which) {
			
			    	 mSelf.finish();
			
			   } }); 
			
			 alertDialog.show();
		}
			
		
	}
	
	//Context.getSystemService(Context.CONNECTIVITY_SERVICE). 


	
/*	protected void onStreamItemClick(ListView l, View v, int position, long id) {
		int streamSelected = mStream[mCurrentStreamSelected];

		//check for comuunication availabilty then play or show error
				//FFMpegMessageBox.show(this, "Error", "[" + file.getName() + "] folder can't be read!");
		
			startPlayer(/*sream url*///);
		
//	}

	private static void startPlayer(/*sream url*/) {
		String streamUrl = "Test";
		mSelf.i = new Intent(mSelf, FFMpegPlayerActivity.class);
		mSelf.i.putExtra(mSelf.getResources().getString(R.string.input_stream), streamUrl);
		mSelf.startActivity(mSelf.i);
		mSelf.mIsPlaying =true;
    }
	
}




