/*
 * Copyright (C) 2007 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cz.havlena.ffmpeg.ui;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import kab.tv.connection.ChannelInfo;
import kab.tv.connection.StreamInfo;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import cz.havlena.ffmpeg.ui.R;

//Need the following import to get access to the app resources, since this
//class is in a sub-package.



public class StreamsGrid extends Activity {
	private static final String 	TAG = "StreamGrid"; 
    GridView mGrid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        loadApps(); // do this in onresume?

        setContentView(R.layout.grid_1);
        mGrid = (GridView) findViewById(R.id.myGrid);
        mGrid.setAdapter(new AppsAdapter());
        
        mGrid.setOnItemClickListener((new OnItemClickListener() {   
        	

			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				Log.d(TAG, "select a stream");
				Intent i = new Intent(StreamsGrid.this,StreamInfoDetails.class);
				i.putExtra(getResources().getString(R.string.input_stream),  mStreams.get(arg2));
				startActivity(i);
				
			}   
        	})); 
        
        
       
        
    
    }

    

    private List<ResolveInfo> mApps;
    private List<StreamInfo> mStreams;
    private List<ChannelInfo> mChannels;

    @SuppressWarnings("rawtypes")
	private void loadApps() {
        Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);

        mApps = getPackageManager().queryIntentActivities(mainIntent, 0);
        
        //loading channels from xml received from server, currently will load it hard coded
        StreamInfo info = new StreamInfo(null);
      //  info.mIcon = BitmapFactory.decodeResource(getResources(),R.drawable.android_hdpi);
        info.setmStreamName("ערוץ 66 - איכות גבוהה");
       // info.mType = StreamType.TV;
     //   info.mURL = "http://switch3.castup.net/cunet/gm.asp?ClipMediaID=160788";
        
        StreamInfo info1 = new StreamInfo(null);
     //   info1.mIcon = BitmapFactory.decodeResource(getResources(),R.drawable.android_hdpi);
        info1.setmStreamName("ערוץ 66 - איכות בינונית");
      //  info1.mType = StreamType.TV;
    //    info1.mURL = "mms://vod.kab.tv/heb_medium";
        
        mStreams = new ArrayList<StreamInfo>();
        
        mStreams.add(info);
        mStreams.add(info1);
        
        ///////////////////////////////////////////////////////////////////////////////////
        
        /// 1. Get configuration XML from server
        
        /// 2. Get channels node
        
        // Loop
        /// 3. Create channel
        
        /// 4. Create streams
        
        /// 5. Create stream in streams
        
        /// 6. Create singelton that will hold channel info list
        
        //////////////////////////////////////////////////////////////////////////////////
        
      }
        
        
        
    

    public class AppsAdapter extends BaseAdapter {
        public AppsAdapter() {
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            ImageView iv;
            View v;
            if (convertView == null) {
            	LayoutInflater li = getLayoutInflater();
				v = li.inflate(R.layout.icon, null);
				TextView tv = (TextView)v.findViewById(R.id.icon_text);  

			    tv.setText(mStreams.get(position).getmStreamName());  

			    iv = (ImageView)v.findViewById(R.id.icon_image); 
               // i = new ImageView(StreamsGrid.this);
               // i.setScaleType(ImageView.ScaleType.FIT_CENTER);
               // i.setLayoutParams(new GridView.LayoutParams(50, 50));
			    ResolveInfo info = mApps.get(position);
	            iv.setImageDrawable(info.activityInfo.loadIcon(getPackageManager()));
	  //          iv.setImageBitmap(mStreams.get(position).mIcon);
            } else {
                v = (View) convertView;
            }

            

            return v;
        }


        public final int getCount() {
            return mStreams.size();
        }

        public final Object getItem(int position) {
            return mStreams.get(position);
        }

        public final long getItemId(int position) {
            return position;
        }
    }
    
    
   

}
