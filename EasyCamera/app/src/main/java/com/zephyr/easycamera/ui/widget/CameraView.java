package com.zephyr.easycamera.ui.widget;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Toast;

import com.zephyr.easycamera.utils.CameraOperateHelper;
import com.zephyr.easycamera.utils.FileUtil;

import java.io.IOException;

/**
 * Created by zephyr on 2017/7/27.
 */

public class CameraView extends SurfaceView implements SurfaceHolder.Callback, Camera.ErrorCallback, Camera.PictureCallback {

    public static final String TAG = "CameraView";

    private Context mContext;

    private int mCameraId = Camera.CameraInfo.CAMERA_FACING_BACK;

    private Camera mCamera;

    SurfaceHolder mHolder;

    CameraOperateHelper mCameraOperateHelper;

    public CameraView(Context context) {
        this(context, null);
    }

    public CameraView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CameraView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        init();
    }

    private void init() {
        setZOrderOnTop(false);
        mHolder = this.getHolder();
        mHolder.addCallback(this);
        mHolder.setFormat(PixelFormat.TRANSLUCENT);
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        mCameraOperateHelper = new CameraOperateHelper(((Activity) mContext));
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
        }
        return super.onTouchEvent(event);
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        boolean b = mCameraOperateHelper.doCheckCameraHare(mContext);
        if (!b) {
            Toast.makeText(mContext, "找不到相机设备", Toast.LENGTH_SHORT).show();
            return;
        }

        if (mCamera == null) {
            mCamera = mCameraOperateHelper.doOpenCamera(mCameraId);
        }

        if (mCamera == null) {
            Toast.makeText(mContext, "相机启动失败", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            mCameraOperateHelper.setCameraParams();
            mCamera.setErrorCallback(this);
            mCamera.setPreviewDisplay(mHolder);
            mCamera.startPreview();
        } catch (IOException e) {
            e.printStackTrace();
            Log.d(TAG, "surfaceCreated: startPreview error!");
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {
        if (surfaceHolder.getSurface() == null) {
            return;
        }
        try {
            mCamera.setPreviewCallback(null);
            mCamera.setErrorCallback(null);
            mCamera.stopPreview();
        } catch (Exception e) {
            Log.d(TAG, "surfaceChanged: stopPreview error" + e.getMessage());
        }

        try {
            mCameraOperateHelper.setCameraParams();
            mCamera.setErrorCallback(this);
            mCamera.setPreviewDisplay(mHolder);
            mCamera.startPreview();
        } catch (IOException e) {
            e.printStackTrace();
            Log.d(TAG, "surfaceChanged: startPreview error!");
        }

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        releaseCamera();
        Log.d(TAG, "surfaceDestroyed:");
    }

    @Override
    public void onError(int i, Camera camera) {

    }

    /**
     * 切换摄像头
     */
    public void switchCamera() {
        int numberOfCameras = Camera.getNumberOfCameras();
        if (numberOfCameras <= 1) return;

        Camera.CameraInfo info = new Camera.CameraInfo();
        for (int i = 0; i < numberOfCameras; i++) {
            Camera.getCameraInfo(i, info);
            if (info.facing == mCameraId) {
                if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                    mCameraId = Camera.CameraInfo.CAMERA_FACING_BACK;
                } else {
                    mCameraId = Camera.CameraInfo.CAMERA_FACING_FRONT;
                }
                break;
            }
        }

        releaseCamera();
        if (mCamera == null) {
            mCamera = mCameraOperateHelper.doOpenCamera(mCameraId);
        }

        if (mCamera == null) {
            Toast.makeText(mContext, "相机启动失败", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            mCameraOperateHelper.setCameraParams();
            mCamera.setErrorCallback(this);
            mCamera.setPreviewDisplay(mHolder);
            mCamera.startPreview();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setmCameraId(int cameraId) {
        this.mCameraId = cameraId;
    }

    public void stopPreview() {
        mCameraOperateHelper.doStopPreview();
    }

    public void statrPreview() {
        mCameraOperateHelper.doStartPreview();
    }

    public void releaseCamera() {
        mCameraOperateHelper.doReleaseCamera();
        mCamera = null;
    }

    public void takePicture() {
        mCameraOperateHelper.doTakePicture(this);
    }

    @Override
    public void onPictureTaken(byte[] bytes, Camera camera) {
        Bitmap tempBitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        Bitmap finalBitmap = null;
        if (tempBitmap == null) return;
        Matrix matrix = new Matrix();
        matrix.postRotate(mCameraOperateHelper.getPictureRotation());

        finalBitmap = Bitmap.createBitmap(tempBitmap, 0, 0, tempBitmap.getWidth(), tempBitmap.getHeight(), matrix, true);
        FileUtil.saveBitmap2File(finalBitmap);

        mCameraOperateHelper.doStartPreview();
        tempBitmap.recycle();
        tempBitmap = null;

    }
}
