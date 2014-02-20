package com.kab.channel66.utils;

import java.util.List;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.Uri;

import com.kab.channel66.R;
import com.kab.channel66.StreamListActivity;

public class CommonUtils {

	public static void RemoveOldPlugin(final Context ct)
	{
		 boolean isInstalled = false;
		    List<ApplicationInfo> packages;
	        PackageManager pm;
	            pm = ct.getPackageManager();        
	            packages = pm.getInstalledApplications(0);
	            for (ApplicationInfo packageInfo : packages) {
	        if(packageInfo.packageName.equals("io.vov.vitamio")) isInstalled =  true;
	        }        
	            
	   

		   if(isInstalled)
		   {
		    AlertDialog chooseToInstall = new AlertDialog.Builder(ct).create();
			chooseToInstall.setTitle("Remove old plug-in");
			chooseToInstall.setMessage("Plugin was installed previously with this app, it is recommneded to uninstall it this plugin in case you do not use it by other app");
			
			chooseToInstall.setButton("Ok", new DialogInterface.OnClickListener() {
			   public void onClick(DialogInterface dialog, int which) {
			      // here you can add functions
				   Uri packageURI = Uri.parse("package:io.vov.vitamio");
				    Intent uninstallIntent = new Intent(Intent.ACTION_DELETE, packageURI);
				    ct.startActivity(uninstallIntent);
				    
				   
				 
		    		
			   }
			});
			chooseToInstall.setButton2("Cancel", new DialogInterface.OnClickListener() {
			   public void onClick(DialogInterface dialog, int which) {
			      // here you can add functions
				  
			   }
			});
			chooseToInstall.setIcon(R.drawable.icon);
			chooseToInstall.show();
			
		   }
	}
}
