package kab.tv.ui;


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



import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;



import kab.tv.connection.ChannelInfo;
import kab.tv.connection.Channels;
import kab.tv.connection.ScheduleData.Day;
import android.app.ExpandableListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.ContextMenu;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ContextMenu.ContextMenuInfo;
import android.webkit.WebView;
import android.widget.AbsListView;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ExpandableListView.ExpandableListContextMenuInfo;

import kab.tv.connection.*;


/**
 * Demonstrates expandable lists using a custom {@link ExpandableListAdapter}
 * from {@link BaseExpandableListAdapter}.
 */

public class ScheduleView extends ExpandableListActivity {

    ExpandableListAdapter mAdapter;
    int mChannelNumber;
    
    ChannelInfo mInfo;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent i = getIntent();
        mChannelNumber =i.getIntExtra(getResources().getString(R.string.input_stream), 0);
        
        try {
			mInfo = Channels.instance().GetChannels().get(mChannelNumber);
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
    
        // Set up our adapter
        mAdapter = new MyExpandableListAdapter();
        setListAdapter(mAdapter);
        registerForContextMenu(getExpandableListView());
        
       
        
    }      

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
        menu.setHeaderTitle("Weekly schedule");
        menu.add(0, 0, 0, R.string.expandable_list_sample_action);
    }
    
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        ExpandableListContextMenuInfo info = (ExpandableListContextMenuInfo) item.getMenuInfo();

        String title = ((TextView) info.targetView).getText().toString();
        
        int type = ExpandableListView.getPackedPositionType(info.packedPosition);
        if (type == ExpandableListView.PACKED_POSITION_TYPE_CHILD) {
            int groupPos = ExpandableListView.getPackedPositionGroup(info.packedPosition); 
            int childPos = ExpandableListView.getPackedPositionChild(info.packedPosition); 
            Toast.makeText(this, title + ": Child " + childPos + " clicked in group " + groupPos,
                    Toast.LENGTH_SHORT).show();
            return true;
        } else if (type == ExpandableListView.PACKED_POSITION_TYPE_GROUP) {
            int groupPos = ExpandableListView.getPackedPositionGroup(info.packedPosition); 
            Toast.makeText(this, title + ": Group " + groupPos + " clicked", Toast.LENGTH_SHORT).show();
            return true;
        }
        
        return false;
    }

    /**
     * A simple adapter which maintains an ArrayList of photo resource Ids. 
     * Each photo is displayed as an image. This adapter supports clearing the
     * list of photos and adding a new photo.
     *
     */
    public class MyExpandableListAdapter extends BaseExpandableListAdapter {
        // Sample data set.  children[i] contains the children (String[]) for groups[i].
       // private String[] groups = { "People Names", "Dog Names", "Cat Names", "Fish Names" };
      /*  private String[][] children = {
                { "Arnold", "Barry", "Chuck", "David" },
                { "Ace", "Bandit", "Cha-Cha", "Deuce" },
                { "Fluffy", "Snuggles" },
                { "Goldy", "Bubbles" }
        };
        */
        public Object getChild(int groupPosition, int childPosition) {
            //return children[groupPosition][childPosition];
        	 DayData daily = mInfo.getmScheduleData().getmData().get(Day.values()[groupPosition]);
        	return daily.getmDaySchedule().get(childPosition);
        }

        public long getChildId(int groupPosition, int childPosition) {
            return childPosition;
        }

        public int getChildrenCount(int groupPosition) {
            //return children[groupPosition].length;
        	DayData daily = mInfo.getmScheduleData().getmData().get(Day.values()[groupPosition]);
            return daily.getmDaySchedule().size();
        }

        public TextView getGenericView() {
            // Layout parameters for the ExpandableListView
            AbsListView.LayoutParams lp = new AbsListView.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, 96);

            TextView textView = new TextView(ScheduleView.this);
            textView.setLayoutParams(lp);
            // Center the text vertically
            textView.setGravity(Gravity.CENTER_VERTICAL | Gravity.LEFT);
            // Set the text starting position
            textView.setPadding(54, 0, 0, 0);
            return textView;
        }
        
        public View getChildView(int groupPosition, int childPosition, boolean isLastChild,
                View convertView, ViewGroup parent) {
            TextView textView = getGenericView();
            
            ///////////////////////////////////////
            AbsListView.LayoutParams lp = new AbsListView.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, 96);
            
            WebView webView = new WebView(ScheduleView.this);
            webView.setLayoutParams(lp);
            // Center the text vertically
           // ((TextView) webView).setGravity(Gravity.CENTER_VERTICAL | Gravity.LEFT);
            // Set the text starting position
            webView.setPadding(54, 0, 0, 0);
            
            
            /////////////////////////////////////////
            textView.setMovementMethod(LinkMovementMethod.getInstance());
            //textView.setText(getChild(groupPosition, childPosition).toString());
            DayData daily = mInfo.getmScheduleData().getmData().get(Day.values()[groupPosition]);
            DayData daily1;
            if(groupPosition+1<getGroupCount())
             daily1 = mInfo.getmScheduleData().getmData().get(Day.values()[groupPosition+1]);
            else
             daily1 = mInfo.getmScheduleData().getmData().get(Day.values()[0]);	
            
           
            
            if(childPosition+1<getChildrenCount(groupPosition))
            	{
            	   textView.setText( Html.fromHtml(daily.getmDaySchedule().get(childPosition).getmTitle())+ " " + Html.fromHtml(daily.getmDaySchedule().get(childPosition).getmTime())+"-"+ Html.fromHtml(daily.getmDaySchedule().get(childPosition+1).getmTime()) + "\n" + Html.fromHtml(daily.getmDaySchedule().get(childPosition).getmDescription()));
            	}
            else
            	textView.setText( Html.fromHtml(daily.getmDaySchedule().get(childPosition).getmTitle())+ " " + Html.fromHtml(daily.getmDaySchedule().get(childPosition).getmTime())+"-"+ Html.fromHtml(daily1.getmDaySchedule().get(0).getmTime()) + "\n" + Html.fromHtml(daily.getmDaySchedule().get(childPosition).getmDescription()));
            return textView;
        }

        public Object getGroup(int groupPosition) {
            //return groups[groupPosition];
        	//return mInfo.getmScheduleData().getmData().get(Day.values()[groupPosition]);
        	//return mInfo.getmScheduleData().getmData().get(Day.values()[groupPosition]);//Day.values()[groupPosition];
        	
        	//check if it is new day than return a view that has day and date
        	Day dayPosition = (Day) mInfo.getmScheduleData().getmData().keySet().toArray()[groupPosition];
        	
        	return mInfo.getmScheduleData().getmData().get(dayPosition);
        }

        public int getGroupCount() {
            //return mInfo.getmScheduleData().getmData().size();
        	 return mInfo.getmScheduleData().getmData().size();//Day.values().length;
        }

        public long getGroupId(int groupPosition) {
            return groupPosition;
        }

        public View getGroupView(int groupPosition, boolean isExpanded, View convertView,
                ViewGroup parent) {
        	DayData daily = null;
        	//check if it is new day than return a view that has day and date
        	//mInfo.getmScheduleData().
        	Day dayPosition = (Day) mInfo.getmScheduleData().getmData().keySet().toArray()[groupPosition];
        	
        	daily = mInfo.getmScheduleData().getmData().get(dayPosition);
        	
        	
            TextView textView = getGenericView();
            //every event has the name and the hour it begins and ends
         
            //textView.setText(getGroup(groupPosition).toString());
            
            SimpleDateFormat curFormater = new SimpleDateFormat("dd/MM/yyyy");
            String tempDate = curFormater.format(daily.getmDate());
    		
            textView.setText(Day.values()[groupPosition].toString() + " " + tempDate);
            return textView;
        }

        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return true;
        }

        public boolean hasStableIds() {
            return true;
        }

    }
}
