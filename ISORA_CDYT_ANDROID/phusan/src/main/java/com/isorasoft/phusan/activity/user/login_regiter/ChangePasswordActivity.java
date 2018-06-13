package com.isorasoft.phusan.activity.user.login_regiter;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.view.View;
import android.widget.EditText;

import com.google.gson.JsonObject;
import com.isorasoft.phusan.BaseActivity;
import com.isorasoft.phusan.R;
import com.isorasoft.phusan.dataaccess.AnalyticInfo;
import com.isorasoft.phusan.dataaccess.UserInfo;
import com.isorasoft.mllibrary.Connectivity;
import com.isorasoft.mllibrary.utils.ConvertUtils;
import com.isorasoft.mllibrary.utils.StringUtils;
import com.isorasoft.mllibrary.utils.ViewUtils;

import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ChangePasswordActivity extends BaseActivity {

    private static final String TAG = ChangePasswordActivity.class.getSimpleName();

    @BindView(R.id.txtCurrentPassword)
    EditText txtCurrentPassword;
    @BindView(R.id.tilCurrentPassword)
    TextInputLayout tilCurrentPassword;
    @BindView(R.id.txtNewPassword)
    EditText txtNewPassword;
    @BindView(R.id.tilNewPassword)
    TextInputLayout tilNewPassword;
    @BindView(R.id.txtConfirmNewPassword)
    EditText txtConfirmNewPassword;
    @BindView(R.id.tilConfirmNewPassword)
    TextInputLayout tilConfirmNewPassword;
    @BindString(R.string.msg_current_password_invalid)
    String msg_current_password_invalid;

    @BindString(R.string.msg_input_current_password)
    String msg_input_current_password;

    @BindString(R.string.msg_password_invalid)
    String msg_password_invalid;

    @BindString(R.string.msg_confirm_password_invalid)
    String msg_confirm_password_invalid;
    private boolean changeByAdmin = false;
    private String email;


    public static void open(Context context) {
        Intent intent = new Intent(context, ChangePasswordActivity.class);
        context.startActivity(intent);
        BaseActivity.setSlideIn(context, TypeSlideIn.in_right_to_left);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        ButterKnife.bind(this);
        ViewUtils.hideKeyboard(getActivity());
        setSlideOut(TypeSlideOut.out_left_to_right);

        try {
            if(getIntent().getExtras().getBoolean("ByAdmin")){
                changeByAdmin = getIntent().getExtras().getBoolean("ByAdmin");
                email = getIntent().getStringExtra("Email");
            }else {
                changeByAdmin = false;
            }

        }catch (Exception e){
            e.printStackTrace();
        }

    }


    @OnClick(R.id.btnClose)
    void close() {
        this.finish();
    }

    private boolean validate() {
        tilCurrentPassword.setError(null);
        tilCurrentPassword.setErrorEnabled(false);
        tilNewPassword.setError(null);
        tilNewPassword.setErrorEnabled(false);
        tilConfirmNewPassword.setError(null);
        tilConfirmNewPassword.setErrorEnabled(false);

        if (txtCurrentPassword.getText().toString().isEmpty()) {
            tilCurrentPassword.setErrorEnabled(true);
            tilCurrentPassword.setError(msg_input_current_password);
            txtCurrentPassword.requestFocus();
            return false;
        }


        if (!StringUtils.Validate.isPassword(txtNewPassword.getText())) {
            tilNewPassword.setErrorEnabled(true);
            tilNewPassword.setError(msg_password_invalid);
            txtNewPassword.requestFocus();
            return false;
        }

        if (!txtNewPassword.getText().toString().equals(txtConfirmNewPassword.getText().toString())) {
            tilConfirmNewPassword.setErrorEnabled(true);
            tilConfirmNewPassword.setError(msg_confirm_password_invalid);
            txtConfirmNewPassword.requestFocus();
            return false;
        }

        return true;
    }

    @OnClick(R.id.btnSave)
    public void save(View view) {
        if (validate()) {
            if (!Connectivity.isConnected()) {
                showCheckConnection();
                return;
            }
            if (!UserInfo.isLogin()) {
                UserInfo.goToLogin(getActivity());
            }

            change_password(txtCurrentPassword.getText().toString(), txtNewPassword.getText().toString());
        }

    }

    private void change_password(final String currentpassword, final String newpassword) {

        new AsyncTask<Void, JsonObject, Void>() {
            @Override
            protected void onPostExecute(Void aVoid) {
                BaseActivity.hideProgress(getContext());
                super.onPostExecute(aVoid);
            }

            @Override
            protected void onPreExecute() {
                BaseActivity.showProgress(getContext());
                super.onPreExecute();
            }

            @Override
            protected void onProgressUpdate(JsonObject... values) {
                if (values.length != 0 && values[0] != null && values[0] != null) {
                    if(!changeByAdmin){
                        JsonObject jsonObject = values[0];
                        JsonObject token = ConvertUtils.toJsonObject(jsonObject.get("Token"));
                        UserInfo.setTokenUser(getActivity(), ConvertUtils.toString(token.get("Id")), newpassword);
                    }
                    showToast(R.string.msg_change_password_success);
                    finish();
                } else {
                    showSnackBar(R.string.activity_change_password_failed);
                }
                super.onProgressUpdate(values);
            }

            @Override
            protected Void doInBackground(Void... params) {
                try {
                    if(changeByAdmin)
                        publishProgress(UserInfo.changePassword(getActivity(), email, currentpassword, newpassword));
                    else
                        publishProgress(UserInfo.changePassword(getActivity(), UserInfo.getCurrentUserEmail(), currentpassword, newpassword));
                } catch (UnknownHostException e) {
                    showTimeOutError();

                } catch (SocketTimeoutException e) {
                    showTimeOutError();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                return null;
            }
        }.execute();
    }

    @Override
    public AnalyticInfo.Screen getScreen() {
        return AnalyticInfo.Screen.ScreenChangePassword;
    }
}