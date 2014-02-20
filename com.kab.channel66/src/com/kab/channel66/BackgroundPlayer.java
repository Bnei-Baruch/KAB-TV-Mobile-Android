package com.kab.channel66;

//import io.vov.vitamio.VitamioInstaller.VitamioNotCompatibleException;
//import io.vov.vitamio.VitamioInstaller.VitamioNotFoundException;

import io.vov.vitamio.LibsChecker;

import java.io.IOException;

import com.kab.channel66.utils.CallStateInterface;
import com.kab.channel66.utils.CallStateListener;

import android.app.Application;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.location.GpsStatus.NmeaListener;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnBufferingUpdateListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.ViewDebug.FlagToString;
import android.widget.Toast;

public class BackgroundPlayer extends BaseService implements OnPreparedListener,OnBufferingUpdateListener,CallStateInterface{

	TelephonyManager telephony;
	private static final int NOTIFICATION_ID = 0;
	private CallStateListener calllistener;
	private  io.vov.vitamio.MediaPlayer mediaPlayer;
	
	NotificationManager mNM;
	Thread thread;
	StreamProxy sp; //adding streamproxy to solve audio failure of some devices before ics - http://stackoverflow.com/questions/9840523/mediaplayer-streams-mp3-in-emulator-but-not-on-device
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) 
	{
		
		super.onStartCommand(intent, flags, startId);
		telephony = (TelephonyManager)this.getSystemService(Context.TELEPHONY_SERVICE); //TelephonyManager object  
		calllistener = new CallStateListener(this); 
		mNM = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);

       if(intent==null)
       {
    	   //no intent recevied defaulting to channel 66
    	   
    	   mNM.cancel(NOTIFICATION_ID);
    	   return START_NOT_STICKY;
       }
        String url = intent.getStringExtra("audioUrl");
		if(url==null)
		{
			mNM.cancel(NOTIFICATION_ID);
			return START_NOT_STICKY;
		}
		


		String songName;
		Class <?> cls;
		if(intent.getBooleanExtra("sviva", false))
		{
			songName = "Sviva Tova";
			cls = WebLogin.class;
			
		}
		else
		{
			songName = "Channel 66";
			cls = StreamListActivity.class;
		}
		// assign the song name to songName
		Intent in = new Intent(getApplicationContext(), cls);
		in.setAction(Intent.ACTION_MAIN);
		in.addCategory(Intent.CATEGORY_LAUNCHER);
		in.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
		
		PendingIntent pi = PendingIntent.getActivity(getApplicationContext(), 0,in
		                ,
		                PendingIntent.FLAG_UPDATE_CURRENT);
		Notification notification = new Notification();
		notification.tickerText = "Playing audio";
		notification.icon = R.drawable.icon;
		notification.flags |= Notification.FLAG_ONGOING_EVENT;
		notification.setLatestEventInfo(getApplicationContext(), "Channel 66",
		                "Playing: " + songName, pi);
		
		startForeground(NOTIFICATION_ID, notification);
		
		 
		
		

		
		//String url = "http://icecast.kab.tv/heb.mp3"; // your URL here
		
//		 mediaPlayer = new MediaPlayer();
		//try {
			mediaPlayer = new io.vov.vitamio.MediaPlayer(this);
//		} catch (VitamioNotCompatibleException e1) {
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		} catch (VitamioNotFoundException e1) {
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		}
		//mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
//		mediaPlayer.setOnBufferingUpdateListener(new OnBufferingUpdateListener() {
//			
//			@Override
//			public void onBufferingUpdate(MediaPlayer mp, int percent) {
//				// TODO Auto-generated method stub
//				if(percent>50)
//					mediaPlayer.start();
//			}
//		});
		
		try {
			mediaPlayer.setDataSource(url);
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		
		
		try {
			mediaPlayer.prepare();
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		mediaPlayer.start();
		
		mNM.notify(NOTIFICATION_ID, notification);

		return START_REDELIVER_INTENT;
		
		
		
		
	}
	
	public void onPrepared(MediaPlayer player) {
        // We now have buffered enough to be able to play
		player.start();
		telephony.listen(calllistener, PhoneStateListener.LISTEN_CALL_STATE); //Register our listener with TelephonyManager
    }
	/*
	private void addNotification()
	{
		NotificationCompat.Builder mBuilder =
		        new NotificationCompat.Builder(this)
		        .setSmallIcon(R.drawable.icon)
		        .setContentTitle("My notification")
		        .setContentText("Hello World!");
		// Creates an explicit intent for an Activity in your app
		//Intent resultIntent = new Intent(this, ResultActivity.class);

		// The stack builder object will contain an artificial back stack for the
		// started Activity.
		// This ensures that navigating backward from the Activity leads out of
		// your application to the Home screen.
		TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
		// Adds the back stack for the Intent (but not the Intent itself)
		//stackBuilder.addParentStack(ResultActivity.class);
		// Adds the Intent that starts the Activity to the top of the stack
		//stackBuilder.addNextIntent(resultIntent);
		PendingIntent resultPendingIntent =
		        stackBuilder.getPendingIntent(
		            0,
		            PendingIntent.FLAG_UPDATE_CURRENT
		        );
		mBuilder.setContentIntent(resultPendingIntent);
		NotificationManager mNotificationManager =
		    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		int mId = 1;
		// mId allows you to update the notification later on.
		mNotificationManager.notify(mId , mBuilder.build());
	}
	*/
	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public void onCreate() {

	    super.onCreate();
	    registerReceiver(mConnReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)) ;  
	}
	
	 private BroadcastReceiver mConnReceiver = new BroadcastReceiver() {
	        public void onReceive(Context context, Intent intent) {
	            boolean noConnectivity = intent.getBooleanExtra(ConnectivityManager.EXTRA_NO_CONNECTIVITY, false);
	            String reason = intent.getStringExtra(ConnectivityManager.EXTRA_REASON);
	            boolean isFailover = intent.getBooleanExtra(ConnectivityManager.EXTRA_IS_FAILOVER, false);

	            NetworkInfo currentNetworkInfo = (NetworkInfo) intent.getParcelableExtra(ConnectivityManager.EXTRA_NETWORK_INFO);
	            NetworkInfo otherNetworkInfo = (NetworkInfo) intent.getParcelableExtra(ConnectivityManager.EXTRA_OTHER_NETWORK_INFO);

	            if(currentNetworkInfo.isConnected()){
	                //Toast.makeText(getApplicationContext(), "Connected", Toast.LENGTH_LONG).show();
	            }else{
	                Toast.makeText(getApplicationContext(), "Lost data connection", Toast.LENGTH_LONG).show();
	                BackgroundPlayer.this.mediaPlayer.stop();
	                BackgroundPlayer.this.stopSelf();
	            }
	        }
	    };
	@Override
	public void onStart(Intent intent, int startId) {

	    super.onStart(intent, startId);
	    try {
			
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    
	    
	}
	
	
	@Override
	public void onDestroy() {

	    super.onDestroy();
	    unregisterReceiver(mConnReceiver);
	    if (mediaPlayer != null)
	    {
	    	mediaPlayer.stop();
	    	mediaPlayer.reset();
	    	mediaPlayer.release();
	    	mediaPlayer = null;
	    	if(sp!=null)
	    		sp.stop();
	    }
	    
	    stopForeground(true);
	    mNM.cancel(NOTIFICATION_ID);
	    

	}

	@Override
	public void onBufferingUpdate(MediaPlayer mp, int percent) {
		// TODO Auto-generated method stub
		
		Log.d("audio","test");
	}

	@Override
	public void Pause() {
		// TODO Auto-generated method stub
		mediaPlayer.pause();
	}

	@Override
	public void Start() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void Resume() {
		// TODO Auto-generated method stub
		mediaPlayer.start();
	}

	@Override
	public void Stop() {
		// TODO Auto-generated method stub
		
	}
	
	
}
