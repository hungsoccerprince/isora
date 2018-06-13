package com.isorasoft.phusan;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;

import com.isorasoft.phusan.constants.Constants;
import com.isorasoft.phusan.dataaccess.DataCacheInfo;
import com.isorasoft.phusan.dataaccess.NotificationInfo;
import com.isorasoft.phusan.dataaccess.UserInfo;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class IntentServicePost extends IntentService {

    private static final String ACTION_READ_NOTIFICATION = "com.isorasoft.bve.action.ACTION_READ_NOTIFICATION";
    private static final String ACTION_LOGOUT = "com.isorasoft.bve.action.ACTION_LOGOUT";
    private static final String ACTION_LOG_ISSUE = "com.isorasoft.bve.action.ACTION_LOG_ISSUE";

    public IntentServicePost() {
        super("IntentServicePost");
    }

    /**
     * Starts this service to perform action Foo with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    // TODO: Customize helper method


    public static void startActionLogout(Context context, String userId) {
        DataCacheInfo.setData(context, DataCacheInfo.EnumCacheType.ListAdmin, null, true);
        Intent intent = new Intent(context, IntentServicePost.class);
        intent.setAction(ACTION_LOGOUT);
        intent.putExtra(Constants.USER_ID, userId);
        context.startService(intent);
    }

    public static void startActionSetReadNotification(Context context, String notificationId, String userId) {
        Intent intent = new Intent(context, IntentServicePost.class);
        intent.setAction(ACTION_READ_NOTIFICATION);
        intent.putExtra(Constants.NOTIFICATION_ID, notificationId);
        intent.putExtra(Constants.USER_ID, userId);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            String userId;
            switch (action) {
                case ACTION_READ_NOTIFICATION:
                    userId = intent.getStringExtra(Constants.USER_ID);
                    String notificationId = intent.getStringExtra(Constants.NOTIFICATION_ID);
                    handleActionReadNotification(userId, notificationId);
                    break;
                case ACTION_LOGOUT:
                    userId = intent.getStringExtra(Constants.USER_ID);
                    handleActionLogout(userId);
                    break;
                case ACTION_LOG_ISSUE:
                    String accountId = intent.getStringExtra(Constants.ACCOUNT_ID);
                    String note = intent.getStringExtra(Constants.NOTE);
                    handleActionLogIssue(accountId, note);
                    break;

            }
        }
    }

    private void handleActionLogIssue(final String accountId, final String note) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    UserInfo.logIssue(accountId, note);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void handleActionReadNotification(final String userId, final String notificationId) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    if (NotificationInfo.readNotification(userId, notificationId) != null) {
                        Intent intent = new Intent(Constants.ACTION_READ_NOTIFICATION);
                        intent.putExtra(Constants.NOTIFICATION_ID, notificationId);
                        sendBroadcast(intent);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }

    private void handleActionLogout(final String userId) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    UserInfo.logout(userId);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }





}
