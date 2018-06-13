package com.isorasoft.phusan.dataaccess;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.ImageView;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.isorasoft.phusan.BaseActivity;
import com.isorasoft.phusan.IntentServicePost;
import com.isorasoft.phusan.IntentServiceSync;
import com.isorasoft.phusan.MainApplication;
import com.isorasoft.phusan.R;
import com.isorasoft.phusan.activity.user.login_regiter.LoginActivity;
import com.isorasoft.phusan.activity.user.profile.UserPageActivity;
import com.isorasoft.phusan.constants.Constants;
import com.isorasoft.phusan.constants.ServerConstants;
import com.isorasoft.phusan.constants.TimeoutConstants;
import com.isorasoft.mllibrary.utils.ClientUtils;
import com.isorasoft.mllibrary.utils.ConvertUtils;
import com.isorasoft.mllibrary.utils.ImageUtils;
import com.isorasoft.mllibrary.utils.StringUtils;
import com.sendbird.android.SendBird;

import org.json.JSONObject;

import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

/**
 * Created by MaiNam on 12/27/2016.
 */

public class UserInfo {

    private static final String TAG = UserInfo.class.getSimpleName();
    static JsonObject currentUser;

    public static boolean isUser() {
        return getCurrentUserRole() == Role.User;
    }


    public enum Role {
        Unknown(-1),
        User(0),
        Doctor(1),
        Admin(2),
        SuperAdmin(3);

        public int getValue() {
            return value;
        }

        private int value;

        Role(int role) {
            this.value = role;
        }

        public static Role valueOf(int role) {
            switch (role) {
                case 0:
                    return User;
                case 1:
                    return Doctor;
                case 2:
                    return Admin;
                case 3:
                    return SuperAdmin;
                default:
                    return Unknown;
            }
        }
    }

    public synchronized static void setCurrentUserFromCache(Context mContext, JsonObject account) {
        Log.d(TAG, "setCurrentUserFromCache: ");
        UserInfo.currentUser = account;
        try {
            if (UserInfo.currentUser != null) {
                Log.d(TAG, "setCurrentUser: currentUser " + currentUser.toString());
                DataCacheInfo.setData(mContext, DataCacheInfo.EnumCacheType.CurrentUser, "", UserInfo.currentUser);
                connect(mContext, getCurrentUserId(), getCurrentUserNickName(), getCurrentUserImageUrl());
                IntentServiceSync.startActionSyncProfiles(mContext, UserInfo.getCurrentUserId());
                IntentServiceSync.startActionSyncReminder(mContext, UserInfo.getCurrentUserId());
            } else {
                DataCacheInfo.setData(mContext, DataCacheInfo.EnumCacheType.CurrentUser, "", null);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public synchronized static void setCurrentUser(Context mContext, JsonObject account) {
        Log.d(TAG, "setCurrentUser: ");
        if(account == null || account.get("Account") == null){
            UserInfo.currentUser = null;
        }else {
            JsonObject department = ConvertUtils.toJsonObject(account.get("Department"));
            JsonObject userCurrent = ConvertUtils.toJsonObject(account.get("Account"));
            userCurrent.add("Department", department);
            UserInfo.currentUser = ConvertUtils.toJsonObject(userCurrent.toString());
        }

        try {
            if (UserInfo.currentUser != null) {
                DataCacheInfo.setData(mContext, DataCacheInfo.EnumCacheType.CurrentUser, "", UserInfo.currentUser);
                connect(mContext, getCurrentUserId(), getCurrentUserNickName(), getCurrentUserImageUrl());
                IntentServiceSync.startActionSyncProfiles(mContext, UserInfo.getCurrentUserId());
                IntentServiceSync.startActionSyncReminder(mContext, UserInfo.getCurrentUserId());
            } else {
                DataCacheInfo.setData(mContext, DataCacheInfo.EnumCacheType.CurrentUser, "", null);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public synchronized static void setTokenUser(Context mContext, String token, String password){
        UserInfo.currentUser.addProperty("LoginToken", token);
        UserInfo.currentUser.addProperty("Password", StringUtils.getMD5(password));
        DataCacheInfo.setData(mContext, DataCacheInfo.EnumCacheType.CurrentUser, "", UserInfo.currentUser);
        Log.d(TAG, "setTokenUser: " + currentUser.toString());
    }

    private static void connect(Context context, String sUserId, final String mNickname, String avatarUrl) {
        IntentServiceSync.connectSendBird(context, sUserId, mNickname, avatarUrl);
    }

    public synchronized static JsonObject getCurrentUserHasSave(Context mContext) {
        try {
            if (currentUser == null) {
                String _currentUser = DataCacheInfo.getData(mContext, DataCacheInfo.EnumCacheType.CurrentUser, "");
                Log.d(TAG, "getCurrentUserHasSave _currentUser: " + _currentUser);
                if (!_currentUser.equals("")) {
                    setCurrentUserFromCache(mContext, ConvertUtils.toJsonObject(_currentUser));
                }
            }
            return currentUser;
        } catch (Exception e) {
        }
        return null;
    }

    public static String getCurrentUserId() {
        try {
            if (!UserInfo.isLogin()) return "";
            return ConvertUtils.toString(UserInfo.getCurrentUser().get("Id"));

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String getCurrentUserNickName() {
        try {
            if (!UserInfo.isLogin()) return "";
            return ConvertUtils.toString(UserInfo.getCurrentUser().get("Nickname"));

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Role getCurrentUserRole() {
        try {
            if (!UserInfo.isLogin()) return Role.Unknown;
            return Role.valueOf(ConvertUtils.toInt(UserInfo.getCurrentUser().get("Role")));

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    public static String getCurrentUserEmail() {
        try {
            if (!UserInfo.isLogin()) return "";
            return ConvertUtils.toString(UserInfo.getCurrentUser().get("Email"));

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String getCurrentUserImageUrl() {
        try {
            if (!UserInfo.isLogin()) return "";
            return ConvertUtils.toString(UserInfo.getCurrentUser().get("AvatarUrl"));

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String getCurrentUserPassword() {
        try {
            if (!UserInfo.isLogin()) return "";
            return ConvertUtils.toString(UserInfo.getCurrentUser().get("Password"));

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    public static JsonObject getCurrentUser() {
        return currentUser;
    }

    public static boolean isLogin() {
        return currentUser != null && !currentUser.toString().equals("{}");
    }

    public static boolean isAdmin() {
        return getCurrentUserRole() == Role.Admin;
    }
    public static boolean isSuperAdmin() {
        return getCurrentUserRole() == Role.SuperAdmin;
    }

    public static void goToLogin(Context context) {
        try {
            context.startActivity(new Intent(context, LoginActivity.class));
            BaseActivity.setSlideIn(context, BaseActivity.TypeSlideIn.in_right_to_left);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean refreshToken(Context mContext) throws UnknownHostException, SocketTimeoutException {
        try {
            String method = ServerConstants.REFRESH_TOKEN + "/" + ConvertUtils.toString(UserInfo.getCurrentUser().get("LoginToken"));
            ClientUtils.DataResponse response = ClientUtils.postData(RequestFormInfo.getRequestFormNew(), method, new JSONObject()
                    , TimeoutConstants.DEFAULT_TIMEOUT
            );

            if (response.isOK()){
                JsonObject jsonObject = ConvertUtils.toJsonObject(response.getBody());
                UserInfo.setCurrentUser(mContext, ConvertUtils.toJsonObject(jsonObject.get(ServerConstants.FIELD_DATA)));
                return true;
            }

        } catch (SocketTimeoutException e) {
            throw e;
        } catch (UnknownHostException e) {
            throw e;
        } catch (Exception e) {
            e.getMessage();
        }
        return false;
    }

    public static JsonObject forgot(String email) throws SocketTimeoutException, UnknownHostException {
        try {
            ClientUtils.DataResponse response = ClientUtils.postData(RequestFormInfo.getRequestFormNew(), ServerConstants.ACCOUNT_FORGOT_PASSWORD, new JSONObject()
                    .put("Email", email), TimeoutConstants.DEFAULT_TIMEOUT);
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

    public static JsonObject logout(String email) throws SocketTimeoutException, UnknownHostException {
        try {
            ClientUtils.DataResponse response = ClientUtils.postData(RequestFormInfo.getRequestFormNew(), ServerConstants.ACCOUNT_LOGOUT, new JSONObject()
                    .put("NicknameOrEmail", email)
                    .accumulate("Device", new JSONObject().put("OS", 1).put("DeviceId", MainApplication.getDeviceId()).put("Token", MainApplication.getUUID())), TimeoutConstants.DEFAULT_TIMEOUT);
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

    public static JsonObject login(Context mContext, String email, String password) throws SocketTimeoutException, UnknownHostException {
        try {
            ClientUtils.DataResponse response = ClientUtils.postData(RequestFormInfo.getRequestFormNew(), ServerConstants.ACCOUNT_LOGIN, new JSONObject()
                    .put("NicknameOrEmail", email)
                    .put("Password", StringUtils.getMD5(password))
                    .accumulate("Device", new JSONObject().put("OS", 1).put("DeviceId", MainApplication.getDeviceId()).put("Token", MainApplication.getUUID())), TimeoutConstants.DEFAULT_TIMEOUT);
            if (response.isOK()){
                JsonObject jsonObject = ConvertUtils.toJsonObject(response.getBody());
                int code = ConvertUtils.toInt(jsonObject.get(ServerConstants.FIELD_CODE));
                switch (code){
                    case 0:
                        return ConvertUtils.toJsonObject(jsonObject.get(ServerConstants.FIELD_DATA));
                    case 1:
                        BaseActivity.showSnackBar(mContext, "Tài khoản đã bị khóa!");
                        return null;
                    case 2:
                        BaseActivity.showSnackBar(mContext, "Tên tài khoản hoặc mật khẩu không đúng!");
                        return null;
                }
                return null;
            }

            BaseActivity.showSnackBar(mContext, "Tên tài khoản hoặc mật khẩu không đúng!");
        } catch (SocketTimeoutException e) {
            throw e;
        } catch (UnknownHostException e) {
            throw e;
        } catch (Exception e) {
        }
        return null;
    }

    public static JsonObject loginSocial(int socialType, String socialId, String name, String email) throws SocketTimeoutException, UnknownHostException {
        try {
            ClientUtils.DataResponse response = ClientUtils.postData(RequestFormInfo.getRequestFormNew(), ServerConstants.LOGIN_SOCIAL, new JSONObject()
                    .put("SocialType", socialType)
                    .put("SocialId", socialId)
                    .put("FullName",name)
                    .put("Email", email)
                    .accumulate("Device", new JSONObject().put("OS", 1).put("DeviceId", MainApplication.getDeviceId()).put("Token", MainApplication.getUUID())), TimeoutConstants.DEFAULT_TIMEOUT);
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

    public static boolean checkExisAccount(String email) throws SocketTimeoutException, UnknownHostException {
        try {
            ClientUtils.DataResponse response = ClientUtils.postData(RequestFormInfo.getRequestFormNew(), ServerConstants.ACCOUNT_CHECK_EXIST_ACCOUNT, new JSONObject()
                            .put("Email", email)
                            .put("Nickname", email)
                    , TimeoutConstants.DEFAULT_TIMEOUT);
            return response.isOK();

        } catch (SocketTimeoutException e) {
            throw e;
        } catch (UnknownHostException e) {
            throw e;
        } catch (Exception e) {
        }
        return false;
    }

    public static JsonObject changePassword(Context mContext, String email, String currentpassword, String newpassword) throws SocketTimeoutException, UnknownHostException {
        try {
            ClientUtils.DataResponse response = ClientUtils.postData(RequestFormInfo.getRequestFormNew(), ServerConstants.ACCOUNT_CHANGE_PASSWORD, new JSONObject()
                    .put("Email", email)
                    .put("OldPassWord", StringUtils.getMD5(currentpassword))
                    .put("NewPassWord", StringUtils.getMD5(newpassword)), TimeoutConstants.DEFAULT_TIMEOUT);

            if(response.getStatus() == Constants.CODE_REFRESH_TOKEN){
                if(UserInfo.refreshToken(mContext)){
                    return changePassword(mContext, email, currentpassword, newpassword);
                }else {
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


    public static void showUserProfile(Context context, String userId, String userName, String imageUrl, int role) {
        UserPageActivity.open(context, userId, userName, imageUrl, role);

    }

    public static JsonObject changeEmail(Context mContext, String userId, String email) throws UnknownHostException, SocketTimeoutException {
        try {
            ClientUtils.DataResponse response = ClientUtils.postData(RequestFormInfo.getRequestFormNew(), ServerConstants.UPDATE_EMAIL, new JSONObject()
                    .put("AccountId", userId)
                    .put("Email", email), TimeoutConstants.DEFAULT_TIMEOUT
            );

            if(response.getStatus() == Constants.CODE_REFRESH_TOKEN){
                if(UserInfo.refreshToken(mContext)){
                    return changeEmail(mContext, userId, email);
                }else {
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

            if(response.getStatus() == 409){
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("Conflict", 409);
                return jsonObject;
            }
            if (response.isOK())
                return ConvertUtils.toJsonObject(response.getBody());
        } catch (SocketTimeoutException e) {
            throw e;
        } catch (UnknownHostException e) {
            throw e;
        } catch (Exception e) {
            e.getMessage();
        }
        return null;
    }


    public static void logIssue(String accountId, String note){
        try {
            ClientUtils.DataResponse response = ClientUtils.putData(RequestFormInfo.getRequestFormNew(), ServerConstants.LOG_ADD, new JSONObject()
                    .put("DeviceType", 1)
                    .put("AccountId", accountId)
                    .put("Note", note), TimeoutConstants.DEFAULT_TIMEOUT);

        } catch (SocketTimeoutException e) {
            e.printStackTrace();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (Exception e) {
        }
    }


    public enum SocialType {
        Default(0),
        Facebook(1),
        Google(2);
        private final int type;

        SocialType(int type) {
            this.type = type;
        }

        public int getValue() {
            return type;
        }
    }

    public enum Gender {
        Male(1),
        Female(0);
        private final int type;

        Gender(int type) {
            this.type = type;
        }

        public int getValue() {
            return type;
        }
    }

    public static void showAvatar(ImageView imageView, String imageAvatar) {
        ImageUtils.loadImageByGlide(imageView, false, 0, 0, imageAvatar, R.drawable.ic_default_avatar, R.drawable.ic_default_avatar, false);
    }
}
