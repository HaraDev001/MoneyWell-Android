package com.money.well.controller.base;

import android.content.Context;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.money.well.R;
import com.money.well.common.Global;

import butterknife.ButterKnife;
import butterknife.Unbinder;

public abstract class BaseFragmentActivity extends AppCompatActivity {
    public AppCompatActivity thisActivity;
    public Context thisContext;
    public View thisView;
    private Unbinder unbinder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(getLayoutId());
        unbinder = ButterKnife.bind(this);

        initView(savedInstanceState);

    }

    public String getServerUrl() {
        String httpUrl;
        if (Global._isCloud)
            httpUrl = getString(R.string.str_cloud_http_server);
        else
            httpUrl = getString(R.string.str_local_http_server);
        return httpUrl;
    }

    protected abstract void initView(Bundle savedInstanceState);

    protected abstract int getLayoutId();

    protected abstract void initWidgetAciotns();
}
