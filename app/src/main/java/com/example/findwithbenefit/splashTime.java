package com.example.proj2;

import android.app.Application;
import android.os.SystemClock;

public class splashTime extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        SystemClock.sleep(800);
    }
}
