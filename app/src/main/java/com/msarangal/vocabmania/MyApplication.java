package com.msarangal.vocabmania;

import android.app.Application;
import android.content.Context;

import androidx.multidex.MultiDex;

import com.google.firebase.crashlytics.FirebaseCrashlytics;

//import com.google.firebase.crashlytics.FirebaseCrashlytics;

// import com.crashlytics.android.Crashlytics;
//import com.firebase.client.Firebase;

// import io.fabric.sdk.android.Fabric;

/**
 * Created by Mandeep on 16/6/2015.
 */
public class MyApplication extends Application {

    public static MyApplication myApplicationInstance;

    @Override
    public void onCreate() {
        super.onCreate();

        myApplicationInstance = this;
        FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(true);
    }

    public static MyApplication getInstance() {
        return myApplicationInstance;
    }

    public static Context getAppContext() {
        return myApplicationInstance.getApplicationContext();
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }
}
