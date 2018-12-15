package com.miniplat.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.miniplat.app.Container;

public class BaseActivity extends AppCompatActivity {
    // layoutContainer;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("BaseActivity", getClass().getSimpleName());

        // 添加到容器
        Container.addActivity(this);
    }

    @Override
    public void setContentView(int layoutResID) {
        setContentView(getLayoutInflater().inflate(layoutResID, null));
    }

    @Override
    public void setContentView(View view) {
        setContentView(view, new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        ));
    }

    @Override
    public void setContentView(View view, ViewGroup.LayoutParams params) {
        /*layoutContainer = (FrameLayout)getLayoutInflater().inflate(
                R.layout.activity_base, null);
        Drawable back = view.getBackground();
        if (back != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                // > SDK 16
                findViewById(R.id.layout_container).setBackground(back);
                view.setBackground(null);
            }
            else {
                findViewById(R.id.layout_container).setBackgroundDrawable(back);
                view.setBackgroundDrawable(null);
            }
        }

        FrameLayout.LayoutParams p = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT);
        layoutContainer.addView(view, p);
        super.setContentView(layoutContainer, params);*/
        super.setContentView(view, params);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // 从容器移除
        Container.removeActivity(this);
    }
}
