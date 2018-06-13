package com.isorasoft.phusan;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.isorasoft.phusan.constants.Constants;
import com.isorasoft.phusan.dataaccess.AppInfo;
import com.isorasoft.phusan.dataaccess.DataCacheInfo;
import com.isorasoft.phusan.dataaccess.NotificationInfo;
import com.isorasoft.phusan.dataaccess.RealmInfo;
import com.isorasoft.phusan.dataaccess.UserInfo;
import com.isorasoft.phusan.utils.AlarmUtils;
import com.isorasoft.mllibrary.utils.ConvertUtils;
import com.isorasoft.mllibrary.utils.NotificationID;
import com.isorasoft.mllibrary.utils.StringUtils;
import com.sendbird.android.GroupChannel;
import com.sendbird.android.SendBird;
import com.sendbird.android.SendBirdException;
import com.sendbird.android.User;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

import io.realm.Realm;
import io.realm.exceptions.RealmError;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class IntentServiceSync extends IntentService {
    private static final String SYNC_TAG_POPULAR = "com.isorasoft.congdongyte.action.SYNC_TAG_POPULAR";
    private static final String SYNC_CONNECT_SEND_BIRD = "com.isorasoft.congdongyte.action.SYNC_CONNECT_SEND_BIRD";
    private static final String SYNC_POLICY_AN_CONDITION = "com.isorasoft.congdongyte.action.SYNC_POLICY_AN_CONDITION";
    private static final String SYNC_CATEGORY = "com.isorasoft.congdongyte.action.SYNC_CATEGORY";
    public static final String SYNC_NOTIFICATION_COUNT = "com.isorasoft.congdongyte.action.SYNC_NOTIFICATION_COUNT";
    private static final String SYNC_TOTAL_MESSENGER_UNREAD_COUNT = "com.isorasoft.congdongyte.action.SYNC_TOTAL_MESSENGER_UNREAD_COUNT";
    private static final String SYNC_DOCTOR_BY_CATEGORY = "com.isorasoft.congdongyte.action.SYNC_DOCTOR_BY_CATEGORY";
    private static final String SYNC_SERVICE_PRICING = "com.isorasoft.congdongyte.action.SYNC_SERVICE_PRICING";
    private static final String SYNC_JOB = "com.isorasoft.congdongyte.action.SYNC_JOB";
    private static final String SYNC_COUNTRY = "com.isorasoft.congdongyte.action.SYNC_COUNTRY";
    private static final String SYNC_PROVINCE = "com.isorasoft.congdongyte.action.SYNC_PROVINCE";
    private static final String SYNC_DISTRICT = "com.isorasoft.congdongyte.action.SYNC_DISTRICT";
    private static final String SYNC_TOWN = "com.isorasoft.congdongyte.action.SYNC_TOWN";
    private static final String SYNC_ADMIN = "com.isorasoft.congdongyte.action.SYNC_ADMIN";
    private static final String SYNC_PROFILE = "SYNC_PROFILE";
    private static final String SYNC_REMINDER = "SYNC_REMINDER";
    private static final String TAG = IntentServiceSync.class.getSimpleName();
    private static final String SYNC_PHONE_SUPPORT = "SYNC_PHONE_SUPPORT";

    public IntentServiceSync() {
        super("IntentServicePost");
    }

    /**
     * Starts this service to perform action Foo with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    // TODO: Customize helper method

    public static void startActionSyncProfiles(Context context, String user) {
        Intent intent = new Intent(context, IntentServiceSync.class);
        intent.putExtra(Constants.USER_ID, user);
        intent.setAction(SYNC_PROFILE);
        context.startService(intent);
    }

    public static void startActionSyncReminder(Context context, String user) {
        Intent intent = new Intent(context, IntentServiceSync.class);
        intent.putExtra(Constants.USER_ID, user);
        intent.setAction(SYNC_REMINDER);
        context.startService(intent);
    }

    // Dang ky SendBird tao user chat
    public static void connectSendBird(Context context, String sUserId, String mNickname, String avatarUrl) {
        Intent intent = new Intent(context, IntentServiceSync.class);
        intent.putExtra(Constants.USER_ID, sUserId);
        intent.putExtra(Constants.USER_NICKNAME, mNickname);
        intent.putExtra(Constants.USER_AVATAR_URL, avatarUrl);
        intent.setAction(SYNC_CONNECT_SEND_BIRD);
        context.startService(intent);
    }

    public static void startActionSyncAdmin(Context context) {
        Intent intent = new Intent(context, IntentServiceSync.class);
        intent.setAction(SYNC_ADMIN);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        String _userid;
        if (intent != null) {
            final String action = intent.getAction();
            switch (action) {

            }
        }
    }
}
