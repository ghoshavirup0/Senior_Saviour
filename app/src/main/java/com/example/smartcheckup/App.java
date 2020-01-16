package com.example.smartcheckup;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

public class App extends Application {

    public static final String channelid="LOCATION SERVICE";

    @Override
    public void onCreate() {
        super.onCreate();
        createnotification();
    }

    private void createnotification() {

        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O)
        {
            NotificationChannel servicechannel=new NotificationChannel(channelid,"LOCATION SERVICE CHANNEL", NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager manager=getSystemService(NotificationManager.class);
            manager.createNotificationChannel(servicechannel);

        }
    }
}
