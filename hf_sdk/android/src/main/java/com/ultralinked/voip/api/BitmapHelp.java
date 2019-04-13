package com.ultralinked.voip.api;

import android.graphics.Bitmap;
import android.os.Build;


/**
 * bitmap help class
 *
 */
public class BitmapHelp {

    private BitmapHelp() {

    }

    private static BitmapUtils bitmapUtils;

    /**
     * BitmapUtils
     *
     * @return
     */
    public static BitmapUtils getBitmapUtils() {
        if (bitmapUtils == null) {
            bitmapUtils = new BitmapUtils();
        }
        return bitmapUtils;
    }

    /**
     *  returns the byte size of the given bitmap
     *
     * @param bitmap origin bitmap
     * @return byte size of the bitmap
     *
     */
    public static int byteSizeOf(Bitmap bitmap) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            return bitmap.getAllocationByteCount();
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1) {
            return bitmap.getByteCount();
        } else {
            return bitmap.getRowBytes() * bitmap.getHeight();
        }
    }

    /**
     * returns the kB size of the give bitmap
     *
     * @param bitmap origin bitmap
     * @return kB size of the bitmap
     *
     */
    public static int kBSizeOf(Bitmap bitmap) {

        if(bitmap==null){
             return 0;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            return bitmap.getAllocationByteCount()/1024;
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1) {
            return bitmap.getByteCount()/1024;
        } else {
            return (bitmap.getRowBytes() * bitmap.getHeight())/1024;
        }
    }


}
