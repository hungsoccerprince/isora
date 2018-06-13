package com.isorasoft.phusan.dataaccess;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;

import com.google.gson.JsonObject;
import com.isorasoft.mllibrary.utils.ClientUtils;
import com.isorasoft.mllibrary.utils.ConvertUtils;
import com.isorasoft.phusan.BaseActivity;
import com.isorasoft.phusan.IntentServicePost;
import com.isorasoft.phusan.activity.user.login_regiter.LoginActivity;
import com.isorasoft.phusan.constants.Constants;
import com.isorasoft.phusan.constants.ServerConstants;
import com.isorasoft.phusan.constants.TimeoutConstants;
import com.sendbird.android.SendBird;

import org.json.JSONObject;

import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

/**
 * Created by MaiNam on 1/11/2017.
 */

public class NotificationInfo {
    public static JsonObject getListWithPaging(Context mContext, int page, int size) throws SocketTimeoutException, UnknownHostException {
        try {
            String method = ServerConstants.NOTIFICATION_GETLIST_PAGING + "?page=" + page + "&size=" + size;
            ClientUtils.DataResponse response = ClientUtils.getDataHeader(RequestFormInfo.getRequestFormNew(), method
                    , TimeoutConstants.DEFAULT_TIMEOUT);
            if (response.isOK()) {
                return ConvertUtils.toJsonObject(response.getBody());
            }
            if(mContext == null)
                return null;

            if(response.getStatus() == Constants.CODE_REFRESH_TOKEN){
                if(UserInfo.refreshToken(mContext)){
                    return getListWithPaging(mContext, page, size);
                }else {
                    IntentServicePost.startActionLogout(mContext, UserInfo.getCurrentUserNickName());
                    Intent intent = new Intent(Constants.ACTION_LOGOUT);
                    mContext.sendBroadcast(intent);
                    UserInfo.setCurrentUser(mContext, null);
                    try {
                        NotificationManager nMgr = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
                        nMgr.cancelAll();
                        UserInfo.goToLogin(mContext);
                        SendBird.disconnect(new SendBird.DisconnectHandler() {
                            @Override
                            public void onDisconnected() {
                            }
                        });
                    } catch (Exception e) {
                        e.getMessage();
                    }
                }
            }
            if(response.getStatus() == Constants.CODE_REQUEST_LOGOUT){
                IntentServicePost.startActionLogout(mContext, UserInfo.getCurrentUserNickName());
                Intent intent = new Intent(Constants.ACTION_LOGOUT);
                mContext.sendBroadcast(intent);
                UserInfo.setCurrentUser(mContext, null);
                try {
                    NotificationManager nMgr = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
                    nMgr.cancelAll();
                    Intent intent1 = new Intent(mContext, LoginActivity.class);
                    intent1.putExtra("REQUEST", "TOKEN_NEW");
                    mContext.startActivity(intent1);
                    BaseActivity.setSlideIn(mContext, BaseActivity.TypeSlideIn.in_right_to_left);
                    SendBird.disconnect(new SendBird.DisconnectHandler() {
                        @Override
                        public void onDisconnected() {
                        }
                    });
                } catch (Exception e) {
                    e.getMessage();
                }
            }
        } catch (SocketTimeoutException e) {
            throw e;
        } catch (UnknownHostException e) {
            throw e;
        } catch (Exception e) {
            throw e;
        }
        return new JsonObject();
    }


    public static JsonObject readNotification(String userId, String notificationId) throws SocketTimeoutException, UnknownHostException {
        try {
            ClientUtils.DataResponse response = ClientUtils.postData(RequestFormInfo.getRequestFormNew(), ServerConstants.NOTIFICATION_READ, new JSONObject()
                            .put("RequestedUserId", userId)
                            .put("NotificationId", notificationId)
                    , TimeoutConstants.DEFAULT_TIMEOUT);
            if (response.isOK())
                return ConvertUtils.toJsonObject(response.getBody());
        } catch (SocketTimeoutException e) {
            throw e;
        } catch (UnknownHostException e) {
            throw e;
        } catch (Exception e) {
        }
        return null;
    }

    public enum EnumNotification {
        Case1(0);

        EnumNotification(int type) {
            this.value = type;
        }

        int value;

        public int getType() {
            return value;
        }

        public EnumNotification valueOf(int value) {
            switch (value) {
                case 0:
                    return Case1;
            }
            return Case1;
        }

    }
}
