package com.money.well.controller.login;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.money.well.R;
import com.money.well.common.Global;
import com.money.well.common.OnMultiClickListener;
import com.money.well.controller.base.BaseActivity;
import com.money.well.utils.dialog.CustomProgress;
import com.money.well.utils.network.HttpCall;
import com.money.well.utils.network.HttpRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import static com.money.well.common.Global.RESULT_FAILED;
import static com.money.well.common.Global.RESULT_SUCCESS;
import static com.money.well.common.Global._isTest;
import static com.money.well.common.Global.userId;

public class SendRequestActivity extends BaseActivity {
    private ImageView imgTopLeft;
    private TextView txtTopTitle;

    private LinearLayout laySendEmail;
    private LinearLayout layConfirmPassword;
    private Button btnSendRequest;
    private Button btnConfirm;
    private TextView txtEmail;
    private TextView txtVerifyCode;
    private LinearLayout layShowCode;
    private TextView txtShowCode;

    private int pageStatus = 1; // 1: email status, 2: verify status
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_request);

        thisActivity = this;
        thisContext = this;
        thisView = findViewById(R.id.lay_send_request);

        initBasicUI();
    }

    private void initBasicUI(){
        laySendEmail= findViewById(R.id.lay_send_email);
        layConfirmPassword= findViewById(R.id.lay_verify_code);
        btnSendRequest= findViewById(R.id.btn_send_request);
        btnSendRequest.setOnClickListener(new OnMultiClickListener() {
            @Override
            public void onMultiClick(View v) {
            if (_isTest){
                Global.userId = 1;
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
        txtTopTitle = findViewById(R.id.txt_top_title);
        txtTopTitle.setText(R.string.title_empty);
        txtEmail = findViewById(R.id.txt_email);
        if (_isTest)
            txtEmail.setText("xinfengbao_world@163.com");
        txtVerifyCode = findViewById(R.id.txt_verify_code);
        layShowCode = findViewById(R.id.lay_show_code);
        txtShowCode = findViewById(R.id.txt_show_code);
    }

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
            String url = "\n" + getServerUrl() + getString(R.string.str_url_send_forgot_password_email);
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
//                            userId = responseData.getInt("userId");
                            pageStatus = 2;
                            showPageStatus();
                        }
                        else if (responseCode == RESULT_FAILED) {
                            String toastContent = getResources().getString(R.string.notice_verify_failed);
                            showToast(toastContent);
                        }
                    } catch (JSONException e) {
                        String toastContent = getResources().getString(R.string.notice_verify_failed);
                        showToast(toastContent);
                    }
                }
            }.execute(httpCallPost);
        }
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
                            Intent intent = new Intent(SendRequestActivity.this, ResetPassword.class);
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
