package com.isorasoft.phusan;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;
import com.isorasoft.phusan.constants.Constants;
import com.isorasoft.phusan.dataaccess.AnalyticInfo;
import com.isorasoft.phusan.dataaccess.UserInfo;
import com.isorasoft.phusan.messaging.sendbird.SendBirdUtils;
import com.isorasoft.phusan.utils.ApplicationUtils;
import com.isorasoft.mllibrary.Connectivity;
import com.isorasoft.mllibrary.constants.ServerConstants;
import com.isorasoft.mllibrary.utils.SharedPreferencesUtil;
import com.sendbird.android.SendBird;

import io.realm.Realm;

/**
 * Created by MaiNam on 12/23/2016.
 */

public class MainApplication extends Application {
    private static final String TAG = MainApplication.class.getSimpleName();
    private static String UUID = null;

    private static String DeviceId = null;

    public static String getUUID() {
        if (UUID == null) {
            try {
                UUID = FirebaseInstanceId.getInstance().getToken();
                if (UUID == null)
                    return "";
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return UUID;
    }

    public static String getDeviceId() {
        if (DeviceId == null)
            return "";
        return DeviceId;
    }

    public static void setDeviceId(String deviceId) {
        DeviceId = deviceId;
    }


    public static void setUUID(String UUID) {
        MainApplication.UUID = UUID;
    }


    @Override
    public void onCreate() {

        super.onCreate();
        ServerConstants.setServerLink(NetworkConst.SERVICE_URL);
        ServerConstants.setFolderRoot("PHUSAN");
        Realm.init(this);
        SendBird.init(SendBirdUtils.appId, this);
        checkConnect(this);
        UserInfo.getCurrentUserHasSave(this);
        MainApplication.setDeviceId(SharedPreferencesUtil.getString(this, Constants.DEVICE_ID, ""));
        if (MainApplication.DeviceId.isEmpty()) {
            MainApplication.setDeviceId(ApplicationUtils.getDeviceId(this));
        }

        getDefaultTracker();
    }

    void checkConnect(Context mContext) {
        Connectivity.setIsConnected(Connectivity.checkConn(mContext));
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    //
    synchronized public Tracker getDefaultTracker() {
        if (AnalyticInfo.getmTracker() == null) {
            GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
            AnalyticInfo.setmTracker(analytics.newTracker(R.xml.global_tracker));
        }
        return AnalyticInfo.getmTracker();
    }
}