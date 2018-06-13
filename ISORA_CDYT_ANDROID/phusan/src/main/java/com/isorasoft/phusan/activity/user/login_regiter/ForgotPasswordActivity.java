package com.isorasoft.phusan.activity.user.login_regiter;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.view.View;
import android.widget.EditText;

import com.google.gson.JsonObject;
import com.isorasoft.phusan.R;
import com.isorasoft.phusan.dataaccess.AnalyticInfo;
import com.isorasoft.phusan.dataaccess.UserInfo;
import com.isorasoft.mllibrary.Connectivity;
import com.isorasoft.phusan.BaseActivity;
import com.isorasoft.mllibrary.utils.StringUtils;
import com.isorasoft.mllibrary.utils.ViewUtils;

import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ForgotPasswordActivity extends BaseActivity {

    private static final String TAG = ForgotPasswordActivity.class.getSimpleName();

    @BindView(R.id.txtEmail)
    EditText txtEmail;
    @BindView(R.id.tilEmail)
    TextInputLayout tilEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        ButterKnife.bind(this);
        ViewUtils.hideKeyboard(getActivity());
        setSlideOut(TypeSlideOut.out_left_to_right);
    }


    @OnClick(R.id.btnClose)
    void close() {
        this.finish();
    }

    @OnClick(R.id.btnForgotPassword)
    public void forgot(View view) {
        tilEmail.setError(null);
        tilEmail.setErrorEnabled(false);
        if (!StringUtils.Validate.isEmail(txtEmail.getText().toString())) {
            tilEmail.setError(getString(R.string.msg_email_invalid));
            tilEmail.setErrorEnabled(true);
            return;
        }
        if (!Connectivity.isConnected()) {
            showCheckConnection();
            return;
        }

        forgot(txtEmail.getText().toString());

    }

    private void forgot(final String email) {

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
                if (values.length != 0) {
                    if (values[0] != null && values[0] != null) {
                        showSnackBar(R.string.msg_forgot_password_success);
                    } else {
                        showSnackBar(R.string.msg_not_exist_account);
                    }
                }

                super.onProgressUpdate(values);
            }

            @Override
            protected Void doInBackground(Void... params) {
                try {
                    if (UserInfo.checkExisAccount(email)) {
                        publishProgress(UserInfo.forgot(email));
                    } else {
                        showSnackBar(R.string.msg_not_exist_account);
                    }
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
        return AnalyticInfo.Screen.ScreenForgotPassword;
    }
}