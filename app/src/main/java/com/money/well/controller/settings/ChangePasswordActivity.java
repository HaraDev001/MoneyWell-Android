package com.money.well.controller.settings;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.money.well.R;
import com.money.well.common.Global;
import com.money.well.common.OnMultiClickListener;
import com.money.well.controller.base.BaseActivity;
import com.money.well.controller.common.SuccessActivity;
import com.money.well.utils.dialog.CustomProgress;
import com.money.well.utils.network.HttpCall;
import com.money.well.utils.network.HttpRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import static com.money.well.common.Global.userPassword;

public class ChangePasswordActivity extends BaseActivity {
    private ImageView imgTopLeft;
    private TextView txtTopTitle;
    private Button btnSave;

    private EditText edtCurrentPassword;
    private EditText edtNewPassword;
    private EditText edtConfirmPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        thisActivity = this;
        thisContext = this;
        thisView = findViewById(R.id.activity_change_password);

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
        txtTopTitle.setText(R.string.title_change_password);

        btnSave = findViewById(R.id.btn_save);
        btnSave.setOnClickListener(new OnMultiClickListener() {
            @Override
            public void onMultiClick(View v) {
                changePassword();
            }
        });

        edtCurrentPassword = findViewById(R.id.edt_old_password);
        edtNewPassword = findViewById(R.id.edt_new_password);
        edtConfirmPassword = findViewById(R.id.edt_confirm_password);
    }

    private void changePassword(){
        String strCurrentPassword = edtCurrentPassword.getText().toString();
        String strNewPassword = edtNewPassword.getText().toString();
        String strConfirmPassword = edtConfirmPassword.getText().toString();

        if (checkInputValues( strCurrentPassword, strNewPassword, strConfirmPassword)){
            showProgressDialog(getString(R.string.message_content_loading));
            HttpCall httpCallPost = new HttpCall();
            httpCallPost.setMethodtype(HttpCall.POST);
            String url = "\n" + getServerUrl() + getString(R.string.str_url_change_password);
            httpCallPost.setUrl(url);
            HashMap<String,String> paramsPost = new HashMap<>();
            paramsPost.put("userId", Global.userId + "");
            paramsPost.put("password", getMD5(strNewPassword));
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
                            userPassword = strNewPassword;
                            gotoSuccessActivity();
                        }
                        else if (responseCode == Global.RESULT_FAILED) {
                            CustomProgress.dismissDialog();
                            showToast("Change failed");
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

    private boolean checkInputValues(String currentPass, String newPass, String confirmPass){
        boolean isValue = true;
        if (currentPass.equals("")){
            showToast(getResources().getString(R.string.notice_input_current_password));
            isValue = false;
        }
        else if (!currentPass.equals(userPassword)){
            showToast(getString(R.string.notice_input_correct_password));
            isValue = false;
        }
        else if (newPass.equals("")){
            showToast(getString(R.string.notice_input_new_password));
            isValue = false;
        }
        else if (confirmPass.equals("")){
            showToast(getString(R.string.notice_input_confirm_password));
            isValue = false;
        }
        else if (!confirmPass.equals(newPass)){
            showToast(getString(R.string.notice_input_correct_confirm_password));
            isValue = false;
        }

        return isValue;
    }

    private void gotoSuccessActivity(){
        Intent intent = new Intent(ChangePasswordActivity.this, SuccessActivity.class);
        Bundle b = new Bundle();
        b.putString("key", getResources().getString(R.string.content_change_password));
        intent.putExtras(b);
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT|Intent.FLAG_ACTIVITY_CLEAR_TOP);
        thisActivity.startActivity(intent);
    }
}
