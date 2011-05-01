package cz.havlena.ffmpeg.ui;

import android.graphics.Bitmap;



enum StreamType
{
	TV,
	RADIO,
	VOD
};

public class StreamInfo {
		String mURL;
		Bitmap mIcon;
		String mStreamName;
		StreamType mType;
}
