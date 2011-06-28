package kab.tv.connection;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;



enum ChannelType
{
	TV,
	RADIO,
	VOD
};


public class ChannelInfo implements Parcelable{
	String mLanguage;
	Bitmap mIcon;
	String mSource;
	private String mName;
	Streams mStreams;
	URL mScheduleuRL;
	Boolean mChannelLoaded;
	private ScheduleData mScheduleData;
	
	
	
	public ChannelInfo()
	{
		mChannelLoaded = false;
	}
	 
	public ChannelInfo(Parcel in) {
		// TODO Auto-generated constructor stub
		if(in!=null)
			MyParcelable(in);
	}
	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	public void writeToParcel(Parcel arg0, int arg1) {
		// TODO Auto-generated method stub 
		arg0.writeStringArray(new String[] {this.mLanguage,  this.getmName(), this.mSource});
		
	}
	
	 public static final Parcelable.Creator<ChannelInfo> CREATOR             = new Parcelable.Creator<ChannelInfo>() 
	 {        
		 public ChannelInfo createFromParcel(Parcel in) 
		 {          
			 return new ChannelInfo(in);     
			 }        
		 public ChannelInfo[] newArray(int size)
		 {            
			 return new ChannelInfo[size];      
			 }   
		 };
		 private void MyParcelable(Parcel in)
		 {        
			
			String[] val = new String[4];	
				in.readStringArray(val);
				
				
			// this.mIcon = intval[1];
			 this.mLanguage = val[0];
			 this.setmName(val[1]);
			 this.mSource = val[2];
			 
			// this.mType = StreamType. (intval[0]);
			 
		 } 
		 
		 public Streams GetStreams()
		 {
			 if(mStreams!=null)
				 return mStreams;
			 else
			 {
				 mStreams = new Streams();
				 return mStreams;
			 }
		 }
		 
		 public void LoadSchedule() throws ParserConfigurationException, SAXException, IOException{
			 
			 /* Get a SAXParser from the SAXPArserFactory. */
	         SAXParserFactory spf = SAXParserFactory.newInstance();
	         SAXParser sp = spf.newSAXParser();

	         /* Get the XMLReader of the SAXParser we created. */
	         XMLReader xr = sp.getXMLReader();
	         /* Create a new ContentHandler and apply it to the XML-Reader*/ 
	         ScheduleHandler myHandler = new ScheduleHandler();
	         xr.setContentHandler(myHandler);
	         
	         
	        // mScheduleuRL = new URL("http://kab.tv/vod/api/schedule/Hebrew");
	         /* Parse the xml-data from our URL. */
	         InputSource source = new InputSource(mScheduleuRL.openStream());
	         source.setEncoding("UTF-8");
	         xr.parse(source);
	         /* Parsing has finished. */
	         setmScheduleData(myHandler.getParsedData());
			 
			 
		 }

		public void setmName(String mName) {
			this.mName = mName;
		}

		public String getmName() {
			return mName;
		}

		public void setmScheduleData(ScheduleData mScheduleData) {
			this.mScheduleData = mScheduleData;
		}

		public ScheduleData getmScheduleData() {
			return mScheduleData;
		}
		
		
		 
}
