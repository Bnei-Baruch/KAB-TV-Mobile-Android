package cz.havlena.ffmpeg.ui;

import kab.tv.connection.StreamInfo;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class StreamInfoDetails extends Activity {

	private static final String 	TAG = "StreamDetail"; 
	private TextView mTextViewDetails;
	private TextView mStreamName;
	private TableRow mStreamRow;
	private TableLayout mStreamTable;
	StreamInfo mInfo;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.streamdetail);
		mTextViewDetails = (TextView) findViewById(R.id.detail_text);
		mStreamName = (TextView) findViewById(R.id.stream);
		mStreamRow = (TableRow)findViewById(R.id.row1);
		Intent i = getIntent();
		
		mInfo = (StreamInfo) i.getParcelableExtra(getResources().getString(R.string.input_stream));
		
		
		
		mTextViewDetails.setText(mInfo.getmStreamName());
		mStreamName.setText(mInfo.getmStreamName());
		
		mStreamRow.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Log.d(TAG, "select a stream");
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
		
		//mStreamTable.setOnClickListener(new TableRow.OnClickListener l)
		
	}
	
}
