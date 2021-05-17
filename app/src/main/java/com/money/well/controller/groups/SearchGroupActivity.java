package com.money.well.controller.groups;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.BaseBundle;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.money.well.R;
import com.money.well.common.Global;
import com.money.well.common.OnMultiClickListener;
import com.money.well.controller.adapter.GroupAdapter;
import com.money.well.controller.base.BaseActivity;
import com.money.well.controller.model.GroupObject;
import com.money.well.utils.dialog.CustomProgress;
import com.money.well.utils.network.HttpCall;
import com.money.well.utils.network.HttpRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.security.acl.Group;
import java.util.ArrayList;
import java.util.HashMap;

import static com.money.well.common.Global.currentGroupAdminName;
import static com.money.well.common.Global.currentGroupId;
import static com.money.well.common.Global.currentGroupJoinStatus;
import static com.money.well.common.Global.currentGroupName;
import static com.money.well.common.Global.currentGroupPhotoUrl;
import static com.money.well.common.Global.groupObjectArrayList;
import static com.money.well.common.Global.userPassword;

public class SearchGroupActivity extends BaseActivity {
    private ImageView imgTopLeft;
    private TextView txtTopTitle;

    private EditText edtGroupSearch;
    private ListView lstGroup;
    private LinearLayout layEmptyResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_group);

        thisActivity = this;
        thisContext = this;
        thisView = findViewById(R.id.activity_search_group);

        initBasicUI();
        searchGroup("");
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
        txtTopTitle.setText(R.string.title_search_group);
        edtGroupSearch = findViewById(R.id.edt_group_search);
        edtGroupSearch.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {
                String strContent = edtGroupSearch.getText().toString();
                searchGroup(strContent);
                edtGroupSearch.setFocusable(true);

            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        });
//        edtGroupSearch.setOnFocusChangeListener(new View.OnFocusChangeListener() {
//            @Override
//            public void onFocusChange(View v, boolean hasFocus) {
//                if (hasFocus) {
//                    dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
//                }
//            }
//        });

        lstGroup = findViewById(R.id.lst_group);
        layEmptyResult = findViewById(R.id.empty_result);
    }

    private void searchGroup(String searchKey){
        showProgressDialog(getString(R.string.message_content_loading));
        HttpCall httpCallPost = new HttpCall();
        httpCallPost.setMethodtype(HttpCall.GET);
        String url = "\n" + getServerUrl() + getString(R.string.str_url_get_group);
        httpCallPost.setUrl(url);
        HashMap<String,String> paramsPost = new HashMap<>();
        paramsPost.put("user_id", Global.userId + "");
        paramsPost.put("search_key", searchKey);
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
                        groupObjectArrayList = new ArrayList<>();
                        JSONArray responseData =  response.getJSONArray("data");
                        for (int i = 0; i < responseData.length(); i++){
                            JSONObject temp = responseData.getJSONObject(i);
                            GroupObject groupObject = new GroupObject();
                            groupObject.setId(temp.getInt("id"));
                            groupObject.setName(temp.getString("name"));
                            groupObject.setPhotoUrl(temp.getString("photo_url"));
                            groupObject.setAdminName(temp.getString("admin_name"));
                            groupObject.setNoticeCount(i);
                            if (temp.getInt("joined_status") == 1){
                                groupObject.setJoinStatus(true);
                            }
                            else {
                                groupObject.setJoinStatus(false);
                            }
                            groupObjectArrayList.add(groupObject);
                        }
                        showGroupList();
                    }
                    else if (responseCode == Global.RESULT_EMPTY) {
                        CustomProgress.dismissDialog();
                        showEmpty();
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

    private void showGroupList(){
        layEmptyResult.setVisibility(View.GONE);
        lstGroup.setVisibility(View.VISIBLE);
        if (groupObjectArrayList != null){
            GroupAdapter groupAdapter = new GroupAdapter(thisContext,
                    R.layout.item_group, groupObjectArrayList);
            lstGroup.setAdapter(groupAdapter);
            lstGroup.setOnItemClickListener(onItemListener);
        }
    }

    ListView.OnItemClickListener onItemListener = new ListView.OnItemClickListener(){
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            currentGroupId = groupObjectArrayList.get(position).getId();
            currentGroupName = groupObjectArrayList.get(position).getName();
            currentGroupPhotoUrl = groupObjectArrayList.get(position).getPhotoUrl();
            currentGroupJoinStatus = groupObjectArrayList.get(position).getJoinStatus();
            currentGroupAdminName = groupObjectArrayList.get(position).getAdminName();

            if (currentGroupJoinStatus){
                Intent intent = new Intent(SearchGroupActivity.this, GroupDetailActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT|Intent.FLAG_ACTIVITY_CLEAR_TOP);
                thisActivity.startActivity(intent);
            }
            else {
                Intent intent = new Intent(SearchGroupActivity.this, JoinGroupActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT|Intent.FLAG_ACTIVITY_CLEAR_TOP);
                thisActivity.startActivity(intent);
            }

        }
    };

    private void showEmpty(){
        layEmptyResult.setVisibility(View.VISIBLE);
        lstGroup.setVisibility(View.GONE);
    }
}
