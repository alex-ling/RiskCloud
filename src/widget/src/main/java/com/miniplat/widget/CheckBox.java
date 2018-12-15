package com.miniplat.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import static android.util.TypedValue.COMPLEX_UNIT_PX;

public class CheckBox extends LinearLayout {
    private OnClickListener listener;
    private ImageView imageView;
    private TextView textView;
    private int uncheckedResId, checkedResId;
    private boolean checked = false;

    public boolean getChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
        onCheck();
    }

    public void setTextOnClick(OnClickListener listener) {
        this.listener = listener;
    }

    public CheckBox(final Context context, AttributeSet attrs) {
        super(context, attrs);

        LayoutInflater.from(context).inflate(R.layout.layout_check_box, this);
        imageView = (ImageView) findViewById(R.id.img_check);
        textView = (TextView) findViewById(R.id.lbl_check);

        imageView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                checked = !checked;
                onCheck();
            }
        });
        textView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onClick(v);
                }
                else {
                    checked = !checked;
                    onCheck();
                }
            }
        });

        TypedArray typeArray = context.obtainStyledAttributes(attrs, R.styleable.CheckBox);
        int checkWidth = typeArray.getLayoutDimension(R.styleable.CheckBox_checkWidth, 0);
        int checkHeight = typeArray.getLayoutDimension(R.styleable.CheckBox_checkHeight, 0);
        String text = typeArray.getString(R.styleable.CheckBox_text);
        int textSize = typeArray.getDimensionPixelOffset(R.styleable.CheckBox_textSize, 0);
        int textColor = typeArray.getColor(R.styleable.CheckBox_textColor, 0xffffff);

        checked = typeArray.getBoolean(R.styleable.CheckBox_checked, false);
        checkedResId = typeArray.getResourceId(R.styleable.CheckBox_checkImage, 0);
        uncheckedResId = typeArray.getResourceId(R.styleable.CheckBox_uncheckImage, 0);

        typeArray.recycle();
        LayoutParams params = new LayoutParams(checkWidth, checkHeight);
        params.gravity = Gravity.CENTER_VERTICAL;
        imageView.setLayoutParams(params);
        textView.setText(text);
        textView.setTextSize(COMPLEX_UNIT_PX, textSize);
        textView.setTextColor(textColor);
        onCheck();
    }

    private void onCheck() {
        if (checked) {
            imageView.setBackgroundResource(checkedResId);
        }
        else {
            imageView.setBackgroundResource(uncheckedResId);
        }
    }
}
