package com.money.well.controller.settings;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.money.well.R;
import com.money.well.common.OnMultiClickListener;
import com.money.well.controller.base.BaseActivity;
import com.money.well.controller.common.SuccessActivity;
import com.money.well.utils.network.HttpCall;
import com.money.well.utils.network.HttpRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import static com.money.well.common.Global.RESULT_EMAIL_DUPLICATE;
import static com.money.well.common.Global.RESULT_FAILED;
import static com.money.well.common.Global.RESULT_SUCCESS;
import static com.money.well.common.Global.VERIFICATION_ERROR_STATUS;
import static com.money.well.common.Global._isTest;
import static com.money.well.common.Global._isVerificationTest;
import static com.money.well.common.Global.userEmail;
import static com.money.well.common.Global.userId;

public class ChangeEmailActivity extends BaseActivity {

    private ImageView imgTopLeft;
    private TextView txtTopTitle;

    private LinearLayout laySendEmail;
    private LinearLayout layConfirmPassword;
    private LinearLayout layShowCode;
    private Button btnSendRequest;
    private Button btnConfirm;
    private TextView txtNotice;
    private EditText txtEmail;
    private EditText txtVerificationCode;
    private TextView txtShowCode;

    private String newEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_email);

        thisActivity = this;
        thisContext = this;
        thisView = findViewById(R.id.activity_verify_email);

        initBasicUI();
    }

    private void initBasicUI(){
        imgTopLeft = findViewById(R.id.img_top_left);
        imgTopLeft.setOnClickListener(new OnMultiClickListener() {
            @Override
            public void onMultiClick(View v) {
                finish();
            }
        });
        txtTopTitle = findViewById(R.id.txt_top_title);
        txtTopTitle.setText(R.string.title_change_email);

        laySendEmail= findViewById(R.id.lay_send_email);
        layConfirmPassword= findViewById(R.id.lay_verify_code);
        layShowCode = findViewById(R.id.lay_show_code);
        btnSendRequest= findViewById(R.id.btn_send_request);
        btnSendRequest.setOnClickListener(new OnMultiClickListener() {
            @Override
            public void onMultiClick(View v) {
            getVerificationCode();
            }
        });
        btnConfirm= findViewById(R.id.btn_confirm);
        btnConfirm.setOnClickListener(new OnMultiClickListener() {
            @Override
            public void onMultiClick(View v) {
                laySendEmail.setVisibility(View.VISIBLE);
                layConfirmPassword.setVisibility(View.GONE);
                btnSendRequest.setVisibility(View.VISIBLE);
                btnConfirm.setVisibility(View.GONE);

                Intent intent = new Intent(ChangeEmailActivity.this, SuccessActivity.class);
                Bundle b = new Bundle();
                b.putString("key", getResources().getString(R.string.content_change_email));
                intent.putExtras(b);
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT|Intent.FLAG_ACTIVITY_CLEAR_TOP);
                thisActivity.startActivity(intent);
            }
        });

        txtNotice = findViewById(R.id.txt_notice);
        txtEmail = findViewById(R.id.txt_email);
        if (_isTest){
            txtEmail.setText("mars@gmail.com");
        }
        txtVerificationCode = findViewById(R.id.txt_verify_code);
        txtShowCode = findViewById(R.id.txt_show_code);
    }

    private void getVerificationCode(){
        String email = txtEmail.getText().toString();
        if (email.equals("")){
            String msgContent = getResources().getString(R.string.notice_input_email);
            showToast(msgContent);
        }
        else {
            HttpCall httpCallPost = new HttpCall();
            httpCallPost.setMethodtype(HttpCall.POST);
            String url = "\n" + getServerUrl() + getString(R.string.str_url_get_verify_code);
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
                            String verificationCode = responseData.getString("verification_code");
                            newEmail = email;
                            txtShowCode.setText(verificationCode);
                            layShowCode.setVisibility(View.VISIBLE);
                            laySendEmail.setVisibility(View.GONE);
                            layConfirmPassword.setVisibility(View.VISIBLE);
                            btnSendRequest.setVisibility(View.GONE);
                            btnConfirm.setVisibility(View.VISIBLE);
                            txtNotice.setText(R.string.notice_input_code);
                            txtTopTitle.setText(R.string.title_verify_email);
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

    private void updateEmail(){
        String verificationCode = txtVerificationCode.getText().toString();
        if (verificationCode.equals("")){
            String toastContent = getResources().getString(R.string.notice_input_code);
            showToast(toastContent);
        }
        else{
            HttpCall httpCallPost = new HttpCall();
            httpCallPost.setMethodtype(HttpCall.PUT);
            String url = "\n" + getServerUrl() + getString(R.string.str_url_update_email);
            httpCallPost.setUrl(url);
            HashMap<String,String> paramsPost = new HashMap<>();
            paramsPost.put("user_id", userId + "");
            paramsPost.put("user_email", newEmail);
            paramsPost.put("verification_code", verificationCode);
            if (_isVerificationTest) {
                paramsPost.put("verification_flag", 1 + "");
            }
            else
                paramsPost.put("verification_flag", 0 + "");
            httpCallPost.setParams(paramsPost);
            new HttpRequest(){
                @Override
                public void onResponse(String str) {
                    super.onResponse(str);
                    try {
                        JSONObject response = new JSONObject(str);
                        int responseCode = (int) response.get("code");
                        if (responseCode == RESULT_SUCCESS) {
                            userEmail = newEmail;

                            String toastContent = getResources().getString(R.string.notice_success);
                            showToast(toastContent);
                        }
//                        else if (responseCode == VERIFICATION_ERROR_STATUS) {
//                            String toastContent = getResources().getString(R.string.notice_verify_failed);
//                            showToast(toastContent);
//                        }
                        else if(responseCode == RESULT_FAILED)
                        {
                            String toastContent = getResources().getString(R.string.notice_verify_failed);
                            showToast(toastContent);
                        }
                        else if(responseCode == RESULT_EMAIL_DUPLICATE){
                            String toastContent = getResources().getString(R.string.notice_verify_duplicated);
                            showToast(toastContent);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        String toastContent = getResources().getString(R.string.notice_verify_failed);
                        showToast(toastContent);
                    }
                }
            }.execute(httpCallPost);
        }

    }}
