package com.money.well.controller.common;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.money.well.R;
import com.money.well.common.OnMultiClickListener;
import com.money.well.controller.base.BaseActivity;

public class SuccessActivity extends BaseActivity {

    private Button btnDone;
    private String bundleContent = "";
    private TextView txtContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_success);
        thisActivity = this;
        thisContext = this;
        thisView = findViewById(R.id.activity_success);

        Bundle b = getIntent().getExtras();
        if(b != null)
            bundleContent = b.getString("key");
        else
            bundleContent = "";

        initBasicUI();
    }

    private void initBasicUI(){
        btnDone = findViewById(R.id.btn_done);
        btnDone.setOnClickListener(new OnMultiClickListener() {
            @Override
            public void onMultiClick(View v) {
                finish();
            }
        });

        txtContent = findViewById(R.id.txt_content);
        if (!bundleContent.equals(""))
            txtContent.setText(bundleContent);
    }
}
