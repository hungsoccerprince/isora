package com.isorasoft.phusan.utils;

import android.content.Context;
import android.provider.Settings;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;

/**
 * Created by MaiNam on 7/6/2016.
 */
public class ApplicationUtils {

    private static final String TAG = ApplicationUtils.class.getSimpleName();

    public static String getDeviceId(Context context) {
        try {
            return Settings.Secure.getString(context.getContentResolver(),
                    Settings.Secure.ANDROID_ID);
        } catch (Exception e) {
            return "";
        }
    }

    public static String getUUID() {
        try {
            String UUID = FirebaseInstanceId.getInstance().getToken();
            if (UUID == null)
                UUID = "";
            Log.d(TAG, "UUID: " + UUID);
            return UUID;
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }
}
