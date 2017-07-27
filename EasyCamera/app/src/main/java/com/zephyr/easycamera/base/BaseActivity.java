package com.zephyr.easycamera.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by zephyr on 2017/7/27.
 */

public abstract class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        setListener();
        initData();

        if (savedInstanceState != null) {
            initFragment();
        }
    }

    protected abstract void initData();

    protected abstract void setListener();

    protected abstract void initFragment();

    protected abstract void initView();
}
