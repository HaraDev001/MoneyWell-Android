package com.money.well.controller.groups;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.money.well.R;
import com.money.well.common.Global;
import com.money.well.common.OnMultiClickListener;
import com.money.well.controller.base.BaseActivity;
import com.money.well.controller.common.SuccessActivity;
import com.money.well.controller.model.GroupObject;
import com.money.well.controller.settings.EditProfileActivity;
import com.money.well.utils.dialog.CustomProgress;
import com.money.well.utils.network.HttpCall;
import com.money.well.utils.network.HttpRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.money.well.common.Global.currentGroupAdminName;
import static com.money.well.common.Global.currentGroupContributionStatus;
import static com.money.well.common.Global.currentGroupGoalCount;
import static com.money.well.common.Global.currentGroupId;
import static com.money.well.common.Global.currentGroupMemberCount;
import static com.money.well.common.Global.currentGroupName;
import static com.money.well.common.Global.currentGroupPhotoUrl;
import static com.money.well.common.Global.groupObjectArrayList;

public class JoinGroupActivity extends BaseActivity {
    private ImageView imgTopLeft;
    private TextView txtTopTitle;
    private CircleImageView imgGroupLogo;
    private TextView txtGroupName;
    private TextView txtGroupAdminName;
    private TextView txtGroupMemberCount;
    private TextView txtGroupContributionStatus;
    private TextView txtGroupGoalCount;
    private Button btnJoinGroup;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_group);
        thisActivity = this;
        thisContext = this;
        thisView = findViewById(R.id.activity_join_group);

        initBasicUI();
        showGroupImage();
        getGroupInformation();
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
        txtTopTitle.setText(R.string.title_join_group);

        imgGroupLogo = findViewById(R.id.img_group);

        txtGroupName = findViewById(R.id.txt_group_name);
        txtGroupAdminName = findViewById(R.id.txt_group_admin);
        txtGroupMemberCount = findViewById(R.id.txt_group_member_count);
        txtGroupContributionStatus = findViewById(R.id.txt_contribution_status);
        txtGroupGoalCount = findViewById(R.id.txt_goal_count);

        btnJoinGroup = findViewById(R.id.btn_join_group);
        btnJoinGroup.setOnClickListener(new OnMultiClickListener() {
            @Override
            public void onMultiClick(View v) {
                joinGroup();
            }
        });
    }

    private void showGroupImage(){
        Glide.with(thisContext)
                .load(currentGroupPhotoUrl)
                .placeholder(R.drawable.user_image)
                .into(imgGroupLogo);
    }

    private void getGroupInformation(){
        showProgressDialog(getString(R.string.message_content_loading));
        HttpCall httpCallPost = new HttpCall();
        httpCallPost.setMethodtype(HttpCall.GET);
        String url = "\n" + getServerUrl() + getString(R.string.str_url_get_group_info);
        httpCallPost.setUrl(url);
        HashMap<String,String> paramsPost = new HashMap<>();
        paramsPost.put("group_id", currentGroupId + "");
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
                        currentGroupName = responseData.getString("name");
                        currentGroupId = responseData.getInt("id");
                        currentGroupPhotoUrl = responseData.getString("photo_url");
                        currentGroupAdminName = responseData.getString("admin_name");
                        currentGroupMemberCount = responseData.getInt("member_count");
                        currentGroupGoalCount = responseData.getInt("goal_count");
                        if (responseData.getInt("contribution_active_status") == 1){
                            currentGroupContributionStatus = true;
                        }
                        else {
                            currentGroupContributionStatus = false;
                        }

                        showGroupInformation();
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

    private void joinGroup(){
        showProgressDialog(getString(R.string.message_content_loading));
        HttpCall httpCallPost = new HttpCall();
        httpCallPost.setMethodtype(HttpCall.POST);
        String url = "\n" + getServerUrl() + getString(R.string.str_url_join_group);
        httpCallPost.setUrl(url);
        HashMap<String,String> paramsPost = new HashMap<>();
        paramsPost.put("user_id", Global.userId + "");
        paramsPost.put("group_id", currentGroupId  + "");
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
                        gotoSuccessActivity();
                    }
                    else if (responseCode == Global.RESULT_ALREADY_EXIST) {
                        CustomProgress.dismissDialog();
                        showToast("You already join in this group");
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

    private void showGroupInformation(){
        showGroupImage();
        txtGroupName.setText(currentGroupName);
        txtGroupAdminName.setText(currentGroupAdminName);
        txtGroupMemberCount.setText(currentGroupMemberCount + "");
        txtGroupGoalCount.setText(currentGroupGoalCount + "");
        if (currentGroupContributionStatus)
            txtGroupContributionStatus.setText("On");
        else
            txtGroupContributionStatus.setText("Off");
    }

    private void gotoSuccessActivity(){
        Intent intent = new Intent(JoinGroupActivity.this, SuccessActivity.class);
        Bundle b = new Bundle();
        b.putString("key", getResources().getString(R.string.content_join_group));
        intent.putExtras(b);
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT|Intent.FLAG_ACTIVITY_CLEAR_TOP);
        thisActivity.startActivity(intent);
    }
}
