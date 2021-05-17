package com.money.well.controller.settings;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
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

import static com.money.well.common.Global._isTest;
import static com.money.well.common.Global.currentDonationId;
import static com.money.well.common.Global.donationContent;
import static com.money.well.common.Global.donationOnceAmount;
import static com.money.well.common.Global.donationPhotoUrl;
import static com.money.well.common.Global.donationTitle;
import static com.money.well.common.Global.paypalFeeRate;
import static com.money.well.common.Global.userId;

public class DonateMoneyActivity extends BaseActivity {
    private ImageView imgTopLeft;
    private TextView txtTopTitle;

    private TextView txtDonationTitle;
    private EditText edtDonationAmount;
    private ImageView imgDonation;
    private TextView txtBalance;
    private TextView txtFee;

    private Button btnDonate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donate_money);

        thisActivity = this;
        thisContext = this;
        thisView = findViewById(R.id.activity_donate_money);

        initBasicUI();
        showDonationInformation();
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
        txtDonationTitle = findViewById(R.id.txt_donation_title);
        edtDonationAmount = findViewById(R.id.edt_donation_amount);
        edtDonationAmount.addTextChangedListener(new TextWatcher() {

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                showBalanceFee();
            }
        });
        imgDonation = findViewById(R.id.img_donation);
        txtBalance = findViewById(R.id.txt_balance);
        txtFee = findViewById(R.id.txt_fee);
        btnDonate = findViewById(R.id.btn_donate);
        btnDonate.setOnClickListener(new OnMultiClickListener() {
            @Override
            public void onMultiClick(View v) {
            if (_isTest)
            {
                String strAmount = edtDonationAmount.getText().toString();
                if (!strAmount.equals("")){
                    double amount = Double.parseDouble(strAmount);
                    String transactionId  = "123456";
                    String payKey = "123456";
                    updateDonationHistory(amount, transactionId, payKey);
                }
                else {
                    showToast("Please input amount");
                }
            }
            }
        });
    }

    private void showDonationInformation(){
        txtDonationTitle.setText(donationTitle);
        edtDonationAmount.setText(donationOnceAmount + "");
        Glide.with(thisContext)
                .load(donationPhotoUrl)
                .placeholder(R.drawable.logo_image)
                .into(imgDonation);
        showBalanceFee();
    }

    private void showBalanceFee(){
        txtBalance.setText("Balance: £" + edtDonationAmount.getText().toString());
        if (edtDonationAmount.getText() != null && !edtDonationAmount.getText().toString().equals("")) {
            int fee = (int) (Integer.parseInt(edtDonationAmount.getText().toString()) * paypalFeeRate);
            txtFee.setText("Fee: £" + fee);
        }
    }

    private void updateDonationHistory(double amount, String transactionId, String payKey){
        showProgressDialog(getString(R.string.message_content_loading));
        HttpCall httpCallPost = new HttpCall();
        httpCallPost.setMethodtype(HttpCall.POST);
        String url = "\n" + getServerUrl() + getString(R.string.str_url_update_donation);
        httpCallPost.setUrl(url);
        HashMap<String,String> paramsPost = new HashMap<>();
        paramsPost.put("user_id", userId + "");
        paramsPost.put("donation_id", currentDonationId + "");
        paramsPost.put("amount", amount + "");
        paramsPost.put("transaction_id", transactionId);
        paramsPost.put("pay_key", payKey);
        httpCallPost.setParams(paramsPost);
        new HttpRequest(){
            @Override
            public void onResponse(String str) {
                super.onResponse(str);
                try {
                    CustomProgress.dismissDialog();
                    JSONObject response = new JSONObject(str);
                    int responseCode = (int) response.get("code");
                    if (responseCode == Global.RESULT_SUCCESS) {
                        gotoSuccessActivity();
                    }
                    else if (responseCode == Global.RESULT_FAILED) {
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

    private void gotoSuccessActivity(){
        Intent intent = new Intent(DonateMoneyActivity.this, SuccessActivity.class);
        Bundle b = new Bundle();
        b.putString("key", getResources().getString(R.string.content_donated));
        intent.putExtras(b);
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT|Intent.FLAG_ACTIVITY_CLEAR_TOP);
        thisActivity.startActivity(intent);
    }

}
