package com.kab.channel66;

import android.app.Application;
import org.acra.*;
import org.acra.annotation.*;


@ReportsCrashes(
        formKey = "", // This is required for backward compatibility but not used
        formUri = "http://www.backendofyourchoice.com/reportpath"
    )

public class MyApplication extends Application {

	@Override
    public void onCreate() {
        super.onCreate();

        // The following line triggers the initialization of ACRA
        //ACRA.init(this);
    }
}
