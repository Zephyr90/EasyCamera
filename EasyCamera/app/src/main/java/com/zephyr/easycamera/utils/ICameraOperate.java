package com.zephyr.easycamera.utils;

import android.content.Context;
import android.hardware.Camera;

/**
 * Created by zephyr on 2017/7/27.
 */

public interface ICameraOperate {

    /**
     * 检测是否有Camera硬件设备
     * @param context
     * @return
     */
    boolean doCheckCameraHare(Context context);

    /**
     * 获取相机实例，并打开
     * @param cameraId
     * @return
     */
    Camera doOpenCamera(int cameraId);


    /**
     * 设置相机参数
     */
    void setCameraParams();

    /**
     * 开始预览
     */
    void doStartPreview();

    /**
     * 停止预览
     */
    void doStopPreview();

    /**
     * 释放相机资源
     */
    void doReleaseCamera();


}
