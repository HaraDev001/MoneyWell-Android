package com.money.well.controller.signup;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

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

public class InputPasswordActivity extends BaseActivity {
    private TextView txtTopTitle;

    private TextView txtPassword;
    private TextView txtConfirmPassword;
    private Button btnResetPassword;
    private ImageView imgTopLeft;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input_password);

        thisActivity = this;
        thisContext = this;
        thisView = findViewById(R.id.activity_input_password);

        txtTopTitle = findViewById(R.id.txt_top_title);
        txtTopTitle.setText(getResources().getString(R.string.title_input_password));

        initBasicUI();
    }

    private void initBasicUI(){
        txtPassword = findViewById(R.id.txt_password);
        txtConfirmPassword = findViewById(R.id.txt_confirm_password);
        btnResetPassword = findViewById(R.id.btn_next);
        btnResetPassword.setOnClickListener(new OnMultiClickListener() {
            @Override
            public void onMultiClick(View v) {
                sendPassword();

            }
        });

        imgTopLeft = findViewById(R.id.img_top_left);
        imgTopLeft.setOnClickListener(new OnMultiClickListener() {
            @Override
            public void onMultiClick(View v) {
                finish();
            }
        });
    }


    private void sendPassword(){
        String strPassword = txtPassword.getText().toString();
        String strConfirmPassword = txtConfirmPassword.getText().toString();

        if (checkInputValues( strPassword, strConfirmPassword)){
            showProgressDialog(getString(R.string.message_content_loading));
            HttpCall httpCallPost = new HttpCall();
            httpCallPost.setMethodtype(HttpCall.POST);
            String url = "\n" + getServerUrl() + getString(R.string.str_url_change_password);
            httpCallPost.setUrl(url);
            HashMap<String,String> paramsPost = new HashMap<>();
            paramsPost.put("userId", Global.userId + "");
            if (Global._isTest){
                paramsPost.put("userId", 1 + "");
            }
            paramsPost.put("password", getMD5(strPassword));
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
                            Global.userPassword = strPassword;
                            showToast("Password setting success");
                            gotoProfileActivity();
                        }
                        else if (responseCode == Global.RESULT_FAILED) {
                            CustomProgress.dismissDialog();
                            showToast("Password setting failed");
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

    private boolean checkInputValues(String pass, String confirmPass){
        boolean isValue = true;
        if (pass.equals("")){
            showToast("Please input password");
            isValue = false;
        }
        else if (confirmPass.equals("")){
            showToast("Please input confirm password");
            isValue = false;
        }
        else if (!confirmPass.equals(pass)){
            showToast("Please input correct confirm password");
            isValue = false;
        }

        return isValue;
    }

    private void gotoProfileActivity(){
        Intent intent = new Intent(InputPasswordActivity.this, ProfileActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT|Intent.FLAG_ACTIVITY_CLEAR_TOP);
        thisActivity.startActivity(intent);

        finish();
    }
}
