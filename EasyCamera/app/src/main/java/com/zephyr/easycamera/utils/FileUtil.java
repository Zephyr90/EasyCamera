package com.zephyr.easycamera.utils;

/**
 * Created by zephyr on 2017/7/28.
 */

import android.graphics.Bitmap;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class FileUtil {
    private static final String TAG = "FileUtil";

    /*image*/
    public static final int IMAGE_TAG = 1;

    /*video*/
    public static final int VIDEO_TAG = 2;


    public static File getOutputMediaFile(int fileType) {
        File file = null;
        file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "sweetCameraAPP");
        if (!file.exists()) {
            if (!file.mkdir()) {
                Log.d(TAG, "error : fail to create directory");
                return null;
            }
        }

        File media = null;
        String timeStamp = new SimpleDateFormat("yyMMdd_HHmmss").format(new Date());

        if (fileType == IMAGE_TAG) {
            //照片
            media = new File(file.getPath() + File.separator + "IMG_" + timeStamp + ".jpg");
        } else if (fileType == VIDEO_TAG) {
            media = new File(file.getPath() + File.separator + "VID_" + timeStamp + ".mp4");
        } else {
            //视频
            return null;
        }
        return media;
    }

    public static void saveBitmap2File(Bitmap bitmap) {
        File file = FileUtil.getOutputMediaFile(FileUtil.IMAGE_TAG);
        if (file == null) return;
        try {
            FileOutputStream outputStream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
            outputStream.flush();
            outputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}