package kab.tv.connection;
import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

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
		 try {
			CommunicationManager.updateConnectivityState();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} // Notify connection manager }  }
	 }
 
 
 };
 
 
