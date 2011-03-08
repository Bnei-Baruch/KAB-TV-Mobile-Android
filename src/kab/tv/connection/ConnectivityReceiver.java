package kab.tv.connection;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;


 public class ConnectivityReceiver extends BroadcastReceiver {
	 @Override 
	 public void onReceive(Context context, Intent intent) { 
		 Log.i(getClass().getName(), "A change in network connectivity has occurred. Notifying communication manager for further action.");   
		 NetworkInfo info = intent.getParcelableExtra(ConnectivityManager.EXTRA_NETWORK_INFO); 
		 if(info != null) { 
			 Log.v(getClass().getName(), "Reported connectivity status is " + info.getState() + ".");   
			 }    
		 CommunicationManager.updateConnectivityState(); // Notify connection manager }  }
	 }
 
 
 };
 
 
