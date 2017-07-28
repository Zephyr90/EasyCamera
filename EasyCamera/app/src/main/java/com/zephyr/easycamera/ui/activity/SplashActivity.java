package com.zephyr.easycamera.ui.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.zephyr.easycamera.R;
import com.zephyr.easycamera.utils.permission.IRequestPermissionCallback;
import com.zephyr.easycamera.utils.permission.ZPermissionHelper;

public class SplashActivity extends AppCompatActivity implements IRequestPermissionCallback{

    private static final int PERMISSION_REQUEST = 0X101;

    private static final int DELAYMILLIS = 3000;

    private String[] mPermissions = new String[]{
            Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    private Handler mHandler = new Handler();

    private Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            Intent intent = new Intent(SplashActivity.this, CameraActivity.class);
            startActivity(intent);
//            overridePendingTransition(); // 跳转动画
            finish();
        }
    };
    ImageView mLogo;
    TextView mAppName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        ZPermissionHelper helper = new ZPermissionHelper(this, mPermissions, PERMISSION_REQUEST, this);
        helper.checkPermissions();

        mLogo = ((ImageView) findViewById(R.id.logo));
        mAppName = ((TextView) findViewById(R.id.app_name));

        Animation animation = AnimationUtils.loadAnimation(this, R.anim.anim_alpha);
        mLogo.startAnimation(animation);
        mAppName.startAnimation(animation);
    }

    @Override
    public void onPermissionGranted() {
        mHandler.postDelayed(mRunnable, DELAYMILLIS);
    }

    @Override
    public void onAfterPermissionGranted(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        mHandler.postDelayed(mRunnable, DELAYMILLIS);
    }

    @Override
    public void onShouldShowRequestPermissionRationale(@NonNull Activity activity, @NonNull String permission) {

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            mHandler.removeCallbacks(mRunnable);
        }
        return super.onKeyDown(keyCode, event);
    }
}
