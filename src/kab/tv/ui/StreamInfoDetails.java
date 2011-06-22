package kab.tv.ui;

import java.io.IOException;
import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.xml.sax.SAXException;



import kab.tv.connection.Channels;
import kab.tv.connection.EventData;
import kab.tv.connection.ScheduleData.Day;
import kab.tv.connection.StreamInfo;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class StreamInfoDetails extends Activity {

	
	private static final String MEDIA = "media";
	private static final int STREAM_VIDEO = 5;
	private static final int LOCAL_AUDIO = 1;	
	private static final String 	TAG = "StreamDetail"; 
	private TextView mTextViewDetails;
	TextView[] mStreamName;
	private TextView mStreamName1;
	private TextView mStreamName2;
	private TextView mStreamName3;
	private TextView mStreamName4;
	private TableRow mStreamRow1;
	private TableRow mStreamRow2;
	private TableRow mStreamRow3;
	private TableRow mStreamRow4;
	TableRow[] mStreamRows;
	private TableLayout mStreamTable;
	private Button mSchedule;
	private ImageView mChannelImage;
	private String mTitleofCurrentProgram;
	String mInfo;
	int mChannelNum;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.streamdetail);
		mTextViewDetails = (TextView) findViewById(R.id.detail_text);
		mStreamName1 = (TextView) findViewById(R.id.streamrow1);
		mStreamName2 = (TextView) findViewById(R.id.streamrow2);
	//	mStreamName3 = (TextView) findViewById(R.id.streamrow3);
	//	mStreamName4 = (TextView) findViewById(R.id.streamrow4);
		mStreamName = new TextView[4];
		mStreamName[0] = mStreamName1;
		mStreamName[1] = mStreamName2;
		mStreamName[2] = mStreamName3;
		mStreamRows = new TableRow[4];
		mStreamRow1 = (TableRow)findViewById(R.id.row1);
		mStreamRow2 = (TableRow)findViewById(R.id.row2);
	//	mStreamRow3 = (TableRow)findViewById(R.id.row3);
	//	mStreamRow4 = (TableRow)findViewById(R.id.row4);
		mStreamRows[0] = mStreamRow1;
		mStreamRows[1] = mStreamRow2;
		mStreamRows[2] = mStreamRow3;
		mStreamRows[3] = mStreamRow4;
		mStreamTable = (TableLayout) findViewById(R.id.table);
		
		mSchedule = (Button)findViewById(R.id.scheduleid);
		Intent i = getIntent();
		try {
		//mInfo = (StreamInfo) i.getParcelableExtra(getResources().getString(R.string.input_stream));
		mChannelNum =i.getIntExtra(getResources().getString(R.string.input_stream), 0);
		
			mInfo = Channels.instance().GetChannels().get(mChannelNum).GetStreams().getmDescription();//GetStream(streamNum);
			String descripiton = Channels.instance().GetChannels().get(mChannelNum).getmName();
		
		
		mTextViewDetails.setText(descripiton);
		
		mChannelImage = (ImageView)findViewById(R.id.channelimage);
		
		Bitmap  bmpCurrent = BitmapFactory.decodeResource(getResources(),R.drawable.android_hdpi);
		 
		mChannelImage.setImageBitmap(bmpCurrent);
		 
		int NumberOfStreams = Channels.instance().GetChannels().get(mChannelNum).GetStreams().getmStreams().size();
		int NumberOfStreamsOS = 0;
		for (int count =0;count<NumberOfStreams-1;count++)
		{
			if(Channels.instance().GetChannels().get(mChannelNum).GetStreams().GetStream(count).getmOS().equals("android") )
			{
				NumberOfStreamsOS++;
			}
		}
		
		for (int count =0;count<=NumberOfStreamsOS-1;count++)
		{
			if(Channels.instance().GetChannels().get(mChannelNum).GetStreams().GetStream(count).getmOS().equals("android") )
			{
				StreamInfo info1 = Channels.instance().GetChannels().get(mChannelNum).GetStreams().GetStream(count);
		
		
				mStreamName[count].setText(info1.getmQaulity() +" " +"quality"+" " +info1.getmType()+" "+"stream" );
			}
		}
		
		Calendar rightNow = Calendar.getInstance();
		int day = rightNow.get(Calendar.DAY_OF_WEEK);
		int currenthour = rightNow.get(Calendar.HOUR_OF_DAY) ;
		int currentmin = rightNow.get(Calendar.MINUTE) ;
		List<EventData> daySchedule  = Channels.instance().GetChannels().get(mChannelNum).getmScheduleData().getmData().get(Day.values()[day]).getmDaySchedule();
		Iterator<EventData> it = daySchedule.iterator();
		SimpleDateFormat curFormater = new SimpleDateFormat("HH:mm"); 
		

		String hourofday = it.next().getmTime();
		String rightnow = rightNow.getTime().toString();
		
		java.util.Date dateObj =  curFormater.parse(hourofday);
		java.util.Date dateObjCurrent =  curFormater.parse(String.format("%d:%d",currenthour,currentmin));
		//int hoursdate = dateObj.getHours();
		while(dateObj.before(dateObjCurrent)) 
		{
			
			dateObj =  curFormater.parse(it.next().getmTime());
			
		}
		
		mTitleofCurrentProgram = it.next().getmTitle();
		
		/*for (int count =3;count>NumberOfStreamsOS-1;count--)
		{
			//mStreamRows[count].setWillNotDraw(true);//.setColumnCollapsed(count,true);
			mStreamRows[count].setVisibility(View.INVISIBLE);//.setColumnCollapsed(count,true);
			//mStreamTable.setColumnCollapsed(count,true);
			//mStreamTable.requestLayout();
			//mStreamRows[count].requestLayout();
			mStreamTable.requestLayout();
		
		}
		*/
		mStreamRow1.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Log.d(TAG, "select a stream 1");
				
				try {
					StreamInfo info1 = Channels.instance().GetChannels().get(mChannelNum).GetStreams().GetStream(0);
					
					
					
					//String programtitle = 
					if(info1.getmFormat().equals("wmv"))
					{
					Intent i = new Intent(getBaseContext(), FFMpegPlayerActivity.class);
					String url;
					if(info1.getmURL().contains("http"))
						url=	getPage(info1.getmURL());
					else
						url = info1.getmURL();
					i.putExtra(getResources().getString(R.string.input_stream), url);
					startActivity(i);
					}
					else
					{
						String url;
						 Intent intent =
			                    new Intent(getBaseContext(),
			                    		MediaPlayer_Android.class);
			            intent.putExtra(MEDIA, STREAM_VIDEO);
			            
			            url = info1.getmURL();
			            
			            intent.putExtra(getResources().getString(R.string.input_stream), url);
			            intent.putExtra(getResources().getString(R.string.programtitle),mTitleofCurrentProgram);
			            startActivity(intent);
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
			}});
		
		mStreamRow2.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Log.d(TAG, "select a stream");
				try {
					StreamInfo info1 = Channels.instance().GetChannels().get(mChannelNum).GetStreams().GetStream(1);
					
					if(info1.getmFormat().equals("wmv"))
					{
					Intent i = new Intent(getBaseContext(), FFMpegPlayerActivity.class);
					String url;
					if(info1.getmURL().contains("http"))
						url=	getPage(info1.getmURL());
					else
						url = info1.getmURL();
					i.putExtra(getResources().getString(R.string.input_stream), url);
					startActivity(i);
					}
					else
					{
						String url;
						 Intent intent =
			                    new Intent(getBaseContext(),
			                    		MediaPlayerAudio_Android.class);
			            intent.putExtra(MEDIA, LOCAL_AUDIO);
			            
			            url = info1.getmURL();
			            
			            intent.putExtra(getResources().getString(R.string.input_stream), url);
			            intent.putExtra(getResources().getString(R.string.programtitle),mTitleofCurrentProgram);
			            intent.putExtra(getResources().getString(R.string.description),mTextViewDetails.getText());
			            startActivity(intent);
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
			}});
		/*
		mStreamRow3.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Log.d(TAG, "select a stream");
				try {
					StreamInfo info1 = Channels.instance().GetChannels().get(mChannelNum).GetStreams().GetStream(2);
					
					if(info1.getmFormat().equals("wmv"))
					{
					Intent i = new Intent(getBaseContext(), FFMpegPlayerActivity.class);
					String url;
					if(info1.getmURL().contains("http"))
						url=	getPage(info1.getmURL());
					else
						url = info1.getmURL();
					i.putExtra(getResources().getString(R.string.input_stream), url);
					startActivity(i);
					}
					else
					{
						String url;
						 Intent intent =
			                    new Intent(getBaseContext(),
			                    		MediaPlayer_Android.class);
			            intent.putExtra(MEDIA, STREAM_VIDEO);
			            
			            url = info1.getmURL();
			            
			            intent.putExtra(getResources().getString(R.string.input_stream), url);
			            
			            startActivity(intent);
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
			}});
		
		mStreamRow4.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Log.d(TAG, "select a stream");
				try {
					StreamInfo info1 = Channels.instance().GetChannels().get(mChannelNum).GetStreams().GetStream(3);
					
					if(info1.getmFormat().equals("wmv"))
					{
					Intent i = new Intent(getBaseContext(), FFMpegPlayerActivity.class);
					String url;
					if(info1.getmURL().contains("http"))
						url=	getPage(info1.getmURL());
					else
						url = info1.getmURL();
					i.putExtra(getResources().getString(R.string.input_stream), url);
					startActivity(i);
					}
					else
					{
						String url;
						 Intent intent =
			                    new Intent(getBaseContext(),
			                    		MediaPlayer_Android.class);
			            intent.putExtra(MEDIA, STREAM_VIDEO);
			            
			            url = info1.getmURL();
			            
			            intent.putExtra(getResources().getString(R.string.input_stream), url);
			            
			            startActivity(intent);
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
			}});
		*/
	/*	
		mStreamName.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Log.d(TAG, "select a stream");
			}
			
			
			
		
		});
	*/	
		
		mSchedule.setOnClickListener(new Button.OnClickListener(){
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Log.d(TAG, "select schedule");
				Intent i = getIntent();
				int ChannelNum =i.getIntExtra(getResources().getString(R.string.input_stream), 0);
				Intent i2 = new Intent(StreamInfoDetails.this,ScheduleView.class);
				i2.putExtra(getResources().getString(R.string.input_stream),  ChannelNum);
				startActivity(i2);
			}});
			
		
		
		//mStreamTable.setOnClickListener(new TableRow.OnClickListener l)
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private String getPage(String url) {
    	String str = "***";

        try
    	{
    		HttpClient hc = new DefaultHttpClient();
    		HttpPost post = new HttpPost(url);

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
	
}
