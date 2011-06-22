package kab.tv.connection;

import kab.tv.ui.FFMpegPlayerActivity;
import kab.tv.ui.MainKabTv;
import kab.tv.ui.MediaPlayer_Android;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

public class CommunicationManager {

	private static boolean _isConnected;
	public static Context mCurrentContext;
		
	
	
	public static void updateConnectivityState() {
		// TODO Auto-generated method stub
		 boolean isConnected = false;  
		 ConnectivityManager _connec = (ConnectivityManager) mCurrentContext.getSystemService(Context.CONNECTIVITY_SERVICE);
		 
		 
		if (_connec != null && (_connec.getNetworkInfo(1).getState() == NetworkInfo.State.CONNECTED) ||(_connec.getNetworkInfo(0).getState() == NetworkInfo.State.CONNECTED)){   
			 isConnected = true;        
			 Log.i(CommunicationManager.class.getName(), "Device is connected to the network. Online mode is available.");
			//notify player - if is playing then start playing
				//FFMpegPlayerActivity.checkCommunicationState(true);
			 MediaPlayer_Android.checkCommunicationState(true);
			 }
		 else {//if (_connec.getNetworkInfo(0).getState() == NetworkInfo.State.DISCONNECTED ||  _connec.getNetworkInfo(1).getState() == NetworkInfo.State.DISCONNECTED ) { 
			 isConnected = false;      
			 Log.w(CommunicationManager.class.getName(), "Device is NOT connected to the network. Offline mode.");   
			 //notify the player the there is no connection / preform suspend
			// FFMpegPlayerActivity.checkCommunicationState(false);
			 MediaPlayer_Android.checkCommunicationState(false);
			 }
		
		 set_isConnected(isConnected);
		 }

	public static void set_isConnected(boolean _isConnected) {
		CommunicationManager._isConnected = _isConnected;
	}

	public static boolean is_isConnected() {
		return _isConnected;
	} 
	};
