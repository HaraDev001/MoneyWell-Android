package com.money.well.controller.settings;

import androidx.appcompat.app.AlertDialog;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.money.well.BuildConfig;
import com.money.well.R;
import com.money.well.common.Global;
import com.money.well.common.OnMultiClickListener;
import com.money.well.controller.base.BaseActivity;
import com.money.well.controller.login.LoginActivity;

import static com.money.well.common.Global.userName;
import static com.money.well.common.Global.userPhotoUrl;

public class ProfileSettingActivity extends BaseActivity {

    private ImageView imgTopLeft;
    private TextView txtTopTitle;

    private ImageView imgAddPhoto;
    private ImageView imgEditUserName;
    private LinearLayout layEditPfofile;
    private LinearLayout layChangeEmail;
    private LinearLayout layChangePaypal;
    private LinearLayout layChangePassword;
    private LinearLayout laySignOut;
    private ImageView imgProfile;
    private TextView txtUserName;
    private TextView txtVersionName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_setting);

        thisActivity = this;
        thisContext = this;
        thisView = findViewById(R.id.activity_profile_setting);

        initBasicUI();

    }

    private void initBasicUI() {
        imgTopLeft = findViewById(R.id.img_top_left);
        imgTopLeft.setOnClickListener(new OnMultiClickListener() {
            @Override
            public void onMultiClick(View v) {
                finish();
            }
        });

        txtTopTitle = findViewById(R.id.txt_top_title);
        txtTopTitle.setText(R.string.title_profile_setting);

        imgAddPhoto = findViewById(R.id.img_add_photo);
        imgAddPhoto.setOnClickListener(new OnMultiClickListener() {
            @Override
            public void onMultiClick(View v) {
                Intent intent = new Intent(ProfileSettingActivity.this, EditPhotoActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT|Intent.FLAG_ACTIVITY_CLEAR_TOP);
                thisActivity.startActivity(intent);
            }
        });
        imgEditUserName = findViewById(R.id.img_edit_name);
        imgEditUserName.setOnClickListener(new OnMultiClickListener() {
            @Override
            public void onMultiClick(View v) {
                Intent intent = new Intent(ProfileSettingActivity.this, EditUsernameActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT|Intent.FLAG_ACTIVITY_CLEAR_TOP);
                thisActivity.startActivity(intent);
            }
        });
        layEditPfofile = findViewById(R.id.lay_edit_profile);
        layEditPfofile.setOnClickListener(new OnMultiClickListener() {
            @Override
            public void onMultiClick(View v) {
                Intent intent = new Intent(ProfileSettingActivity.this, EditProfileActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT|Intent.FLAG_ACTIVITY_CLEAR_TOP);
                thisActivity.startActivity(intent);
            }
        });
        layChangeEmail = findViewById(R.id.lay_change_email);
        layChangeEmail.setOnClickListener(new OnMultiClickListener() {
            @Override
            public void onMultiClick(View v) {
                Intent intent = new Intent(ProfileSettingActivity.this, ChangeEmailActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT|Intent.FLAG_ACTIVITY_CLEAR_TOP);
                thisActivity.startActivity(intent);
            }
        });
        layChangePaypal = findViewById(R.id.lay_change_paypal);
        layChangePaypal.setOnClickListener(new OnMultiClickListener() {
            @Override
            public void onMultiClick(View v) {
                Intent intent = new Intent(ProfileSettingActivity.this, ChangePaypalActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT|Intent.FLAG_ACTIVITY_CLEAR_TOP);
                thisActivity.startActivity(intent);
            }
        });
        layChangePassword = findViewById(R.id.lay_change_password);
        layChangePassword.setOnClickListener(new OnMultiClickListener() {
            @Override
            public void onMultiClick(View v) {
                Intent intent = new Intent(ProfileSettingActivity.this, ChangePasswordActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT|Intent.FLAG_ACTIVITY_CLEAR_TOP);
                thisActivity.startActivity(intent);
            }
        });
        laySignOut = findViewById(R.id.lay_sign_out);
        laySignOut.setOnClickListener(new OnMultiClickListener() {
            @Override
            public void onMultiClick(View v) {
                new AlertDialog.Builder(thisContext)
//                        .setTitle("")
                        .setMessage("Would you like sign out?")
//                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int whichButton) {
                                Toast.makeText(ProfileSettingActivity.this, "Log out", Toast.LENGTH_SHORT).show();
                                tryLogout();
                            }})
                        .setNegativeButton(android.R.string.no, null).show();
            }
        });
        imgProfile = findViewById(R.id.img_profile);
        Glide.with(thisContext)
                .load(userPhotoUrl)
                .into(imgProfile);
        txtUserName = findViewById(R.id.txt_user_name);
        txtUserName.setText(userName);

        txtVersionName = findViewById(R.id.txt_version_name);
        showVersionName();
    }

    private void tryLogout(){
        Intent intent = new Intent(ProfileSettingActivity.this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT|Intent.FLAG_ACTIVITY_CLEAR_TOP);
        thisActivity.startActivity(intent);
        Global.clickTabIndex = Global.HOME_CLICKED;
        finish();
    }

    private void showVersionName(){
        int versionCode = BuildConfig.VERSION_CODE;
        String versionName = BuildConfig.VERSION_NAME;
        txtVersionName.setText("Version " + versionName);
    }
}
