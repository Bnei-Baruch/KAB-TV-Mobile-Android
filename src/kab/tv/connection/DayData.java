package kab.tv.connection;

import java.util.Date;
import java.util.List;

public class DayData {
    private List<EventData> mDaySchedule;
    private Date mDate;
	public void setmDaySchedule(List<EventData> mDaySchedule) {
		this.mDaySchedule = mDaySchedule;
	}
	public List<EventData> getmDaySchedule() {
		return mDaySchedule;
	}
	public void setmDate(Date mDate) {
		this.mDate = mDate;
	}
	public Date getmDate() {
		return mDate;
	}
}
