package com.zephyr.easycamera.ui.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.zephyr.easycamera.R;
import com.zephyr.easycamera.ui.fragment.CameraFragment;

public class MainActivity extends AppCompatActivity {

    Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mToolbar = ((Toolbar) findViewById(R.id.mainToolbar));
        setSupportActionBar(mToolbar);
        initFragment(savedInstanceState);
    }

    private void initFragment(Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            FragmentManager supportFragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = supportFragmentManager.beginTransaction();
            fragmentTransaction.add(R.id.fragment_container, new CameraFragment());
            fragmentTransaction.commit();
        }
    }

}
