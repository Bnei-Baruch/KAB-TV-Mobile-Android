package com.kab.channel66;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import com.apphance.android.Apphance;

import com.google.analytics.tracking.android.EasyTracker;
import com.google.myjson.Gson;
import com.kab.channel66.R;

import io.vov.vitamio.VitamioInstaller.VitamioNotCompatibleException;
import io.vov.vitamio.VitamioInstaller.VitamioNotFoundException;
import io.vov.vitamio.widget.VideoView;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.PowerManager;

import android.preference.PreferenceManager;
import android.provider.ContactsContract.CommonDataKinds.Event;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Application;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
//import android.util.Log;
import com.apphance.android.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.webkit.HttpAuthHandler;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.Toast;




public class WebLogin extends Activity implements WebCallbackInterface {

	private WebView mLoginwebView;
	private WebViewClient mClient;
	private Events events;
	
	private ProgressDialog myProgressDialog = null;
	private StreamAvailabilityChecker myChecker = null;
	PowerManager.WakeLock wl = null;
	JSONObject serverJSON = null;
	String content = null;
	public class JavaScriptInterface {
	    Context mContext;

	    /** Instantiate the interface and set the context */
	    JavaScriptInterface(Context c) {
	        mContext = c;
	    }

	    /** Show a toast from the web page */
	    public void showToast(String toast) {
	        Toast.makeText(mContext, toast, Toast.LENGTH_LONG).show();
	    }
	   
	}
	public static final String APP_KEY = "54a42bcfd5ce353a43b79e786acd37bf5bf4a62a";
//	private CheckUpdateTask checkUpdateTask;
	
	
	
    @SuppressLint({ "SetJavaScriptEnabled", "NewApi", "NewApi", "NewApi" })
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_login);
//        Apphance.startNewSession(this, APP_KEY, Apphance.Mode.Silent);
//	    Apphance.setReportOnShakeEnabled(true);
//        System.setProperty("http.keepAlive", "false");
//

        
        
        mLoginwebView = (WebView) findViewById(R.id.webloginview);
       
        mClient = new WebViewClient()
        	{
        	    @Override
        	    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        	    	 Boolean success;
        	    	if(url.contains("http://icecast.kab.tv"))
    	    		{
    	    			
    	    		
    	    			StreamAvailabilityChecker checker = new StreamAvailabilityChecker();
    	    			checker.execute(url);
    	    			//checker.execute("http://icecast.kab.tv/live1-heb-574bcfd5.mp31");
    	    			try {
							if(!checker.get())
							{
								setValid(false);
								mLoginwebView.loadUrl("javascript:Android.showToast('Currently no broadcast')");
								
								return true;
							}
							else
							{
								
								setValid(true);
							}
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (ExecutionException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
    	    			
    	    		}
        	    	if(url.contains("login"))
    	    		{
//    	    			 SharedPreferences userInfoPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
//    	    			 SharedPreferences.Editor editor = userInfoPreferences.edit();
//    	    			 editor.putBoolean("activated", false);
//    	    			  success = editor.commit();
    	    			  setActivated(false);
    	    			  
    		    			
    	    		}
        	    	else
        	    	{
//        	    		SharedPreferences userInfoPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
//        	    		 SharedPreferences.Editor editor = userInfoPreferences.edit();
//    	    			 editor.putBoolean("activated", true);
//    	    			  success = editor.commit();
    	    			  setActivated(true);
        	    	}
        	         view.loadUrl(url);
        	        return true;
        	    }
    	    
        	    @Override
        	    public void onReceivedHttpAuthRequest (WebView view, HttpAuthHandler handler, String host, String realm)
        	    {
        	    	Log.v("WebViewClient","got auth request");
        	    	
        	    }
        	    @Override
        	    public void onPageFinished(WebView view, final String url)
        	    {
        	    	Log.v("WebViewClient","page finished");
        	    	if(url.contains("http://kabbalahgroup.info/"))
        	    	{
        	    		//remove the type of login
        	    		//mLoginwebView.loadUrl("javascript:(function() { " + "document.getElementsByTag('fieldset')[0].style.display = 'none'; " + "})()");
        	    		//mLoginwebView.loadUrl("javascript:(function() { " + "elem = document.getElementsByTag('fieldset'); if (elem) {elem.style.display = 'none; ';})()");
        	    	}
        	    	//check what language what clicked
        	    	if(url.contains("http://kabbalahgroup.info/internet/en/mobile") || url.contains("http://icecast.kab.tv"))
        	    	{
//        	    		SharedPreferences userInfoPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
//       	    		 SharedPreferences.Editor editor = userInfoPreferences.edit();
//   	    			 editor.putBoolean("activated", true);
//   	    			  Boolean success = editor.commit();
   	    			 setActivated(true);
   	    			  
        	    		//if got here then move to video
        	    		if(url.contains("http://icecast.kab.tv"))
        	    		{
        	    			
        	    		//	if(!checkAvailability(url))
        	    		//		return;
        	    			StreamAvailabilityChecker checker = new StreamAvailabilityChecker();
        	    			//checker.execute("http://icecast.kab.tv/live1-heb-574bcfd5.mp31");
        	    			checker.execute(url);
        	    			try {
								if(!checker.get())
								{
									mLoginwebView.loadUrl("javascript:Android.showToast('Currently no broadcast, please try again later')");
									return;
								}
						
									

							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							} catch (ExecutionException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
        	    			
        	    		
	    					
	    					//Check which lang was clicked
   	    				  Set<String> keyset = events.locale.keySet();
   	    				 String url1 = null;
   	    				String jsonrep = null;
   	    				final ArrayList<String> streamList = new ArrayList<String>();
   	    				
   	    				  for(int i=0;i<keyset.size();i++)
   	    				  {
   	    					 
   	    					  if(url.contains(keyset.toArray()[i].toString()))
   	    					  {
   	    						 url1=   (events.locale.get(keyset.toArray()[i])).pages.get(0).urls.urlslist.get(0).url_value.toString();
   	    						 Log.e("url val", url1);
   	    						 
   	    						 //build parameters to pass to list
   	    						 for(int j=0;j<(events.locale.get(keyset.toArray()[i])).pages.size();j++)
   	    						 {
   	    							 
   	    						 jsonrep = new Gson().toJson((events.locale.get(keyset.toArray()[i])).pages.get(j));
   	    						 streamList.add(jsonrep);
   	    						 }
   	    						 EasyTracker.getTracker().trackEvent("web login", "lang", keyset.toArray()[i].toString(),0L);
        	    				  
   	    						 
   	    					  }
   	    				  }
   	    				  
   	    				  //check if no video available then go straight to audio
   	    				  
	    					
        	    			//ask user if he wants audio or video
        	    			AlertDialog chooseVideoAudio = new AlertDialog.Builder(WebLogin.this).create();
        	    			chooseVideoAudio.setTitle("Video or Audio?");
        	    			chooseVideoAudio.setButton("Audio", new DialogInterface.OnClickListener() {
        	    			   public void onClick(DialogInterface dialog, int which) {
        	    			      // here you can add functions
        	    				   //Audio selected
        	    				   EasyTracker.getTracker().trackEvent("web login", "button pressed", "audio",0L);
         	    				   
         	    				   Uri uri = Uri.parse(url);
         	    				  Intent player = new Intent(Intent.ACTION_VIEW,uri);
         	    				 
          	    				 // player.setDataAndType(uri, "mp3");
        	    				   //player.putExtra("path", url.toString());
        	    				   //player.putExtra("type", "audio");
        	        	    		startActivity(player);
        	        	    		
        	    			   }
        	    			});
        	    			chooseVideoAudio.setButton2("Video", new DialogInterface.OnClickListener() {
         	    			   public void onClick(DialogInterface dialog, int which) {
         	    			      // here you can add functions
         	    				  EasyTracker.getTracker().trackEvent("web login", "button pressed", "video",0L);
         	    				  

         	    			/*	  //Check which lang was clicked
         	    				  Set<String> keyset = events.locale.keySet();
         	    				 String url1 = null;
         	    				String jsonrep = null;
         	    				ArrayList<String> streamList = new ArrayList<String>();
         	    				
         	    				  for(int i=0;i<keyset.size();i++)
         	    				  {
         	    					 
         	    					  if(url.contains(keyset.toArray()[i].toString()))
         	    					  {
         	    						 url1=   (events.locale.get(keyset.toArray()[i])).pages.get(0).urls.urlslist.get(0).url_value.toString();
         	    						 Log.e("url val", url1);
         	    						 
         	    						 //build parameters to pass to list
         	    						 for(int j=0;j<(events.locale.get(keyset.toArray()[i])).pages.size();j++)
         	    						 {
         	    							 
         	    						 jsonrep = new Gson().toJson((events.locale.get(keyset.toArray()[i])).pages.get(j));
         	    						 streamList.add(jsonrep);
         	    						 }
         	    						 
         	    					  }
         	    				  }
         	    				  */
         	    				 Intent intent = new Intent(WebLogin.this,StreamListActivity.class);
     	    					intent.putStringArrayListExtra("channel",streamList);
     	    					startActivity(intent);
         	    				  
         	    			   }

							
         	    			});
        	    			chooseVideoAudio.setIcon(R.drawable.icon);
        	    			chooseVideoAudio.show();
        	    			
        	    			
        	    		}
        	    		
        	    	}
        	    	
        	    }
        	   
        	
        };
        
        
        mLoginwebView.setWebViewClient(mClient);
        mLoginwebView.getSettings().setJavaScriptEnabled(true);
        mLoginwebView.addJavascriptInterface(new JavaScriptInterface(this), "Android");
        
        String url = new String("http://kabbalahgroup.info/");
        mLoginwebView.loadUrl(url);
        
        
        
    }

    
    private void setValid(boolean val)
    {
    	SharedPreferences shared = PreferenceManager.getDefaultSharedPreferences(WebLogin.this);
		SharedPreferences.Editor edit = shared.edit();
		edit.putBoolean("valid", val);
		edit.commit();
    }
    private void setKey(String val)
    {
    	SharedPreferences shared = PreferenceManager.getDefaultSharedPreferences(WebLogin.this);
		SharedPreferences.Editor edit = shared.edit();
		edit.putString("key", val);
		edit.commit();
    }
    
	private void setActivated(boolean val)
    {
    	SharedPreferences shared = PreferenceManager.getDefaultSharedPreferences(WebLogin.this);
		SharedPreferences.Editor edit = shared.edit();
		edit.putBoolean("activated", val);
		edit.commit();
    }
	
   
//    @Override
//	  public void onResume() {
//	    super.onResume();
//	    checkForCrashes();
//	  }
//	private void checkForUpdates() {
//	    checkUpdateTask = (CheckUpdateTask)getLastNonConfigurationInstance();
//	    if (checkUpdateTask != null) {
//	      checkUpdateTask.attach(this);
//	    }
//	    else {
//	      checkUpdateTask = new CheckUpdateTask(this, "http://10.0.0.6/", "io.vov.android.vitamio.demo");
//	      checkUpdateTask.execute();
//	    }
//	  }

//	  @Override
//	  public Object onRetainNonConfigurationInstance() {
//	    checkUpdateTask.detach();
//	    return checkUpdateTask;
//	  }
	  
//	  private void checkForCrashes() {
//		    CrashManager.register(this, "http://10.0.0.6/", "io.vov.android.vitamio.demo");
//		  }
private boolean checkAvailability(String url)
{
	//check availability of stream
	
	URLConnection cn = null;
	try {

		cn = new URL(url).openConnection();
	} catch (MalformedURLException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	try {
		cn.connect();
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	InputStream stream = null;
	try {
		stream = cn.getInputStream();
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	if (stream == null) {

	Log.e(getClass().getName(),url);
	
	mLoginwebView.loadUrl("javascript:Android.showToast('Currently no broadcast, please try again later')");
	
	return false;
	}
	return true;
}


@Override
public boolean onCreateOptionsMenu(Menu menu) {
    MenuInflater inflater = getMenuInflater();
    inflater.inflate(R.menu.activity_web_login, menu);
    return true;
	 
}
@SuppressLint("Wakelock")
@Override
public boolean onOptionsItemSelected(MenuItem item) {
    // Handle item selection
    switch (item.getItemId()) {
        case R.id.Autocheck:
        	PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        	  wl = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "My Tag");
        	 wl.acquire();
        	 myProgressDialog = new ProgressDialog(WebLogin.this);
        	 myProgressDialog.setTitle("Waiting for broadcast...");
//             myProgressDialog.show(WebLogin.this,"Waiting for broadcast...",null,true,true,new OnCancelListener() {
// 	            public void onCancel(DialogInterface pd) {
// 	            	autocheckdone();
//	            }
//	        });
//             
        	 myProgressDialog = ProgressDialog
        		        .show(this, "Waiting for broadcast...",
        		        null, true, true,
        		        new OnCancelListener() {
        		            public void onCancel(DialogInterface pd) {
        		                autocheckdone();
        		            }
        		        });      
             
        	myChecker = new StreamAvailabilityChecker();
        	myChecker.setAuto(true);
        	 myChecker.setWeb(this);
        	myChecker.execute("http://icecast.kab.tv/live1-heb-574bcfd5.mp3");
		
            // myProgressDialog.hide();
        	 return true;
        case R.id.channel66:
        	Intent intent = new Intent(WebLogin.this,StreamListActivity.class);
        	startActivity(intent);
            return true;
        default:
            return super.onOptionsItemSelected(item);
    }
  
}

@Override
public void onResume()
{
	super.onResume();
	
	 myProgressDialog = new ProgressDialog(this);
     myProgressDialog.show();
     //http://kabbalahgroup.info/internet/events/render_event_response?locale=he&source=stream_container&type=update_presets&timestamp=2011-11-25+13:29:53+UTC&stream_preset_id=3&flash=true&wmv=true
     ContentParser cparser = new ContentParser();
     cparser.execute("http://kabbalahgroup.info/internet/events/render_event_response?locale=he&source=stream_container&type=update_presets&timestamp=2011-11-25+13:29:53+UTC&stream_preset_id=3&flash=true&wmv=true");
      JSONParser parser = new JSONParser();
      parser.execute("http://mobile.kbb1.com/kab_channel/sviva_tova/jsonresponseexample.json");
     
      try {
     	 serverJSON = parser.get();
     	  content = cparser.get();
     	 myProgressDialog.hide();
		} catch (InterruptedException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		} catch (ExecutionException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
      
      
     	
      //parse key
      if(content!=null)
      {
     	 int i = content.indexOf("special-")+"special-".length() ;
     	 String key = content.substring(i, i+8);
     	 setKey(key);
      }
      
     JSONObject returned_Val = serverJSON;
     String time_stamp = null;
     String isUpdate = null;
     String version = null;
     //test events
     try {
     	if(returned_Val==null)
     	{
     		Toast.makeText(this, "Could not retrieve data from server",5);
     		return;
     	}
			time_stamp = returned_Val.getString("time_stamp");
			isUpdate = returned_Val.getString("updateandlock");
			version = returned_Val.getString("version");
			
			if(isUpdate.equalsIgnoreCase("true"))
			{
				String versionName = getResources().getString(R.string.version_name);
				if(Float.parseFloat(version)>Float.parseFloat(versionName))
				{
					AlertDialog chooseToInstall = new AlertDialog.Builder(WebLogin.this).create();
					chooseToInstall.setTitle("New version available, do you want to update?");
					
					chooseToInstall.setButton("Ok", new DialogInterface.OnClickListener() {
					   public void onClick(DialogInterface dialog, int which) {
					      // here you can add functions
						   Intent goToMarket = new Intent(Intent.ACTION_VIEW)
						    .setData(Uri.parse("market://details?id=com.kab.channel66"));
						startActivity(goToMarket); 
		 				   
		 				 
		    	    		
					   }
					});
					chooseToInstall.setButton2("Cancel", new DialogInterface.OnClickListener() {
		 			   public void onClick(DialogInterface dialog, int which) {
		 			      // here you can add functions
		 				  finish();
		 			   }
		 			});
					chooseToInstall.show();
				}
					
			}
			
		} catch (JSONException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
     events = new Events(returned_Val, getApplicationContext());
     events.parse();
     
     ///
       try {
     	
     	
			io.vov.vitamio.VitamioInstaller.checkVitamioInstallation(this);
		} catch (VitamioNotCompatibleException e) {
			// TODO Auto-generated catch block
			AlertDialog chooseToInstall = new AlertDialog.Builder(WebLogin.this).create();
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
			AlertDialog chooseToInstall = new AlertDialog.Builder(WebLogin.this).create();
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
		
     
}
@Override 
public void onBackPressed()
{
	super.onBackPressed();
	autocheckdone();
}

private void autocheckdone()
{
	if(myChecker!=null)
	{
	myChecker.cancel(true);
	myProgressDialog.hide();
	myChecker.setAuto(false);
	if(wl.isHeld())
	wl.release();
	}
}
@Override
public void onPause()
{
	super.onPause();
	autocheckdone();
}


@Override
public void streamfound() {
	// TODO Auto-generated method stub
	runOnUiThread(new Runnable() {
        @Override
        public void run() {
           //Your code to run in GUI thread here
        	myProgressDialog.hide();
       
        	Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        	Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);
        	r.play();
        	
        	mLoginwebView.loadUrl("javascript:Android.showToast('Broadcast started...')");
        	wl.release();
        }//public void run() {
});
	
}
}
