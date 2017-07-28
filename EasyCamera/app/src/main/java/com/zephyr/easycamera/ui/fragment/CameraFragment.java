package com.zephyr.easycamera.ui.fragment;

import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.zephyr.easycamera.R;
import com.zephyr.easycamera.ui.widget.CameraView;
import com.zephyr.easycamera.utils.DisplayUtil;

/**
 * Created by zephyr on 2017/7/27.
 */

public class CameraFragment extends Fragment implements View.OnClickListener{

    public static final String TAG = "CameraFragment";

    private Activity mActivity;

    private View mRootView;
    CameraView mCameraView;
    TextView mRatio4_3;
    TextView mRatio16_9;
    int mDisplayHeight;
    int mDisplayWidth;
    LinearLayout mSwitchBar;
    View mRatio5_3;
    View mTakePhoto;
    View mCameraSwitcher;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mActivity = ((Activity) context);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (mRootView == null) {
            mRootView = inflater.inflate(R.layout.layout_camera, container, false);
        }
        init();
        return mRootView;
    }

    private void init() {
        mTakePhoto = mRootView.findViewById(R.id.ivTakePhoto);
        mCameraSwitcher = mRootView.findViewById(R.id.camera_switcher);
        mSwitchBar = ((LinearLayout) mRootView.findViewById(R.id.switchBar));
        mCameraView = ((CameraView) mRootView.findViewById(R.id.camera_view));
        mRatio4_3 = ((TextView) mRootView.findViewById(R.id.switchRatio_4_3));
        mRatio16_9 = ((TextView) mRootView.findViewById(R.id.switchRatio_16_9));
        mRatio5_3 = mRootView.findViewById(R.id.switchRatio_5_3);
        mRatio5_3.setOnClickListener(this);
        mRatio4_3.setOnClickListener(this);
        mRatio16_9.setOnClickListener(this);
        mTakePhoto.setOnClickListener(this);
        mCameraSwitcher.setOnClickListener(this);
        Point screenMetrics = DisplayUtil.getScreenMetrics(mActivity);
        mDisplayHeight = screenMetrics.y;
        mDisplayWidth = screenMetrics.x;
    }

    @Override
    public void onPause() {
        super.onPause();
        mCameraView.releaseCamera();
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.switchRatio_4_3:
                int width_4_3 = mDisplayWidth;
                int height_4_3 = width_4_3 / 3 * 4;
                switchRatio(width_4_3, height_4_3);
                break;
            case R.id.switchRatio_16_9:
                int height_16_9 = mDisplayHeight;
                int width_16_9 = height_16_9 / 16 * 9;
                switchRatio(width_16_9, height_16_9);
                break;
            case R.id.switchRatio_5_3:
                int width_5_3 = mDisplayWidth - 100;
                int height_5_3 = width_5_3 / 3 * 5;
                switchRatio(width_5_3, height_5_3);
                break;
            case R.id.ivTakePhoto:
                mCameraView.takePicture();
                break;
            case R.id.camera_switcher:
                mCameraView.switchCamera();
                break;
        }
    }

    private void switchRatio(int width, int height) {
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams)mCameraView.getLayoutParams();
        layoutParams.height = height;
        layoutParams.width = width;
        mCameraView.setLayoutParams(layoutParams);

    }
}
