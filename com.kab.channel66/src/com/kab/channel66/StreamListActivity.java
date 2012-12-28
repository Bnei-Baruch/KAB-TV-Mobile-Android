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

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

public class StreamListActivity extends ListActivity {

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
	    	 
	    	description.add("Channel 66 Video");
	    	description.add("Channel 66 Audio");
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
	    } else if(item.equals("Channel 66 Video"))
	    	{
	    		
  				 
	    		player.putExtra("path",  ExtractMMSfromAsx("http://streams.kab.tv/heb.asx"));
	    		startActivity(player);
				 
	    	}
	    else if(item.equals("Channel 66 Audio"))
    	{
	    	Uri uri = Uri.parse("http://icecast.kab.tv/heb.mp3");
	    	Intent player1 = new Intent(Intent.ACTION_VIEW,uri);
			startActivity(player1);	  
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
	  
	 }


	 @Override
	 public void onStop() {
	   super.onStop();
	    // The rest of your onStop() code.
	   EasyTracker.getInstance().activityStop(this); // Add this method.
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
			 return false;
	 }
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
	         default:
	             return super.onOptionsItemSelected(item);
	     }
	 }
}
