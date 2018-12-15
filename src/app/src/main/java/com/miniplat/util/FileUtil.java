package com.miniplat.util;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;

import com.miniplat.app.AppCtx;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class FileUtil {
    public static String getExternalPublicPictureFolder() {
        return Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                .getAbsolutePath();
    }

    public static String getExternalPictureFolder() {
        return AppCtx.getContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
                .getAbsolutePath();
    }

    public static String getExternalFolder(String path) {
        return AppCtx.getContext().getExternalFilesDir(path).getAbsolutePath();
    }

    public static void makeFolder(String path)
    {
        File file = new File(path);
        if(!file.exists())
        {
            file.mkdirs();
        }
    }

    public static String getFolder(String path)
    {
        int index = path.lastIndexOf("/");
        if (index != -1)
        {
            return path.substring(index + 1, path.length());
        }
        else {
            return null;
        }
    }

    public static Uri getUriFromPath(String path) {
        File picPath = new File(path);
        Uri uri = null;
        if (picPath.exists()) {
            uri = Uri.fromFile(picPath);
        }
        return uri;
    }

    public static String getPathFromUri(Uri uri) {
        if (null == uri) {
            return null;
        }
        final String scheme = uri.getScheme();
        String data = null;
        if (scheme == null) {
            data = uri.getPath();
        } else if (ContentResolver.SCHEME_FILE.equals(scheme)) {
            data = uri.getPath();
        } else if (ContentResolver.SCHEME_CONTENT.equals(scheme)) {
            Cursor cursor = AppCtx.getContext().getContentResolver().query(uri,
                    new String[] { MediaStore.Images.ImageColumns.DATA }, null, null, null);
            if (null != cursor) {
                if (cursor.moveToFirst()) {
                    int index = cursor.getColumnIndexOrThrow(MediaStore.Images.ImageColumns.DATA);
                    if (index > -1) {
                        data = cursor.getString(index);
                    }
                }
                cursor.close();
            }
        }
        return data;
    }

    public static File writeFile(byte[] bytes, String outputFile) {
        File ret = null;
        BufferedOutputStream stream = null;
        try {
            ret = new File(outputFile);
            FileOutputStream fs = new FileOutputStream(ret);
            stream = new BufferedOutputStream(fs);
            stream.write(bytes);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            if (stream != null) {
                try {
                    stream.close();
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return ret;
    }

    public static void deleteFile(String path) {
        if (!TextUtils.isEmpty(path)) {
            File file = new File(path);
            if (file.exists()) {
                file.delete();
            }
        }
    }
}
