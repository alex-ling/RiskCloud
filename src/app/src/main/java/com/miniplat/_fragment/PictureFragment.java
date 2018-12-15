package com.miniplat._fragment;

import android.app.Dialog;
import android.content.Context;
import android.net.Uri;
import android.support.v4.app.DialogFragment;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.view.SimpleDraweeView;
import com.miniplat._activity.R;
import java.io.File;

public class PictureFragment extends DialogFragment {
    private static final String ARG_FILE_PATH = "file_path";
    private SimpleDraweeView mImageView;

    public static PictureFragment newInstance(String filePath) {
        PictureFragment fragment = new PictureFragment();
        Bundle args = new Bundle();
        args.putString(ARG_FILE_PATH, filePath);
        fragment.setArguments(args);
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Bundle args = getArguments();
        String filePath = args.getString(ARG_FILE_PATH);
        Uri uri = Uri.fromFile(new File(filePath));

        Context ctx = getContext();
        Fresco.initialize(ctx);

        View contentView = LayoutInflater.from(ctx).inflate(R.layout.dialog_picture, null);
        mImageView = (SimpleDraweeView) contentView.findViewById(R.id.image);
        mImageView.setImageURI(uri);

        Dialog dialog = new Dialog(getContext());
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);
        dialog.setContentView(contentView);

        Window window = dialog.getWindow();
        //消除边距
        window.getDecorView().setPadding(0, 0, 0, 0);
        WindowManager.LayoutParams layoutParams = window.getAttributes();
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        layoutParams.height = WindowManager.LayoutParams.MATCH_PARENT;
        window.setAttributes(layoutParams);

        return dialog;
    }
}