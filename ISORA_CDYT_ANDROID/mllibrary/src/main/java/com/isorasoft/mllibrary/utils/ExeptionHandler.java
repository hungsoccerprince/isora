package com.isorasoft.mllibrary.utils;

import android.app.Activity;
import android.os.Build;
import android.util.Log;

import java.util.Date;

/**
 * Created by MaiNam on 8/19/2016.
 */

public class ExeptionHandler implements Thread.UncaughtExceptionHandler {

    private static final String TAG = ExeptionHandler.class.getSimpleName();
    private Thread.UncaughtExceptionHandler defaultUEH;

    private Activity app = null;

    public ExeptionHandler(Activity app) {
        this.defaultUEH = Thread.getDefaultUncaughtExceptionHandler();
        this.app = app;
    }

    public void uncaughtException(Thread t, Throwable e) {

        if(true){
            return;
        }
        StackTraceElement[] arr = e.getStackTrace();
        String report = e.toString() + "\n\n";
        report += "--------- Stack trace ---------\n\n";
        report += "-------------------------------\n\n";
        report += "--------- Device ---------\n\n";
        report += "Brand: " + Build.BRAND + "\n";
        report += "Device: " + Build.DEVICE + "\n";
        report += "Model: " + Build.MODEL + "\n";
        report += "Id: " + Build.ID + "\n";
        report += "Product: " + Build.PRODUCT + "\n";
        report += "-------------------------------\n\n";
        report += "--------- Firmware ---------\n\n";
        report += "SDK: " + Build.VERSION.SDK + "\n";
        report += "Release: " + Build.VERSION.RELEASE + "\n";
        report += "Incremental: " + Build.VERSION.INCREMENTAL + "\n";
        report += "-------------------------------\n\n";
        for (int i = 0; i < arr.length; i++) {
            report += "    " + arr[i].toString() + "\n";
        }
        report += "-------------------------------\n\n";

// If the exception was thrown in a background thread inside
// AsyncTask, then the actual exception can be found with getCause
        report += "--------- Cause ---------\n\n";
        Throwable cause = e.getCause();
        if (cause != null) {
            report += cause.toString() + "\n\n";
            arr = cause.getStackTrace();
            for (int i = 0; i < arr.length; i++) {
                report += "    " + arr[i].toString() + "\n";
            }
        }
        report += "-------------------------------\n\n";
        Log.d(TAG, "uncaughtException: " + report);

        FileInfo.writeFile("logcdyt" + ConvertUtils.toDateString(new Date().getTime(),"yyyy-MM-dd HH:mm:ss") + ".log", report.getBytes());
//
//            try {
//                FileOutputStream trace = app.openFileOutput(
//                        "stack.trace", Context.MODE_PRIVATE);
//                trace.write(report.getBytes());
//                trace.close();
//            } catch(IOException ioe) {
//// ...
//            }

        defaultUEH.uncaughtException(t, e);
    }
}
