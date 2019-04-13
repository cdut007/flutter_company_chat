package com.ultralinked.voip.api;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.util.Base64;

import com.ultralinked.voip.api.utils.FileUtils;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * bitmap utils class
 *
 */
public class BitmapUtils {

    /**
     * convert the dip to pix
     *
     * @param context
     * @param dips the dips to convert
     * @return the pixs value of the dips int this device
     *
     */
    public static int dip2pix(Context context, int dips) {
        int densityDpi = context.getResources().getDisplayMetrics().densityDpi;
        return (dips * densityDpi) / 160;
    }

    /**
     * convert the pix to dip m
     *
     * @param context
     * @param pixs the pix to convert
     * @return the dip value of the pixs int this device
     *
     */
    public static int pix2dip(Context context, int pixs) {
        int densityDpi = context.getResources().getDisplayMetrics().densityDpi;
        return (pixs * 160) / densityDpi;
    }


    /**
     * save the bitmp to the local path
     *
     * @param bitmap the bitmap to save
     * @param descPath the path to save the bitmap
     *
     */
    public static void saveBitmap(Bitmap bitmap, String descPath) {
        File file = new File(descPath);
        if(file.exists()){
            file.delete();

        }
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }
        if (!file.exists()) {
            try {
                bitmap.compress(CompressFormat.JPEG, 100, new FileOutputStream(
                        file));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }

        if (null != bitmap) {
            bitmap.recycle();
            bitmap = null;
        }
    }

    /**
     * convert the bitmap to round corner bitmap
     *
     * @param bitmap
     * @return  the round corner bitmap
     *
     */
    public static Bitmap getRoundedCornerBitmap(Bitmap bitmap) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
                bitmap.getHeight(), Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);
        final float roundPx = 12;

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

        paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        if (null != bitmap) {
            bitmap.recycle();
            bitmap = null;
        }

        return output;
    }
    /**
     * convert the bitmap to round corner bitmap
     *
     * @param bitmap
     * @return  the round corner bitmap
     *
     */
    public static Bitmap toRoundBitmap(Bitmap bitmap) {
        if (bitmap == null) {
            return null;
        }
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        float roundPx;
        float left, top, right, bottom, dst_left, dst_top, dst_right, dst_bottom;
        if (width <= height) {
            roundPx = width / 2;
            top = 0;
            bottom = width;
            left = 0;
            right = width;
            height = width;
            dst_left = 0;
            dst_top = 0;
            dst_right = width;
            dst_bottom = width;
        } else {
            roundPx = height / 2;
            float clip = (width - height) / 2;
            left = clip;
            right = width - clip;
            top = 0;
            bottom = height;
            width = height;
            dst_left = 0;
            dst_top = 0;
            dst_right = height;
            dst_bottom = height;
        }
        Bitmap output = Bitmap.createBitmap(width, height, Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect src = new Rect((int) left, (int) top, (int) right,
                (int) bottom);
        final Rect dst = new Rect((int) dst_left, (int) dst_top,
                (int) dst_right, (int) dst_bottom);
        final RectF rectF = new RectF(dst);
        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
        paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
        canvas.drawBitmap(bitmap, src, dst, paint);

        if(null != bitmap) {
            bitmap.recycle();
            bitmap = null;
        }
        return output;
    }

    /**
     * convert the input stream to byte array
     *
     * @param inStream
     * @return  the byte array of the input stream
     *
     */
    private static byte[] readStream(InputStream inStream) throws Exception {
        byte[] buffer = new byte[1024];
        int len = -1;
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        while ((len = inStream.read(buffer)) != -1) {
            outStream.write(buffer, 0, len);
        }
        byte[] data = outStream.toByteArray();
        outStream.close();
        inStream.close();
        return data;

    }

    /**
     * get the bitmap from byte array
     *
     * @param bytes
     * @param opts the bitmap config options
     * @return  the bitmap of the byte array
     *
     */
    public static Bitmap getPicFromBytes(byte[] bytes,
                                         BitmapFactory.Options opts) {
        if (bytes != null) {
            if (opts != null) {
                return BitmapFactory.decodeByteArray(bytes, 0, bytes.length,
                        opts);
            }
            else {
                return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            }
        }
        return null;
    }

    /**
     * zoom the bitmap to specified size
     *
     * @param bitmap origin bitmap
     * @param w the width of the target bitmap
     * @param h the height of the target bitmap
     * @return  the target bitmap
     *
     */
    public static Bitmap zoomBitmap(Bitmap bitmap, int w, int h) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        Matrix matrix = new Matrix();
        float scaleWidth = (float) w / (float) width;
        float scaleHeight = (float) h / (float) height;
        matrix.postScale(scaleWidth, scaleHeight);
        Bitmap newBmp = Bitmap.createBitmap(bitmap, 0, 0, width, height,
                matrix, true);
        return newBmp;
    }


    /**
     * get the byte array of the origin bitmap
     *
     * @param bmp origin bitmap
     * @return  byte array of the bitmap
     *
     */
    public static byte[] Bitmap2Bytes(Bitmap bmp) {
        if (bmp == null) {
            return null;
        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(CompressFormat.PNG, 100, baos);

        if (bmp != null) {
            bmp.recycle();
            bmp = null;
        }

        return baos.toByteArray();
    }

    @SuppressLint("NewApi")
    /**
     * get the base64 string of the origin bitmap
     *
     * @param bitmap origin bitmap
     * @return  base64 of the origin bitmap
     *
     */
    public static String getBitmapStrBase64(Bitmap bitmap) {

        if(bitmap==null){

            return  "iVBORw0KGgoAAAANSUhEUgAAAEsAAABLCAQAAACSR7JhAAADtUlEQVR4Ac3YA2Bj6QLH0XPT1Fzbtm29tW3btm3bfLZtv7e2ObZnms7d8Uw098tuetPzrxv8wiISrtVudrG2JXQZ4VOv+qUfmqCGGl1mqLhoA52oZlb0mrjsnhKpgeUNEs91Z0pd1kvihA3ULGVHiQO2narKSHKkEMulm9VgUyE60s1aWoMQUbpZOWE+kaqs4eLEjdIlZTcFZB0ndc1+lhB1lZrIuk5P2aib1NBpZaL+JaOGIt0ls47SKzLC7CqrlGF6RZ09HGoNy1lYl2aRSWL5GuzqWU1KafRdoRp0iOQEiDzgZPnG6DbldcomadViflnl/cL93tOoVbsOLVM2jylvdWjXolWX1hmfZbGR/wjypDjFLSZIRov09BgYmtUqPQPlQrPapecLgTIy0jMgPKtTeob2zWtrGH3xvjUkPCtNg/tm1rjwrMa+mdUkPd3hWbH0jArPGiU9ufCsNNWFZ40wpwn+62/66R2RUtoso1OB34tnLOcy7YB1fUdc9e0q3yru8PGM773vXsuZ5YIZX+5xmHwHGVvlrGPN6ZSiP1smOsMMde40wKv2VmwPPVXNut4sVpUreZiLBHi0qln/VQeI/LTMYXpsJtFiclUN+5HVZazim+Ky+7sAvxWnvjXrJFneVtLWLyPJu9K3cXLWeOlbMTlrIelbMDlrLenrjEQOtIF+fuI9xRp9ZBFp6+b6WT8RrxEpdK64BuvHgDk+vUy+b5hYk6zfyfs051gRoNO1usU12WWRWL73/MMEy9pMi9qIrR4ZpV16Rrvduxazmy1FSvuFXRkqTnE7m2kdb5U8xGjLw/spRr1uTov4uOgQE+0N/DvFrG/Jt7i/FzwxbA9kDanhf2w+t4V97G8lrT7wc08aA2QNUkuTfW/KimT01wdlfK4yEw030VfT0RtZbzjeMprNq8m8tnSTASrTLti64oBNdpmMQm0eEwvfPwRbUBywG5TzjPCsdwk3IeAXjQblLCoXnDVeoAz6SfJNk5TTzytCNZk/POtTSV40NwOFWzw86wNJRpubpXsn60NJFlHeqlYRbslqZm2jnEZ3qcSKgm0kTli3zZVS7y/iivZTweYXJ26Y+RTbV1zh3hYkgyFGSTKPfRVbRqWWVReaxYeSLarYv1Qqsmh1s95S7G+eEWK0f3jYKTbV6bOwepjfhtafsvUsqrQvrGC8YhmnO9cSCk3yuY984F1vesdHYhWJ5FvASlacshUsajFt2mUM9pqzvKGcyNJW0arTKN1GGGzQlH0tXwLDgQTurS8eIQAAAABJRU5ErkJggg==";

        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(CompressFormat.PNG, 100, baos);
        byte[] bytes = baos.toByteArray();
        String data = Base64.encodeToString(bytes, 0, bytes.length, Base64.DEFAULT);

        if (bitmap != null) {
            bitmap.recycle();
            bitmap = null;
        }

        return data;
    }


    /**
     * get the bitmap of the base64 encode bitmap
     *
     * @param base64String
     * @return  target bitmap of the base64 encode bitmap
     *
     */
    public static Bitmap getBitmapFromBase64String(String base64String) {

        byte[] data = Base64.decode(base64String, Base64.DEFAULT);

        Bitmap bmp = BitmapFactory.decodeByteArray(data, 0, data.length);

        return bmp;

    }

    /**
     * get the bitmap of the drawable
     *
     * @param drawable origin drawable
     * @return  target bitmap of the origin drawable
     *
     */
    public static Bitmap drawableToBitmap(Drawable drawable) {

        Bitmap bitmap = Bitmap
                .createBitmap(
                        drawable.getIntrinsicWidth(),
                        drawable.getIntrinsicHeight(),
                        drawable.getOpacity() != PixelFormat.OPAQUE ? Config.ARGB_8888
                                : Config.RGB_565);
        Canvas canvas = new Canvas(bitmap);

        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(),
                drawable.getIntrinsicHeight());
        drawable.draw(canvas);

        return bitmap;

    }


    /**
     * get the byte array the of specified path image
     *
     * @param maxNumOfPixels
     * @param imgPath the path of the image
     * @return byte array of the image in the path
     *
     */
    public static byte[] compressBitmap(int maxNumOfPixels, String imgPath) {
        double maxSize = 100.00;
        Bitmap bitmap = loadBitmap(maxNumOfPixels, imgPath);
        if (null != bitmap) {
            byte[] bBitmap = Bitmap2Bytes(bitmap);
            if (bitmap != null) {
                bitmap.recycle();
                bitmap = null;
            }
            double mid = bBitmap.length / 1024;
            if (mid > maxSize) {
                double i = mid / maxSize;
                bBitmap = compressBitmap((int) (maxNumOfPixels / Math.abs(i)), imgPath);
            }
            return bBitmap;
        } else {
            return null;
        }
    }

    /**
     * get the bitmap of specified path image
     *
     * @param maxNumOfPixels
     * @param imgpath the path of the image
     * @return bitmap of the image in the path
     *
     */
    public static Bitmap loadBitmap(int maxNumOfPixels, String imgpath) {
        Bitmap bitmap = null;
        try {
            FileInputStream f = new FileInputStream(new File(imgpath));


            final BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(imgpath, options);

            if (0 == maxNumOfPixels) {
                maxNumOfPixels = 1024 * 1024;
            }
            options.inSampleSize = computeSampleSize(options, -1,
                    maxNumOfPixels);

            options.inJustDecodeBounds = false;
            bitmap = BitmapFactory.decodeStream(f, null, options);

        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();

        }
        return bitmap;
    }

    /**
     * computeSampleSize
     *
     * @param  options
     * @param maxNumOfPixels
     * @param minSideLength
     * @return sampleSize
     *
     */
    public static int computeSampleSize(BitmapFactory.Options options,

                                        int minSideLength, int maxNumOfPixels) {

        int initialSize = computeInitialSampleSize(options, minSideLength,

                maxNumOfPixels);

        int roundedSize;

        if (initialSize <= 8) {

            roundedSize = 1;

            while (roundedSize < initialSize) {

                roundedSize <<= 1;

            }

        } else {

            roundedSize = (initialSize + 7) / 8 * 8;

        }

        return roundedSize;
    }

    /**
     * computeInitialSampleSize
     *
     * @param  options
     * @param minSideLength
     * @param maxNumOfPixels
     * @return initial sample size
     *
     */
    private static int computeInitialSampleSize(BitmapFactory.Options options, int minSideLength, int maxNumOfPixels) {

        double w = options.outWidth;

        double h = options.outHeight;

        int lowerBound = (maxNumOfPixels == -1) ? 1 :

                (int) Math.ceil(Math.sqrt(w * h / maxNumOfPixels));

        int upperBound = (minSideLength == -1) ? 128 :

                (int) Math.min(Math.floor(w / minSideLength),

                        Math.floor(h / minSideLength));

        if (upperBound < lowerBound) {

            // return the larger one when there is no overlapping zone.

            return lowerBound;

        }

        if ((maxNumOfPixels == -1) &&

                (minSideLength == -1)) {

            return 1;

        } else if (minSideLength == -1) {

            return lowerBound;

        } else {

            return upperBound;

        }
    }



    public static String saveFile(Bitmap bm, String fileName) {
        if (bm == null){
            return null;
        }
        String path = MessagingApi.THUMB_DIRECTORY;
        FileUtils.createFileDir(path);

        File myCaptureFile = new File(path + "thumbnail_"+ FileUtils.getFileNameNoEx(fileName)+".jpg");
        if (myCaptureFile.exists()){
            return  myCaptureFile.getPath();
        }
        try {
            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(myCaptureFile));
            bm.compress(CompressFormat.JPEG, 100, bos);
            bos.flush();
            bos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (bm != null && !bm.isRecycled()) {
            bm.recycle();
            bm = null;
        }
        return myCaptureFile.getPath();
    }
    public static String getSDPath(){

        return FileUtils.getSDPath();
    }
    public static Bitmap Bytes2Bimap(byte[] b) {
        if (b.length != 0) {
            return BitmapFactory.decodeByteArray(b, 0, b.length);
        } else {
            return null;
        }
    }
    public static Bitmap getResizedBitmap(Context context, Uri uri, int widthLimit, int heightLimit) throws IOException {
        String path = null;
        Bitmap result = null;
        if(uri.getScheme().equals("file")) {
            path = uri.toString().substring(5);
        } else {
            if(!uri.getScheme().equals("content")) {
                return null;
            }

            Cursor exifInterface = context.getContentResolver().query(uri, new String[]{"_data"}, (String)null, (String[])null, (String)null);
            exifInterface.moveToFirst();
            path = exifInterface.getString(0);
            exifInterface.close();
        }

        ExifInterface exifInterface1 = new ExifInterface(path);
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);
        int orientation = exifInterface1.getAttributeInt("Orientation", 0);
        int width;
        if(orientation == 6 || orientation == 8 || orientation == 5 || orientation == 7) {
            width = widthLimit;
            widthLimit = heightLimit;
            heightLimit = width;
        }

        width = options.outWidth;
        int height = options.outHeight;
        int sampleW = 1;

        int sampleH;
        for(sampleH = 1; width / 2 > widthLimit; sampleW <<= 1) {
            width /= 2;
        }

        while(height / 2 > heightLimit) {
            height /= 2;
            sampleH <<= 1;
        }

        boolean sampleSize = true;
        options = new BitmapFactory.Options();
        int sampleSize1;
        if(widthLimit != 2147483647 && heightLimit != 2147483647) {
            sampleSize1 = Math.max(sampleW, sampleH);
        } else {
            sampleSize1 = Math.max(sampleW, sampleH);
        }

        options.inSampleSize = sampleSize1;

        Bitmap bitmap;
        try {
            bitmap = BitmapFactory.decodeFile(path, options);
        } catch (OutOfMemoryError var22) {
            var22.printStackTrace();
            options.inSampleSize <<= 1;
            bitmap = BitmapFactory.decodeFile(path, options);
        }

        Matrix matrix = new Matrix();
        if(bitmap == null) {
            return bitmap;
        } else {
            int w = bitmap.getWidth();
            int h = bitmap.getHeight();
            if(orientation == 6 || orientation == 8 || orientation == 5 || orientation == 7) {
                int xS = w;
                w = h;
                h = xS;
            }

            switch(orientation) {
                case 2:
                    matrix.preScale(-1.0F, 1.0F);
                    break;
                case 3:
                    matrix.setRotate(180.0F, (float)w / 2.0F, (float)h / 2.0F);
                    break;
                case 4:
                    matrix.preScale(1.0F, -1.0F);
                    break;
                case 5:
                    matrix.setRotate(90.0F, (float)w / 2.0F, (float)h / 2.0F);
                    matrix.preScale(1.0F, -1.0F);
                    break;
                case 6:
                    matrix.setRotate(90.0F, (float)w / 2.0F, (float)h / 2.0F);
                    break;
                case 7:
                    matrix.setRotate(270.0F, (float)w / 2.0F, (float)h / 2.0F);
                    matrix.preScale(1.0F, -1.0F);
                    break;
                case 8:
                    matrix.setRotate(270.0F, (float)w / 2.0F, (float)h / 2.0F);
            }

            float xS1 = (float)widthLimit / (float)bitmap.getWidth();
            float yS = (float)heightLimit / (float)bitmap.getHeight();
            matrix.postScale(Math.min(xS1, yS), Math.min(xS1, yS));

            try {
                result = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
                return result;
            } catch (OutOfMemoryError var21) {
                var21.printStackTrace();
                android.util.Log.d("ResourceCompressHandler", "OOMHeight:" + bitmap.getHeight() + "Width:" + bitmap.getHeight() + "matrix:" + xS1 + " " + yS);
                return null;
            }
        }
    }
    /**
     * @param sourceBitmap
     * @param n
     *            compress multiple
     * @return
     */
    private static Bitmap compressImageWithThumbnailUtils(Bitmap sourceBitmap, int n) {
        Bitmap targetBitmap = null;
        int w, h;
        float ratio = 1.0f;
        if (n < 10) {
            ratio = (1 - ((float) n / 10));
            w = (int) (sourceBitmap.getWidth() * ratio);
            h = (int) (sourceBitmap.getHeight() * ratio);
        } else {
            return null;
        }

        targetBitmap = ThumbnailUtils.extractThumbnail(sourceBitmap, w, h);

        return targetBitmap;

    }

    /**
     * @param sourceBitmap
     * @param maxKbSize
     *            max size
     * @param base
     *            compress multiple
     * @return
     */
    public static Bitmap compressImage(Bitmap sourceBitmap, int maxKbSize, int base) {

        Bitmap targetBitmap = compressImageWithThumbnailUtils(sourceBitmap, base);
        if (targetBitmap == null) {
            Log.i("compressedImage","compressImage failed return null");
            return null;
        }
        if (BitmapHelp.kBSizeOf(targetBitmap) <= maxKbSize) {
            Log.i("compressedImage","compressImage succ current base:"+base);
            return targetBitmap;
        } else {
            return compressImage(sourceBitmap, maxKbSize, base + 1);
        }
    }

    /**
     * Rotaing picture to normal degree
     * @param angle
     * @param filePath
     * @return the rotaed picture
     */
    public static Bitmap rotaingImageFile(int angle , String filePath) {

        BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();

        bitmapOptions.inSampleSize = 8;

        Bitmap cameraBitmap = BitmapFactory.decodeFile(filePath, bitmapOptions);

        Matrix matrix = new Matrix();

        matrix.postRotate(angle);

        Bitmap resizedBitmap = Bitmap.createBitmap(cameraBitmap, 0, 0, cameraBitmap.getWidth(), cameraBitmap.getHeight(), matrix, true);

        saveBitmap(resizedBitmap,filePath);

        return resizedBitmap;
    }

    /**
     * Get the picture degress
     * @param path
     * @return picture degress
     */
    public static int readPictureDegree(String path) {
        int degree  = 0;
        try {
            ExifInterface exifInterface = new ExifInterface(path);
            int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return degree;
    }


   /** @param imagePath 图像的路径
    * @return 生成的缩略图*/
    public static Bitmap loadImagePrew(String imagePath) {
//            Bitmap bitmap = null;
//            BitmapFactory.Options options = new BitmapFactory.Options();
//            options.inJustDecodeBounds = true;
//            // 获取这个图片的宽和高，注意此处的bitmap为null
//            bitmap = BitmapFactory.decodeFile(imagePath, options);
//            // 计算缩放比
//            options.inSampleSize = computeSampleSize(options, -1,
//                    1024 * 5);//5KB
//
//             options.inJustDecodeBounds = false; // 设为 false
//            // 重新读入图片，读取缩放后的bitmap，注意这次要把options.inJustDecodeBounds 设为 false
//            bitmap = BitmapFactory.decodeFile(imagePath, options);
//        if (bitmap==null){
//            Log.i("getThumbnail","loadImagePrew is null");
//        }

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        // 获取这个图片的宽和高，注意此处的bitmap为null
        Bitmap bitmap  = BitmapFactory.decodeFile(imagePath, options);
        options.inJustDecodeBounds = false; // 设为 false
        // 计算缩放比
        int h = options.outHeight;
        int w = options.outWidth;
        int width = 96;
        int height = 96;//default
        int beWidth = w / width;
        int beHeight = h / height;
        int be = 1;
        if (beWidth < beHeight) {
            be = beWidth;
        } else {
            be = beHeight;
        }
        if (be <= 0) {
            be = 1;
        }
        options.inSampleSize = be;
        // 重新读入图片，读取缩放后的bitmap，注意这次要把options.inJustDecodeBounds 设为 false
        bitmap = BitmapFactory.decodeFile(imagePath, options);
        if (bitmap!=null)
        {
            Log.i("imageThumb", "bitmap w==`" + bitmap.getWidth() +";bitmap height:"+bitmap.getHeight());
        }else{
            Log.i("imageThumb", "bitmap is null");
        }
        int degree = readPictureDegree(imagePath);
        if (degree != 0) {// need roration
            try{
                Log.i("imageThumb", "compress degree==`" + degree);

                Bitmap thumbImage = BitmapFactory.decodeFile(imagePath);
                // 旋转图片 动作
                Matrix matrix = new Matrix();
                ;
                matrix.postRotate(degree);
                // 创建新的图片
                Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0,
                        bitmap.getWidth()-1, bitmap.getHeight()-1, matrix, true);
                Log.i("imageThumb", "resize image w ==`" + resizedBitmap.getWidth()+"; h="+resizedBitmap.getHeight());
                return  resizedBitmap;
            }catch (Exception e){
                Log.i("imageThumb", "compress failed==`" + e.getLocalizedMessage());
            }

            }


        return bitmap;
    }


}
