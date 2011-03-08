package cz.havlena.ffmpeg.ui;

import java.io.File;
import java.util.Arrays;
import java.util.Comparator;


import com.media.ffmpeg.FFMpeg;


import android.app.TabActivity;
import android.os.Bundle;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;
import android.view.LayoutInflater;
import android.view.View;


import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;


import android.content.Intent;

import android.widget.TextView;

import kab.tv.connection.*;

public class MainKabTv extends ListActivity {

	private static final String TAG = "MainKabTv";
	
	int mCurrentStreamSelected;
	int bIsPlaying;
	
	private String 			mTitle = "KAB TV 66";
	private TextView 		mTextViewLocation;
	private int	[]			mStream;
	ConnectivityReceiver    mComNotifier;
	public Intent i;

	
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//TabHost tabHost = getTabHost();	
		//LayoutInflater.from(this).inflate(R.layout.mainkabtv, tabHost.getTabContentView(), true);
		setContentView(R.layout.ffmpeg_file_explorer);
		mTextViewLocation = (TextView) findViewById(R.id.textview_path);
		mTextViewLocation.append("Kab 66");
		startPlayer(/*sream url*/);
		CommunicationManager.mCurrentContext = this;

	}
	
	
	

	public static boolean checkCommunicationState(boolean status) {
		
		
		return true;
	}
	
	//Context.getSystemService(Context.CONNECTIVITY_SERVICE). 


	
/*	protected void onStreamItemClick(ListView l, View v, int position, long id) {
		int streamSelected = mStream[mCurrentStreamSelected];

		//check for comuunication availabilty then play or show error
				//FFMpegMessageBox.show(this, "Error", "[" + file.getName() + "] folder can't be read!");
		
			startPlayer(/*sream url*///);
		
//	}

	private void startPlayer(/*sream url*/) {
		String streamUrl = "Test";
		i = new Intent(this, FFMpegPlayerActivity.class);
    	i.putExtra(getResources().getString(R.string.input_stream), streamUrl);
    	startActivity(i);
    }
	
}




