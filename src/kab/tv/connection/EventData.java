package kab.tv.connection;

public class EventData {
	private String mDescription;
    private String mTitle;
    private String mTime;
	public EventData(EventData mCurrentEventdata) {
		// TODO Auto-generated constructor stub
		mDescription = new String(mCurrentEventdata.mDescription);
		mTitle = new String(mCurrentEventdata.mTitle);
		setmTime(new String(mCurrentEventdata.getmTime()));
	}
	public EventData() {
		// TODO Auto-generated constructor stub
	}
	public void setmTitle(String mTitle) {
		this.mTitle = mTitle;
	}
	public String getmTitle() {
		return mTitle;
	}
	public void setmDescription(String mDescription) {
		this.mDescription = mDescription;
	}
	public String getmDescription() {
		return mDescription;
	}
	public void setmTime(String mTime) {
		this.mTime = mTime;
	}
	public String getmTime() {
		return mTime;
	}
}
