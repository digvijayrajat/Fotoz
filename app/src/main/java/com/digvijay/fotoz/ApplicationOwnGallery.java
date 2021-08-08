package com.digvijay.fotoz;

import android.app.Application;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Point;
import android.os.Handler;
import android.view.Display;
import android.view.WindowManager;

import io.github.inflationx.calligraphy3.CalligraphyConfig;
import io.github.inflationx.calligraphy3.CalligraphyInterceptor;
import io.github.inflationx.viewpump.ViewPump;


public class ApplicationOwnGallery extends Application {

    public static Context applicationContext=null;
    public static volatile Handler applicationHandler = null;
    public static Point displaySize = new Point();
    public static float density = 1;


    @Override
    public void onCreate() {
        super.onCreate();
        applicationContext = getApplicationContext();
        applicationHandler = new Handler(applicationContext.getMainLooper());
        checkDisplaySize();
        density = ApplicationOwnGallery.applicationContext.getResources().getDisplayMetrics().density;

        ViewPump.init(ViewPump.builder()
                .addInterceptor(new CalligraphyInterceptor(
                        new CalligraphyConfig.Builder()
                                .setDefaultFontPath("fonts/qsem.ttf")
                                .setFontAttrId(R.attr.fontPath)
                                .build()))
                .build());
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    public static int dp(float value) {
        return (int) Math.ceil(density * value);
    }

    public static void checkDisplaySize() {
        try {
            WindowManager manager = (WindowManager)ApplicationOwnGallery.applicationContext.getSystemService(Context.WINDOW_SERVICE);
            if (manager != null) {
                Display display = manager.getDefaultDisplay();
                if (display != null) {
                    if(android.os.Build.VERSION.SDK_INT < 13) {
                        displaySize.set(display.getWidth(), display.getHeight());
                    } else {
                        display.getSize(displaySize);
                    }
                }
            }
        } catch (Exception e) {
        }
    }


}
