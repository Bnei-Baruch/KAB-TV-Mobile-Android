package kab.tv.connection;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import kab.tv.ui.StreamsGrid;


import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import android.app.ProgressDialog;
import android.content.Intent;

import android.content.Context;
import android.widget.Toast;

public class Channels {
	
	private static List<ChannelInfo> mChannels;
	URL mConfigurationUrl;
	Boolean mDataLoaded;
	public int mNumberOfChannelsLoaded;
	public int mNumberOfStreamsLoaded;
	 protected Channels() throws ParserConfigurationException, SAXException, IOException {
	     // ...
		 mDataLoaded = false;
		 mNumberOfChannelsLoaded = -1;
		 mNumberOfStreamsLoaded = -1;
		
	   }

	 
	 static private Channels _instance = null;

	 
	 static public Channels instance() throws ParserConfigurationException, SAXException, IOException {
	      if(null == _instance) {
	         _instance = new Channels();
	         _instance.mConfigurationUrl =  new URL("http://mobile.kbb1.com/kab_channel/android.xml");
	         setmChannels(new ArrayList<ChannelInfo>());
	      }
	      return _instance;
	   }

	 public void LoadData() throws ParserConfigurationException, SAXException, IOException
	 {
		 
		
         
		 if(isLoaded())
			 return;
		 
		
		 /* Get a SAXParser from the SAXPArserFactory. */
         SAXParserFactory spf = SAXParserFactory.newInstance();
         SAXParser sp = spf.newSAXParser();

         /* Get the XMLReader of the SAXParser we created. */
         XMLReader xr = sp.getXMLReader();
         /* Create a new ContentHandler and apply it to the XML-Reader*/ 
         ConfigurationHandler myHandler = new ConfigurationHandler();
         xr.setContentHandler(myHandler);
         
         /* Parse the xml-data from our URL. */
         xr.parse(new InputSource(mConfigurationUrl.openStream()));
         /* Parsing has finished. */

         /* Our ExampleHandler now provides the parsed data to us. */
        

         /* Set the result to be displayed in our GUI. */
        

		 ///////////////////////////////////////////////////////////////////////////////////
	        
	       
	 }
	

	public void setStreamUrl(String string) {
		// TODO Auto-generated method stub
		this.getmChannels().get(mNumberOfChannelsLoaded).GetStreams().GetStream(mNumberOfStreamsLoaded).setmURL(string);
		
	}
	
	public void setStreamData(String format, String type, String quality, String os) {
		// TODO Auto-generated method stub
		this.getmChannels().get(mNumberOfChannelsLoaded).GetStreams().GetStream(mNumberOfStreamsLoaded).mFormat = format;
		this.getmChannels().get(mNumberOfChannelsLoaded).GetStreams().GetStream(mNumberOfStreamsLoaded).setmType(type);
		this.getmChannels().get(mNumberOfChannelsLoaded).GetStreams().GetStream(mNumberOfStreamsLoaded).setmQaulity(quality);
		this.getmChannels().get(mNumberOfChannelsLoaded).GetStreams().GetStream(mNumberOfStreamsLoaded).setmOS(os);
		
	}
	
	public void setChannelName(String string) {
		// TODO Auto-generated method stub
		this.getmChannels().get(mNumberOfChannelsLoaded).setmName(string);
		
	}

	public void LoadSchedule()throws ParserConfigurationException, SAXException, IOException {
		// TODO Auto-generated method stub
		//if(mNumberOfChannelsLoaded<2)
		this.getmChannels().get(mNumberOfChannelsLoaded).LoadSchedule();
	}

	
	public void LoadChannel()
	{
		mNumberOfChannelsLoaded++;
		ChannelInfo channel = new ChannelInfo();
		this.getmChannels().add(channel);
	}

	public void LoadStream() {
		// TODO Auto-generated method stub
		mNumberOfStreamsLoaded++;
		StreamInfo stream = new StreamInfo(null);
		this.getmChannels().get(mNumberOfChannelsLoaded).GetStreams().getmStreams().add(stream);
	}

	public void resetStreams() {
		// TODO Auto-generated method stub
		mNumberOfStreamsLoaded = -1;
	}
	
	public List<ChannelInfo> GetChannels() {
		// TODO Auto-generated method stub
		return getmChannels();
	}

	public static void setmChannels(List<ChannelInfo> mChannels) {
		Channels.mChannels = mChannels;
	}

	public List<ChannelInfo> getmChannels() {
		return mChannels;
	}

	public void SetChannelLoaded(boolean b) {
		// TODO Auto-generated method stub
		this.getmChannels().get(mNumberOfChannelsLoaded).mChannelLoaded = b;
	}

	public boolean isLoaded() {
		// TODO Auto-generated method stub
		if(this.getmChannels().size()>0)
			return true;
		else
			return false;
	}

	public void setScheduleUrl(String string) throws MalformedURLException {
		// TODO Auto-generated method stub
		URL schedule = new URL(string);
		this.getmChannels().get(mNumberOfChannelsLoaded).mScheduleuRL = schedule;
	}

	 public static void checkCommunicationStateConfiguration(boolean status, Context mCurrentContext) throws ParserConfigurationException, SAXException, IOException {
			
	    	if(Channels.instance() == null)
	    		return;
	    	
	    		if(status && !Channels.instance().isLoaded())
	    		{
	    			Channels.instance().reload();
	    			Intent i = new Intent("com.myapp.app.DATA_REFRESH");
	    			mCurrentContext.sendBroadcast(i);
	    			
	    		}
	    		else if(!status){

	    		/*	AlertDialog alertDialog = new AlertDialog.Builder(mSelf).create();
	    			alertDialog.setTitle("Communication disconnected");
	    			alertDialog.setMessage("Do you want to wait or quit?");
	    			 alertDialog.setButton("Wait", new DialogInterface.OnClickListener() {
	    				     public void onClick(DialogInterface dialog, int which) {*/
	    			Channels.instance().setEmptyView();
	    				     return;
	    				
	    	/*			   } }); 
	    			 alertDialog.setButton2("Quit", new DialogInterface.OnClickListener() {
	    			     public void onClick(DialogInterface dialog, int which) {
	    			
	    			    	 mSelf.onBackPressed();
	    			
	    			   } }); 
	    			
	    			 alertDialog.show();*/
	    		}
	    			
	    		
	    	}

	private void setEmptyView() {
		// TODO Auto-generated method stub
		
	}

	private void reload() throws ParserConfigurationException, SAXException, IOException {
		// TODO Auto-generated method stub
		LoadData();
	}

	public void setIconName(String icon) {
		// TODO Auto-generated method stub
		this.getmChannels().get(mNumberOfChannelsLoaded).setmIcon(icon);
	}
	
	 
}
