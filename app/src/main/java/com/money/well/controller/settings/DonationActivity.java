package com.money.well.controller.settings;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
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

import static com.money.well.common.Global.currentDonationId;
import static com.money.well.common.Global.donationContent;
import static com.money.well.common.Global.donationOnceAmount;
import static com.money.well.common.Global.donationPhotoUrl;
import static com.money.well.common.Global.donationTitle;
import static com.money.well.common.Global.userDonatedAmount;
import static com.money.well.common.Global.userId;

public class DonationActivity extends BaseActivity {
    private ImageView imgTopLeft;
    private TextView txtTopTitle;
    private ImageView imgDonateMoney;

    private TextView txtDonationTitle;
    private TextView txtDonatedAmount;
    private TextView txtDonationContent;
    private ImageView imgDonation;
    private CoordinatorLayout coordinatorLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donation);

        thisActivity = this;
        thisContext = this;
        thisView = findViewById(R.id.activity_donation);

        final Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        initBasicUI();

        getDonationInformation();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
//            Snackbar.make(coordinatorLayout, "Back button pressed", Snackbar.LENGTH_LONG).show();
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
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
        txtTopTitle.setText(R.string.title_empty);
        imgDonateMoney = findViewById(R.id.img_donate_money);
        imgDonateMoney.setOnClickListener(new OnMultiClickListener() {
            @Override
            public void onMultiClick(View v) {
                gotoDonateMoneyActivity();
            }
        });

        txtDonationTitle = findViewById(R.id.txt_donation_title);
        txtDonatedAmount = findViewById(R.id.txt_donated_amount);
        imgDonation = findViewById(R.id.img_donation);
        txtDonationContent = findViewById(R.id.txt_donation_content);
        coordinatorLayout = findViewById(R.id.activity_donation);

//        final Drawable upArrow = getResources().getDrawable(R.drawable.back);
//        upArrow.setColorFilter(getResources().getColor(R.color.black), PorterDuff.Mode.SRC_ATOP);
//        getSupportActionBar().setHomeAsUpIndicator(upArrow);
    }

    private void getDonationInformation(){
        showProgressDialog(getString(R.string.message_content_loading));
        HttpCall httpCallPost = new HttpCall();
        httpCallPost.setMethodtype(HttpCall.GET);
        String url = "\n" + getServerUrl() + getString(R.string.str_url_get_donation_info);
        httpCallPost.setUrl(url);
        HashMap<String,String> paramsPost = new HashMap<>();
        paramsPost.put("user_id", userId + "");
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
                        currentDonationId = responseData.getInt("id");
                        donationPhotoUrl = responseData.getString("photo_url");
                        donationTitle = responseData.getString("name");
                        donationContent = responseData.getString("content");
                        donationOnceAmount = responseData.getInt("amount");
                        userDonatedAmount = responseData.getInt("donate_amount");
                        showDonationInformation();
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

    private void showDonationInformation(){
        txtDonationTitle.setText(donationTitle);
        txtDonatedAmount.setText(standardDecimalFormat(userDonatedAmount + ""));
//        txtDonationContent.setText(donationContent);
        Glide.with(thisContext)
                .load(donationPhotoUrl)
                .placeholder(R.drawable.logo_image)
                .into(imgDonation);
    }

    private void gotoDonateMoneyActivity(){
        Intent intent = new Intent(DonationActivity.this, DonateMoneyActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT|Intent.FLAG_ACTIVITY_CLEAR_TOP);
        thisActivity.startActivity(intent);
    }

}
