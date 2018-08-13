package com.example.pc_304.testemoji;

import android.app.Application;
import android.content.Context;

/**
 * Created by KangJH on 2018/8/7.
 * The harder you work, the luckier you will be.
 */

public class ShowSelfApp extends Application {
    static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
    }

    public static Context getContext() {
        return context;
    }
}
