package kab.tv.connection;



import java.util.Map;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;





public class StreamInfo implements Parcelable {
		String mURL;
		Bitmap mIcon;
		private String mStreamName;
		String mFormat;
		String mQaulity;
		String mType;
		public StreamInfo(Parcel in) {
			// TODO Auto-generated constructor stub
			if(in!=null)
				MyParcelable(in);
		}
		public StreamInfo() {
			// TODO Auto-generated constructor stub
		}
		@Override
		public int describeContents() {
			// TODO Auto-generated method stub
			return 0;
		}
		@Override
		public void writeToParcel(Parcel arg0, int arg1) {
			// TODO Auto-generated method stub 
			arg0.writeStringArray(new String[] {this.mURL,  this.getmStreamName()});
			//arg0.writeIntArray(new int[] {this.mType.ordinal(),0});
		}
		
		 public static final Parcelable.Creator<StreamInfo> CREATOR             = new Parcelable.Creator<StreamInfo>() 
		 {        
			 public StreamInfo createFromParcel(Parcel in) 
			 {          
				 return new StreamInfo(in);     
				 }        
			 public StreamInfo[] newArray(int size)
			 {            
				 return new StreamInfo[size];      
				 }   
			 };
			 private void MyParcelable(Parcel in)
			 {        
				
				String[] val = new String[2];	
					in.readStringArray(val);
					
					int[] intval = new int[2];
					in.readIntArray(intval);
				// this.mIcon = intval[1];
				 this.setmStreamName(val[1]);
				 this.mURL = val[0];
				// this.mType = StreamType. (intval[0]);
				 
			 }
			public void setmStreamName(String mStreamName) {
				this.mStreamName = mStreamName;
			}
			public String getmStreamName() {
				return mStreamName;
			} 
}
