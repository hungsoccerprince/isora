package com.isorasoft.phusan.activity.user.login_regiter;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.isorasoft.mllibrary.Connectivity;
import com.isorasoft.mllibrary.utils.ConvertUtils;
import com.isorasoft.mllibrary.utils.StringUtils;
import com.isorasoft.mllibrary.utils.ViewUtils;
import com.isorasoft.phusan.BaseActivity;
import com.isorasoft.phusan.IntentServiceSync;
import com.isorasoft.phusan.R;
import com.isorasoft.phusan.constants.Constants;
import com.isorasoft.phusan.dataaccess.AnalyticInfo;
import com.isorasoft.phusan.dataaccess.UserInfo;

import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.UnknownHostException;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LoginActivity extends BaseActivity implements GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = LoginActivity.class.getSimpleName();

    CallbackManager callbackManager;
    private static final int RC_SIGN_IN = 9001;
    private GoogleApiClient mGoogleApiClient;
    View btnLoginFacebook, btnLoginGoogle;

    @BindView(R.id.txtEmail)
    EditText txtEmail;
    @BindView(R.id.tilEmail)
    TextInputLayout tilEmail;
    @BindView(R.id.txtPassword)
    EditText txtPassword;
    @BindView(R.id.tilPassword)
    TextInputLayout tilPassword;

    @BindString(R.string.msg_email_or_nickname_invalid)
    String msg_email_invalid;

    @BindString(R.string.msg_password_invalid)
    String msg_password_invalid;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        ViewUtils.hideKeyboard(getActivity());
        setSlideOut(TypeSlideOut.out_left_to_right);

        String request = getIntent().getStringExtra("REQUEST");
        if(request != null && request.equals("TOKEN_NEW")){
            Toast.makeText(getActivity(), R.string.msg_request_logout, Toast.LENGTH_LONG).show();
        }

        callbackManager = CallbackManager.Factory.create();
        final LoginButton mButtonLogin = (LoginButton) findViewById(R.id.login_button);

        mButtonLogin.setReadPermissions("email");
        LoginManager.getInstance().logOut();

//        try {
//            PackageInfo info = getPackageManager().getPackageInfo(
//                    "com.isorasoft.benhviene",
//                    PackageManager.GET_SIGNATURES);
//            for (Signature signature : info.signatures) {
//                MessageDigest md = MessageDigest.getInstance("SHA");
//                md.update(signature.toByteArray());
//                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
//            }
//        } catch (PackageManager.NameNotFoundException e) {
//
//        } catch (NoSuchAlgorithmException e) {
//
//        }

//        mButtonLogin.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
//            @Override
//            public void onSuccess(LoginResult loginResult) {
//                // App code
//                System.out.println("onSuccess");
//                String accessToken = loginResult.getAccessToken().getToken();
//                Log.i("accessToken", accessToken);
//
//                GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
//
//                    @Override
//                    public void onCompleted(JSONObject object, GraphResponse response) {
//                        Log.i("LoginActivity", response.toString());
//                        // Get facebook data from login
//                        Bundle bFacebookData = getFacebookData(object);
//                    }
//                });
//                Bundle parameters = new Bundle();
//                parameters.putString("fields", "id, first_name, last_name, email,gender, birthday, location"); // Parámetros que pedimos a facebook
//                request.setParameters(parameters);
//                request.executeAsync();
//            }
//
//            @Override m
//            public void onCancel() {
//                // App code
//                Log.d(TAG, "onCancel: ");
//                Toast.makeText(getActivity(), "Fb onCancel", Toast.LENGTH_SHORT).show();
//            }
//
//            @Override
//            public void onError(FacebookException exception) {
//                // App code
//                Log.d(TAG, "onError: ");
//                Toast.makeText(getActivity(), "Fb onError", Toast.LENGTH_SHORT).show();
//            }
//        });


        mButtonLogin.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        Log.d(TAG, "Login success");

                        GraphRequest request = GraphRequest.newMeRequest(
                                loginResult.getAccessToken(),
                                new GraphRequest.GraphJSONObjectCallback() {
                                    @Override
                                    public void onCompleted(final JSONObject object, final GraphResponse response) {
                                        new Handler().postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                try {
                                                    Log.d(TAG, "run: " + object.toString());

                                                    JsonParser parser = new JsonParser();
                                                    String strRawResponse = response.getRawResponse();
                                                    Log.d(TAG, "strRawResponse " + strRawResponse);
                                                    JsonObject data = parser.parse(strRawResponse).getAsJsonObject();
                                                    String fbid = data.get("id").getAsString();
                                                    String name = "";
                                                    try {
                                                        name = ConvertUtils.toString(data.get("name"));
                                                    } catch (Exception ex) {
                                                        Log.e(TAG, "missing name");
                                                    }

                                                    loginSocial(fbid, Constants.SOCIAL_FB, name, "");
                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                    Toast.makeText(getActivity(), "Có lỗi xảy ra, bạn vui lòng thử lại!", Toast.LENGTH_LONG).show();
                                                }
                                            }
                                        }, Constants.DELAY_LOGIN_FACEBOOK);
                                    }
                                }
                        );
                        Bundle parameters = new Bundle();
                        parameters.putString("fields", "id, email, gender, birthday, name"); // Parámetros que pedimos a facebook
                        request.setParameters(parameters);
                        request.executeAsync();
                    }
                    @Override
                    public void onCancel() {
                        Log.w(TAG, "Login Cancel");
                    }

                    @Override
                    public void onError(FacebookException error) {
                        Log.e(TAG, "Login Error " + error.getMessage());
                        error.printStackTrace();
                    }
                }
        );

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        btnLoginFacebook = findViewById(R.id.loginFacebook);
        btnLoginFacebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!Connectivity.isConnected()) {
                    showCheckConnection();
                    return;
                }
                mButtonLogin.performClick();
            }
        });

        btnLoginGoogle = findViewById(R.id.loginGoogle);
        btnLoginGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!Connectivity.isConnected()) {
                    showCheckConnection();
                    return;
                }
                signIn();
            }
        });

    }

    private Bundle getFacebookData(JSONObject object) {

        try {
            Bundle bundle = new Bundle();
            String id = object.getString("id");

            try {
                URL profile_pic = new URL("https://graph.facebook.com/" + id + "/picture?width=200&height=150");
                Log.i("profile_pic", profile_pic + "");
                bundle.putString("profile_pic", profile_pic.toString());

            } catch (MalformedURLException e) {
                e.printStackTrace();
                return null;
            }

            bundle.putString("idFacebook", id);
            if (object.has("first_name"))
                bundle.putString("first_name", object.getString("first_name"));
            if (object.has("last_name"))
                bundle.putString("last_name", object.getString("last_name"));
            if (object.has("email"))
                bundle.putString("email", object.getString("email"));
            if (object.has("gender"))
                bundle.putString("gender", object.getString("gender"));
            if (object.has("birthday"))
                bundle.putString("birthday", object.getString("birthday"));
            if (object.has("location"))
                bundle.putString("location", object.getJSONObject("location").getString("name"));
            return bundle;
        }
        catch(Exception e) {
            Log.d(TAG,"Error parsing JSON");
            e.printStackTrace();
        }
        return  null;
    }

    public void loginSocial(final String socialId, final int socialType, final String displayName, final String email) {

        Log.d(TAG, "loginSocial: " + socialId + socialType + displayName);

        new AsyncTask<Void, JsonObject, Void>() {
            @Override
            protected void onPreExecute() {
                showProgress();
            }

            @Override
            protected Void doInBackground(Void... params) {
                try {
                    publishProgress(UserInfo.loginSocial(socialType, socialId, displayName, email));
                } catch (final SocketTimeoutException e) {
                    showTimeOutError(new ReloadListener() {
                        @Override
                        public void reload() {
                            loginSocial(socialId, socialType, displayName, email);
                        }
                    });
                } catch (Exception ex) {
                    ex.printStackTrace();
                    publishProgress();
                }
                return null;
            }

            @Override
            protected void onProgressUpdate(JsonObject... values) {
                if (values.length != 0) {
                    UserInfo.setCurrentUser(LoginActivity.this, values[0]);
                } else {
                    Toast.makeText(LoginActivity.this, getResources().getString(R.string.activity_login_failed), Toast.LENGTH_LONG).show();
                }
                super.onProgressUpdate(values);
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                hideProgress();
                if (UserInfo.isLogin()) {

                    Intent intent = new Intent(Constants.ACTION_LOGIN);
                    sendBroadcast(intent);
                    finish();
                }
                super.onPostExecute(aVoid);
            }
        }.execute();
    }


    @OnClick(R.id.btnRegister)
    public void register(View view) {
        startActivity(RegisterActivity.class, TypeSlideIn.in_right_to_left);
        finish(500);
    }

    @OnClick(R.id.btnForgotPassword)
    public void forgotPassword(View view) {
        startActivity(ForgotPasswordActivity.class, TypeSlideIn.in_right_to_left);
        finish(500);
    }

    @OnClick(R.id.btnClose)
    void close() {
        this.finish();
    }


    @OnClick(R.id.btnLogin)
    public void login(View view) {
        if (validate()) {
            if (!Connectivity.isConnected()) {
                showCheckConnection();
                return;
            }
            login(txtEmail.getText().toString().trim(), txtPassword.getText().toString());
        }
    }

    private void login(final String email, final String password) {

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
                if (values != null && values[0] != null) {

                    JsonObject account = ConvertUtils.toJsonObject(values[0].get("Account"));
                    if(ConvertUtils.toInt(account.get("Role")) == UserInfo.Role.Admin.getValue()){
                        int adminRole = ConvertUtils.toInt(account.get("AdminRole"));
                        int check = (byte) adminRole & 1;
                        if(check != 1){
                            showSnackBar("Vui lòng đăng nhập trên website!");
                        }else{
                            UserInfo.setCurrentUser(getActivity(), values[0]);
                            Intent intent = new Intent(Constants.ACTION_LOGIN);
                            sendBroadcast(intent);
                            finish();
                        }
                    }else{
                        UserInfo.setCurrentUser(getActivity(), values[0]);
                        Intent intent = new Intent(Constants.ACTION_LOGIN);
                        sendBroadcast(intent);
                        finish();
                    }
                }
                super.onProgressUpdate(values);
            }

            @Override
            protected Void doInBackground(Void... params) {
                try {
                    publishProgress(UserInfo.login(getActivity(), email, password));
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



    private boolean validate() {
        tilEmail.setError(null);
        tilEmail.setErrorEnabled(false);
        tilPassword.setError(null);
        tilPassword.setErrorEnabled(false);
        if (txtEmail.getText().toString().trim().isEmpty()) {
            tilEmail.setErrorEnabled(true);
            tilEmail.setError(msg_email_invalid);
            txtEmail.requestFocus();
            return false;
        }
        if (!StringUtils.Validate.isPassword(txtPassword.getText())) {
            tilPassword.setErrorEnabled(true);
            tilPassword.setError(msg_password_invalid);
            txtPassword.requestFocus();
            return false;
        }
        return true;
    }

    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void handleSignInResult(GoogleSignInResult result) {
        Log.d(TAG, "handleSignInResult:" + result.isSuccess());

        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            final GoogleSignInAccount acct = result.getSignInAccount();
            try {
                loginSocial(acct.getId(),
                        Constants.SOCIAL_GG,
                        acct.getDisplayName(), acct.getEmail());
            } catch (Exception e) {
                e.printStackTrace();
            }

        } else {
            Toast.makeText(LoginActivity.this, getResources().getString(R.string.activity_login_failed), Toast.LENGTH_LONG).show();
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        Log.d(TAG, "onActivityResult ");
        Log.d(TAG, "onActivityResult requestCode " + requestCode);
        Log.d(TAG, "onActivityResult resultCode " + resultCode);
        Log.d(TAG, "onActivityResult data " + data);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        } else {
            callbackManager.onActivityResult(requestCode, resultCode, data);
        }
    }


    @Override
    public AnalyticInfo.Screen getScreen() {
        return AnalyticInfo.Screen.ScreenLogin;
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}