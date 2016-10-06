package com.buboslabwork.mycorz;

import android.util.Log;

public class ImageError {
    private static final String TAG = "ImageZoomCrop";

    public static void e(Throwable e){
        Log.e(TAG,e.getMessage(),e);
    }

    public static void e(String msg){
        Log.e(TAG,msg);
    }

}