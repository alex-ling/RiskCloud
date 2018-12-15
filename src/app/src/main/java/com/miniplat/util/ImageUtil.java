package com.miniplat.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ImageView;

import com.facebook.common.file.FileUtils;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;

import com.miniplat.app.AppCtx;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.UUID;

import id.zelory.compressor.Compressor;

public class ImageUtil {
    private static final DisplayImageOptions opts = new DisplayImageOptions.Builder()
        .cacheInMemory(true)
        .cacheOnDisk(true)
        .considerExifParams(true)
        .bitmapConfig(Bitmap.Config.RGB_565)
        .displayer(new SimpleBitmapDisplayer())
        .imageScaleType(ImageScaleType.EXACTLY_STRETCHED)
        .build();

    public static String loadValid(ImageView view) {
        String type = UUID.randomUUID().toString();
        String url = AppCtx.URL + "api/draw/getvalidimage?type="+ type;
        ImageLoader.getInstance().displayImage(url, view, opts);
        return type;
    }

    public static void loadUrl(ImageView view, String url) {
        ImageLoader.getInstance().displayImage(url, view, opts);
    }

    public static File compressImage(String oldPath, String folder) {
        //Bitmap bitmap = decodeImage(oldPath);
        //File image = saveImage(rotateImage(oldPath, bitmap), oldPath);
        File image = new File(oldPath);
        try {
            return new Compressor(AppCtx.getContext())
                .setMaxWidth(800)
                .setMaxHeight(600)
                .setQuality(80)
                .setCompressFormat(Bitmap.CompressFormat.JPEG)
                .setDestinationDirectoryPath(folder)
                .compressToFile(image);
        }
        catch (IOException e) {
            e.printStackTrace();
            return image;
        }
    }

    public static File compressImage2(String oldPath, String newPath) {
        //Bitmap oldBitmap = resizeImage(oldPath);
        Bitmap oldBitmap = decodeImage(oldPath);
        Bitmap newBitmap = rotateImage(oldPath, oldBitmap);
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        newBitmap.compress(Bitmap.CompressFormat.JPEG, 100, os);

        byte[] bytes = os.toByteArray();
        File file = null ;
        try {
            file = FileUtil.writeFile(bytes, newPath);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        finally{
            if (newBitmap != null ){
                if (!newBitmap.isRecycled()) {
                    newBitmap.recycle();
                }
                newBitmap = null;
            }
            if (oldBitmap != null ){
                if (!oldBitmap.isRecycled()) {
                    oldBitmap.recycle();
                }
                oldBitmap  = null;
            }
        }
        return file;
    }

    public static Bitmap resizeImage(String filePath) {
        int IMAGE_MAX_SIZE = 600;
        Bitmap b = null;
        File f = new File(filePath);
        if (f == null) {
            return null;
        }

        BitmapFactory.Options o = new BitmapFactory.Options();
        o.inJustDecodeBounds = true;
        try {
            FileInputStream fis = new FileInputStream(f);
            BitmapFactory.decodeStream(fis, null, o);
            fis.close();

            int scale = 1;
            if (o.outHeight > IMAGE_MAX_SIZE || o.outWidth > IMAGE_MAX_SIZE) {
                scale = (int)Math.pow(2, (int)Math.round(Math.log(IMAGE_MAX_SIZE /
                    (double)Math.max(o.outHeight, o.outWidth)) / Math.log(0.5)));
            }

            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;
            fis = new FileInputStream(f);
            b = BitmapFactory.decodeStream(fis, null, o2);
            fis.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return b;
    }

    private static Bitmap decodeImage(String filePath){
        return BitmapFactory.decodeFile(filePath);
    }

    private static File saveImage(Bitmap bitmap, String filePath) {
        File file = new File(filePath);
        try {
            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
            bos.flush();
            bos.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return file;
    }

    private static Bitmap rotateImage(String filePath, Bitmap bitmap){
        int degree = readImageDegree(filePath);
        return rotateImageView(degree, bitmap);
    }

    public static Bitmap rotateImageView(int angle , Bitmap bitmap) {
        Matrix matrix = new Matrix();;
        matrix.postRotate(angle);
        return Bitmap.createBitmap(bitmap, 0, 0,
            bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }

    public static int readImageDegree(String path) {
        int degree  = 0;
        try {
            ExifInterface ei = new ExifInterface(path);
            int o = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL);
            switch (o) {
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
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return degree;
    }
}
