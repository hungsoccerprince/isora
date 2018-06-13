package com.isorasoft.phusan.activity.user.login_regiter;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.isorasoft.mllibrary.utils.AndroidPermissionUtils;
import com.isorasoft.mllibrary.utils.ViewUtils;
import com.isorasoft.phusan.BaseActivity;
import com.isorasoft.phusan.R;
import com.isorasoft.phusan.dataaccess.AnalyticInfo;
import com.soundcloud.android.crop.Crop;

import java.io.File;
import java.util.Date;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class RegisterActivity extends BaseActivity {

    private static final String TAG = RegisterActivity.class.getSimpleName();

    private static int b1 = 1;
    private static int b2 = 2;
    private static int b3 = 4;
    private static int b4 = 8;
    private static int b5 = 16;
    private static int b6 = 32;
    private static int b7 = 64;
    private static int b8 = 128;

    private int checkRole = 0;

    @BindView(R.id.txtNickName)
    EditText txtNickName;
    @BindView(R.id.tilNickName)
    TextInputLayout tilNickName;
    @BindView(R.id.txtEmail)
    EditText txtEmail;
    @BindView(R.id.tilEmail)
    TextInputLayout tilEmail;
    @BindView(R.id.txtPassword)
    EditText txtPassword;
    @BindView(R.id.tilPassword)
    TextInputLayout tilPassword;
    @BindView(R.id.txtConfirmPassword)
    EditText txtConfirmPassword;
    @BindView(R.id.tilConfirmPassword)
    TextInputLayout tilConfirmPassword;
    @BindView(R.id.ivAvatar)
    ImageView ivAvatar;

    @BindView(R.id.tilFullName)
    TextInputLayout tilFullName;

    @BindView(R.id.txtFullName)
    TextView txtFullName;

    @BindView(R.id.rbGroup)
    RadioGroup rbGroup;

    @BindView(R.id.rbOne)
    RadioButton rbOne;

    @BindView(R.id.rbTwo)
    RadioButton rbTwo;

    @BindView(R.id.rbThree)
    RadioButton rbThree;

    @BindView(R.id.btnLogin)
    TextView btnLogin;

    @BindString(R.string.msg_nickname_invalid)
    String msg_nickname_invalid;

    @BindString(R.string.msg_email_invalid)
    String msg_email_invalid;

    @BindString(R.string.msg_password_invalid)
    String msg_password_invalid;

    @BindString(R.string.msg_confirm_password_invalid)
    String msg_confirm_password_invalid;

    @BindView(R.id.viewSetRole)
    View viewSetRole;

    @BindView(R.id.check01)
    CheckBox check01;

    @BindView(R.id.check02)
    CheckBox check02;

    @BindView(R.id.check03)
    CheckBox check03;

    @BindView(R.id.check04)
    CheckBox check04;

    @BindView(R.id.check05)
    CheckBox check05;

    @BindView(R.id.check06)
    CheckBox check06;

    @BindView(R.id.check07)
    CheckBox check07;

    private Uri uriAvatar;
    private String departmentId;
    private int typeAccount;
    private int index = 2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ButterKnife.bind(this);
        ViewUtils.hideKeyboard(getActivity());

        typeAccount = 0;


    }

    @OnClick(R.id.btnClose)
    void close() {
        this.finish();
    }

    public void pickImage(View v) {
        BaseActivity.requestPermission(getContext(), new AndroidPermissionUtils.OnCallbackRequestPermission() {
            @Override
            public void onSuccess() {
                Crop.pickImage(getActivity());
            }

            @Override
            public void onFailed() {
                BaseActivity.showSnackBar(getContext(), R.string.common_request_permission_access_file);
            }

        }, AndroidPermissionUtils.TypePermission.PERMISSION_WRIRE_EXTERNAL_STORAGE);
    }

    private void handleCrop(int resultCode, Intent result) {
        Log.d(TAG, String.valueOf(result));
        if (resultCode == Activity.RESULT_OK) {
            uriAvatar = Crop.getOutput(result);
            Log.d(TAG, String.valueOf(uriAvatar));
            ivAvatar.setImageDrawable(getResources().getDrawable(R.drawable.ic_default_avatar));
            ivAvatar.setImageURI(uriAvatar);
        } else if (resultCode == Crop.RESULT_ERROR) {
            BaseActivity.showSnackBar(getContext(), "Vui lòng chọn ảnh có định dạng png, jpg, gif!");
        }
    }


    private void beginCrop(Uri source) {
        Uri destination = Uri.fromFile(new File(getContext().getCacheDir(), new Date().getTime() + ".png"));
        Crop.of(source, destination).asSquare().start(getActivity());
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent result) {
        if (requestCode == Crop.REQUEST_PICK && resultCode == Activity.RESULT_OK) {
            beginCrop(result.getData());
        } else if (requestCode == Crop.REQUEST_CROP) {
            handleCrop(resultCode, result);
        }
        super.onActivityResult(requestCode, resultCode, result);
    }

    @Override
    public AnalyticInfo.Screen getScreen() {
        return AnalyticInfo.Screen.ScreenRegister;
    }

    @OnClick(R.id.btnSelectAvatar)
    public void pickAvatar(View view) {
        pickImage(view);
    }

    @OnClick(R.id.ivAvatar)
    public void pickAvatar2(View view) {
        pickImage(view);
    }

    @OnClick(R.id.btnLogin)
    public void login(View view) {
        startActivity(LoginActivity.class, TypeSlideIn.in_right_to_left);
        finish(500);
    }

}
