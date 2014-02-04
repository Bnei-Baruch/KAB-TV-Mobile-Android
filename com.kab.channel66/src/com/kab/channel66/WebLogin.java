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
import org.apache.http.util.LangUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.apphance.android.Apphance;
import com.google.analytics.tracking.android.EasyTracker;
import com.google.myjson.Gson;
import com.google.myjson.JsonObject;
import com.google.myjson.JsonParseException;
import com.kab.channel66.R;
import com.parse.Parse;
import com.parse.PushService;







import io.vov.vitamio.LibsChecker;
//import io.vov.vitamio.VitamioInstaller.VitamioNotCompatibleException;
//import io.vov.vitamio.VitamioInstaller.VitamioNotFoundException;
import io.vov.vitamio.widget.VideoView;
import android.R.string;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
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
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.HttpAuthHandler;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;




public class WebLogin extends BaseActivity implements WebCallbackInterface {

	private WebView mLoginwebView;
	private WebViewClient mClient;
	private Events events;
	private String TranslationInfoString;
	private int status = 0;
	private ProgressDialog myProgressDialog = null;
	private StreamAvailabilityChecker myChecker = null;
	PowerManager.WakeLock wl = null;
	JSONObject serverJSON = null;
	String content = null;
	Dialog playDialog;
	AlertDialog.Builder alert;
	Intent svc;
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
        if (!LibsChecker.checkVitamioLibs(this))
			return;
        PushService.subscribe(this, "", WebLogin.class);
        PushService.setDefaultPushCallback(this, WebLogin.class);
        
        mLoginwebView = (WebView) findViewById(R.id.webloginview);
       if(mClient == null)
        mClient = new WebViewClient()
        	{
    	   
    	   @Override
    	   public void onReceivedError (WebView view, int errorCode, String description, String failingUrl)
    	   {
    		   status = errorCode; //spome error occured
    		   //check if active and if logged in then suggest user to play the last know stream
    		   AlertDialog.Builder alert1 = new AlertDialog.Builder(WebLogin.this);                 
	        	 alert1.setTitle("Error");  
	        	 alert1.setMessage("We have encountered an error would you like to play the last known stream?");                
	        	 alert1.setPositiveButton("Ok", new DialogInterface.OnClickListener() {  
	 	        	     public void onClick(DialogInterface dialog, int whichButton) { 
	 	        	    	 //play last known stream
	 	        	    	 final String  urlfinal;
	 	        	    	 String url = "http://icecast.kab.tv/live1-heb-574bcfd5.mp3";
	 	        	    	String lang = getLastKnownLang();
	 	        	    	if(lang == null)
	 	        	    		new AlertDialog.Builder(WebLogin.this).setTitle("No last known stream").setPositiveButton("Ok", new DialogInterface.OnClickListener() {
									
									@Override
									public void onClick(DialogInterface dialog, int which) {
										// TODO Auto-generated method stub
										dialogBackpressed();
									}
								});
	 	        	    	urlfinal = url = url.replace("heb", lang);
	 	        	    	svc=new Intent(WebLogin.this, BackgroundPlayer.class);
	        		    	 svc.putExtra("audioUrl", url);
	        		    	 svc.putExtra("sviva", true);
	        	            startService(svc);
	        	            playDialog = new Dialog(WebLogin.this);
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
	        							svc=new Intent(WebLogin.this, BackgroundPlayer.class);
	        							svc.putExtra("audioUrl", urlfinal);
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
	        	            
	 	        	         return;                 
		        	        }  
		        	      });  

	        	 alert1.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

		        	         public void onClick(DialogInterface dialog, int which) {
		        	             // TODO Auto-generated method stub
		        	        	 
		        	        	 Intent intent = new Intent(WebLogin.this,StreamListActivity.class);
		        	         	startActivity(intent);   
		        	        return;   
		        	         }
		        	     });
	        	 alert1.show();
    		   
    	   }
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
        	    	if(url.contains("login") )
    	    		{   	    			 
    	    			  setActivated(false);	
    	    		}
        	    	else
        	    	{
        	    		
        	    		
        	    		     
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
        	    		
        	    	
        	    	}
        	    	//check what language what clicked
        	    	if(url.contains("http://kabbalahgroup.info/internet/en/mobile") || url.contains("http://icecast.kab.tv"))
        	    	{
        	    		if(getActivated() && getGroup().length()==0)// && url.contains("http://kabbalahgroup.info/internet/en/mobile"))
        	    		{
        	    		if(alert==null)
        	    		{	
        	    		 alert = new AlertDialog.Builder(WebLogin.this);                 
     	 	        	 alert.setTitle("Group name");  
     	 	        	 alert.setMessage("Please enter group name you belong:");                

     	 	        	  // Set an EditText view to get user input   
     	 	        	  final EditText input = new EditText(WebLogin.this); 
     	 	        	  alert.setView(input);

     	 	        	     alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {  
     	 	        	     public void onClick(DialogInterface dialog, int whichButton) {  
     	 	        	         String value = input.getText().toString();
     	 	        	         setGroup(value);
     	 	        	         Log.d( "Login", "Group member : " + value);
     	 	        	         EasyTracker.getTracker().trackEvent("Group", "name", value,0L);
     	 	        	         alert = null;
     		        	         return;                  
     		        	        }  
     		        	      });  

     		        	     alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

     		        	         public void onClick(DialogInterface dialog, int which) {
     		        	             // TODO Auto-generated method stub
     		        	        	 alert = null;
     		        	             return;   
     		        	         }
     		        	     });
     		        	             alert.show();
     		        	             return;
        	    		}   
        	    	}
        	    		
        	    		
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
   	    						setLastKnownLang(keyset.toArray()[i].toString());
   	    						 
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
         	    				 
          	    				  player.setDataAndType(uri, "audio/*");
        	    				 
          	    				  
          	    				  
          	    				  //check if translations audio is needed
          	    				  //what wifi are we in?
          	    				 WifiManager mainWifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);
          	    			    WifiInfo currentWifi = mainWifi.getConnectionInfo();
          	    				String ssid = currentWifi.getSSID();
          	    				String urlTrans= null;
          	    				try {	
          	    				
          	    				JSONObject TranslationJsonObj = new JSONObject(TranslationInfoString);
          	    				JSONArray ssidArray;
								
									ssidArray = TranslationJsonObj.getJSONArray("ssid");
								 
									for(int count=0;count<ssidArray.length();count++)
									{
										if(ssidArray.get(count).equals(ssid))
										{
											JSONArray array = TranslationJsonObj.getJSONArray("urls");
											
											 for (int i=0;i< array.length();i++) {
												 JSONObject URLO =  (JSONObject)array.get(i);
												 String key = URLO.keys().next().toString();
												
												 if(url.contains(key))
												 {
													 urlTrans = URLO.getString(key);
													break;
												 }
													
											}
										}
									}
          	    				}
          	    				catch (JSONException e)
          	    				{
          	    					android.util.Log.e("Parse","Failed to parse json of translation");
          	    					e.printStackTrace();
          	    				}
          	    				
          	    				
        	        	    		//background audio player
        	        	    		 svc=new Intent(WebLogin.this, BackgroundPlayer.class);
        	        		    	 svc.putExtra("audioUrl", urlTrans!=null?urlTrans:url);
        	        		    	 svc.putExtra("sviva", true);
        	        	            startService(svc);
        	        	            playDialog = new Dialog(WebLogin.this);
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
        	        							svc=new Intent(WebLogin.this, BackgroundPlayer.class);
        	        							svc.putExtra("audioUrl", url);
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

    public void dialogBackpressed()
	 {
		 playDialog.hide();
		 if(svc!=null)
			   stopService(svc);
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
	
	private boolean getActivated()
    {
    	SharedPreferences shared = PreferenceManager.getDefaultSharedPreferences(WebLogin.this);
		return shared.getBoolean("activated", false);
    }
	
	private void setGroup(String val)
    {
    	SharedPreferences shared = PreferenceManager.getDefaultSharedPreferences(WebLogin.this);
		SharedPreferences.Editor edit = shared.edit();
		edit.putString("group", val);
		edit.commit();
    }
	
	private String getGroup()
    {
    	SharedPreferences shared = PreferenceManager.getDefaultSharedPreferences(WebLogin.this);
		return shared.getString("group", "");
    }
	 private void setLastKnownLang(String val)
	    {
	    	SharedPreferences shared = PreferenceManager.getDefaultSharedPreferences(WebLogin.this);
			SharedPreferences.Editor edit = shared.edit();
			edit.putString("lang", val);
			edit.commit();
	    }
	 private String getLastKnownLang()
	    {
	    	SharedPreferences shared = PreferenceManager.getDefaultSharedPreferences(WebLogin.this);
			return shared.getString("lang", "eng");
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
public boolean onPrepareOptionsMenu (Menu menu)
{
	MenuItem Item = menu.findItem(R.id.quality);
	SharedPreferences shared = PreferenceManager.getDefaultSharedPreferences(WebLogin.this);
	
	if(shared.getBoolean("quality", false))
		Item.setTitle(getResources().getString(R.string.quality)+": High");
	else
		Item.setTitle(getResources().getString(R.string.quality)+": Medium");	
	return true;
}
private void updateMenuTitles() {
//    MenuItem bedMenuItem = menu.findItem(R.id.quality);
//    if (inBed) {
//        bedMenuItem.setTitle(outOfBedMenuTitle);
//    } else {
//        bedMenuItem.setTitle(inBedMenuTitle);
//    }
}
@Override
public boolean onCreateOptionsMenu(Menu menu) {
    MenuInflater inflater = getMenuInflater();
    inflater.inflate(R.menu.activity_web_login, menu);
    updateMenuTitles();
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
        case R.id.quality:
        	SharedPreferences shared = PreferenceManager.getDefaultSharedPreferences(WebLogin.this);
        	SharedPreferences.Editor edit = shared.edit();
        	if(shared.getBoolean("quality", false))
         	{
         		Toast.makeText(WebLogin.this, "Changed quality to medium", Toast.LENGTH_LONG).show();
         		edit.putBoolean("quality", false);
     			edit.commit();
         	}
         	else
         	{
         		Toast.makeText(WebLogin.this, "Changed quality to high", Toast.LENGTH_LONG).show();
         		edit.putBoolean("quality", true);
     			edit.commit();
         	}
            return true;
        default:
            return super.onOptionsItemSelected(item);
    }
  
}

@Override
public void onResume()
{
	super.onResume();
	EasyTracker.getInstance().setContext(this);
	
	
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
     	 int i = content.indexOf("\"secret_word\":\"") ;
     	
     	 if(i==-1 || content.length()<i+8)
     	 {
     		Toast.makeText(this, "No valid broadcast, please try again later",5);
     		return;
     	 }
     	 i+= "\"secret_word\":\"".length();
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
			TranslationInfoString = returned_Val.getString("TranslationWIFISupport");
			
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
     /*
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
		*/
		if(status!=0)
		{
				mLoginwebView.setWebViewClient(mClient);
		        mLoginwebView.getSettings().setJavaScriptEnabled(true);
		        mLoginwebView.addJavascriptInterface(new JavaScriptInterface(this), "Android");
		        
		        String url = new String("http://kabbalahgroup.info/");
		        mLoginwebView.loadUrl(url);
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
