package kab.tv.connection;

import java.util.List;

import android.graphics.Bitmap;

public class Streams {
  List <StreamInfo> mStreams;
  Bitmap mImage;
  String mDescription;
  
  public StreamInfo GetStream(int i)
  {
	  return mStreams.get(i);
  }
}
