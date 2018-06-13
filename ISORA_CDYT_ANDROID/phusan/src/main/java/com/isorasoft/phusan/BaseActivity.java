package com.isorasoft.phusan;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.isorasoft.phusan.activity.main.UpdateActivity;
import com.isorasoft.phusan.constants.Constants;
import com.isorasoft.phusan.dataaccess.AnalyticInfo;
import com.isorasoft.phusan.dataaccess.RealmInfo;
import com.isorasoft.phusan.dataaccess.UserInfo;
import com.isorasoft.phusan.utils.MultiFilePickerUtils;
import com.isorasoft.mllibrary.Connectivity;

import io.realm.Realm;

/**
 * Created by MaiNam on 12/26/2016.
 */

public abstract class BaseActivity extends com.isorasoft.mllibrary.activity.BaseActivity {
    Realm realm;
    private static final String TAG = BaseActivity.class.getSimpleName();

    public synchronized void gotoLogin() {
        UserInfo.goToLogin(getActivity());
    }

    public synchronized void showUserInfo(String userId) {
//        UserInfo.showUserInfo(this, userId);
    }

    public synchronized void chatWithUser(String userId, String userName, String imageUrl, boolean isOnline) {
        if (!UserInfo.isLogin()) {
            UserInfo.goToLogin(getActivity());
            return;
        }
        if (!Connectivity.isConnected()) {
            showCheckConnection();
            return;
        }

    }

    public void showHomeButton(Menu menu) {
        showHomeButton(menu, UpdateActivity.class, null);
    }

    public void goHome() {
        goHome(UpdateActivity.class);
    }

    @Override
    public BaseActivity getBaseActivity() {
        return this;
    }

    public Realm getRealm() {
        if (realm == null || realm.isClosed()) {
            realm = RealmInfo.getRealm(this);
        }
        return realm;
    }

    public static Realm getRealm(Context context) {
        if (context instanceof BaseActivity)
            return ((BaseActivity) context).getRealm();
        return null;
    }

    @Override
    protected void onDestroy() {
        RealmInfo.closeRealm(realm);
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }


    public static BaseActivity from(Fragment fragment) {
        try {
            return (BaseActivity) fragment.getContext();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static BaseActivity from(Context context) {
        try {
            return (BaseActivity) context;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public BaseActivity getActivity() {
        return this;
    }

    public void showToast(@StringRes int activity_update_profile_update_success) {
        Toast.makeText(this, activity_update_profile_update_success, Toast.LENGTH_LONG).show();
    }

    MultiFilePickerUtils.OnCallbackSelectPicture callbackSelectPicture = null;

    public void selectPicture(MultiFilePickerUtils.OnCallbackSelectPicture callback) {
        callbackSelectPicture = callback;
        MultiFilePickerUtils.selectPicture(getActivity());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        MultiFilePickerUtils.getPictureSelected(requestCode, resultCode, data, callbackSelectPicture);
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void createDialog() {
        dialog = new Dialog(this, com.isorasoft.mllibrary.R.style.LoadingDialog);
        View v = this.getLayoutInflater().inflate(R.layout.include_new_progressloading, null);
        dialog.setContentView(v);
        dialog.setCancelable(false);
        dialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        dialog.getWindow().setGravity(Gravity.BOTTOM);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(this.getResources().getColor(com.isorasoft.mllibrary.R.color.dialog_background)));
        View v2 = v.findViewById(com.isorasoft.mllibrary.R.id.img_tool_back);
        v2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    void setScreenName(AnalyticInfo.Screen screen) {
        AnalyticInfo.sendAnanlyticOpenScreen(screen);
    }


    public abstract AnalyticInfo.Screen getScreen();

    @Override
    protected void onStart() {
        super.onStart();
        setScreenName(getScreen());
    }

    @Override
    public void onCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
        Realm.init(getApplicationContext());
        String notificationId = getIntent().getStringExtra(Constants.NOTIFICATION_ID);
        if (notificationId != null && !notificationId.isEmpty()) {
            IntentServicePost.startActionSetReadNotification(getContext(), notificationId, UserInfo.getCurrentUserId());
        }
    }

    public static void showToast(Context context, int send_schedule_success_with_booking) {
        try {
            Toast.makeText(context, send_schedule_success_with_booking, Toast.LENGTH_LONG).show();
        } catch (Exception e) {

        }
    }

    public static void showSnackBar(Context context, @StringRes int mes, @StringRes int callAgain, ReloadListener reloadListener) {
        try {
            BaseActivity.from(context).showSnackBar(context.getString(mes), context.getString(callAgain), reloadListener);
        } catch (Exception e) {

        }
    }
}
