package com.kab.channel66;

import io.vov.vitamio.VitamioInstaller.VitamioNotCompatibleException;
import io.vov.vitamio.VitamioInstaller.VitamioNotFoundException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import com.apphance.android.Log;
import com.google.analytics.tracking.android.EasyTracker;
import com.google.myjson.Gson;
import com.kab.channel66.Events.Page;
import com.kab.channel66.Events.Pages;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ListActivity;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.DialogInterface.OnCancelListener;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

public class StreamListActivity extends BaseListActivity {

	private ServiceConnection connection = new ServiceConnection() {
		@Override
		public void onServiceConnected(ComponentName className, IBinder iservice) {
           // mService = ILocService.Stub.asInterface(iservice);
           // mBound = true;
        }
		@Override
        public void onServiceDisconnected(ComponentName className) {
          //  mService = null;
           // mBound = false;
        }

	
    };
    Dialog playDialog;
    Intent svc;
	private ArrayList<Page> pages;
	public void onCreate(Bundle icicle) {
	    super.onCreate(icicle);
	    
        try {
        	
        	
			io.vov.vitamio.VitamioInstaller.checkVitamioInstallation(this);
		} catch (VitamioNotCompatibleException e) {
			// TODO Auto-generated catch block
			AlertDialog chooseToInstall = new AlertDialog.Builder(StreamListActivity.this).create();
			chooseToInstall.setTitle("Install missing plug-in");
			
			chooseToInstall.setButton("Ok", new DialogInterface.OnClickListener() {
			   public void onClick(DialogInterface dialog, int which) {
			      // here you can add functions
				   Intent goToMarket = new Intent(Intent.ACTION_VIEW)
				    .setData(Uri.parse("market://details?id="+io.vov.vitamio.VitamioInstaller.getCompatiblePackage()));
				startActivity(goToMarket); 
				   
				 
   	    		
			   }
			});
			chooseToInstall.setButton2("Cancel", new DialogInterface.OnClickListener() {
			   public void onClick(DialogInterface dialog, int which) {
			      // here you can add functions
				  finish();
			   }
			});
			chooseToInstall.setIcon(R.drawable.icon);
			chooseToInstall.show();
			
		} catch (VitamioNotFoundException e) {
			// TODO Auto-generated catch block
			AlertDialog chooseToInstall = new AlertDialog.Builder(StreamListActivity.this).create();
			chooseToInstall.setTitle("Missing plug-in");
			chooseToInstall.setMessage("Install plug-in from Google play?");
			chooseToInstall.setButton("Ok", new DialogInterface.OnClickListener() {
			   public void onClick(DialogInterface dialog, int which) {
			      // here you can add functions
				   Intent goToMarket = new Intent(Intent.ACTION_VIEW)
				    .setData(Uri.parse("market://details?id="+io.vov.vitamio.VitamioInstaller.getCompatiblePackage()));
				startActivity(goToMarket); 
				   
				 
   	    		
			   }
			});
			chooseToInstall.setButton2("Cancel", new DialogInterface.OnClickListener() {
			   public void onClick(DialogInterface dialog, int which) {
			      // here you can add functions
				  finish();
			   }
			});
			chooseToInstall.setIcon(R.drawable.icon);
			chooseToInstall.show();
			e.printStackTrace();
		}
	    
	    ArrayList<String> channels = new ArrayList<String>();
	    channels = getIntent().getStringArrayListExtra("channel");
	    ArrayList<String> description = new ArrayList<String>();
	    
	    if(channels!=null)
	    {
	    pages = new ArrayList<Page>();
	     
	    for(int i=0;i<channels.size();i++)
	    {
	    	pages.add(new Gson().fromJson(channels.get(i), Page.class));
	    	description.add(pages.get(i).description);
	    }
	    }
	    else
	    {
	    	
	    	
	    	description.add("ערוץ 66 - וידאו");
	    	description.add("ערוץ 66 - אודיו");
	    	
	    	
	    	description.add("Канал 66 на Русском - Видео");
	    	description.add("Канал 66 на Русском - Аудио");
	    }
	    ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
	        android.R.layout.simple_list_item_1, description);
	    setListAdapter(adapter);
	    
	  }
	private String ExtractMMSfromAsx(String url1) {
		// TODO Auto-generated method stub
		//Making HTTP request
		String ret = "";
		ASXExtractor asxextractor = new ASXExtractor();
		asxextractor.execute(url1);
		try {
			ret =  asxextractor.get();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ret;
	}
	private void playStreamInList(int index)
	{
		String item = (String) getListAdapter().getItem(index);
		Intent player = new Intent(StreamListActivity.this, VideoPlayerActivity.class);
	    
		
		if(pages!=null)
	    {
	    for(int i=0;i<pages.size();i++)
	    	if(pages.get(i).description.equalsIgnoreCase(item))
	    	{
	    		String url1;
	    		
	    		//set the quality
	    		Boolean high = PreferenceManager.getDefaultSharedPreferences(this).getBoolean("quality", false);
	    		if(!high)
	    		{
	    			 url1 = pages.get(i).urls.urlslist.get(1).url_value;
	    		}
	    		else
	    		{
	    			 url1 = pages.get(i).urls.urlslist.get(0).url_value;
	    		}
	    		
	    		//playvideo
	    		String mms_url = null;
	    		//replace key
	    		String key = PreferenceManager.getDefaultSharedPreferences(this).getString("key", null);
	    		if(key!=null)
	    		{
	    		int j = url1.indexOf("special-")+ "special-".length();
	    		String replace = url1.substring(j, j+8);
	    		url1 = url1.replace(replace, key);
	    		}
	    		
	    		
	    		
	    			
	    		
	    		
				 if(url1.contains("asx")){
					mms_url = ExtractMMSfromAsx(url1.trim());
   				 player.putExtra("path", mms_url);
				 }
				 else
				 {
					player.putExtra("path", url1);
				 }
				
				
				 EasyTracker.getTracker().trackEvent("Stream list", "on item clicked",url1,0L);
 				
    	    		startActivity(player);
				 
	    	}
	    }
	}
	
	 @Override
	  protected void onListItemClick(ListView l, View v, int position, long id) {
	    String item = (String) getListAdapter().getItem(position);
	    Toast.makeText(this, item + " selected", Toast.LENGTH_LONG).show();
	   // Intent player = new Intent(StreamListActivity.this, VideoViewDemo.class);
	    Intent player = new Intent(StreamListActivity.this, VideoPlayerActivity.class);
	    
	    if(pages!=null)
	    {
	    for(int i=0;i<pages.size();i++)
	    	if(pages.get(i).description.equalsIgnoreCase(item))
	    	{
	    		
	    		
	    		String url1 = pages.get(i).urls.urlslist.get(0).url_value;
	    		//playvideo
	    		String mms_url = null;
	    		//replace key
	    		String key = PreferenceManager.getDefaultSharedPreferences(this).getString("key", null);
	    		if(key!=null)
	    		{
	    		int j = url1.indexOf("special-")+ "special-".length();
	    		String replace = url1.substring(j, j+8);
	    		url1 = url1.replace(replace, key);
	    		}
	    		
	    		
				 if(url1.contains("asx")){
					mms_url = ExtractMMSfromAsx(url1.trim());
   				 player.putExtra("path", mms_url);
				 }
				 else
				 {
					player.putExtra("path", url1);
				 }
				
				
				 EasyTracker.getTracker().trackEvent("Stream list", "on item clicked",url1,0L);
 				
    	    		startActivity(player);
				 
	    	}
	    } else if(item.equals("ערוץ 66 - וידאו"))
	    	{
	    		//"mms://wms1.il.kab.tv/heb"
  				// String url = ExtractMMSfromAsx("http://streams.kab.tv/heb.asx");
	    	SharedPreferences shared = PreferenceManager.getDefaultSharedPreferences(StreamListActivity.this);
         	
         	if(shared.getBoolean("quality", false))
         	{
	    		player.putExtra("path", ExtractMMSfromAsx("http://streams.kab.tv/heb.asx"));//"rtsp://wms1.il.kab.tv/heb");// ExtractMMSfromAsx("http://streams.kab.tv/heb.asx"));
	    		startActivity(player);
         	}
         	else
         	{
         		player.putExtra("path", ExtractMMSfromAsx("http://streams.kab.tv/heb_medium.asx"));//"rtsp://wms1.il.kab.tv/heb");// ExtractMMSfromAsx("http://streams.kab.tv/heb.asx"));
	    		startActivity(player);
         	}
				 
	    	}
	    else if(item.equals("ערוץ 66 - אודיו"))
    	{
	    	 svc=new Intent(this, BackgroundPlayer.class);
	    	 svc.putExtra("audioUrl", "http://icecast.kab.tv/heb.mp3");
            startService(svc);
            playDialog = new Dialog(this);
            playDialog.setTitle("Playing audio");
            playDialog.setContentView(R.layout.mediacontroller);
            final ImageButton ask = (ImageButton) playDialog.findViewById(R.id.mediacontroller_ask);
            final ImageButton but = (ImageButton) playDialog.findViewById(R.id.mediacontroller_play_pause);
            but.setImageResource(R.drawable.mediacontroller_pause01);
            but.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					if(svc!=null)
					{
					but.setImageResource(R.drawable.mediacontroller_play01);
					stopService(svc);
					svc= null;
					}
					else
					{
						but.setImageResource(R.drawable.mediacontroller_pause01);
						svc=new Intent(StreamListActivity.this, BackgroundPlayer.class);
						svc.putExtra("audioUrl", "http://icecast.kab.tv/heb.mp3");
						startService(svc);
					}
				}
			});
            ask.setImageResource(R.drawable.system_help);
            ask.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Questions question = new Questions(StreamListActivity.this);
		        	question.show();
				}
			});
            
            playDialog.setOnCancelListener(new DialogInterface.OnCancelListener()
            {
                @Override
				public
                void onCancel(DialogInterface dialog)
                {
                     dialogBackpressed();
                }
            });
            playDialog.show();      
            
//            
//            bindService(svc, connection, Context.BIND_AUTO_CREATE);
//	    	Uri uri = Uri.parse("http://icecast.kab.tv/heb.mp3");
//	    	Intent player1 = new Intent(Intent.ACTION_VIEW,uri);
//	    	 player1.setDataAndType(uri, "audio/*");
//			startActivity(player1);	  
			//http://stackoverflow.com/questions/14043618/background-music-in-my-app-doesnt-start
			
    	}
	    else if(item.equals("Канал 66 на Русском - Видео"))
    	{
	    	SharedPreferences shared = PreferenceManager.getDefaultSharedPreferences(StreamListActivity.this);
         	
         	if(shared.getBoolean("quality", false))
         	{
	    	player.putExtra("path",  ExtractMMSfromAsx("http://streams.kab.tv/rus.asx"));
    		startActivity(player);
         	}
         	else
         	{
         		player.putExtra("path",  ExtractMMSfromAsx("http://streams.kab.tv/rus_medium.asx"));
        		startActivity(player);
         	}
    	}
	    else if(item.equals("Канал 66 на Русском - Аудио"))
    	{
//	    	Uri uri = Uri.parse("http://icecast.kab.tv/rus.mp3");
//	    	Intent player1 = new Intent(Intent.ACTION_VIEW,uri);
//	    	 player1.setDataAndType(uri, "audio/*");
//			startActivity(player1);	 
	    	 svc=new Intent(this, BackgroundPlayer.class);
	    	 svc.putExtra("audioUrl", "http://icecast.kab.tv/rus.mp3");
            startService(svc);
            playDialog = new Dialog(this);
            playDialog.setTitle("Playing audio");
            playDialog.setContentView(R.layout.mediacontroller);
            final ImageButton but = (ImageButton) playDialog.findViewById(R.id.mediacontroller_play_pause);
            but.setImageResource(R.drawable.mediacontroller_pause01);
            but.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					if(svc!=null)
					{
					but.setImageResource(R.drawable.mediacontroller_play01);
					stopService(svc);
					svc= null;
					}
					else
					{
						but.setImageResource(R.drawable.mediacontroller_pause01);
						svc=new Intent(StreamListActivity.this, BackgroundPlayer.class);
						svc.putExtra("audioUrl", "http://icecast.kab.tv/rus.mp3");
						startService(svc);
					}
				}
			});
            playDialog.setOnCancelListener(new DialogInterface.OnCancelListener()
            {
                @Override
				public
                void onCancel(DialogInterface dialog)
                {
                     dialogBackpressed();
                }
            });
            playDialog.show();      
            
    	}
	    		
	    
	  }
//	 private boolean checkAvailability(String url)
//	 {
//	 	//check availability of stream
//	 	
//	 	URLConnection cn = null;
//	 	try {
//	 		cn = new URL(url).openConnection();
//	 	} catch (MalformedURLException e) {
//	 		// TODO Auto-generated catch block
//	 		e.printStackTrace();
//	 		return false;
//	 	} catch (IOException e) {
//	 		// TODO Auto-generated catch block
//	 		e.printStackTrace();
//	 		return false;
//	 	}
//	 	try {
//	 		cn.connect();
//	 	} catch (IOException e) {
//	 		// TODO Auto-generated catch block
//	 		e.printStackTrace();
//	 	}
//	 	InputStream stream = null;
//	 	try {
//	 		stream = cn.getInputStream();
//	 	} catch (IOException e) {
//	 		// TODO Auto-generated catch block
//	 		e.printStackTrace();
//	 	}
//	 	if (stream == null) {
//
//	 	Log.e(getClass().getName(),url);
//	 	
//	 	Toast.makeText(this, "Currently no broadcast, please try again later", Toast.LENGTH_LONG).show();
//	 	
//	 	return false;
//	 	}
//	 	return true;
//	 }
	 
	 @Override
	 public void onStart() {
	   super.onStart();
	    // The rest of your onStart() code.
	   EasyTracker.getInstance().setContext(this.getApplicationContext());
	   EasyTracker.getInstance().activityStart(this);
	   playStreamInList(0);
	 }
	 @Override
	 public void onDestroy() {
	   super.onDestroy();
	    // The rest of your onStart() code.
	   if(svc!=null)
		   stopService(svc);
	   

	 }


	 @Override
	 public void onStop() {
	   super.onStop();
	    // The rest of your onStop() code.
	   EasyTracker.getInstance().activityStop(this); // Add this method.
	   SharedPreferences userInfoPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		 Boolean activated = userInfoPreferences.getBoolean("activated", false);
		 if(activated)
			 finish();
	 }
	 
	 @Override
	 public boolean onCreateOptionsMenu(Menu menu) {
		 SharedPreferences userInfoPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		 Boolean activated = userInfoPreferences.getBoolean("activated", false);
		 if(!activated)
		 {
	     MenuInflater inflater = getMenuInflater();
	     inflater.inflate(R.menu.streamoptionmenu, menu);
	     return true;
		 }
		 else
		 {
			 MenuInflater inflater = getMenuInflater();
		     inflater.inflate(R.menu.streamoptionmenu_activated, menu);
		     return true;
		 }
	 }
	 
	 public void dialogBackpressed()
	 {
		 playDialog.hide();
		 if(svc!=null)
			   stopService(svc);
	 }
	 @Override
	 public void onBackPressed()
	 {
		 super.onBackPressed();
		 
		 if(svc!=null)
			   stopService(svc);	
		 
		 	 
	 }
	 @SuppressLint("ShowToast")
	@Override
	 public boolean onOptionsItemSelected(MenuItem item) {
	     // Handle item selection
	     switch (item.getItemId()) {
	         case R.id.login:
	        	 AlertDialog.Builder alert = new AlertDialog.Builder(this);                 
	        	 alert.setTitle("Login");  
	        	 alert.setMessage("Enter Pin :");                

	        	  // Set an EditText view to get user input   
	        	  final EditText input = new EditText(this); 
	        	  alert.setView(input);

	        	     alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {  
	        	     public void onClick(DialogInterface dialog, int whichButton) {  
	        	         String value = input.getText().toString();
	        	         EasyTracker.getTracker().trackEvent("Stream list", "pin code value",value,0L);
		      				
	        	         if(value.equals("arvut"))
	        	         {
	        	        	 Intent intent = new Intent(getApplicationContext(), WebLogin.class);
	        		            startActivity(intent);
	        	         }
	        	         Log.d( "Login", "Pin Value : " + value);
	        	        
	        	         return;                  
	        	        }  
	        	      });  

	        	     alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

	        	         public void onClick(DialogInterface dialog, int which) {
	        	             // TODO Auto-generated method stub
	        	             return;   
	        	         }
	        	     });
	        	             alert.show();
	           
	            
	             return true;
	         case R.id.sviva:
	        	 
	        	 onBackPressed();
	        	 return true;
	         case R.id.quality:
	         	SharedPreferences shared = PreferenceManager.getDefaultSharedPreferences(StreamListActivity.this);
	         	SharedPreferences.Editor edit = shared.edit();
	         	if(shared.getBoolean("quality", false))
	         	{
	         		Toast.makeText(StreamListActivity.this, "Changed quality to medium", Toast.LENGTH_LONG).show();
	         		edit.putBoolean("quality", false);
	     			edit.commit();
	         	}
	         	else
	         	{
	         		Toast.makeText(StreamListActivity.this, "Changed quality to high", Toast.LENGTH_LONG).show();
	         		edit.putBoolean("quality", true);
	     			edit.commit();
	         	}
	         	
	             return true;
	        	 
	         default:
	             return super.onOptionsItemSelected(item);
	     }
	 }
	 
	 public boolean onPrepareOptionsMenu (Menu menu)
	 {
	 	MenuItem Item = menu.findItem(R.id.quality);
	 	SharedPreferences shared = PreferenceManager.getDefaultSharedPreferences(StreamListActivity.this);
	 	
	 	if(shared.getBoolean("quality", false))
	 		Item.setTitle(getResources().getString(R.string.quality)+": High");
	 	else
	 		Item.setTitle(getResources().getString(R.string.quality)+": Medium");	
	 	return true;
	 }
	 
	 public boolean isOnline(Context context) { 
		    ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);    
		    NetworkInfo netInfo = cm.getActiveNetworkInfo();    
		    return netInfo != null && netInfo.isConnected();
		}
}
