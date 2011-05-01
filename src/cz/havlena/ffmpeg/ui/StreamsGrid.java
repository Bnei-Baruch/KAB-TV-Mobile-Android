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

import java.util.ArrayList;
import java.util.List;

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
				Log.d(TAG, "Not specified video file");
				
			}   
        	})); 
        
        
       
        
    
    }

    

    private List<ResolveInfo> mApps;
    private List<StreamInfo> mStreams;

    @SuppressWarnings("rawtypes")
	private void loadApps() {
        Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);

        mApps = getPackageManager().queryIntentActivities(mainIntent, 0);
        
        //loading streams from xml received from server, currently will load it hard coded
        StreamInfo info = new StreamInfo();
        info.mIcon = BitmapFactory.decodeResource(getResources(),R.drawable.android_hdpi);
        info.mStreamName = "ערוץ 66 - איכות גבוהה";
        info.mType = StreamType.TV;
        info.mURL = "http://switch3.castup.net/cunet/gm.asp?ClipMediaID=160788";
        
     
        mStreams = new ArrayList<StreamInfo>();
        
        mStreams.add(info);
        
        
        
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

			    tv.setText(mStreams.get(position).mStreamName);  

			    iv = (ImageView)v.findViewById(R.id.icon_image); 
               // i = new ImageView(StreamsGrid.this);
               // i.setScaleType(ImageView.ScaleType.FIT_CENTER);
               // i.setLayoutParams(new GridView.LayoutParams(50, 50));
			    ResolveInfo info = mApps.get(position);
	            iv.setImageDrawable(info.activityInfo.loadIcon(getPackageManager()));
	            iv.setImageBitmap(mStreams.get(position).mIcon);
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
