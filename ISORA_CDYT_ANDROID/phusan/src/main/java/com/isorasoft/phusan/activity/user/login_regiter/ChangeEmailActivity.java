package com.isorasoft.phusan.activity.user.login_regiter;

import android.app.Dialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.text.Html;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.isorasoft.phusan.BaseActivity;
import com.isorasoft.phusan.R;
import com.isorasoft.phusan.dataaccess.AnalyticInfo;
import com.isorasoft.phusan.dataaccess.UserInfo;
import com.isorasoft.mllibrary.Connectivity;
import com.isorasoft.mllibrary.utils.ConvertUtils;
import com.isorasoft.mllibrary.utils.DialogUtils;
import com.isorasoft.mllibrary.utils.StringUtils;
import com.isorasoft.mllibrary.utils.ViewUtils;

import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by hungs on 7/11/2017.
 */

public class ChangeEmailActivity extends BaseActivity {

    @BindString(R.string.msg_email_invalid)
    String msg_email_invalid;

    @BindView(R.id.tilNewEmail)
    TextInputLayout tilNewEmail;

    @BindView(R.id.tilPassword)
    TextInputLayout tilPassword;

    @BindView(R.id.edtPassword)
    TextView edtPassword;

    @BindView(R.id.edtNewEmail)
    EditText edtNewEmail;
    private String email, userId;

    @OnClick(R.id.btnClose)
    void close() {
        this.finish();
    }

    private boolean validate() {
        tilNewEmail.setError(null);
        tilPassword.setError(null);

        if(edtPassword.getText().toString().equals("")){
            tilPassword.setErrorEnabled(true);
            tilPassword.requestFocus();
            tilPassword.setError("Nhập mật khẩu");
            return false;
        }

        if (!StringUtils.Validate.isEmail(edtNewEmail.getText())) {
            tilNewEmail.setErrorEnabled(true);
            tilNewEmail.setError(msg_email_invalid);
            tilNewEmail.requestFocus();
            return false;
        }

        String email = edtNewEmail.getText().toString();
        int split = email.indexOf("@");
        String s = (String) email.subSequence(0,split);
        if(s.length()>30){
            tilNewEmail.setErrorEnabled(true);
            tilNewEmail.setError("Email không hợp lệ");
            tilNewEmail.requestFocus();
            return false;
        }

        if(email.equals(UserInfo.getCurrentUserEmail())){
            tilNewEmail.setErrorEnabled(true);
            tilNewEmail.setError("Đã trùng với email hiện tại");
            tilNewEmail.requestFocus();
            return false;
        }

        if(!StringUtils.getMD5(edtPassword.getText().toString()).equals(ConvertUtils.toString(UserInfo.getCurrentUser().get("Password")))){
            tilPassword.setErrorEnabled(true);
            tilPassword.requestFocus();
            tilPassword.setError("Mật khẩu không đúng");
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

            changeEmail(edtNewEmail.getText().toString());
        }
    }

    private void changeEmail(final String email) {
        try {
            new AsyncTask<Void, JsonObject, Void>(){
                @Override
                protected void onPreExecute() {
                    showProgress();
                }

                @Override
                protected void onPostExecute(Void aVoid) {
                    hideProgress();
                }

                @Override
                protected void onProgressUpdate(JsonObject... values) {
                    if(values!=null && values[0] != null){
                        JsonObject jsonObject = values[0];

                        if(ConvertUtils.toInt(jsonObject.get("Conflict")) == 409){
                            StringBuilder stringBuilder = new StringBuilder();
                            stringBuilder.append("Địa chỉ Email này đã tồn tại, bạn vui lòng kiểm tra lại!");
                            showDialogFalse(stringBuilder, false);
                        }else {
                            StringBuilder stringBuilder = new StringBuilder();
                            stringBuilder.append("Thông tin xác nhận đã được gửi về email mới của bạn. <br>").append("Vui lòng đăng nhập email để xác nhận!");
                            showDialogFalse(stringBuilder, true);
                        }
                    }else {
                        Toast.makeText(getActivity(), "Có lỗi xảy ra, bạn vui lòng thử lại!", Toast.LENGTH_LONG).show();
                    }

                }

                @Override
                protected Void doInBackground(Void... params) {
                    try {
                        publishProgress(UserInfo.changeEmail(getActivity(), userId, email));
                    } catch (UnknownHostException e) {
                        e.printStackTrace();
                    } catch (SocketTimeoutException e) {
                        e.printStackTrace();
                        showTimeOutError();
                    }
                    return null;
                }
            }.execute();
        }catch (Exception e){
            e.printStackTrace();
            Toast.makeText(getActivity(), "Có lỗi xảy ra, bạn vui lòng thử lại!", Toast.LENGTH_LONG).show();
        }
    }

    private void showDialogFalse(StringBuilder stringBuilder, final boolean finish) {
        View view = getActivity().getLayoutInflater().inflate(R.layout.dialog_confirm_base, null);
        TextView tvTitle = (TextView) view.findViewById(R.id.tvTitle);
        TextView tvOK = (TextView) view.findViewById(R.id.tvOK);
        TextView tvNo = (TextView) view.findViewById(R.id.tvNo);
        tvNo.setVisibility(View.GONE);
        tvOK.setText("Đồng ý");

        tvTitle.setText(Html.fromHtml(stringBuilder.toString()));
        DialogUtils.Builder builder = new DialogUtils.Builder(getActivity(), view);
        final Dialog dialog = builder.create();
        dialog.setCancelable(false);
        dialog.show();

        tvOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                if(finish)
                    finish();
            }
        });
    }



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_email);
        ButterKnife.bind(this);
        ViewUtils.hideKeyboard(getActivity());
        setSlideOut(TypeSlideOut.out_left_to_right);

        email = getIntent().getStringExtra("Email");
        userId = getIntent().getStringExtra("UserId");


    }

    @Override
    public AnalyticInfo.Screen getScreen() {
        return null;
    }
}
