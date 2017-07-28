package com.zephyr.easycamera.utils;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.util.Log;
import android.view.OrientationEventListener;
import android.view.Surface;

import java.util.List;

/**
 * 相机操作类，用于管理相机
 * Created by zephyr on 2017/7/27.
 */

public class CameraOperateHelper implements ICameraOperate{

    public static final String TAG = "CameraOperateHelper";

    private static final int PICTURE_WIDTH = 1280;

    private static final int PICTURE_HEIGHT = 720;
    final Activity mActivity;

    private int mCameraId = Camera.CameraInfo.CAMERA_FACING_FRONT; // 相机id，用户区分前后置相机

    Camera mCamera;
    int mJpegRotation;

    public CameraOperateHelper(Activity c) {
        mActivity = c;
    }

    @Override
    public boolean doCheckCameraHare(Context context) {
        if (context == null) return false;
        if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public Camera doOpenCamera(int cameraId) {
        boolean find = false;
        try {
            int numberOfCameras = Camera.getNumberOfCameras();
            Camera.CameraInfo info = new Camera.CameraInfo();
            for (int i = 0; i < numberOfCameras; i++) {
                Camera.getCameraInfo(i, info);
                if (info.facing == cameraId) {
                    mCameraId = cameraId;
                    find = true;
                    break;
                }
            }
            if (find == true) {
                mCamera = Camera.open(mCameraId);
            } else {
                mCameraId = Camera.CameraInfo.CAMERA_FACING_BACK;
                mCamera = Camera.open(mCameraId);
            }
            return mCamera;
        } catch (Exception e) {
            Log.d(TAG, "doOpenCamera: camera open ERROR" + e.toString());
            return null;
        }
    }

    @Override
    public void setCameraParams() {// TODO 后期可以传入不同的照片长宽,目前默认为1280x720
        Camera.Parameters parameters = mCamera.getParameters();
        List<Camera.Size> supportedPictureSizes = parameters.getSupportedPictureSizes();
        List<Camera.Size> supportedPreviewSizes = parameters.getSupportedPreviewSizes();

        Camera.Size optimalPictureSize = getOptimalSize(supportedPictureSizes, PICTURE_HEIGHT, PICTURE_WIDTH);
        parameters.setPictureSize(optimalPictureSize.width, optimalPictureSize.height);
        Log.d(TAG, "setCameraParams: picture width : " + optimalPictureSize.width + " , picture height : " + optimalPictureSize.height);

        Camera.Size optimalPreviewSize = getOptimalSize(supportedPreviewSizes, 1280, 720);//TODO 根据CameraView的长宽设置
        parameters.setPreviewSize(optimalPreviewSize.width, optimalPictureSize.height);
        Log.d(TAG, "setCameraParams: preview width : " + optimalPreviewSize.width + " , preview height : " + optimalPreviewSize.height);

        int orientation = correctPreviewOrientation(mCameraId, getDisplayRotation(mActivity));
        mCamera.setDisplayOrientation(orientation);

        // TODO 后期增加闪光灯设置
        mJpegRotation = getJpegRotation(mCameraId, getDisplayRotation(mActivity));

        parameters.setPictureFormat(PixelFormat.JPEG);

        parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
        mCamera.cancelAutoFocus();

        mCamera.setParameters(parameters);
    }

    @Override
    public void doStartPreview() {
        if (mCamera != null) {
            mCamera.startPreview();
        }
    }

    @Override
    public void doStopPreview() {
        if (mCamera != null) {
            mCamera.stopPreview();
        }
    }

    @Override
    public void doReleaseCamera() {
        if (mCamera != null) {
            mCamera.setPreviewCallback(null);
            mCamera.setErrorCallback(null);
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
        }
    }

    @Override
    public void doTakePicture(Camera.PictureCallback callback) {
        mCamera.takePicture(null, null, callback);
    }

    /**
     * 寻找w/h比例相当并且h高度绝对值相差最小的Size
     * @param supportedPictureSizes
     * @param pictureWidth
     * @param pictureHeight
     * @return
     */
    private Camera.Size getOptimalSize(List<Camera.Size> supportedPictureSizes, int pictureWidth, int pictureHeight) {

        if (supportedPictureSizes == null) return null;
        double ASPECT_TOLERANCE = 0.1;
        int targetRatio = pictureWidth / pictureHeight;
        double minDiff = Double.MAX_VALUE;
        Camera.Size optimalSize = null;

        int sizes = supportedPictureSizes.size();
        for (int i = 0; i < sizes; i++) {
            Camera.Size size = supportedPictureSizes.get(i);
            int ratio = size.width / size.height;
            if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE) continue;
            if (Math.abs(size.width - pictureWidth) < minDiff) {
                optimalSize = size;
                minDiff = Math.abs(size.width - pictureWidth);
            }
        }

        // 如果没有比例相差小于0.1的size的话，则忽略比例
        if (optimalSize == null) {
            minDiff = Double.MAX_VALUE;
            for (Camera.Size size : supportedPictureSizes) {
                if (Math.abs(size.width - pictureWidth) < minDiff) {
                    optimalSize = size;
                    minDiff = Math.abs(size.width - pictureWidth);
                }
            }
        }
        return optimalSize;
    }

    // 所有角度枚举
    public final class Degree {
        public static final int ROTATION_0 = 0;
        public static final int ROTATION_90 = 90;
        public static final int ROTATION_180 = 180;
        public static final int ROTATION_270 = 270;
        public static final int ROTATION_360 = 360;
    }

    /**
     * 获取手握设备方向
     * @param activity 不传值则默认为正握0
     * @return
     */
    public int getDisplayRotation(Activity activity) {
        if(activity==null)
            return Degree.ROTATION_0;
        int rotation = activity.getWindowManager().getDefaultDisplay().getRotation();// 屏幕旋转角度和设备物理角度相反，比如逆时针转90度，则这里rotation为ROTATION_90，
        switch (rotation) {
            case Surface.ROTATION_0:
                return Degree.ROTATION_0;
            case Surface.ROTATION_90:
                return Degree.ROTATION_90;
            case Surface.ROTATION_180:
                return Degree.ROTATION_180;
            case Surface.ROTATION_270:
                return Degree.ROTATION_270;
        }
        return Degree.ROTATION_0;
    }

    /**
     * 矫正预览方向偏差
     * @param cameraId
     */
    private int correctPreviewOrientation(int cameraId, int degree) {
        Camera.CameraInfo info = new Camera.CameraInfo();
        Camera.getCameraInfo(cameraId, info);
        int result;
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation + degree) % Degree.ROTATION_360;
            result = (Degree.ROTATION_360 - result) % Degree.ROTATION_360; // compensate
                                                                             // the
                                                                            // mirror
        } else { // back-facing
            result = (info.orientation - degree + Degree.ROTATION_360)
                    % Degree.ROTATION_360;
        }
        return result;
    }

    /**
     * 获得照片旋转角度
     * @param cameraId
     * @param orientation
     * @return
     */
    private int getJpegRotation(int cameraId, int orientation) {
        // See android.hardware.Camera.Parameters.setRotation for
        // documentation.
        int rotation = 0;
        if (orientation != OrientationEventListener.ORIENTATION_UNKNOWN) {
            Camera.CameraInfo info = new Camera.CameraInfo();
            Camera.getCameraInfo(cameraId, info);
            if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                rotation = (info.orientation - orientation + Degree.ROTATION_360)
                        % Degree.ROTATION_360;
            } else { // back-facing camera
                rotation = (info.orientation + orientation)
                        % Degree.ROTATION_360;
            }
        }
        return rotation;
    }

    public boolean isSupported(String value, List<String> supported) {
        return supported == null ? false : supported.indexOf(value) >= 0;
    }

    public int getPictureRotation() {
        return mJpegRotation;
    }

}
