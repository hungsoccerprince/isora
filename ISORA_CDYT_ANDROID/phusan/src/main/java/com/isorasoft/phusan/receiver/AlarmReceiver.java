package com.isorasoft.phusan.receiver;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;

import com.isorasoft.phusan.R;
import com.isorasoft.phusan.activity.main.UpdateActivity;
import com.isorasoft.phusan.constants.Constants;
import com.isorasoft.phusan.dataaccess.UserInfo;

/**
 * Created by MaiNam on 3/6/2017.
 */

public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.net.conn.ALARM_REMINDER")) {
            String userId = intent.getStringExtra(Constants.USER_ID);
            int count = intent.getIntExtra(Constants.NUMBER_ITEM, 0);
            if (UserInfo.getCurrentUserId().equals(userId) && count != 0) {
//                showNotification(context, context.getString(R.string.app_name), context.getString(R.string.notification_confirm_booking));
            }
        }
    }

    public void showNotification(Context context, String title, String content) {
        Intent intent = new Intent(context, UpdateActivity.class);
        intent.putExtra(Constants.FROM_NOTIFICATION, true);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.ic_logo)
                .setContentTitle(title)
                .setContentText(content)
                .setOngoing(true)
                .setAutoCancel(false)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0, notificationBuilder.build());
    }
}
