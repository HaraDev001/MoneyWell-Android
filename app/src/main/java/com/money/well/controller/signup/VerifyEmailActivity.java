package com.money.well.controller.signup;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.money.well.R;
import com.money.well.common.Global;
import com.money.well.common.OnMultiClickListener;
import com.money.well.controller.base.BaseActivity;
import com.money.well.controller.login.LoginActivity;
import com.money.well.utils.dialog.CustomProgress;
import com.money.well.utils.network.HttpCall;
import com.money.well.utils.network.HttpRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.HashMap;

import static com.money.well.common.Global.RESULT_EMAIL_DUPLICATE;
import static com.money.well.common.Global.RESULT_FAILED;
import static com.money.well.common.Global.RESULT_SUCCESS;
import static com.money.well.common.Global._isTest;
import static com.money.well.common.Global.userId;
import static com.money.well.common.Global.strVerifyEmailActivity;

public class VerifyEmailActivity extends BaseActivity {

    private TextView txtTopTitle;

    private LinearLayout laySendEmail;
    private LinearLayout layConfirmPassword;
    private LinearLayout layShowCode;
    private TextView txtShowCode;
    private Button btnSendRequest;
    private Button btnConfirm;
    private ImageView imgTopLeft;
    private TextView txtEmail;
    private TextView txtVerifyCode;
    private ImageView imgFacebook;
    private LoginButton facebookLoginButton;
    private CallbackManager callbackManager;

    private int pageStatus = 1; // 1: email status, 2: verify status

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_email);


        thisActivity = this;
        thisContext = this;
        thisView = findViewById(R.id.activity_verify_email);

        strVerifyEmailActivity = "VerifyEmailActivity";

        initBasicUI();
        callbackManager = CallbackManager.Factory.create();
        facebookLoginButton.setReadPermissions(Arrays.asList("email", "public_profile"));

        facebookLoginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {

            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {

            }
        });
        pageStatus = 1;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        strVerifyEmailActivity = "";
    }

    private void initBasicUI(){
        txtTopTitle = findViewById(R.id.txt_top_title);
        txtTopTitle.setText(getResources().getString(R.string.title_verify_email));
        laySendEmail= findViewById(R.id.lay_send_email);
        layConfirmPassword= findViewById(R.id.lay_verify_code);
        layShowCode = findViewById(R.id.lay_show_code);
        imgFacebook = findViewById(R.id.img_facebook);
        imgFacebook.setOnClickListener(new OnMultiClickListener() {
            @Override
            public void onMultiClick(View v) {
                facebookLoginButton.performClick();
            }
        });
        facebookLoginButton = findViewById(R.id.login_button);
        btnSendRequest= findViewById(R.id.btn_send_request);
        btnSendRequest.setOnClickListener(new OnMultiClickListener() {
            @Override
            public void onMultiClick(View v) {
                if (_isTest){
                    pageStatus = 2;
                    showPageStatus();
                }
                else {
                    getVerificationCode();
                }
            }
        });
        btnConfirm= findViewById(R.id.btn_confirm);
        btnConfirm.setOnClickListener(new OnMultiClickListener() {
            @Override
            public void onMultiClick(View v) {
                sendVerificationCode();
            }
        });

        imgTopLeft = findViewById(R.id.img_top_left);
        imgTopLeft.setOnClickListener(new OnMultiClickListener() {
            @Override
            public void onMultiClick(View v) {
                finish();
            }
        });
        txtEmail = findViewById(R.id.txt_email);
        if (_isTest){
            txtEmail.setText("mars@gmail.com");
        }
        txtVerifyCode = findViewById(R.id.txt_verify_code);
        txtShowCode = findViewById(R.id.txt_show_code);
    }

    //region facebook login

    AccessTokenTracker tokenTracker = new AccessTokenTracker() {
        @Override
        protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken currentAccessToken) {
            if (currentAccessToken == null){
               showToast("user logout");
            }
            else if (strVerifyEmailActivity.equals("VerifyEmailActivity")){
                loadUserProfile(currentAccessToken);
            }
        }
    };

    private void loadUserProfile(AccessToken newAccessToken){
        GraphRequest request = GraphRequest.newMeRequest(newAccessToken, new GraphRequest.GraphJSONObjectCallback() {
            @Override
            public void onCompleted(JSONObject object, GraphResponse response) {

                if (!object.optString("email").equals("") || object.optString("email") != null){
                    String email = object.optString("email");
                    String first_name = object.optString("first_name");
                    String last_name = object.optString("last_name");
                    String id = object.optString("id");
                    String image_url = "http://graph.facebook.com/" + id + "/picture?type=normal";
                    sendEmailSocialRegister(id, email, first_name, last_name, image_url, Global.sdkFacebook);
                }
                else {
                    showToast("Email not bind");
                }

            }
        });

        Bundle parameters = new Bundle();
        parameters.putString("fields", "first_name, last_name, email, id");
        request.setParameters(parameters);
        request.executeAsync();
    }
    //endregion

    private void getVerificationCode(){
        String email = txtEmail.getText().toString();
        if (email.equals("")){
            String msgContent = getResources().getString(R.string.notice_input_email);
            showToast(msgContent);
        }
        else if (!isEmailValid(email)){
            showToast("Please input correct email address");
        }
        else {
            HttpCall httpCallPost = new HttpCall();
            httpCallPost.setMethodtype(HttpCall.POST);
            String url = "\n" + getServerUrl() + getString(R.string.str_url_send_register_email);
            httpCallPost.setUrl(url);
            HashMap<String,String> paramsPost = new HashMap<>();
            paramsPost.put("email", email);
            httpCallPost.setParams(paramsPost);
            new HttpRequest(){
                @Override
                public void onResponse(String str) {
                    super.onResponse(str);
                    try {
                        JSONObject response = new JSONObject(str);
                        int responseCode = (int) response.get("code");
                        if (responseCode == RESULT_SUCCESS) {
                            JSONObject responseData =  response.getJSONObject("data");
                            userId = responseData.getInt("userId");
                            pageStatus = 2;
                            showPageStatus();
                        }
                        else if (responseCode == RESULT_EMAIL_DUPLICATE) {
                            String toastContent = getResources().getString(R.string.notice_email_duplicate);
                            showToast(toastContent);
                        }
                        else if (responseCode == RESULT_FAILED) {
                            String toastContent = getResources().getString(R.string.notice_verify_failed);
                            showToast(toastContent);
                        }
                    } catch (JSONException e) {
                        String toastContent = getResources().getString(R.string.notice_network_error);
                        showToast(toastContent);
                    }
                }
            }.execute(httpCallPost);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);

//        if (requestCode == RG_SIGN_IN){
//            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
//            handleSignInResult(task);
//        }
    }

    private void sendEmailSocialRegister(String sdkId, String email, String firstName, String lastName, String photo_url, int sdkType){
        facebookLogout();
        HttpCall httpCallPost = new HttpCall();
        httpCallPost.setMethodtype(HttpCall.POST);
        String url = "\n" + getServerUrl() + getString(R.string.str_url_social_register);
        httpCallPost.setUrl(url);
        HashMap<String,String> paramsPost = new HashMap<>();
        paramsPost.put("email", email);
        paramsPost.put("facebook_id", sdkId);
        paramsPost.put("name", firstName + " " + lastName);
        paramsPost.put("photo_url", photo_url);
        httpCallPost.setParams(paramsPost);
        new HttpRequest(){
            @Override
            public void onResponse(String str) {
                super.onResponse(str);
                try {
                    JSONObject response = new JSONObject(str);
                    int responseCode = (int) response.get("code");
                    if (responseCode == RESULT_SUCCESS) {
                        JSONObject responseData =  response.getJSONObject("data");
                        showToast("facebook register success");
                        Intent intent = new Intent(VerifyEmailActivity.this, LoginActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT|Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        thisActivity.startActivity(intent);
                        finish();
                    }
                    else if (responseCode == RESULT_EMAIL_DUPLICATE) {
                        String toastContent = getResources().getString(R.string.notice_email_duplicate);
                        showToast(toastContent);
                    }
                    else if (responseCode == RESULT_FAILED) {
                        String toastContent = getResources().getString(R.string.notice_verify_failed);
                        showToast(toastContent);
                    }
                } catch (JSONException e) {
                    String toastContent = getResources().getString(R.string.notice_network_error);
                    showToast(toastContent);
                }
            }
        }.execute(httpCallPost);

    }

    private void sendVerificationCode(){
        String code = txtVerifyCode.getText().toString();
        if (code.equals("")) {
            showToast("Pleas input code");
        }
        else {
            showProgressDialog(getString(R.string.message_content_loading));
            HttpCall httpCallPost = new HttpCall();
            httpCallPost.setMethodtype(HttpCall.POST);
            String url = "\n" + getServerUrl() + getString(R.string.str_url_send_verification_code);
            httpCallPost.setUrl(url);
            HashMap<String,String> paramsPost = new HashMap<>();
            if (_isTest){
                paramsPost.put("userId",  1 + "");
            }
            else {
                paramsPost.put("userId", userId + "");
            }
            paramsPost.put("code", code);
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
                            pageStatus = 1;
                            showPageStatus();
                            Intent intent = new Intent(VerifyEmailActivity.this, InputPasswordActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT|Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            thisActivity.startActivity(intent);
                            finish();
                        }
                        else if (responseCode == Global.RESULT_VERIFICATION_CODE_USED) {
                            CustomProgress.dismissDialog();
                            showToast("Code already used");
                        }
                        else if (responseCode == Global.RESULT_VERIFICATION_CODE_INCORRECT) {
                            CustomProgress.dismissDialog();
                            showToast("Code is incorrect");
                        }
                        else if (responseCode == Global.RESULT_FAILED) {
                            CustomProgress.dismissDialog();
                            showToast("Failed");
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

    private void showPageStatus(){
        switch (pageStatus){
            case 1:
                showEmailStatus();
                break;
            case 2:
                showVerificationStatus();
                break;
        }
    }

    private void showEmailStatus(){
        laySendEmail.setVisibility(View.VISIBLE);
        layConfirmPassword.setVisibility(View.GONE);
        btnSendRequest.setVisibility(View.VISIBLE);
        btnConfirm.setVisibility(View.GONE);
        layShowCode.setVisibility(View.GONE);
    }

    private void showVerificationStatus(){
        laySendEmail.setVisibility(View.GONE);
        layConfirmPassword.setVisibility(View.VISIBLE);
        btnSendRequest.setVisibility(View.GONE);
        btnConfirm.setVisibility(View.VISIBLE);
        layShowCode.setVisibility(View.GONE);
    }
}
