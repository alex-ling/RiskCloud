package com.miniplat.widget;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class SheetDialog {
    private Context context;
    private Dialog dialog;
    private TextView txt_title;
    private TextView txt_cancel;
    private LinearLayout content;
    private ScrollView scroll;
    private boolean showTitle = false;
    private List<SheetItem> items;
    private Display display;
    private OnCancelListener cancel;

    public interface OnCancelListener {
        void onCancel();
    }

    public SheetDialog setCancelListener(OnCancelListener listener) {
        cancel = listener;
        return this;
    }

    public SheetDialog(Context context) {
        this.context = context;
        WindowManager windowManager = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        display = windowManager.getDefaultDisplay();
    }

    public SheetDialog builder() {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.layout_sheet_dialog, null);
        view.setMinimumWidth(display.getWidth());

        scroll = (ScrollView) view.findViewById(R.id.scroll);
        content = (LinearLayout) view.findViewById(R.id.content);
        txt_title = (TextView) view.findViewById(R.id.txt_title);
        txt_cancel = (TextView) view.findViewById(R.id.txt_cancel);
        txt_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cancel != null) {
                    cancel.onCancel();
                }
                dialog.dismiss();
            }
        });

        dialog = new Dialog(context, R.style.ActionSheetDialogStyle);
        dialog.setContentView(view);
        Window dialogWindow = dialog.getWindow();
        dialogWindow.setGravity(Gravity.LEFT | Gravity.BOTTOM);
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.x = 0;
        lp.y = 0;
        dialogWindow.setAttributes(lp);

        return this;
    }

    public SheetDialog setTitle(String title) {
        showTitle = true;
        txt_title.setVisibility(View.VISIBLE);
        txt_title.setText(title);
        return this;
    }

    public SheetDialog setCancelable(boolean cancel) {
        dialog.setCancelable(cancel);
        return this;
    }

    public SheetDialog setCanceledOnTouchOutside(boolean cancel) {
        dialog.setCanceledOnTouchOutside(cancel);
        return this;
    }

    public SheetDialog addItem(String strItem, OnClickListener listener) {
        if (items == null) items = new ArrayList<SheetItem>();
        items.add(new SheetItem(strItem, listener));
        return this;
    }

    private void setSheetItems() {
        if (items == null || items.size() <= 0) {
            return;
        }

        int size = items.size();
        if (size >= 5) {
            LinearLayout.LayoutParams params =
                    (LinearLayout.LayoutParams)scroll.getLayoutParams();
            params.height = display.getHeight() / 2;
            scroll.setLayoutParams(params);
        }

        for (int i = 1; i <= size; i++) {
            final int index = i;
            SheetItem sheetItem = items.get(i - 1);
            String strItem = sheetItem.name;
            final OnClickListener listener = sheetItem.listener;

            TextView textView = new TextView(context);
            textView.setText(strItem);
            textView.setTextSize(18);
            textView.setGravity(Gravity.CENTER);

            int resId = 0;
            if (size == 1) {
                resId = showTitle ?
                        R.drawable.sheet_dialog_bottom
                        : R.drawable.sheet_dialog_top;
            }
            else {
                if (showTitle) {
                    resId = i >= 1 && i < size ?
                            R.drawable.sheet_dialog_middle
                            : R.drawable.sheet_dialog_bottom;
                }
                else {
                    if (i == 1) {
                        resId = R.drawable.sheet_dialog_top;
                    }
                    else if (i < size) {
                        resId = R.drawable.sheet_dialog_middle;
                    }
                    else {
                        resId = R.drawable.sheet_dialog_bottom;
                    }
                }
            }
            textView.setBackgroundResource(resId);
            textView.setTextColor(content.getResources().getColor(R.color.clr_sheet_text));
            float scale = context.getResources().getDisplayMetrics().density;
            int height = (int)(50 * scale + 0.5f); //(int)(45 * scale + 0.5f);
            textView.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, height));
            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        listener.onClick(index);
                    }
                    dialog.dismiss();
                }
            });
            content.addView(textView);
        }
    }

    public void show() {
        setSheetItems();
        dialog.show();
    }

    public interface OnClickListener {
        void onClick(int which);
    }

    public class SheetItem {
        String name;
        OnClickListener listener;
        public SheetItem(String name, OnClickListener listener) {
            this.name = name;
            this.listener = listener;
        }
    }
}
