package com.money.well.controller.settings;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.money.well.R;
import com.money.well.common.OnMultiClickListener;
import com.money.well.controller.base.BaseActivity;
import com.money.well.common.Global;
import com.money.well.controller.home.HomeNewActivity;
import com.money.well.controller.login.LoginActivity;
import com.money.well.utils.network.HttpCall;
import com.money.well.utils.network.HttpRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class HelpActivity extends BaseActivity {

    private ImageView imgTopLeft;
    private TextView txtTopTitle;

    private LinearLayout layFaq;
    private LinearLayout layContactUs;
    private LinearLayout layPrivacyPolicy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);

        thisActivity = this;
        thisContext = this;
        thisView = findViewById(R.id.activity_help);

        initBasicUI();
        getHelpContent();

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
        txtTopTitle.setText(R.string.title_help);

        layFaq = findViewById(R.id.lay_faq);
        layFaq.setOnClickListener(new OnMultiClickListener() {
            @Override
            public void onMultiClick(View v) {
                Intent intent = new Intent(HelpActivity.this, HelpContentActivity.class);
                Bundle b = new Bundle();
                b.putInt("key", Global.HELP_FAQ);
                intent.putExtras(b);
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT|Intent.FLAG_ACTIVITY_CLEAR_TOP);
                thisActivity.startActivity(intent);
            }
        });

        layContactUs = findViewById(R.id.lay_contact_us);
        layContactUs.setOnClickListener(new OnMultiClickListener() {
            @Override
            public void onMultiClick(View v) {
                Intent intent = new Intent(HelpActivity.this, HelpContentActivity.class);
                Bundle b = new Bundle();
                b.putInt("key", Global.HELP_CONTACT_US);
                intent.putExtras(b);
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT|Intent.FLAG_ACTIVITY_CLEAR_TOP);
                thisActivity.startActivity(intent);
            }
        });

        layPrivacyPolicy = findViewById(R.id.lay_policy);
        layPrivacyPolicy.setOnClickListener(new OnMultiClickListener() {
            @Override
            public void onMultiClick(View v) {
                Intent intent = new Intent(HelpActivity.this, HelpContentActivity.class);
                Bundle b = new Bundle();
                b.putInt("key", Global.HELP_PRIVATE_POLICY);
                intent.putExtras(b);
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT|Intent.FLAG_ACTIVITY_CLEAR_TOP);
                thisActivity.startActivity(intent);
            }
        });
    }

    private void getHelpContent(){
        HttpCall httpCallPost = new HttpCall();
        httpCallPost.setMethodtype(HttpCall.GET);
        String url = "\n" + getServerUrl() + getString(R.string.str_url_help_content);
        httpCallPost.setUrl(url);
        HashMap<String,String> paramsPost = new HashMap<>();
        httpCallPost.setParams(paramsPost);
        new HttpRequest(){
            @Override
            public void onResponse(String str) {
                super.onResponse(str);
                try {
                    JSONObject response = new JSONObject(str);
                    int responseCode = (int) response.get("code");
                    if (responseCode == Global.RESULT_SUCCESS) {
                        JSONObject responseData =  response.getJSONObject("data");
                        Global.helpFAQ = responseData.getString("faq");
                        Global.helpContact = responseData.getString("contact_us");
                        Global.helpPrivacy = responseData.getString("privacy");
                    }
                    else if (responseCode == Global.RESULT_FAILED) {
                        Toast.makeText(thisContext, "failed!!!", Toast.LENGTH_SHORT).show();
                    }
                    else if (responseCode == Global.RESULT_EMAIL_PASSWORD_INCORRECT) {
                        Toast.makeText(thisContext, "account and password incorrect!!!", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(thisContext, "exception!!!", Toast.LENGTH_SHORT).show();
                }

            }
        }.execute(httpCallPost);
    }

}
