package com.miniplat.widget;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.AttributeSet;
import android.util.Log;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

public class ProWebView extends WebView {
    private ProgressBar progressBar;
    private OnChromeListener listener;

    public interface OnChromeListener {
        public void receivedTitle(String title);
        public void startCapture(ValueCallback<Uri> callback);
        public void startCaptures(ValueCallback<Uri[]> callback);
    }
    public void setOnChromeListener(OnChromeListener listener) {
        this.listener = listener;
    }

    public ProWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
        progressBar = new ProgressBar(context, null,
                android.R.attr.progressBarStyleHorizontal);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, 6);
        progressBar.setLayoutParams(layoutParams);

        Drawable d = context.getResources().getDrawable(R.drawable.web_view);
        progressBar.setProgressDrawable(d);
        addView(progressBar);
        setWebChromeClient(new WebChromeClient());
    }

    public class WebChromeClient extends android.webkit.WebChromeClient {
        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            if (newProgress == 100) {
                progressBar.setVisibility(GONE);
            } else {
                if (progressBar.getVisibility() == GONE)
                    progressBar.setVisibility(VISIBLE);
                progressBar.setProgress(newProgress);
            }
            super.onProgressChanged(view, newProgress);
        }
        @Override
        public void onReceivedTitle(WebView view, String title) {
            super.onReceivedTitle(view, title);
            if (listener != null) {
                listener.receivedTitle(title);
            }
        }
        public void openFileChooser(ValueCallback<Uri> uploadMsg) {
            if (listener != null) {
                listener.startCapture(uploadMsg);
            }
        }
        public void openFileChooser(ValueCallback<Uri> uploadMsg,
            String acceptType) {
            if (listener != null) {
                listener.startCapture(uploadMsg);
            }
        }
        public void openFileChooser(ValueCallback<Uri> uploadMsg,
            String acceptType, String capture) {
            if (listener != null) {
                listener.startCapture(uploadMsg);
            }
        }
        @Override
        public boolean onShowFileChooser(WebView webView,
            ValueCallback<Uri[]> filePathCallback, FileChooserParams fileChooserParams) {
            if (listener != null) {
                listener.startCaptures(filePathCallback);
            }
            return true;
        }
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        LayoutParams lp = (LayoutParams) progressBar.getLayoutParams();
        lp.x = l;
        lp.y = t;
        progressBar.setLayoutParams(lp);
        super.onScrollChanged(l, t, oldl, oldt);
    }
}
