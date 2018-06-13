package com.isorasoft.mllibrary.activity;

import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.isorasoft.mllibrary.R;

import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.isorasoft.mllibrary.constants.Constants;
import com.isorasoft.mllibrary.utils.AndroidPermissionUtils;
import com.isorasoft.mllibrary.utils.ClientUtils;
import com.isorasoft.mllibrary.utils.GoogleAccountUtils;
import com.isorasoft.mllibrary.utils.RecordVideoUtils;
import com.isorasoft.mllibrary.utils.TakePictureUtils;
import com.isorasoft.mllibrary.utils.ViewGroupCleanerUtils;
import com.isorasoft.mllibrary.utils.ViewUtils;

import java.util.HashMap;


public class BaseActivity extends AppCompatActivity {
    private static final String NEED_RESTART = "NEED_RESTART";
    private static final String TAG = BaseActivity.class.getSimpleName();
    private Handler mHandler;
    private static final int PUSH_MAIN_THREAD = 10004;
    public static final int TIMEOUT_REQUEST_EXCEPTION = 10001;
    public static final int CHECK_CONNECTIVITY = 10003;
    public static final int SHOW_SNACKBAR_MESSAGE = 10002;

    public boolean hasBookmark;
    public Menu mMenu;

    public void requestAuthorization(Intent intent, GoogleAccountUtils.OnCallback callback) {
        this.googleAccountCallBack = callback;
        startActivityForResult(intent, Constants.REQUEST_AUTHORIZATION);
    }


    public enum TypeSlideOut {
        out_left_to_right,
        out_top_to_bottom,
        out_none
    }

    public enum TypeSlideIn {
        in_right_to_left,
        in_bottom_to_up,
        in_none
    }

    public Dialog dialog = null;

    private TypeSlideOut typeSlide = TypeSlideOut.out_none;

    public TypeSlideOut getSlideOut() {
        return typeSlide;
    }

    public void setSlideOut(TypeSlideOut typeSlide) {
        this.typeSlide = typeSlide;
    }


    public void pullDataFromHandler(Message msg) {

    }

    public void pushToMainThread(Object objects, Bundle dataBundle) {
        try {

            Message message = new Message();
            message.what = PUSH_MAIN_THREAD;
            message.obj = objects;
            message.setData(dataBundle);
            getHandler().sendMessage(message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    ReloadListener reload;


    public interface OnCallBackBroadcastListener {
        void onCallBackBroadcastListener(Context context, String action, Intent intent);
    }

    HashMap<String, ReceiverItem> listAcctionCallBack = null;

    public class ReceiverItem {
        private String action;
        private OnCallBackBroadcastListener onCallBackBroadcastListener;

        public String getAction() {
            return action;
        }

        public void setAction(String action) {
            this.action = action;
        }

        public OnCallBackBroadcastListener getOnCallBackBroadcastListener() {
            return onCallBackBroadcastListener;
        }

        public void setOnCallBackBroadcastListener(OnCallBackBroadcastListener onCallBackBroadcastListener) {
            this.onCallBackBroadcastListener = onCallBackBroadcastListener;
        }

        public ReceiverItem(String action, OnCallBackBroadcastListener listener) {
            this.action = action;
            this.onCallBackBroadcastListener = listener;
        }
    }

    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                ReceiverItem receiverItem = listAcctionCallBack.get(intent.getAction());
                receiverItem.getOnCallBackBroadcastListener().onCallBackBroadcastListener(context, intent.getAction(), intent);

            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    };

    private boolean hasRegister = false;

    public synchronized void registerBroadcastReceiver(ReceiverItem... receiverItems) {
        try {
            if (hasRegister)
                throw new Exception("Bạn đã đăng ký broadcast rồi");

            listAcctionCallBack = new HashMap<>();
            if (receiverItems.length > 0) {
                IntentFilter intentFilter = new IntentFilter();
                for (ReceiverItem receiverItem : receiverItems) {
                    intentFilter.addAction(receiverItem.getAction());
                    if (!listAcctionCallBack.containsKey(receiverItem.action)) {
                        listAcctionCallBack.put(receiverItem.action, receiverItem);
                    }
                }
                if (intentFilter.countActions() != 0) {
                    registerReceiver(broadcastReceiver, intentFilter);
                    hasRegister = true;
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public synchronized void unregisterBroadcastReceiver() {
        try {
            if (hasRegister && broadcastReceiver != null) {
                unregisterReceiver(broadcastReceiver);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        hasRegister = false;

    }

    public Handler getHandler() {
        if (mHandler == null) {
            mHandler = new Handler(Looper.getMainLooper()) {
                @Override
                public void handleMessage(final Message msg) {
                    switch (msg.what) {
                        case PUSH_MAIN_THREAD:
                            pullDataFromHandler(msg);
                            break;
                        case CHECK_CONNECTIVITY:
                            try {

                                Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), R.string.common_msg_check_connectivity, Snackbar.LENGTH_LONG);
                                if (reload != null) {
                                    snackbar.setAction(R.string.common_msg_retry, new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            try {
                                                reload.reload();
                                                reload = null;
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    }).setActionTextColor(Color.RED);
                                }
//                        if (msg.obj instanceof ReloadListener && msg != null && view != null) {
//                            snackbar.setAction(R.string.msg_retry, new View.OnClickListener() {
//                                @Override
//                                public void onClick(View v) {
//                                    try {
//                                        ((ReloadListener) msg.obj).reload();
//                                    } catch (Exception e) {
//                                        e.printStackTrace();
//                                    }
//                                }
//                            }).setActionTextColor(Color.RED);
//                        }
                                snackbar.show();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            break;

                        case TIMEOUT_REQUEST_EXCEPTION:
                            try {

                                Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), R.string.common_msg_connection_error, Snackbar.LENGTH_LONG);
                                if (reload != null) {
                                    snackbar.setAction(R.string.common_msg_retry, new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            try {
                                                reload.reload();
                                                reload = null;
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    }).setActionTextColor(Color.RED);
                                }
//                        if (msg.obj instanceof ReloadListener && msg != null && view != null) {
//                            snackbar.setAction(R.string.msg_retry, new View.OnClickListener() {
//                                @Override
//                                public void onClick(View v) {
//                                    try {
//                                        ((ReloadListener) msg.obj).reload();
//                                    } catch (Exception e) {
//                                        e.printStackTrace();
//                                    }
//                                }
//                            }).setActionTextColor(Color.RED);
//                        }
                                snackbar.show();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            break;
                        case SHOW_SNACKBAR_MESSAGE:
                            try {

                                String mes = msg.getData().getString(Constants.SNACKBAR_MESSAGE);
                                if (mes == null) mes = "";
                                Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), mes, Snackbar.LENGTH_LONG);
                                String buttonName = msg.getData().getString(Constants.SNACKBAR_BUTTON_RELOAD_NAME);
                                if (buttonName == null) buttonName = "";
                                if (reload != null && !buttonName.isEmpty()) {
                                    snackbar.setAction(buttonName, new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            try {
                                                reload.reload();
                                                reload = null;
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    }).setActionTextColor(Color.RED);
                                }
//                        if (msg.obj instanceof ReloadListener && msg != null && view != null) {
//                            snackbar.setAction(R.string.msg_retry, new View.OnClickListener() {
//                                @Override
//                                public void onClick(View v) {
//                                    try {
//                                        ((ReloadListener) msg.obj).reload();
//                                    } catch (Exception e) {
//                                        e.printStackTrace();
//                                    }
//                                }
//                            }).setActionTextColor(Color.RED);
//                        }
                                snackbar.show();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            break;
                    }
                    super.handleMessage(msg);
                }
            };
        }
        return mHandler;
    }

    public String ignoreBroadCastCode;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        isSaveInstance = false;
//        Thread.setDefaultUncaughtExceptionHandler(new ExeptionHandler(this));
        ignoreBroadCastCode = ClientUtils.generateBroadcastCode(this.getClass().getName());
    }

    public void createDialog() {
        dialog = new Dialog(this, R.style.LoadingDialog);
        View v = this.getLayoutInflater().inflate(R.layout.include_progressloading, null);
        dialog.setContentView(v);
        dialog.setCancelable(false);
        dialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        dialog.getWindow().setGravity(Gravity.BOTTOM);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(this.getResources().getColor(R.color.dialog_background)));
        View v2 = v.findViewById(R.id.img_tool_back);
        v2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    public static void showProgress(Context context) {
        try {
            if (context instanceof BaseActivity)
                ((BaseActivity) context).showProgress();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void hideProgress(Context context) {
        try {
            if (context instanceof BaseActivity)
                ((BaseActivity) context).hideProgress();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void showProgress() {
        try {
            if (dialog == null) {
                createDialog();
            }
            if (!dialog.isShowing())
                dialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void hideProgress() {
        if (dialog != null && dialog.isShowing()) {
            try {
                dialog.dismiss();
                dialog = null;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    public interface ReloadListener {
        void reload();

    }

    public static void showSnackBar(Context context, String msg) {
        try {
            if (context instanceof BaseActivity)
                ((BaseActivity) context).showSnackBar(msg);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void showSnackBar(Context context, @StringRes int resourceId) {
        try {
            String string = (context.getResources().getString(resourceId));
            showSnackBar(context, string);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void showSnackBar(Context context, String msg, String callAgainName, ReloadListener reloadListener) {
        try {
            if (context instanceof BaseActivity)
                ((BaseActivity) context).showSnackBar(msg, callAgainName, reloadListener);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public synchronized void showSnackBar(View v, String msg) {
        try {
            Snackbar snackbar = Snackbar.make(v, msg, Snackbar.LENGTH_INDEFINITE);
            snackbar.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public synchronized void showSnackBar(@IdRes int id, String msg) {
        showSnackBar(findViewById(id), msg);
    }

    public synchronized void showSnackBar(String msg, String callAgainName, ReloadListener reloadListener) {
        this.reload = reloadListener;
        Message message = new Message();
        message.what = SHOW_SNACKBAR_MESSAGE;
        if (msg == null)
            msg = "";
        message.getData().putString(Constants.SNACKBAR_MESSAGE, msg);
        if (callAgainName != null)
            message.getData().putString(Constants.SNACKBAR_BUTTON_RELOAD_NAME, callAgainName);
        getHandler().sendMessage(message);
    }

    public synchronized void showSnackBar(CharSequence msg) {
        showSnackBar(msg == null ? "" : msg.toString());
    }

    public synchronized void showSnackBar(String msg) {
        this.reload = null;
        Message message = new Message();
        message.what = SHOW_SNACKBAR_MESSAGE;
        if (msg == null)
            msg = "";
        message.getData().putString(Constants.SNACKBAR_MESSAGE, msg);
        getHandler().sendMessage(message);
    }

    public synchronized void showSnackBar(@StringRes int resourceId) {
        try {
            String string = getResources().getString(resourceId);
            showSnackBar(string);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public synchronized void showTimeOutError(ReloadListener reloadListener) {
        this.reload = reloadListener;
        Message message = new Message();
        message.what = TIMEOUT_REQUEST_EXCEPTION;
        message.obj = reloadListener;
        getHandler().sendMessage(message);
    }


    public static void showTimeOutError(Context context) {
        try {
            if (context instanceof BaseActivity)
                ((BaseActivity) context).showTimeOutError();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void showTimeOutError(Context context, ReloadListener reloadListener) {
        try {
            if (context instanceof BaseActivity)
                ((BaseActivity) context).showTimeOutError(reloadListener);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public synchronized void showTimeOutError() {
        this.reload = null;
        Message message = new Message();
        message.what = TIMEOUT_REQUEST_EXCEPTION;
        getHandler().sendMessage(message);
    }


    public synchronized void showCheckConnection(ReloadListener reloadListener) {
        this.reload = reloadListener;
        Message message = new Message();
        message.what = CHECK_CONNECTIVITY;
        message.obj = reloadListener;
        getHandler().sendMessage(message);
    }


    public static void showCheckConnection(Context context) {
        try {
            if (context instanceof BaseActivity)
                ((BaseActivity) context).showCheckConnection();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void showCheckConnection(Context context, ReloadListener reloadListener) {
        try {
            if (context instanceof BaseActivity)
                ((BaseActivity) context).showCheckConnection(reloadListener);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public synchronized void showCheckConnection() {
        this.reload = null;
        Message message = new Message();
        message.what = CHECK_CONNECTIVITY;
        getHandler().sendMessage(message);
    }

    private final int HOME_BUTTON_ID = 1001;
    Intent goHome = null;

    public void showHomeButton(Menu menu, Class<?> activity, Bundle bundle) {
        try {
            if (menu != null && activity != null) {
                MenuItem menuItem = menu.add(0, HOME_BUTTON_ID, 0, "Trang chủ");
                menuItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
                menuItem.setIcon(getResources().getDrawable(R.drawable.ic_home_w));

                goHome = new Intent(this, activity);
                goHome.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                if (bundle != null)
                    goHome.putExtras(bundle);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void showHomeButton(Menu menu, Class<?> activity) {
        showHomeButton(menu, activity, null);
    }

    public void goHome(Class<?> activity) {
        Intent _goHome = new Intent(this, activity);
        _goHome.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(_goHome);
    }

    public boolean onHomeButtonClick() {
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        try {

            switch (item.getItemId()) {
                case android.R.id.home:
                    // app icon in action bar clicked; goto parent activity.
                    if (!onHomeButtonClick())
                        break;
                    this.finish();
                    slideOut();
                    return true;
                case HOME_BUTTON_ID:
                    if (goHome != null) {
                        startActivity(goHome, TypeSlideIn.in_bottom_to_up);
                    }
                    return true;
                default:
                    return super.onOptionsItemSelected(item);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        mMenu = menu;
        return super.onCreateOptionsMenu(menu);
    }

    public void finish(boolean useSlide) {
        super.finish();
        if (useSlide)
            slideOut();
    }

    public void finish(final boolean useSlide, int delaytime) {
        getHandler().postDelayed(new Runnable() {
            @Override
            public void run() {
                finish(useSlide);
            }
        }, delaytime);
    }

    public void finish(long delaytime) {
        try {
            getHandler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    finish();
                }
            }, delaytime);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void finish() {
        finish(true);
    }


    public void startActivity(Class<?> cls, TypeSlideIn slideIn) {
        try {
            Intent intent = new Intent(this, cls);
            startActivity(intent, slideIn);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void startActivity(Class<?> cls) {
        try {
            Intent intent = new Intent(this, cls);
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void startActivity(Intent intent, TypeSlideIn slideIn) {
        try {
            startActivity(intent);
            setSlideIn(this, slideIn);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    public void startActivity(Intent intent, TypeSlideIn slideIn, int requestCode) {
        try {
            startActivityForResult(intent, requestCode);
            setSlideIn(this, slideIn);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        slideOut();
    }

    public void showFullScreen() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    public void showBackButton(boolean show) {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(show);
        actionBar.setDisplayHomeAsUpEnabled(show);
    }

    public void slideOut() {
        switch (typeSlide) {
            case out_top_to_bottom:
                overridePendingTransition(R.anim.no_change, R.anim.slide_out_down);
                break;
            case out_left_to_right:
                overridePendingTransition(R.anim.slide_out_right, R.anim.slide_in_right);
                break;
            case out_none:
                break;
        }
    }

    public static void setSlideIn(Context mActivity, TypeSlideIn typeSlideIn) {
        if (!(mActivity instanceof Activity))
            return;
        Activity activity = (Activity) mActivity;
        switch (typeSlideIn) {
            case in_bottom_to_up:
                activity.overridePendingTransition(R.anim.slide_in_up, R.anim.no_change);
                break;
            case in_right_to_left:
                activity.overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
                break;
            case in_none:
                break;
        }
    }

    public BaseActivity getBaseActivity() {
        return this;
    }

    public BaseActivity getContext() {
        return this;
    }

    public AppCompatActivity getActivity() {
        return this;
    }


    private ViewTreeObserver.OnGlobalLayoutListener keyboardLayoutListener = new ViewTreeObserver.OnGlobalLayoutListener() {
        @Override
        public void onGlobalLayout() {
            Log.d(TAG, "onGlobalLayout change");
            Log.d(TAG, "rootLayout.getRootView().getHeight() " + rootLayout.getRootView().getHeight());
            Log.d(TAG, "rootLayout.getHeight() " + rootLayout.getHeight());

            int heightDiff = rootLayout.getRootView().getHeight() - rootLayout.getHeight();
            int contentViewTop = getWindow().findViewById(Window.ID_ANDROID_CONTENT).getTop();

            Log.d(TAG, "heightDiff " + heightDiff);
            Log.d(TAG, "contentViewTop " + contentViewTop);


            LocalBroadcastManager broadcastManager = LocalBroadcastManager.getInstance(BaseActivity.this);


            if (heightDiff <= contentViewTop) {
                onHideKeyboard();

                Intent intent = new Intent("KeyboardWillHide");
                broadcastManager.sendBroadcast(intent);
            } else {
                int keyboardHeight = heightDiff - contentViewTop;
                onShowKeyboard(keyboardHeight);

                Intent intent = new Intent("KeyboardWillShow");
                intent.putExtra("KeyboardHeight", keyboardHeight);
                broadcastManager.sendBroadcast(intent);
            }


        }
    };

    private boolean keyboardListenersAttached = false;
    private ViewGroup rootLayout;

    protected void onShowKeyboard(int keyboardHeight) {
    }

    protected void onHideKeyboard() {
    }

    protected void attachKeyboardListeners() {
        Log.d(TAG, "attachKeyboardListeners");
        if (keyboardListenersAttached) {
            return;
        }


        keyboardListenersAttached = true;
    }


    public static Handler getHandler(Context context) {
        if (context instanceof BaseActivity)
            return ((BaseActivity) context).getHandler();
        return null;
    }

    public static void post(Context context, Runnable runnable) {
        try {
            if (context instanceof BaseActivity)
                ((BaseActivity) context).getHandler().post(runnable);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void postDelayed(Context context, Runnable runnable, long delayTime) {
        try {
            if (context instanceof BaseActivity)
                ((BaseActivity) context).getHandler().postDelayed(runnable, delayTime);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean isDestroyed() {
        return super.isDestroyed();
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "onDestroy() called with: " + "");
        hideProgress();

        super.onDestroy();
        unregisterBroadcastReceiver();

        if (keyboardListenersAttached) {
            rootLayout.getViewTreeObserver().removeGlobalOnLayoutListener(keyboardLayoutListener);
        }

        try {
            ViewGroupCleanerUtils.clean((ViewGroup) findViewById(android.R.id.content));
            ViewGroupCleanerUtils.destroy();
        } catch (Exception e) {
            e.printStackTrace();
        }
        ViewUtils.hideKeyboard(this);
        callbackTakePicture = null;
        callbackRecordVideo = null;
        callbackSelectPicture = null;
        callbackSelectVideo = null;

    }

    TakePictureUtils.OnCallbackTakePicture callbackTakePicture = null;
    RecordVideoUtils.OnCallback callbackRecordVideo = null;
    TakePictureUtils.OnCallbackTakePicture callbackSelectPicture = null;
    RecordVideoUtils.OnCallback callbackSelectVideo = null;
    public AndroidPermissionUtils.OnCallbackRequestPermission callbackRequestPermission = null;

    public void selectPicture(TakePictureUtils.OnCallbackTakePicture callback) {
        callbackSelectPicture = callback;
        TakePictureUtils.selectPicture(getActivity());
    }

    public void takePicture(TakePictureUtils.OnCallbackTakePicture callback) {
        callbackTakePicture = callback;
        TakePictureUtils.takeImage(getActivity());
    }

    public void recordVideo(RecordVideoUtils.OnCallback callback) {
        callbackRecordVideo = callback;
        RecordVideoUtils.recordVideo(getActivity());
    }

    public void selectVideo(RecordVideoUtils.OnCallback callback) {
        callbackSelectVideo = callback;
        RecordVideoUtils.selectVideo(getActivity());
    }


    GoogleAccountUtils.OnCallback googleAccountCallBack;

    public void selectAccount(GoogleAccountCredential credential, GoogleAccountUtils.OnCallback callback) {
        this.googleAccountCallBack = callback;
        GoogleAccountUtils.selectAccount(credential, this);
    }

    public void requestPermission(AndroidPermissionUtils.OnCallbackRequestPermission onCallbackRequestPermission, AndroidPermissionUtils.TypePermission... typePermissions) {
        callbackRequestPermission = onCallbackRequestPermission;
        boolean hasPermission = AndroidPermissionUtils.mayRequestPermission(getActivity(), typePermissions);
        if (callbackRequestPermission != null) {
            if(hasPermission) callbackRequestPermission.onSuccess();
        }
    }

    public static void requestPermission(Context context, AndroidPermissionUtils.OnCallbackRequestPermission onCallbackRequestPermission, AndroidPermissionUtils.TypePermission... typePermissions) {
        if (context instanceof BaseActivity) {
            ((BaseActivity) context).requestPermission(onCallbackRequestPermission, typePermissions);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        AndroidPermissionUtils.getPermission(requestCode, grantResults, callbackRequestPermission, this);
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        GoogleAccountUtils.retrieveData(requestCode, resultCode, data, googleAccountCallBack);
        TakePictureUtils.getPictureTaken(requestCode, resultCode, callbackTakePicture);
        RecordVideoUtils.getRecordVideo(requestCode, resultCode, callbackRecordVideo);

        if (Build.VERSION.SDK_INT < 19) {
            TakePictureUtils.getPictureSelected(requestCode, resultCode, data, callbackSelectPicture);
            RecordVideoUtils.getVideoSelected(requestCode, resultCode, data, callbackSelectVideo);
        } else {
            TakePictureUtils.getPictureSelected(getActivity(), requestCode, resultCode, data, callbackSelectPicture);
            RecordVideoUtils.getVideoSelected(getActivity(), requestCode, resultCode, data, callbackSelectVideo);
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    public boolean isSaveInstance = false;

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putBoolean(NEED_RESTART, true);
        isSaveInstance = true;
        Log.d(TAG, "onSaveInstanceState: " + true);
        super.onSaveInstanceState(outState);
    }

    public boolean restartIfError(Bundle outState) {
        try {
            if (outState != null && outState.getBoolean(NEED_RESTART, false)) {
                startActivity(getIntent());
                finish(500);
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public int getLineNumber() {
        return Thread.currentThread().getStackTrace()[2].getLineNumber();
    }


    public static BaseActivity from(Fragment fragment) {
        try {
            return (BaseActivity) fragment.getContext();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void restart(FragmentActivity activity, int i) {
        try {
            BaseActivity.from(activity).restart(i);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void restart(long delayTime) {
        try {
            startActivity(getIntent());
            finish(delayTime);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static BaseActivity from(Context context) {
        try {
            return (BaseActivity) context;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void setActionBarOverlay() {
        try {
            getWindow().requestFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean isPackageInstalled(String packagename, PackageManager packageManager) {
        try {
            packageManager.getPackageInfo(packagename, PackageManager.GET_ACTIVITIES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    public void sendBroadcastOpenPage(String action) {
        Intent intent = new Intent(action);
        intent.putExtra(Constants.IGNORE_CODE, ignoreBroadCastCode);
        sendBroadcast(intent);
    }

    public boolean finishIfHasOpened(@NonNull String action, @NonNull Intent intent) {
        if (action.equals(intent.getAction())) {
            if (!intent.getStringExtra(Constants.IGNORE_CODE).equals(ignoreBroadCastCode)) {
                finish(false, 200);
                return true;
            }
        }
        return false;
    }

    public void setStatusBarTranslucent(boolean makeTranslucent) {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT)
            if (makeTranslucent) {
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            } else {
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            }
    }

}
