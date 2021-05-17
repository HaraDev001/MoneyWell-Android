package com.money.well.controller.home;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.money.well.R;
import com.money.well.common.Global;
import com.money.well.common.OnMultiClickListener;
import com.money.well.controller.adapter.MainViewPagerAdapter;
import com.money.well.controller.base.BaseActivity;
import com.money.well.controller.base.CCPCustomViewPager;
import com.money.well.controller.groups.SearchGroupActivity;
import com.money.well.controller.home.fragment.ContactUsFragment;
import com.money.well.controller.home.fragment.GroupFragment;
import com.money.well.controller.home.fragment.HomeFragment;
import com.money.well.controller.home.fragment.SettingFragment;
import com.money.well.controller.login.LoginActivity;
import com.money.well.controller.settings.HelpActivity;

import java.util.ArrayList;

import butterknife.ButterKnife;

import static com.money.well.common.Global.CONTACTS_CLICKED;
import static com.money.well.common.Global.GROUP_CLICKED;
import static com.money.well.common.Global.HOME_CLICKED;
import static com.money.well.common.Global.SETTING_CLICKED;
import static com.money.well.common.Global.clickTabIndex;

public class HomeNewActivity extends BaseActivity {
    private ImageView imgTopLeft;
    private ImageView imgTopRight;
    private TextView txtTopTitle;

    LinearLayout homeLayout;
    CCPCustomViewPager vp;
    TextView txtBottomHome;
    TextView txtBottomGroup;
    TextView txtBottomContact;
    TextView txtBottomSetting;
    TextView tvUnreadHomeNumber;
    TextView tvUnreadGroupNumber;
    TextView getTvUnreadContactNumber;
    FrameLayout flayHome;
    FrameLayout flayGroup;
    FrameLayout flayContact;
    FrameLayout flaySetting;
    ImageView imgBottomHome;
    ImageView imgBottomGroup;
    ImageView imgBottomContacts;
    ImageView imgBottomSetting;

    LinearLayout mainBottom;

    private int index;
    private ArrayList<Fragment> fragmentList;
    public static HomeNewActivity homeUI;
    public MainViewPagerAdapter mMainViewPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_new);
        ButterKnife.bind(this);
        thisActivity = this;
        thisContext = this;
        thisView = findViewById(R.id.activity_home_new);

        initBasicUI();
        initFragments();
        vp.setSlideEnabled(true);
        vp.setOffscreenPageLimit(4);
        mMainViewPagerAdapter = new MainViewPagerAdapter(getSupportFragmentManager(),
                fragmentList);
        vp.setAdapter(mMainViewPagerAdapter);
    }


    @Override
    protected void onResume() {
        super.onResume();
        reSetButton();
        showBottomUI(clickTabIndex);
        vp.setCurrentItem(Global.clickTabIndex, false);
    }


    private void initFragments() {
        fragmentList = new ArrayList<Fragment>();

        HomeFragment homeFra = HomeFragment.newInstance();//Home
        fragmentList.add(homeFra);
        GroupFragment groupFra = GroupFragment.newInstance();//Groups
        fragmentList.add(groupFra);
        ContactUsFragment contactFra = ContactUsFragment.newInstance();//Contacts
        fragmentList.add(contactFra);
        SettingFragment settingFra = SettingFragment.newInstance();//setting
        fragmentList.add(settingFra);
    }

    private void initBasicUI(){
        imgTopLeft = findViewById(R.id.img_top_left);
        imgTopLeft.setImageResource(R.drawable.growth_icon);
        imgTopLeft.setOnClickListener(new OnMultiClickListener() {
            @Override
            public void onMultiClick(View v) {
                if (clickTabIndex == HOME_CLICKED){
                    Intent intent = new Intent(HomeNewActivity.this, AnalyticsActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT|Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    thisActivity.startActivity(intent);
//                finish();
                }
            }
        });
        imgTopRight = findViewById(R.id.img_top_right);
        imgTopRight.setImageResource(R.drawable.help_icon);
        imgTopRight.setOnClickListener(new OnMultiClickListener() {
            @Override
            public void onMultiClick(View v) {
                if (clickTabIndex == HOME_CLICKED){
                    Intent intent = new Intent(HomeNewActivity.this, HelpActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT|Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    thisActivity.startActivity(intent);
//                finish();
                }
                else if (clickTabIndex == GROUP_CLICKED){
                    Intent intent = new Intent(HomeNewActivity.this, SearchGroupActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT|Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    thisActivity.startActivity(intent);
//                finish();
                }
            }
        });
        txtTopTitle = findViewById(R.id.txt_top_title);
        txtTopTitle.setText(R.string.title_empty);
        vp = findViewById(R.id.vp);
        vp.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                clickTabIndex = index = position;
                showBottomUI(position);
            }

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
        imgBottomHome = findViewById(R.id.img_bottom_home);
        imgBottomGroup = findViewById(R.id.img_bottom_group);
        imgBottomContacts = findViewById(R.id.img_bottom_contacts);
        imgBottomSetting = findViewById(R.id.img_bottom_setting);
        flayHome = findViewById(R.id.flay_home);
        flayHome.setOnClickListener(new OnMultiClickListener() {
            @Override
            public void onMultiClick(View v) {
                Global.clickTabIndex = index = HOME_CLICKED;
                showBottomUI(clickTabIndex);
                vp.setCurrentItem(index, true);
            }
        });

        flayGroup = findViewById(R.id.flay_group);
        flayGroup.setOnClickListener(new OnMultiClickListener() {
            @Override
            public void onMultiClick(View v) {
                Global.clickTabIndex = index = GROUP_CLICKED;
                showBottomUI(clickTabIndex);
                vp.setCurrentItem(index, true);
            }
        });

        flayContact = findViewById(R.id.flay_contact);
        flayContact.setOnClickListener(new OnMultiClickListener() {
            @Override
            public void onMultiClick(View v) {
                Global.clickTabIndex = index = CONTACTS_CLICKED;
                showBottomUI(clickTabIndex);
                vp.setCurrentItem(index, true);
            }
        });


        flaySetting = findViewById(R.id.flay_setting);
        flaySetting.setOnClickListener(new OnMultiClickListener() {
            @Override
            public void onMultiClick(View v) {
                Global.clickTabIndex = index = SETTING_CLICKED;
                showBottomUI(clickTabIndex);
                vp.setCurrentItem(index, true);
            }
        });

        txtBottomHome = findViewById(R.id.txt_bottom_home);
        txtBottomGroup = findViewById(R.id.txt_bottom_group);
        txtBottomContact = findViewById(R.id.txt_bottom_contact);
        txtBottomSetting = findViewById(R.id.txt_bottom_setting);

    }

    private void reSetButton() {
        imgBottomHome.setImageResource(R.drawable.home_icon);
        imgBottomGroup.setImageResource(R.drawable.group_icon);
        imgBottomContacts.setImageResource(R.drawable.contact_icon_tab);
        imgBottomSetting.setImageResource(R.drawable.setting_icon);
        txtBottomHome.setTextColor(getResources().getColor(R.color.font_gray));
        txtBottomGroup.setTextColor(getResources().getColor(R.color.font_gray));
        txtBottomContact.setTextColor(getResources().getColor(R.color.font_gray));
        txtBottomSetting.setTextColor(getResources().getColor(R.color.font_gray));
    }

    private void showTitleBar(){
        imgTopRight.setVisibility(View.VISIBLE);
        imgTopLeft.setVisibility(View.VISIBLE);
        switch (clickTabIndex)  {
            case 0:
                imgTopLeft.setImageResource(R.drawable.growth_icon);
                imgTopRight.setImageResource(R.drawable.help_icon);
                txtTopTitle.setText(R.string.title_empty);
                break;
            case 1:
                imgTopLeft.setVisibility(View.GONE);
                imgTopRight.setImageResource(R.drawable.search_icon);
                txtTopTitle.setText(R.string.title_group);
                break;
            case 2:
                imgTopLeft.setVisibility(View.GONE);
                imgTopRight.setVisibility(View.GONE);
                txtTopTitle.setText(R.string.title_contact);
                break;
            case 3:
                imgTopLeft.setVisibility(View.GONE);
                imgTopRight.setVisibility(View.GONE);
                txtTopTitle.setText(R.string.title_empty);
                break;
            default:
                break;
        }
    }

    private void searchGroup(){

    }

    private void showBottomUI(int index){
        switch (index){
            case 0:
                reSetButton();
                showTitleBar();
                imgBottomHome.setImageResource(R.drawable.home_icon_selected);
                txtBottomHome.setTextColor(getResources().getColor(R.color.font_blue));
                break;
            case 1:
                showTitleBar();
                reSetButton();
                imgBottomGroup.setImageResource(R.drawable.group_icon_selected);
                txtBottomGroup.setTextColor(getResources().getColor(R.color.font_blue));
                break;
            case 2:
                showTitleBar();
                reSetButton();
                imgBottomContacts.setImageResource(R.drawable.contact_icon_selected);
                txtBottomContact.setTextColor(getResources().getColor(R.color.font_blue));
                break;
            case 3:
                showTitleBar();
                reSetButton();
                imgBottomSetting.setImageResource(R.drawable.setting_icon_selected);
                txtBottomSetting.setTextColor(getResources().getColor(R.color.font_blue));
                break;
            default:
                break;
        }
    }


}
