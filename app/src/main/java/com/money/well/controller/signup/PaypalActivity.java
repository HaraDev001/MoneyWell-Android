package com.money.well.controller.signup;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.money.well.R;
import com.money.well.common.OnMultiClickListener;
import com.money.well.controller.base.BaseActivity;
import com.money.well.controller.home.HomeActivity;

public class PaypalActivity extends BaseActivity {

    private TextView txtNewAccount;
    private Button btnGotoHome;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paypal);

        thisActivity = this;
        thisContext = this;
        thisView = findViewById(R.id.activity_paypal);

        initBasicUI();
    }

    private void initBasicUI(){
        txtNewAccount = findViewById(R.id.txt_new_paypal);
        btnGotoHome = findViewById(R.id.btn_go_home);
        btnGotoHome.setOnClickListener(new OnMultiClickListener() {
            @Override
            public void onMultiClick(View v) {
                Intent intent = new Intent(PaypalActivity.this, HomeActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT|Intent.FLAG_ACTIVITY_CLEAR_TOP);
                thisActivity.startActivity(intent);

                finish();
            }
        });
    }
}
