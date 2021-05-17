package com.money.well.controller.settings;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.money.well.R;
import com.money.well.common.OnMultiClickListener;
import com.money.well.controller.base.BaseActivity;
import com.money.well.utils.network.HttpCall;
import com.money.well.utils.network.HttpRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import static com.money.well.common.Global.RESULT_FAILED;
import static com.money.well.common.Global.RESULT_SUCCESS;
import static com.money.well.common.Global.userId;
import static com.money.well.common.Global.userName;

public class EditUsernameActivity extends BaseActivity {
    private ImageView imgTopLeft;
    private TextView txtTopTitle;
    private EditText edtUserName;
    private Button btnSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_username);

        thisActivity = this;
        thisContext = this;
        thisView = findViewById(R.id.activity_edit_username);

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
        txtTopTitle.setText(R.string.title_edit_username);

        edtUserName = findViewById(R.id.edt_user_name);
        edtUserName.setText(userName);
        btnSave = findViewById(R.id.btn_save);
        btnSave.setOnClickListener(new OnMultiClickListener() {
            @Override
            public void onMultiClick(View v) {
                updateUserName();
            }
        });
    }

    private void updateUserName(){
        String name = edtUserName.getText().toString();
        if (checkInputValue(name)){
            HttpCall httpCallPost = new HttpCall();
            httpCallPost.setMethodtype(HttpCall.GET);
            String url = "\n" + getServerUrl() + getString(R.string.str_url_update_name);
            httpCallPost.setUrl(url);
            HashMap<String,String> paramsPost = new HashMap<>();
            paramsPost.put("user_id", userId + "");
            paramsPost.put("user_name", name);
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
                            userName = responseData.getString("name");
                            showToast(getResources().getString(R.string.notice_update_success));
                        }
                        else if (responseCode == RESULT_FAILED) {
                            String toastContent = getResources().getString(R.string.notice_update_failed);
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

    private boolean checkInputValue(String name){
        boolean isValue = true;
        if (name.equals("")){
            showToast("Please input user name");
            isValue = false;
        }
        else if (name.equals(userName)){
            showToast("Please change user name");
            isValue = false;
        }
        return  isValue;
    }
}
