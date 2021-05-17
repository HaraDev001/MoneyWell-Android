package com.money.well.controller.groups;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.money.well.R;
import com.money.well.common.OnMultiClickListener;
import com.money.well.controller.base.BaseActivity;
import com.money.well.controller.common.SuccessActivity;
import com.money.well.controller.login.LoginActivity;
import com.money.well.controller.login.SendRequestActivity;

public class EditGroupActivity extends BaseActivity {
    private ImageView imgTopLeft;
    private TextView txtTopTitle;

    private Button btnEditGoup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_group);

        thisActivity = this;
        thisContext = this;
        thisView = findViewById(R.id.activity_edit_group);

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
        txtTopTitle.setText(R.string.title_edit_group);
        btnEditGoup = findViewById(R.id.btn_save);
        btnEditGoup.setOnClickListener(new OnMultiClickListener() {
            @Override
            public void onMultiClick(View v) {
                Intent intent = new Intent(EditGroupActivity.this, SuccessActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT|Intent.FLAG_ACTIVITY_CLEAR_TOP);
                thisActivity.startActivity(intent);

//                finish();
            }
        });
    }
}
