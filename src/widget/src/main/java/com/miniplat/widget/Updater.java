package com.miniplat.widget;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.FileProvider;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class Updater implements View.OnClickListener {
    private ProgressBar mProgress;
    private TextView mPercentView, mCancleView;

    private Context mContext;
    private String apkUrl = "";
    private Dialog downloadDialog;
    private static final String saveFileName = "/update.apk";

    private static final int DOWN_UPDATE = 1;
    private static final int DOWN_OVER = 2;
    private int progress;

    private String fileRootPath_;
    private Thread downLoadThread;
    private boolean interceptFlag = false;
    private String apkFile;
    private boolean mIsFocure;

    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case DOWN_UPDATE:
                    mProgress.setProgress(progress);
                    mPercentView.setText(progress + "%");
                    break;
                case DOWN_OVER:
                    downloadDialog.dismiss();
                    installApk();
                    break;
                default:
                    break;
            }
        }

        ;
    };

    public Updater(Context context) {
        this.mContext = context;
    }

    public void DownloadFile(String Url, boolean isFocure) {
        mIsFocure = isFocure;
        apkUrl = Url.startsWith(",") ? Url.substring(1) : Url;
        showDownloadDialog();
    }

    private void showDownloadDialog() {
        LayoutInflater inflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.layout_updator_dialog, null);

        LinearLayout layout = (LinearLayout) view.findViewById(R.id.dialog_layout);
        mCancleView = (TextView) view.findViewById(R.id.down_service_cancle);
        mPercentView = (TextView) view.findViewById(R.id.down_service_percent);
        mProgress = (ProgressBar) view.findViewById(R.id.down_service_bar);
        LinearLayout mCancleLayout = (LinearLayout) view.findViewById(R.id.down_cancle_dialog);
        if (mIsFocure) {
            mCancleLayout.setVisibility(View.GONE);
        } else {
            mCancleLayout.setVisibility(View.VISIBLE);
        }
        mCancleView.setOnClickListener(this);

        downloadDialog = new Dialog(mContext, R.style.finger_dialog);
        downloadDialog.setCancelable(false);
        downloadDialog.setContentView(layout, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT));
        downloadDialog.show();

        downloadApk();
    }

    private Runnable mdownApkRunnable = new Runnable() {
        public void run() {
            try {
                URL url = new URL(apkUrl);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.connect();
                int length = conn.getContentLength();
                InputStream is = conn.getInputStream();

                File file = mContext.getExternalFilesDir("apk");
                //File file = Environment.getExternalStorageDirectory();
                if (!file.exists()) {
                    file.mkdir();
                }
                fileRootPath_ = file.getAbsolutePath();
                apkFile = fileRootPath_ + saveFileName;

                File ApkFile = createFile(apkFile);
                FileOutputStream fos = new FileOutputStream(ApkFile);
                int count = 0;
                byte buf[] = new byte[1024];

                do {
                    int numread = is.read(buf);
                    count += numread;
                    progress = (int) (((float) count / length) * 100);
                    mHandler.sendEmptyMessage(DOWN_UPDATE);
                    if (numread <= 0) {
                        mHandler.sendEmptyMessage(DOWN_OVER);
                        break;
                    }
                    fos.write(buf, 0, numread);
                } while (!interceptFlag);// 点击取消就停止下载.

                fos.close();
                is.close();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    };

    private File createFile(String path) {
        String rootPath = "data/data/com.miniplat.risk/files";
        if (path.startsWith("./")) {
            path = path.substring(2);
        }

        File file = null;
        if (path.startsWith("/")) {
            // 是一个绝对路径文件
            file = new File(path);
        } else
            file = new File(rootPath + path);

        if (file.exists()) {
            // 文件存在删掉存在文件
            file.delete();
        }

        try {
            String parent = file.getParent();
            File pfile = new File(parent);
            pfile.mkdirs(); // 创建目录
            file.createNewFile();
        } catch (Exception e) {
            // 目录不存在或其他错误
            try {
                String parent = file.getParent();
                File pfile = new File(parent);
                pfile.mkdirs(); // 创建目录
                file.createNewFile();
                return file;
            } catch (Exception x) {
                String parent = rootPath + path;
                parent = parent.replaceAll("\\\\", "/");
                parent = parent.substring(0, parent.lastIndexOf("/"));
                File pfile = new File(parent);
                pfile.mkdirs(); // 创建目录
                try {
                    file.createNewFile();
                    return file;
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                return null;
            }
        }
        return file;
    }

    private void downloadApk() {
        downLoadThread = new Thread(mdownApkRunnable);
        downLoadThread.start();
    }

    private void installApk() {
        File apkfile = new File(apkFile);
        if (!apkfile.exists()) {
            return;
        }

        // 开始安装
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Uri apkUri = FileProvider.getUriForFile(mContext.getApplicationContext(),
                    mContext.getPackageName() + ".fileprovider", apkfile);
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_ACTIVITY_NEW_TASK);
            i.setDataAndType(apkUri, "application/vnd.android.package-archive");
            mContext.startActivity(i);
        }
        else {
            // 设置权限
            String[] command = {"chmod", "777", apkfile.getPath() };
            ProcessBuilder builder = new ProcessBuilder(command);
            try {
                builder.start();
            } catch (IOException e) {
                e.printStackTrace();
            }

            Intent i = new Intent(Intent.ACTION_VIEW);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            i.setAction(Intent.ACTION_VIEW);
            i.setDataAndType(Uri.parse("file://" + apkfile.toString()),
                    "application/vnd.android.package-archive");
            mContext.startActivity(i);
        }
    }

    @Override
    public void onClick(View v) {
        interceptFlag = true;
        downloadDialog.cancel();
    }
}