package kab.tv.connection;

import java.net.URL;
import java.util.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;


enum Day
{
 Sunday,
 Monday,
 Thuesday,
 Wendasday,
 Thursday,
 Friday,
 Saturday
}

enum Tags
{
 Description,
 Title,
 Time
}
public class ScheduleData {
	URL mScheduleURL;//http://kab.tv/vod/api/schedule/Hebrew
	Day mCurrentday;
	EventData mCurrentEventdata;
	Tags mFlag;
	Map<Day,DayData> mData;
	String mCurrentDate;
	
	public ScheduleData(){
		mCurrentEventdata =  new EventData();
		mData = new HashMap<Day,DayData>();
	}
	
	public void setDayData(String data){
		
		switch (mFlag)
		{
		case Description:
			mCurrentEventdata.mDescription = data;
			break;
		case Title:
			mCurrentEventdata.mTitle = data;
			break;
		case Time:
			mCurrentEventdata.mTime = data;
			break;
			
			
		}
	}
	
	public void SetFlag(Tags flag){
		
		mFlag = flag;
	}

	public void SetEvent() {
		// TODO Auto-generated method stub
		EventData data = new EventData();
		data = mCurrentEventdata;
		if(mData.get(mCurrentday) !=null)
		{
			mData.get(mCurrentday).mDaySchedule.add(data);
			
		}
		else
		{
			DayData daydata = new DayData();
			daydata.mDaySchedule.add(data);
			mData.put(mCurrentday, daydata);
		}
	}


	public void setMonth(String string) {
		// TODO Auto-generated method stub
		mCurrentDate = (mCurrentDate + string + "/");
	}

	public void setYear(String string) {
		// TODO Auto-generated method stub
		mCurrentDate = (mCurrentDate + string );
	}

	public void setDayInMonth(String string) {
		// TODO Auto-generated method stub
		mCurrentDate = (mCurrentDate + string + "/");
	}

	public void buildDate() throws ParseException {
		// TODO Auto-generated method stub
		SimpleDateFormat curFormater = new SimpleDateFormat("dd/MM/yyyy"); 
		Date dateObj = curFormater.parse(mCurrentDate); 
		mData.get(mCurrentday).mDate = dateObj;
	}
}
