package com.money.well.controller.settings;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.money.well.R;
import com.money.well.common.OnMultiClickListener;
import com.money.well.controller.base.BaseActivity;
import com.money.well.common.Global;

public class HelpContentActivity extends BaseActivity {
    private ImageView imgTopLeft;
    private TextView txtTopTitle;

    private TextView txtContent;

    public static int bundlekey = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help_content);

        thisActivity = this;
        thisContext = this;
        thisView = findViewById(R.id.activity_help_content);

        Bundle b = getIntent().getExtras();
        if(b != null)
            bundlekey = b.getInt("key");

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
        txtContent = findViewById(R.id.txt_content);

        if (bundlekey == Global.HELP_FAQ){
            txtTopTitle.setText(R.string.title_help_faq);
            txtContent.setText(Global.helpFAQ);
        }
        else if (bundlekey == Global.HELP_CONTACT_US){
            txtTopTitle.setText(R.string.title_help_contact);
            txtContent.setText(Global.helpContact);
        }
        else if (bundlekey == Global.HELP_PRIVATE_POLICY){
            txtTopTitle.setText(R.string.title_help_policy);
            txtContent.setText(Global.helpPrivacy);
        }

    }
}
