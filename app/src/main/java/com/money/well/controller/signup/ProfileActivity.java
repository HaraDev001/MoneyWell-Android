package com.money.well.controller.signup;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import com.money.well.R;
import com.money.well.common.Global;
import com.money.well.common.OnMultiClickListener;
import com.money.well.controller.base.BaseActivity;
import com.money.well.controller.model.NationObject;
import com.money.well.utils.network.HttpCall;
import com.money.well.utils.network.HttpRequest;
import com.ycuwq.datepicker.date.DatePickerDialogFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import static com.money.well.common.Global._isTest;
import static com.money.well.common.Global.userId;

public class ProfileActivity extends BaseActivity {
    private TextView txtTopTitle;

    private Button btnNext;
    private ImageView imgTopLeft;

    private ImageView imgSelectNation;
    private ImageView imgSelectGender;
    private ImageView imgSelectBirthday;
    private EditText edtNationality;
    private EditText edtUserName;
    private EditText edtGender;
    private EditText edtBirthday;
    private String[] listCountryItems;
    private int currentCheckedItemId = 0;
    private int currentCheckedItemNo = 0;
    private String[] listGender = {"Male", "Female"};
    private int currentGenderNo = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        thisActivity = this;
        thisContext = this;
        thisView = findViewById(R.id.activity_profile);

        initBasicUI();

        getNationList();
    }

    private void initBasicUI(){
        txtTopTitle = findViewById(R.id.txt_top_title);
        txtTopTitle.setText(getResources().getString(R.string.title_profile_setting));
        btnNext = findViewById(R.id.btn_next);
        btnNext.setOnClickListener(new OnMultiClickListener() {
            @Override
            public void onMultiClick(View v) {
                updateUserProfile();

            }
        });

        imgTopLeft = findViewById(R.id.img_top_left);
        imgTopLeft.setOnClickListener(new OnMultiClickListener() {
            @Override
            public void onMultiClick(View v) {
                finish();
            }
        });

        edtNationality = findViewById(R.id.txt_nationality);
        edtUserName = findViewById(R.id.txt_name);
        edtGender = findViewById(R.id.txt_gender);
        edtBirthday = findViewById(R.id.txt_birthday);

        imgSelectNation = findViewById(R.id.img_select_nation);
        imgSelectNation.setOnClickListener(new OnMultiClickListener() {
            @Override
            public void onMultiClick(View v) {
                AlertDialog.Builder mBuilder = new AlertDialog.Builder(ProfileActivity.this);
                mBuilder.setTitle(getResources().getString(R.string.alert_select_nationality));
                mBuilder.setSingleChoiceItems(listCountryItems, currentCheckedItemNo, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        edtNationality.setText(listCountryItems[i]);
                        currentCheckedItemId = Global.nationObjectArrayList.get(i).getId();
                        currentCheckedItemNo = i;
                        dialogInterface.dismiss();
                    }
                });

                AlertDialog mDialog = mBuilder.create();
                mDialog.show();
            }
        });
        imgSelectGender = findViewById(R.id.img_select_gender);
        imgSelectGender.setOnClickListener(new OnMultiClickListener() {
            @Override
            public void onMultiClick(View v) {
                AlertDialog.Builder mBuilder = new AlertDialog.Builder(ProfileActivity.this);
                mBuilder.setTitle(getResources().getString(R.string.alert_select_gender));
                mBuilder.setSingleChoiceItems(listGender, currentGenderNo, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        edtGender.setText(listGender[i]);
                        currentGenderNo = i;
                        dialogInterface.dismiss();
                    }
                });

                AlertDialog mDialog = mBuilder.create();
                mDialog.show();
            }
        });
        imgSelectBirthday = findViewById(R.id.img_select_birthday);
        imgSelectBirthday.setOnClickListener(new OnMultiClickListener() {
            @Override
            public void onMultiClick(View v) {
                DatePickerDialogFragment datePickerDialogFragment = new DatePickerDialogFragment();
                datePickerDialogFragment.setOnDateChooseListener(new DatePickerDialogFragment.OnDateChooseListener() {
                    @Override
                    public void onDateChoose(int year, int month, int day) {
                        Calendar c = Calendar.getInstance();
                        int currentYear = c.get(Calendar.YEAR);
                        int currentMonth = c.get(Calendar.MONTH) + 1;
                        int currentDay = c.get(Calendar.DAY_OF_MONTH);
                        if(year > currentYear)
                        {
                            edtBirthday.setText("");
                            String msgContent = getString(R.string.notice_birthday_overlay);
                            showToast(msgContent);
                        }
                        else if (year == currentYear && month > currentMonth){
                            edtBirthday.setText("");
                            String msgContent = getString(R.string.notice_birthday_overlay);
                            showToast(msgContent);
                        }
                        else if (year == currentYear && month == currentMonth && day > currentDay){
                            edtBirthday.setText("");
                            String msgContent = getString(R.string.notice_birthday_overlay);
                            showToast(msgContent);
                        }
                        else {
                            edtBirthday.setText(year + "-" + month + "-" + day);
                        }
                    }
                });
                datePickerDialogFragment.show(getFragmentManager(), "DatePickerDialogFragment");
            }
        });
    }

    private void getNationList(){
        HttpCall httpCallPost = new HttpCall();
        httpCallPost.setMethodtype(HttpCall.GET);
        String url = "\n" + getServerUrl() + getString(R.string.str_url_get_nation_list);
        httpCallPost.setUrl(url);
        HashMap<String,String> paramsPost = new HashMap<>();
        httpCallPost.setParams(paramsPost);
        new HttpRequest(){
            @Override
            public void onResponse(String str) {
                super.onResponse(str);
                try {
                    JSONObject response = new JSONObject(str);
                    int responseCode = (int) response.get("code");
                    if (responseCode == Global.RESULT_SUCCESS) {
                        JSONArray responseData =  response.getJSONArray("data");
                        Global.nationObjectArrayList = new ArrayList<>();
                        listCountryItems = new String[responseData.length()];
                        for (int i = 0; i < responseData.length(); i++) {
                            JSONObject temp = responseData.getJSONObject(i);
                            NationObject nationObject = new NationObject();
                            nationObject.setId(temp.getInt("id"));
                            nationObject.setName(temp.getString("name"));
                            nationObject.setPhonePrefix(temp.getString("phone_number_prefix"));
                            if (i == 0){
                                nationObject.setSelectStatus(true);
                                currentCheckedItemId = temp.getInt("id");
                            }
                            else{
                                nationObject.setSelectStatus(false);
                            }
                            Global.nationObjectArrayList.add(nationObject);
                            listCountryItems[i] = temp.getString("name");
                        }
                        currentCheckedItemNo = 0;
                    }
                    else if (responseCode == Global.RESULT_FAILED) {
                        showToast("Failed");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    showToast("Network error");
                }

            }
        }.execute(httpCallPost);
    }

    private void updateUserProfile(){
        int nation = currentCheckedItemId;
        int gender = currentGenderNo;
        String strNation = edtNationality.getText().toString();
        String strGender = edtGender.getText().toString();
        String name = edtUserName.getText().toString();
        String birthday = edtBirthday.getText().toString();
        if (checkInputValues(strNation, strGender, name, birthday)){
            HttpCall httpCallPost = new HttpCall();
            httpCallPost.setMethodtype(HttpCall.PUT);
            String url = "\n" + getServerUrl() + getString(R.string.str_url_get_update_profile);
            httpCallPost.setUrl(url);
            HashMap<String,String> paramsPost = new HashMap<>();
            if (_isTest)
                paramsPost.put("user_id", 1 + "");

            else
                paramsPost.put("user_id", userId + "");
            paramsPost.put("nation", nation + "");
            paramsPost.put("gender", gender + "");
            paramsPost.put("birthday", birthday);
            paramsPost.put("name", name);
            httpCallPost.setParams(paramsPost);
            new HttpRequest(){
                @Override
                public void onResponse(String str) {
                    super.onResponse(str);
                    try {
                        JSONObject response = new JSONObject(str);
                        int responseCode = (int) response.get("code");
                        if (responseCode == Global.RESULT_SUCCESS) {
                            gotoUploadPhotoActivity();
                        }
                        else if (responseCode == Global.RESULT_FAILED) {
                            showToast("Failed");
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        showToast("Network error");
                    }

                }
            }.execute(httpCallPost);
        }
    }

    private boolean checkInputValues(String nation, String gender, String name, String birthday){
        boolean isValue = true;
        if (nation.equals("")){
            showToast(getString(R.string.notice_input_nation));
            isValue = false;
        }
        else if (name.equals("")){
            showToast(getString(R.string.notice_input_name));
            isValue = false;
        }
        else if (gender.equals("")){
            showToast(getString(R.string.notice_input_gender));
            isValue = false;
        }
        else if (birthday.equals("")){
            showToast(getString(R.string.notice_input_birthday));
            isValue = false;
        }
        return isValue;
    }

    private void gotoUploadPhotoActivity(){
        Intent intent = new Intent(ProfileActivity.this, UploadPhotoActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT|Intent.FLAG_ACTIVITY_CLEAR_TOP);
        thisActivity.startActivity(intent);

        finish();
    }
}
