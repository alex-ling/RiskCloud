package com.miniplat.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import static android.util.TypedValue.COMPLEX_UNIT_PX;

public class TitleBar extends RelativeLayout {
    private OnClickListener leftListener, rightListener;
    private Button leftButton, rightButton;
    private TextView titleTextView;

    public void setLeftOnClick(OnClickListener listener) {
        this.leftListener = listener;
    }

    public void setRightOnClick(OnClickListener listener) {
        this.rightListener = listener;
    }

    public void setLeftVisibility(boolean flag){
        if (flag) {
            leftButton.setVisibility(View.VISIBLE);
        }
        else {
            leftButton.setVisibility(View.GONE);
        }
    }

    public void setRightVisibility(boolean flag){
        if (flag) {
            rightButton.setVisibility(View.VISIBLE);
        }
        else {
            rightButton.setVisibility(View.GONE);
        }
    }

    public void setTitle(String title) {
        titleTextView.setText(title);
    }

    public TitleBar(final Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.layout_title_bar, this);
        leftButton = (Button) findViewById(R.id.leftButton);
        rightButton = (Button) findViewById(R.id.rightButton);
        titleTextView = (TextView) findViewById(R.id.titleText);
        leftButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (leftListener != null) {
                    leftListener.onClick(v);
                }
            }
        });
        rightButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (rightListener != null) {
                    rightListener.onClick(v);
                }
            }
        });

        TypedArray typeArray = context.obtainStyledAttributes(attrs, R.styleable.TitleBar);
        int leftBtnBackground = typeArray.getResourceId(R.styleable.TitleBar_leftBackground, 0);
        int rightBtnBackground = typeArray.getResourceId(R.styleable.TitleBar_rightBackground, 0);
        String titleText = typeArray.getString(R.styleable.TitleBar_titleText);
        int titleTextSize = typeArray.getDimensionPixelOffset(R.styleable.TitleBar_titleTextSize, 0);
        int titleTextColor = typeArray.getColor(R.styleable.TitleBar_titleTextColor, 0x0093fe);

        typeArray.recycle();
        leftButton.setBackgroundResource(leftBtnBackground);
        rightButton.setBackgroundResource(rightBtnBackground);
        titleTextView.setText(titleText);
        titleTextView.setTextSize(COMPLEX_UNIT_PX, titleTextSize);
        titleTextView.setTextColor(titleTextColor);
    }
}