package com.kab.channel66.utils;

import android.app.Application;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.widget.Toast;

/**
 * Listener to detect incoming calls. 
 */
public class CallStateListener extends PhoneStateListener {
	
	CallStateInterface m_player;
	
	public CallStateListener(CallStateInterface player)
	{
		m_player = player;
	}
	
 @Override
 public void onCallStateChanged(int state, String incomingNumber) {
     switch (state) {
         case TelephonyManager.CALL_STATE_RINGING:
         // called when someone is ringing to this phone
        	 m_player.Pause();
         
         break;
         case TelephonyManager.CALL_STATE_IDLE:
        	 m_player.Resume();       
         break;
     }
 }
}
