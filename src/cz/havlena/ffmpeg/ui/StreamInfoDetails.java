package cz.havlena.ffmpeg.ui;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.xml.sax.SAXException;

import kab.tv.connection.Channels;
import kab.tv.connection.StreamInfo;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class StreamInfoDetails extends Activity {

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
	String mInfo;
	int mChannelNum;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.streamdetail);
		mTextViewDetails = (TextView) findViewById(R.id.detail_text);
		mStreamName1 = (TextView) findViewById(R.id.streamrow1);
		mStreamName2 = (TextView) findViewById(R.id.streamrow2);
		mStreamName3 = (TextView) findViewById(R.id.streamrow3);
		mStreamName4 = (TextView) findViewById(R.id.streamrow4);
		mStreamName = new TextView[4];
		mStreamName[0] = mStreamName1;
		mStreamName[1] = mStreamName2;
		mStreamName[2] = mStreamName3;
		mStreamRows = new TableRow[4];
		mStreamRow1 = (TableRow)findViewById(R.id.row1);
		mStreamRow2 = (TableRow)findViewById(R.id.row2);
		mStreamRow3 = (TableRow)findViewById(R.id.row4);
		mStreamRow4 = (TableRow)findViewById(R.id.row4);
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
			String descripiton = Channels.instance().GetChannels().get(mChannelNum).GetStreams().getmDescription();
		
		
		mTextViewDetails.setText(descripiton);
		
		int NumberOfStreams = Channels.instance().GetChannels().get(mChannelNum).GetStreams().getmStreams().size();
		
		for (int count =0;count<NumberOfStreams;count++)
		{
		StreamInfo info1 = Channels.instance().GetChannels().get(mChannelNum).GetStreams().GetStream(count);
		
		
		mStreamName[count].setText(info1.getmQaulity() +" " +"quality"+" " +info1.getmType()+" "+"stream" );
		
		}
		
		for (int count =3;count>NumberOfStreams-1;count--)
		{
			mStreamTable.setColumnCollapsed(count,true);
		
		}
		mStreamRow1.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Log.d(TAG, "select a stream 1");
				
				try {
					StreamInfo info1 = Channels.instance().GetChannels().get(mChannelNum).GetStreams().GetStream(0);
					
					
					Intent i = new Intent(getBaseContext(), FFMpegPlayerActivity.class);
					String url;
					if(info1.getmURL().contains("http"))
						url=	getPage(info1.getmURL());
					else
						url = info1.getmURL();
					i.putExtra(getResources().getString(R.string.input_stream), url);
					startActivity(i);
					
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
					StreamInfo info1 = Channels.instance().GetChannels().get(mChannelNum).GetStreams().GetStream(0);
					
					
					Intent i = new Intent(getBaseContext(), FFMpegPlayerActivity.class);
					String url;
					if(info1.getmURL().contains("http"))
						url=	getPage(info1.getmURL());
					else
						url = info1.getmURL();
					i.putExtra(getResources().getString(R.string.input_stream), url);
					startActivity(i);
					
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
		
		mStreamRow3.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Log.d(TAG, "select a stream");
				try {
					StreamInfo info1 = Channels.instance().GetChannels().get(mChannelNum).GetStreams().GetStream(0);
					
					
					Intent i = new Intent(getBaseContext(), FFMpegPlayerActivity.class);
					String url;
					if(info1.getmURL().contains("http"))
						url=	getPage(info1.getmURL());
					else
						url = info1.getmURL();
					i.putExtra(getResources().getString(R.string.input_stream), url);
					startActivity(i);
					
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
					StreamInfo info1 = Channels.instance().GetChannels().get(mChannelNum).GetStreams().GetStream(0);
					
					
					Intent i = new Intent(getBaseContext(), FFMpegPlayerActivity.class);
					String url;
					if(info1.getmURL().contains("http"))
						url=	getPage(info1.getmURL());
					else
						url = info1.getmURL();
					i.putExtra(getResources().getString(R.string.input_stream), url);
					startActivity(i);
					
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
