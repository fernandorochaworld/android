package br.com.inteligenti.lavoutanovov2;

import android.app.Application;

import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;

/**
 * Created by fernando on 15/12/17.
 */

public class LoginFacebookApp extends Application {

    // Location updates intervals
    public static int UPDATE_INTERVAL = 3000; // 3 sec
    public static int FATEST_INTERVAL = 3000; // 5 sec
    public static int DISPLACEMENT = 10; // 10 meters


    @Override
    public void onCreate() {
        super.onCreate();
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);
    }
}
