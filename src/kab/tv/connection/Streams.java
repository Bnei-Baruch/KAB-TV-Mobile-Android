package kab.tv.connection;

import java.util.ArrayList;
import java.util.List;

import android.graphics.Bitmap;

public class Streams {
  private List <StreamInfo> mStreams;
  Bitmap mImage;
  private String mDescription;
  
  public Streams()
  {
	  setmStreams(new ArrayList<StreamInfo>());
  }
  public StreamInfo GetStream(int i)
  {
	  if(getmStreams().get(i)!=null)
		  return getmStreams().get(i);
	  else
	  {
		  StreamInfo info = new StreamInfo();
		  getmStreams().add(info); 
		  return getmStreams().get(i);
	  }
  }
public void setmDescription(String mDescription) {
	this.mDescription = mDescription;
}
public String getmDescription() {
	return mDescription;
}
public void setmStreams(List <StreamInfo> mStreams) {
	this.mStreams = mStreams;
}
public List <StreamInfo> getmStreams() {
	return mStreams;
}
}
