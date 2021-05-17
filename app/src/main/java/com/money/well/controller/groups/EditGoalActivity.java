package com.money.well.controller.groups;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.money.well.R;
import com.money.well.common.OnMultiClickListener;
import com.money.well.controller.base.BaseActivity;

public class EditGoalActivity extends BaseActivity {
    private ImageView imgTopLeft;
    private TextView txtTopTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_goal);
        thisActivity = this;
        thisContext = this;
        thisView = findViewById(R.id.activity_edit_goal);

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
        txtTopTitle.setText(R.string.title_edit_goal);

    }
}
