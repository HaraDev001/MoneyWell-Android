package com.money.well.controller.groups;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.tabs.TabLayout;
import com.money.well.R;
import com.money.well.common.OnMultiClickListener;
import com.money.well.controller.base.BaseActivity;
import com.money.well.controller.contacts.TransactionListFragment;
import com.money.well.controller.groups.detail.ContributionFragment;
import com.money.well.controller.groups.detail.MemberFragment;
import com.money.well.controller.home.fragment.HomeFragment;
import com.money.well.utils.AppBarStateChangeListener;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.money.well.common.Global.currentGroupAdminName;
import static com.money.well.common.Global.currentGroupName;
import static com.money.well.common.Global.currentGroupPhotoUrl;
import static com.money.well.common.Global.donationPhotoUrl;
import static com.money.well.common.Global.userName;

public class GroupDetailActivity extends BaseActivity {

    private ImageView imgTopLeft;
    private TextView txtTopTitle;
    private CircleImageView imgGroup;
    private TextView txtGroupName;
    private TextView txtGroupAdmin;
    private TextView txtUserRole;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_detail);
        thisActivity = this;
        thisContext = this;
        thisView = findViewById(R.id.activity_group_detail);

        ViewPager viewPager = findViewById(R.id.viewpager);
        if (viewPager != null) {
            setupViewPager(viewPager);
        }
        TabLayout tabLayout = findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        initBasicUI();
        showGroupInformation();
    }

    private void setupViewPager(ViewPager viewPager) {
        Adapter adapter = new Adapter(getSupportFragmentManager());
        adapter.addFragment(new MemberFragment(), getString(R.string.group_detail_member));
        adapter.addFragment(new ContributionFragment(), getString(R.string.group_detail_contribution));
        adapter.addFragment(new TransactionListFragment(), getString(R.string.group_detail_goal));
        viewPager.setAdapter(adapter);
    }

    static class Adapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragments = new ArrayList<>();
        private final List<String> mFragmentTitles = new ArrayList<>();

        public Adapter(FragmentManager fm) {
            super(fm);
        }

        public void addFragment(Fragment fragment, String title) {
            mFragments.add(fragment);
            mFragmentTitles.add(title);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragments.get(position);
        }

        @Override
        public int getCount() {
            return mFragments.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitles.get(position);
        }
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
        txtTopTitle.setText(R.string.title_empty);
        txtTopTitle.setText(currentGroupName + "");

        imgGroup = findViewById(R.id.img_group);
        txtGroupName = findViewById(R.id.txt_group_title);
        txtGroupAdmin = findViewById(R.id.txt_admin_name);
        txtUserRole = findViewById(R.id.txt_user_role);

        AppBarLayout appBarLayout = findViewById(R.id.appbar);

        appBarLayout.addOnOffsetChangedListener(new AppBarStateChangeListener() {
            @Override
            public void onStateChanged(AppBarLayout appBarLayout, State state) {
                if(state.equals(State.COLLAPSED)) {
                    txtTopTitle.setText(currentGroupName);
//                    Toast.makeText(getApplicationContext(), "COLLAPSED", Toast.LENGTH_SHORT).show();
                }
                else if (state.equals(State.EXPANDED)) {
                    txtTopTitle.setText("");
//                    Toast.makeText(getApplicationContext(), "EXPANDED", Toast.LENGTH_SHORT).show();
                }
                else if ((state.equals(State.IDLE))){
//                    Toast.makeText(getApplicationContext(), "IDLE", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void showGroupInformation(){
        txtGroupName.setText(currentGroupName);
        txtGroupAdmin.setText("Super Admin : " + currentGroupAdminName);
        if (currentGroupAdminName.equals(userName)){
            txtUserRole.setText("Your role : " + getString(R.string.group_role_admin));
        }
        else {
            txtUserRole.setText("Your role : " + getString(R.string.group_role_member));
        }
        Glide.with(thisContext)
                .load(currentGroupPhotoUrl)
                .into(imgGroup);
    }
}
