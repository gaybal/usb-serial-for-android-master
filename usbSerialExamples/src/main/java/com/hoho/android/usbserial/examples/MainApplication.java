package com.hoho.android.usbserial.examples;

import android.app.Application;
import android.content.Context;

import com.android.usbport.USBDevice;

import java.util.List;

/**
 * Created by chenyuye on 17/12/18.
 */

public class MainApplication extends Application {

    private static Context context = null;
    public List<USBDevice> mEntries = null;
    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();

        AppException.getInstance().init(context);
    }

    public static Context getContextObject(){
        return context;
    }
}
