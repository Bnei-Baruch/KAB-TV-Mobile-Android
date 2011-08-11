package kab.tv.ui;


import com.google.android.apps.analytics.GoogleAnalyticsTracker;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Locale;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.xml.sax.SAXException;



import com.media.ffmpeg.FFMpeg;




import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.TabActivity;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TabHost;
import android.widget.Toast;
import android.widget.TabHost.TabSpec;
import android.widget.TabWidget;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;


import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;


import android.content.Intent;
import android.content.res.Resources;

import android.widget.TextView;

import kab.tv.connection.*;

public class MainKabTv extends TabActivity implements Runnable /*implements TabHost.TabContentFactory*/{

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
	 private static final int DIALOG1_KEY = 0;
	 private static final int DIALOG2_KEY = 1;
	 ProgressDialog mDialog1;
	 Thread thread;
	 private int demoDelay = 0;
	 GoogleAnalyticsTracker tracker;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		TabHost tabHost = getTabHost();	
		//LayoutInflater.from(this).inflate(R.layout.mainkabtv, tabHost.getTabContentView(), true);
	//	setContentView(R.layout.mainkabtv);
		//mTextViewLocation = (TextView) findViewById(R.id.textview_path);
		//mTextViewLocation.append("Kab 66");
		//startPlayer(/*sream url*/);
		 tracker = GoogleAnalyticsTracker.getInstance();
		 
		 tracker.start("UA-3288058-60", this);
		 
		 tracker.trackPageView("/Application started");
		 
		 tracker.dispatch();
		 
		 
		//LayoutInflater.from(this).inflate(R.layout.tabmain, tabHost.getTabContentView(), true);
		   Resources res = getResources(); // Resource object to get Drawables
	        //TabHost tabHost = getTabHost();  // The activity TabHost
	        TabHost.TabSpec spec;  // Resusable TabSpec for each tab
	        Intent intent;  // Reusable Intent for each tab
	        tabHost.removeAllViews();
	        TabWidget tabs = new TabWidget(this);
	        tabs.setId(android.R.id.tabs);
	        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
	        params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
	        tabs.setLayoutParams(params);
	        FrameLayout content = new FrameLayout(this);
	        content.setId(android.R.id.tabcontent);
	        content.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
	        RelativeLayout relative = new RelativeLayout(this);
	        relative.setLayoutParams(new RelativeLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
	        relative.addView(content);
	        relative.addView(tabs);
	        tabHost.addView(relative);
	        tabHost.setup();

	       
			
			
		
		Intent i = new Intent(this, FFMpegPlayerActivity.class);
		i.putExtra(getResources().getString(R.string.input_stream), "mms://vod.kab.tv/heb_medium");
		 tabHost.addTab(tabHost.newTabSpec("Channels")
	                .setIndicator("Channels")
	                .setContent(new Intent(this, StreamsGrid.class)));
		 	//tabHost.addTab(tabHost.newTabSpec("Settings")
	       //         .setIndicator("Settings")
	       //          .setContent(i));
	       
	               
	       
	        
	        
		CommunicationManager.mCurrentContext = this;
		mComNotifier = new ConnectivityReceiver();
		registerReceiver(mComNotifier,
                new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
		//i = new Intent(this, ConnectivityManagerTestActivity.class);
    	
    	//startActivity(i);
		mSelf  = this;
		//startPlayer(/*sream url*/);
		demoDelay = 1000;
		 Channels channels;
			try {
				channels = Channels.instance();
				if(!channels.isLoaded())
				{
					 
					 // set up mProgressdialog
				    if (mDialog1 == null) {
				        ProgressDialog dialog = new ProgressDialog(this);
				        //dialog.setTitle("Loading");
				        dialog.setMessage("Loading");
				        dialog.setIndeterminate(true);
				        dialog.setCancelable(false);
				        mDialog1 = dialog;
				    }
				 
				        Handler handler = new Handler();
				        showProgressDialog(); //show progress dialog here
				 
				        handler.postDelayed(new Runnable() {
				            public void run() {
				                dismissProgressDialog(); //clearing progress dialog
				                //ensureUi(); //map the stuff from class to xml
				                //populateUi(); //grab the data from whatever
				            }
				        }, demoDelay); //demodelay will set the delay time

	               // thread = new Thread(this);
	               // thread.start();		        			
	                
				} 
			} catch (ParserConfigurationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SAXException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
	}
	
	private ProgressDialog showProgressDialog() {
		mDialog1.show();
	    return mDialog1;
	}
	 
	private void dismissProgressDialog() {
	    try {
	    	mDialog1.dismiss();
	    } catch (IllegalArgumentException e) {
	        // android will do its jon
	 
	    }
	}
	  public void run() {
		  try {
			  Looper.prepare();
			  mDialog1 = ProgressDialog.show(this, "Working..", "Calculating Pi", true,
                      false);
			 
			Channels.instance().LoadData();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
          handler.sendEmptyMessage(0);
  }

  private Handler handler = new Handler() {
          @Override
          public void handleMessage(Message msg) {
        	  mDialog1.dismiss();
                  

          }
  };
  
 
	@Override
	public void onResume()
	{
		
			
		try {
			
			Channels.instance().LoadData();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		super.onResume();
	}
	 @Override
	    protected Dialog onCreateDialog(int id) {
	        switch (id) {
	            
	            case DIALOG1_KEY: {
	            	mDialog1 = new ProgressDialog(this);
	            	mDialog1.setMessage("Please wait while loading...");
	            	mDialog1.setIndeterminate(true);
	            	mDialog1.setCancelable(true);
	                return mDialog1;
	            }
	        }
	        return null;
	    }  
	
	@Override
	public void onDestroy()
	{
		unregisterReceiver(mComNotifier);
		super.onDestroy();
		// Stop the tracker when it is no longer needed.
		tracker.stop();
		 
	}
	
	 /** {@inheritDoc} */
    public View createTabContent(String tag) {
        final TextView tv = new TextView(this);
        tv.setText("Content for tab with tag " + tag);
        return tv;
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
		//String streamUrl = "http://switch3.castup.net/cunet/gm.asp?ClipMediaID=160788";
		
		//String streamUrl = "mms://vod.kab.tv/heb_medium"; 
		String streamUrl = mSelf.getPage(); 
		mSelf.i = new Intent(mSelf, FFMpegPlayerActivity.class);
		mSelf.i.putExtra(mSelf.getResources().getString(R.string.input_stream), streamUrl);
		mSelf.startActivity(mSelf.i);
		mSelf.mIsPlaying =true;
    }
	
	private String getPage() {
    	String str = "***";

        try
    	{
    		HttpClient hc = new DefaultHttpClient();
    		HttpPost post = new HttpPost("http://switch3.castup.net/cunet/gm.asp?ClipMediaID=160788");

    		HttpResponse rp = hc.execute(post);

    		if(rp.getStatusLine().getStatusCode() == HttpStatus.SC_OK)
    		{
    			str = EntityUtils.toString(rp.getEntity());
    			int mmsindex = str.indexOf("mms");
    			int mmsindexlast= 0;
    			if(mmsindex>0)
    				mmsindexlast = str.indexOf('"', mmsindex);
    			str = str.substring(mmsindex, mmsindexlast);
    		}
    	}catch(IOException e){
    		e.printStackTrace();
    	}  
    	
    	return str;
    }
	

	 void showToast(CharSequence msg) {
		 TabHost tabHost = getTabHost();
	        Toast.makeText(tabHost.getContext(), msg, Toast.LENGTH_SHORT).show();
	    }
	
}




