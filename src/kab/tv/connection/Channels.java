package kab.tv.connection;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;


import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

public class Channels {
	
	static List<ChannelInfo> mChannels;
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
	         _instance.mConfigurationUrl =  new URL("http://mobile.kbb1.com/kab_channel/configuration.xml");
	         mChannels = new ArrayList<ChannelInfo>();
	      }
	      return _instance;
	   }

	 public void LoadData() throws ParserConfigurationException, SAXException, IOException
	 {
		 
		 
		 
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
		this.mChannels.get(mNumberOfChannelsLoaded).GetStreams().GetStream(mNumberOfStreamsLoaded).mURL = string;
		
	}
	
	public void setStreamData(String format, String type, String quality) {
		// TODO Auto-generated method stub
		this.mChannels.get(mNumberOfChannelsLoaded).GetStreams().GetStream(mNumberOfStreamsLoaded).mFormat = format;
		this.mChannels.get(mNumberOfChannelsLoaded).GetStreams().GetStream(mNumberOfStreamsLoaded).mType = type;
		this.mChannels.get(mNumberOfChannelsLoaded).GetStreams().GetStream(mNumberOfStreamsLoaded).mQaulity = quality;
		
		
	}
	
	public void setChannelName(String string) {
		// TODO Auto-generated method stub
		this.mChannels.get(mNumberOfChannelsLoaded).mName= string;
		
	}

	public void LoadSchedule()throws ParserConfigurationException, SAXException, IOException {
		// TODO Auto-generated method stub
		this.mChannels.get(mNumberOfChannelsLoaded).LoadSchedule();
	}

	
	public void LoadChannel()
	{
		mNumberOfChannelsLoaded++;
		ChannelInfo channel = new ChannelInfo();
		this.mChannels.add(channel);
	}

	public void LoadStream() {
		// TODO Auto-generated method stub
		mNumberOfStreamsLoaded++;
		StreamInfo stream = new StreamInfo(null);
		this.mChannels.get(mNumberOfChannelsLoaded).GetStreams().mStreams.add(stream);
	}
	 
}
