package com.money.well.controller.groups;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
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
import com.money.well.controller.contacts.MemberDetailActivity;
import com.money.well.utils.dialog.CustomProgress;
import com.money.well.utils.network.HttpCall;
import com.money.well.utils.network.HttpRequest;

import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.money.well.common.Global.contributionReceiverId;
import static com.money.well.common.Global.contributionReceiverName;
import static com.money.well.common.Global.contributionReceiverPayPalAccount;
import static com.money.well.common.Global.contributionReceiverPhotoUrl;
import static com.money.well.common.Global.paypalApplicationId;
import static com.money.well.common.Global.paypalPayKey;
import static com.money.well.common.Global.paypalRequestUrl;
import static com.money.well.common.Global.paypalSecurityPassword;
import static com.money.well.common.Global.paypalSecuritySignature;
import static com.money.well.common.Global.paypalSecurityUseId;
import static com.money.well.common.Global.userPaypalEmail;

public class SendMoneyActivity extends BaseActivity {
    private ImageView imgTopLeft;
    private TextView txtTopTitle;
    private Button btnSendMoney;

    private CircleImageView imgReceiver;
    private TextView txtReceiverName;
    private EditText edtAmount;

    private double sendAmount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_money);

        thisActivity = this;
        thisContext = this;
        thisView = findViewById(R.id.activity_send_money);

        initBasicUI();

        getReceiverInformation();
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
        btnSendMoney = findViewById(R.id.btn_send_money);
        btnSendMoney.setOnClickListener(new OnMultiClickListener() {
            @Override
            public void onMultiClick(View v) {
                sendMoney();
//                Intent intent = new Intent(SendMoneyActivity.this, SuccessActivity.class);
//                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT|Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                thisActivity.startActivity(intent);
            }
        });

        txtReceiverName = findViewById(R.id.txt_receiver_name);
        imgReceiver = findViewById(R.id.img_receiver);
        edtAmount = findViewById(R.id.edt_amount);
    }

    private void getReceiverInformation(){
        showProgressDialog(getString(R.string.message_content_loading));
        HttpCall httpCallPost = new HttpCall();
        httpCallPost.setMethodtype(HttpCall.GET);
        String url = "\n" + getServerUrl() + getString(R.string.str_url_get_receiver_information);
        httpCallPost.setUrl(url);
        HashMap<String,String> paramsPost = new HashMap<>();
        paramsPost.put("receiver_id", contributionReceiverId + "");
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
                        JSONObject responseData = response.getJSONObject("data");
                        contributionReceiverPhotoUrl = responseData.getString("photo_url");
                        contributionReceiverName = responseData.getString("name");
                        contributionReceiverPayPalAccount = responseData.getString("paypal_email");
                        showReceiverInformation();
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

    private void showReceiverInformation(){
        txtReceiverName.setText(contributionReceiverName);
        Glide.with(thisContext)
                .load(contributionReceiverPhotoUrl)
                .into(imgReceiver);
    }

    private void sendMoney(){
        String strAmount = edtAmount.getText().toString();
        if (strAmount != null && !strAmount.equals("")){
            sendAmount = Double.parseDouble(strAmount);
            if (checkPaypalAccount(userPaypalEmail, contributionReceiverPayPalAccount, sendAmount)){
                //showToast("sender: " + userPaypalEmail + ", receiver: " + contributionReceiverPayPalAccount + "amount: " + sendAmount);
                getPayKey httpRequest = new getPayKey();
                httpRequest.execute();
            }
        }
    }

    class getPayKey extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... strings) {
            try {
                StringEntity params = new StringEntity("{ \n" +
                        "\n" +
                        "\"actionType\":\"PAY\", \n" +
                        "\"senderEmail\":\"" + userPaypalEmail + "\", \n" +
                        "\"cancelUrl\":\"https://www.paypal.com/us/home\", \n" +
                        "\"currencyCode\":\"GBP\", \n" +
                        "\"feesPayer\":\"SENDER\", \n" +
                        "\"receiverList\":{ \n" +
                        "\"receiver\":[{ \n" +
                        "\"email\":\"" + contributionReceiverPayPalAccount + "\", \n" +
                        "\"amount\":" + sendAmount + " \n" +
                        "}] \n" +
                        "}, \n" +
                        "\"requestEnvelope\":{ \n" +
                        "\"errorLanguage\":\"en_US\" \n" +
                        "}, \n" +
                        "\"returnUrl\":\"https://www.paypal.com/us/home\" \n" +
                        "} ");
                HttpClient Client = new DefaultHttpClient();
                HttpPost httppost;
                ResponseHandler<String> responseHandler = new BasicResponseHandler();
                httppost = new HttpPost(paypalRequestUrl);
                httppost.setHeader("X-PAYPAL-SECURITY-USERID",  paypalSecurityUseId);
                httppost.setHeader("X-PAYPAL-SECURITY-PASSWORD",  paypalSecurityPassword);
                httppost.setHeader("X-PAYPAL-SECURITY-SIGNATURE",  paypalSecuritySignature);
                httppost.setHeader("X-PAYPAL-REQUEST-DATA-FORMAT",  "json");
                httppost.setHeader("X-PAYPAL-RESPONSE-DATA-FORMAT",  "json");
                httppost.setHeader("X-PAYPAL-APPLICATION-ID",  paypalApplicationId);
                httppost.setHeader("Content-Type", "application/json");
                httppost.setEntity(params);
                String content = Client.execute(httppost, responseHandler);
                return content;

            } catch (Exception e) {
                e.printStackTrace();
                System.out.println(e);
            }
            return "Cannot Connect";
        }

        protected void onPostExecute(String result) {
            JSONObject obj = null;
            try {
                obj = new JSONObject(result);
                paypalPayKey = obj.getString("payKey");

                Intent intent = new Intent(SendMoneyActivity.this, PaymentActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT|Intent.FLAG_ACTIVITY_CLEAR_TOP);
                thisActivity.startActivity(intent);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }


    private boolean checkPaypalAccount(String sender, String receiver, Double amount) {
        boolean isValue = true;
        if (sender.equals("")){
            showToast(getString(R.string.notice_your_paypal_email_not_exist));
            isValue = false;
        }
        else if (receiver.equals("")){
            showToast(getString(R.string.notice_receiver_paypal_email_not_exist));
            isValue = false;
        }
        else  if (amount == null || amount == 0){
            showToast(getString(R.string.notice_input_amount));
            isValue = false;
        }
        return isValue;
    }
}
