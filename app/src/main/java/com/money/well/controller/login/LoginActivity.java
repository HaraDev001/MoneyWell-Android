package com.money.well.controller.login;


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginBehavior;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.money.well.R;
import com.money.well.common.OnMultiClickListener;
import com.money.well.controller.base.BaseActivity;
import com.money.well.controller.home.HomeNewActivity;
import com.money.well.controller.signup.VerifyEmailActivity;
import com.money.well.utils.dialog.CustomProgress;
import com.money.well.utils.network.HttpCall;
import com.money.well.utils.network.HttpRequest;
import com.money.well.common.Global;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.HashMap;

import static com.money.well.common.Global.sdkFacebook;
import static com.money.well.common.Global.sdkGoogle;
import static com.money.well.common.Global.strVerifyEmailActivity;
import static com.money.well.common.Global.userEmailBindingStatus;

public class LoginActivity extends BaseActivity {

    private Button btnLogin;
    private TextView txtForgotPassword;
    private TextView txtNewMoneyWell;
    private TextView txtSignUp;
    private EditText edtEmail;
    private EditText edtPassword;
    private ImageView imgFacebook;
    private ImageView imgGoogle;

    private LoginButton facebookLoginButton;

    private CallbackManager callbackManager;

    int RG_SIGN_IN = 0;
    SignInButton signInButton;
    GoogleSignInClient mGoogleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        thisActivity = this;
        thisContext = this;
        thisView = findViewById(R.id.lay_login);

        initBasicUI();

        callbackManager = CallbackManager.Factory.create();
        facebookLoginButton.setReadPermissions(Arrays.asList("email", "public_profile"));
        facebookLoginButton.setLoginBehavior(LoginBehavior.WEB_ONLY);

        facebookLoginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {

                Log.d("loginActivity", "facebook login success!");
                if (loginResult.getAccessToken() == null){
                    Log.e("LoginActivity", "access token null");
                }
                else{
                    if (!strVerifyEmailActivity.equals("VerifyEmailActivity")){
                        loadUserProfile(loginResult.getAccessToken());
                    }
                }

            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {

            }
        });

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
    }

    private void initBasicUI(){
        btnLogin = findViewById(R.id.btn_login);
        btnLogin.setOnClickListener(new OnMultiClickListener() {
            @Override
            public void onMultiClick(View v) {
                if (Global._isTest){
                    tryUserLogin();
                }
                else {
                    tryUserLogin();
                }
//                tryUserLogin();

            }
        });
        txtForgotPassword = findViewById(R.id.txt_forgot_password);
        txtForgotPassword.setOnClickListener(new OnMultiClickListener() {
            @Override
            public void onMultiClick(View v) {
                Intent intent = new Intent(LoginActivity.this, SendRequestActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT|Intent.FLAG_ACTIVITY_CLEAR_TOP);
                thisActivity.startActivity(intent);

//                finish();
            }
        });
        txtNewMoneyWell = findViewById(R.id.txt_new_moneywell);
        txtSignUp = findViewById(R.id.txt_sign_up);
        txtSignUp.setOnClickListener(new OnMultiClickListener() {
            @Override
            public void onMultiClick(View v) {
                Intent intent = new Intent(LoginActivity.this, VerifyEmailActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT|Intent.FLAG_ACTIVITY_CLEAR_TOP);
                thisActivity.startActivity(intent);

//                finish();
            }
        });
        edtEmail = findViewById(R.id.txt_email);
        edtPassword= findViewById(R.id.txt_password);
        imgFacebook = findViewById(R.id.img_facebook);
        imgFacebook.setOnClickListener(new OnMultiClickListener() {
            @Override
            public void onMultiClick(View v) {
                facebookLoginButton.performClick();
            }
        });
        imgGoogle = findViewById(R.id.img_google);
        imgGoogle.setOnClickListener(new OnMultiClickListener() {
            @Override
            public void onMultiClick(View v) {
                googleSignIn();
            }
        });

        facebookLoginButton = findViewById(R.id.login_button);
        signInButton = findViewById(R.id.sign_in_button);

        if (Global._isTest){
            edtEmail.setText("mars@mail.com");
            edtPassword.setText("aa");
        }
    }

    private void googleSignIn(){
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RG_SIGN_IN);
    }

    //region google login
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RG_SIGN_IN){
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            showInformation(account);
        } catch (ApiException e) {
            Log.e("Google sign in Error", "signInResult: Failed categoryName");
            Toast.makeText(LoginActivity.this, "Google sign in failed", Toast.LENGTH_LONG).show();
        }
    }

    //endregion

    //region facebook login

//    AccessTokenTracker tokenTracker = new AccessTokenTracker() {
//        @Override
//        protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken currentAccessToken) {
//            if (currentAccessToken == null){
////                Toast.makeText(LoginActivity.this, "User Logged out", Toast.LENGTH_SHORT).show();
//            }
//
//            else{
//                if (!strVerifyEmailActivity.equals("VerifyEmailActivity")){
//                    loadUserProfile(currentAccessToken);
//                }
//            }
//        }
//    };

    private void loadUserProfile(AccessToken newAccessToken){
        GraphRequest request = GraphRequest.newMeRequest(newAccessToken, new GraphRequest.GraphJSONObjectCallback() {
            @Override
            public void onCompleted(JSONObject object, GraphResponse response) {

                try {
                    String first_name = object.getString("first_name");
                    String last_name = object.getString("last_name");
//                    String email = object.getString("email");
                    String id = object.getString("id");
                    String image_url = "http://graph.facebook.com/" + id + "/picture?type=normal";
                    trySocialLogin(id, Global.sdkFacebook);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        Bundle parameters = new Bundle();
        parameters.putString("fields", "first_name, last_name, email, id");
        request.setParameters(parameters);
        request.executeAsync();
    }
    //endregion

    private void showInformation(GoogleSignInAccount account){
        String email = account.getEmail();
        String id = account.getId();
        trySocialLogin(id, sdkGoogle);
    }

    private void tryUserLogin(){
        String email = edtEmail.getText().toString();
        String password = edtPassword.getText().toString();
        if (checkInputValues(email,password)) {
            showProgressDialog(getString(R.string.message_content_loading));
            HttpCall httpCallPost = new HttpCall();
            httpCallPost.setMethodtype(HttpCall.GET);
            String url = "\n" + getServerUrl() + getString(R.string.str_url_login);
            httpCallPost.setUrl(url);
            HashMap<String,String> paramsPost = new HashMap<>();
            paramsPost.put("email", email);
            paramsPost.put("password", getMD5(password));
            httpCallPost.setParams(paramsPost);
            new HttpRequest(){
                @Override
                public void onResponse(String str) {
                    super.onResponse(str);
                    try {
                        JSONObject response = new JSONObject(str);
                        int responseCode = (int) response.get("code");
                        if (responseCode == Global.RESULT_SUCCESS) {
                            CustomProgress.dismissDialog();
                            JSONObject responseData =  response.getJSONObject("data");
                            Global.userId = responseData.getInt("id");
                            Global.userName = responseData.optString("name");
                            Global.userEmail = responseData.optString("email");
                            Global.userPaypalEmail = responseData.optString("paypal_email");
                            Global.userPassword = password;
                            Global.userPhotoUrl = responseData.optString("photo_url");
                            Global.userBirthday = responseData.optString("birthday");
                            Global.userNationId = responseData.getInt("nation_id");
                            Global.userGender = responseData.getInt("gender");
                            Intent intent = new Intent(LoginActivity.this, HomeNewActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT|Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            thisActivity.startActivity(intent);
                            finish();
                        }
                        else if (responseCode == Global.RESULT_FAILED) {
                            CustomProgress.dismissDialog();
                            showToast("Failed");
                        }
                        else if (responseCode == Global.RESULT_EMAIL_PASSWORD_INCORRECT) {
                            CustomProgress.dismissDialog();
                            showToast("Email and password incorrect");
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        CustomProgress.dismissDialog();
                        showToast("Network error");
                    }

                }
            }.execute(httpCallPost);
        }
    }

    public boolean checkInputValues(String email, String password) {
        boolean isValue = true;
        if(email.equals("") || password.equals("")) {
            isValue = false;
        }
        return isValue;
    }

    private void trySocialLogin(String email, final int sdkType){
        if (sdkType == sdkFacebook){
            facebookLogout();
        }
        showProgressDialog(getString(R.string.message_content_loading));
        HttpCall httpCallPost = new HttpCall();
        httpCallPost.setMethodtype(HttpCall.GET);
        String url = "\n" + getServerUrl() + getString(R.string.str_url_social_login);
        httpCallPost.setUrl(url);
        HashMap<String,String> paramsPost = new HashMap<>();
        paramsPost.put("facebook_id", email);
        paramsPost.put("type", sdkType + "");
        httpCallPost.setParams(paramsPost);
        new HttpRequest(){
            @Override
            public void onResponse(String str) {
                super.onResponse(str);
                try {
                    JSONObject response = new JSONObject(str);
                    int responseCode = (int) response.get("code");
                    if (responseCode == Global.RESULT_SUCCESS) {
                        CustomProgress.dismissDialog();
                        JSONObject responseData =  response.getJSONObject("data");
                        Global.userId = responseData.getInt("id");
                        if (sdkType == Global.sdkFacebook){
                            Global.userFacebookId = responseData.optString("facebook_id");
                        }
                        else if (sdkType == sdkGoogle){
                            Global.userGoogleId = responseData.optString("google_id");
                        }
                            Global.userEmail = responseData.getString("email");
                            Global.userId = responseData.getInt("id");
                            Global.userName = responseData.optString("name");
                            Global.userEmail = responseData.optString("email");
                            Global.userPaypalEmail = responseData.optString("paypal_email");
                            Global.userPhotoUrl = responseData.optString("photo_url");
                            Global.userBirthday = responseData.optString("birthday");
                            Global.userNationId = responseData.getInt("nation_id");
                            Global.userGender = responseData.getInt("gender");
                            Intent intent = new Intent(LoginActivity.this, HomeNewActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT|Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            thisActivity.startActivity(intent);
                            finish();
                    }
                    else if (responseCode == Global.RESULT_FAILED) {
                        CustomProgress.dismissDialog();
                        showToast("Login failed");
                    }
                    else if (responseCode == Global.RESULT_EMAIL_PASSWORD_INCORRECT) {
                        CustomProgress.dismissDialog();
                        showToast("This account doesn't exist");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    CustomProgress.dismissDialog();
                    showToast("Network error");
                }
            }
        }.execute(httpCallPost);
    }
}
