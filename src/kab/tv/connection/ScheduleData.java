package kab.tv.connection;

import java.net.URL;
import java.util.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;




enum Tags
{
 Description,
 Title,
 Time
}
public class ScheduleData {
	
	public enum Day
	{
	 Sunday,
	 Monday,
	 Tuesday,
	 Wednesday,
	 Thursday,
	 Friday,
	 Saturday
	}
	
	URL mScheduleURL;//http://kab.tv/vod/api/schedule/Hebrew
	Day mCurrentday;
	EventData mCurrentEventdata;
	Tags mFlag;
	private SortedMap<Day,DayData> mData;
	String mCurrentDate;
	boolean mLoaded;
	
	
	public ScheduleData(){
		mCurrentEventdata =  new EventData();
		setmData(new TreeMap<Day,DayData>());
	}
	
	public void setDayData(String data){
		
		switch (mFlag)
		{
		case Description:
			mCurrentEventdata.setmDescription(data);
			break;
		case Title:
			mCurrentEventdata.setmTitle(data);
			break;
		case Time:
			mCurrentEventdata.setmTime(data);
			break;
			
			
		}
	}
	
	public void SetFlag(Tags flag){
		
		mFlag = flag;
	}

	public void SetEvent() {
		// TODO Auto-generated method stub
		EventData data = new EventData(mCurrentEventdata);
		//mCurrentEventdata;
		if(getmData().get(mCurrentday) !=null)
		{
			getmData().get(mCurrentday).getmDaySchedule().add(data);
			
		}
		else
		{
			DayData daydata = new DayData();
			daydata.setmDaySchedule(new ArrayList<EventData>());
			daydata.getmDaySchedule().add(data);
			getmData().put(mCurrentday, daydata);
		}
	}


	public void setMonth(String string) {
		// TODO Auto-generated method stub
		string = string.trim();
		if(mCurrentDate!=null)
		mCurrentDate = (mCurrentDate + string + "/");
		else
			mCurrentDate = (string + "/");
	}

	public void setYear(String string) {
		// TODO Auto-generated method stub
		string = string.trim();
		if(mCurrentDate!=null)
		mCurrentDate = (mCurrentDate + string );
		else
			mCurrentDate = (string + "/");
	}

	public void setDayInMonth(String string) {
		// TODO Auto-generated method stub
		string = string.trim();
		if(mCurrentDate!=null)
		mCurrentDate = (mCurrentDate + string + "/");
		else
			mCurrentDate = (string + "/");
	}

	public void buildDate() throws ParseException {
		// TODO Auto-generated method stub
		SimpleDateFormat curFormater = new SimpleDateFormat("dd/MM/yyyy"); 
		Date dateObj = curFormater.parse(mCurrentDate); 
		getmData().get(mCurrentday).setmDate(dateObj);
		mCurrentDate = null;
	}

	public void setmData(SortedMap<Day,DayData> mData) {
		this.mData = mData;
		
	}

	public Map<Day,DayData> getmData() {
		return mData;
	
	}
	
	
	 private static final Comparator<Day> sComparator = new Comparator() {
	       	public int compare(Day arg0, Day arg1) {
				// TODO Auto-generated method stub
	       		if(arg0.ordinal()>arg1.ordinal())
	       			return 1;
	       			else
	       			return 0;
			}

			@Override
			public int compare(Object object1, Object object2) {
				// TODO Auto-generated method stub
				return 0;
			}
	 };
}

