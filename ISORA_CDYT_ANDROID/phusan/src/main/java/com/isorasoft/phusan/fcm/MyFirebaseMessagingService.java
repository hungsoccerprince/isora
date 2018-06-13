/**
 * Copyright 2016 Google Inc. All Rights Reserved.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.isorasoft.phusan.fcm;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.text.Html;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.isorasoft.phusan.R;
import com.isorasoft.phusan.activity.SplashActivity;
import com.isorasoft.phusan.activity.main.UpdateActivity;
import com.isorasoft.phusan.constants.Constants;
import com.isorasoft.phusan.dataaccess.NotificationInfo;
import com.isorasoft.phusan.dataaccess.UserInfo;
import com.isorasoft.mllibrary.utils.ConvertUtils;
import com.isorasoft.mllibrary.utils.NotificationID;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMsgService";

    /**
     * Called when message is received.
     *
     * @param remoteMessage Object representing the message received from Firebase Cloud Messaging.
     */
    // [START receive_message]
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.d(TAG, "From: " + remoteMessage.getFrom());

        // Gui tin nhan chu dong tu admin
        // Hung xu ly noi dung nha
        if (remoteMessage.getFrom().equals(Constants.SLASH + Constants.TOPICS + Constants.SLASH + getString(R.string.topic_name))) {
            try {
                String message = remoteMessage.getData().get("message");
                String title = remoteMessage.getData().get("title");
                String type = remoteMessage.getData().get("type");
                createNotificationFromAdmin(title,message, type);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        } else {
            if (!UserInfo.isLogin())
                return;
            if (remoteMessage.getData().size() > 0) {
                try {
                    Log.d(TAG, "Message data payload: " + remoteMessage.getData());
                    String content = remoteMessage.getData().get("Content");
                    if (content.equals(""))
                        return;
                    sendNotificationPost(content, ConvertUtils.toInt(remoteMessage.getData().get("Type")), remoteMessage.getData().get("Id"), remoteMessage.getData().get("ParentId"));
                } catch (Exception e) {
                    e.getMessage();
                }
            }
            if (remoteMessage.getNotification() != null) {
                Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
            }
        }
//        sendNotification(remoteMessage.getData().get("message"));
    }
    // [END receive_message]

    /**
     * Create and show a simple notification containing the received FCM message.
     *
     * @param messageBody FCM message body received.
     */
    private void sendNotification(String messageBody) {
        Intent intent = new Intent(this, UpdateActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_logo)
                .setContentTitle("SendBird")
                .setContentText(Html.fromHtml(messageBody))
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }


    private void createNotificationFromAdmin(String title, String message, String type) {
        Log.d(TAG, "createNotificationFromAdmin: ");
        try {
            int typeValue = Integer.parseInt(type);
            Intent intent = new Intent(this, SplashActivity.class);

            // chuyen den dau thi thay class do vao
            switch (typeValue) {
                case 0:
                    intent = new Intent(this, SplashActivity.class);
                    break;
                case 1:
                    intent = new Intent(this, SplashActivity.class);
                    break;
                case 2:
                    intent = new Intent(this, SplashActivity.class);
                    break;
            }

            intent.putExtra("title", title);
            intent.putExtra("message", message);
            intent.putExtra("type", type);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 1, intent,
                    PendingIntent.FLAG_ONE_SHOT);

            Log.d(TAG, "createNotificationFromAdmin: " + message);
            Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            Notification noti = new Notification.Builder(this)
                    .setContentTitle(title)
                    .setSmallIcon(R.drawable.ic_logo)
                    .setAutoCancel(true)
                    .setSound(defaultSoundUri)
                    .setStyle(new Notification.BigTextStyle()
                            .bigText(message))
                    .setContentIntent(pendingIntent)
                    .build();
            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(0 , noti);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void sendNotificationPost(String messageBody, int type, String id, String detailId) {
        if (detailId == "" || id == "")
            return;

        Intent intent = null;
        NotificationInfo.EnumNotification enumNotification = NotificationInfo.EnumNotification.Case1.valueOf(type);
        switch (enumNotification) {
        }

        PendingIntent pendingIntent = PendingIntent.getActivity(this, NotificationID.getID(detailId), intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_logo)
                .setContentTitle(getString(R.string.app_name))
                .setContentText(Html.fromHtml(messageBody))
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }


}