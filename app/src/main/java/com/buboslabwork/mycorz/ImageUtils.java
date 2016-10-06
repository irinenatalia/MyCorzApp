package com.buboslabwork.mycorz;

import android.net.Uri;

import java.io.File;

public class ImageUtils {

    public static Uri getImageUri(String path) {
        return Uri.fromFile(new File(path));
    }
}
