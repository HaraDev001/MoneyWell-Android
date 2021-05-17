package com.money.well.controller.settings;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.money.well.R;
import com.money.well.common.OnMultiClickListener;
import com.money.well.controller.base.BaseActivity;
import com.money.well.controller.common.SuccessActivity;

public class ChangePaypalActivity extends BaseActivity {
    private ImageView imgTopLeft;
    private TextView txtTopTitle;

    private Button btnSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_paypal);

        thisActivity = this;
        thisContext = this;
        thisView = findViewById(R.id.activity_change_paypal);

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
        txtTopTitle.setText(R.string.title_change_paypal);
        btnSave = findViewById(R.id.btn_save);
        btnSave.setOnClickListener(new OnMultiClickListener() {
            @Override
            public void onMultiClick(View v) {
                new AlertDialog.Builder(thisContext)
                        .setMessage("Would you like to change paypal email?")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int whichButton) {
                                Toast.makeText(thisContext, "Save", Toast.LENGTH_SHORT).show();
                                changePaypalEmail();
                            }})
                        .setNegativeButton(android.R.string.no, null).show();
            }
        });
    }

    private void changePaypalEmail(){
        Intent intent = new Intent(ChangePaypalActivity.this, SuccessActivity.class);
        Bundle b = new Bundle();
        b.putString("key", getResources().getString(R.string.content_change_paypal_email));
        intent.putExtras(b);
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT|Intent.FLAG_ACTIVITY_CLEAR_TOP);
        thisActivity.startActivity(intent);
    }


}
