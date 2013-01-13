package com.kab.channel66;

import java.io.IOException;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.view.ViewDebug.FlagToString;

public class BackgroundPlayer extends Service {

	MediaPlayer mediaPlayer;
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) 
	{
		 
		//Uri uri = Uri.parse("http://icecast.kab.tv/heb.mp3");
    	//Intent player1 = new Intent(Intent.ACTION_VIEW,uri);
    	// player1.setDataAndType(uri, "audio/*");
    	
    	// player1.setFlags(intent.FLAG_ACTIVITY_NEW_TASK);
		//startActivity(player1);	
		
		
		//String url = "http://icecast.kab.tv/heb.mp3"; // your URL here
		String url = intent.getStringExtra("audioUrl");
		 mediaPlayer = new MediaPlayer();
		mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
		try {
			mediaPlayer.setDataSource(url);
			mediaPlayer.prepare(); // might take long! (for buffering, etc)
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
		
		mediaPlayer.start();
		return startId;
		
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
	       
	}
	@Override
	public void onStart(Intent intent, int startId) {

	    super.onStart(intent, startId);
	    
	}
	
	
	public void onDestroy() {

	    super.onDestroy();
	    mediaPlayer.stop();
	    mediaPlayer.release();
	}
}
