package com.miniplat._fragment;

import android.annotation.TargetApi;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.HttpAuthHandler;
import android.webkit.ValueCallback;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.donkingliang.imageselector.ImageSelectorActivity;
import com.donkingliang.imageselector.utils.ImageSelector;
import com.donkingliang.imageselector.utils.ImageSelectorUtils;
import com.miniplat._activity.AddActivity;
import com.miniplat._activity.BuildConfig;
import com.miniplat._activity.CaptureActivity;
import com.miniplat._activity.LoginActivity;
import com.miniplat._activity.MainTabActivity;
import com.miniplat._activity.R;
import com.miniplat.app.AppCtx;
import com.miniplat.app.Container;
import com.miniplat.auth.Membership;
import com.miniplat.ui.BaseFragment;
import com.miniplat.util.AnimUtil;
import com.miniplat.util.FileUtil;
import com.miniplat.util.ImageUtil;
import com.miniplat.util.ToastUtil;
import com.miniplat.widget.SheetDialog;
import com.miniplat.widget.ProWebView;
import com.miniplat.widget.TitleBar;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static android.app.Activity.RESULT_OK;
import static android.view.View.LAYER_TYPE_SOFTWARE;
import static android.view.View.SCROLLBARS_INSIDE_OVERLAY;

public class WebFragment extends BaseFragment {
    private TitleBar titleBar;
    private View layoutView;
    private ProWebView webView;
    private boolean loading = true;
    private boolean showBack = true;
    private boolean showLocal = false;
    private boolean isTop = false;
    private boolean isLocation = true;
    private String title, url;
    private ValueCallback<Uri> callback;
    private ValueCallback<Uri[]> callbacks;
    private double lat, lng;
    private Uri currentUri;

    public static WebFragment newInstance(String title, String url) {
        WebFragment fragment = new WebFragment();
        fragment.setUrl(title, url);
        return fragment;
    }

    public WebFragment() {
        this.title = "发现风险";
        this.url = "webapp/risk/add";
    }

    public void setUrl(String title, String url) {
        this.title = title;
        this.url = url;
        this.isLocation = false;
        this.showBack = false;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater i, ViewGroup c, Bundle state) {
        if (layoutView == null) {
            layoutView = i.inflate(R.layout.fragment_webview, c, false);
            initView(layoutView, state);
        }
        else {
            if (layoutView.getParent() != null) {
                ((ViewGroup) layoutView.getParent()).removeView(layoutView);
            }
        }
        return layoutView;
    }

    private void initView(View v, Bundle savedInstanceState) {
        if (isLocation) {
            Intent i = getActivity().getIntent();
            lat = i.getDoubleExtra("lat", 0.0);
            lng = i.getDoubleExtra("lng", 0.0);
        }

        titleBar = (TitleBar) v.findViewById(R.id.title_bar);
        webView = (ProWebView) v.findViewById(R.id.web_view);

        titleBar.setTitle(title);
        if (!showBack) titleBar.setLeftVisibility(false);
        titleBar.setLeftOnClick(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goBack();
            }
        });

        webView.setWebContentsDebuggingEnabled(true);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setAppCacheEnabled(true);
        webView.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);
        webView.getSettings().setDisplayZoomControls(false);
        webView.getSettings().setDomStorageEnabled(true);
        webView.getSettings().setDefaultTextEncodingName("UTF-8");
        webView.getSettings().setAllowContentAccess(true);
        webView.getSettings().setAllowFileAccess(true);
        webView.getSettings().setAllowFileAccessFromFileURLs(false);
        webView.getSettings().setAllowUniversalAccessFromFileURLs(false);
        webView.getSettings().setUseWideViewPort(true);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setRenderPriority(WebSettings.RenderPriority.HIGH);
        webView.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);
        webView.setScrollBarStyle(SCROLLBARS_INSIDE_OVERLAY);
        webView.setLayerType(LAYER_TYPE_SOFTWARE, new Paint());

        webView.setOnChromeListener(new ProWebView.OnChromeListener() {
            @Override
            public void receivedTitle(String title) {
                onReceivedTitle(title);
            }
            @Override
            public void startCapture(ValueCallback<Uri> cb) {
                callback = cb;
                onImageCapture();
            }
            @Override
            public void startCaptures(ValueCallback<Uri[]> cb) {
                callbacks = cb;
                onImageCapture();
            }
        });
        webView.setWebViewClient(new WebViewClient() {
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                Uri uri = Uri.parse(url);
                if (uri.getScheme().equals("native")) {
                    switch (uri.getAuthority())
                    {
                        case "event":
                            String eventId = uri.getQueryParameter("id");
                            double lng = Double.valueOf(uri.getQueryParameter("lng"));
                            double lat = Double.valueOf(uri.getQueryParameter("lat"));
                            goEvent(eventId, lng, lat);
                            break;
                        case "index":
                            loadWebUrl();
                            break;
                        case "logout":
                            logout();
                            break;
                        case "goback":
                            doBack();
                            break;
                    }
                }
                else {
                    isTop = false;
                    view.loadUrl(url);
                }
                return true;
            }
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
            }
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                pageLoaded();
            }
            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                super.onReceivedError(view, errorCode, description, failingUrl);

                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                    loadLocalUrl();
                }
                //if (errorCode == ERROR_HOST_LOOKUP
                //      || errorCode == ERROR_CONNECT
                //      || errorCode == ERROR_TIMEOUT) {
                //}
            }
            @TargetApi(Build.VERSION_CODES.M)
            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                super.onReceivedError(view, request, error);

                if (request.isForMainFrame()) {
                    loadLocalUrl();
                }
                //if (NetworkUtil.isNetworkConnection()) {
                //    loadWebUrl();
                //}
            }
            @Override
            public void onReceivedHttpAuthRequest(WebView view, HttpAuthHandler handler, String host, String realm) {
                ToastUtil.warn("身份过期，请重新登录");
                LoginActivity.show();
            }
        });
        webView.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int keyCode, KeyEvent keyEvent) {
                return onKeyPress(view, keyCode, keyEvent);
            }
        });

        loadWebUrl();
    }

    public void loadWebUrl() {
        showLocal = false;
        final Map<String, String> map = new HashMap<String, String>();
        map.put("Authorization", "Bearer " + Membership.getToken().access_token);

        String fullUrl = AppCtx.URL + url + "?version=" + BuildConfig.VERSION_NAME;
        if (isLocation) {
            fullUrl += "&lat=" + lat + "&lng=" + lng;
        }
        webView.loadUrl(fullUrl, map);
        webView.clearHistory();
        isTop = true; //标记为最顶级
    }

    private void loadLocalUrl() {
        showLocal = true;
        webView.loadUrl("file:///android_asset/network.html");
        webView.clearHistory();
        isTop = true; //标记为最顶级
    }

    private void pageLoaded() {
        if (loading && webView.getVisibility() == View.GONE) {
            AnimUtil.fadeIn(webView);
            loading = false;
        }

        // 设置返回按钮
        titleBar.setLeftVisibility(showBack || webView.canGoBack());
    }

    private void onReceivedTitle(String title) {
        if (!title.equals("网页无法显示") && !showLocal) {
            titleBar.setTitle(title);
        }
    }

    private boolean onKeyPress(View view, int keyCode, KeyEvent keyEvent) {
        if (keyEvent.getAction() == KeyEvent.ACTION_DOWN) {
            if (keyCode == KeyEvent.KEYCODE_BACK ) {
                goBack();
                return true;
            }
        }
        return false;
    }

    private void goEvent(String eventId, double lng, double lat) {
        ((MainTabActivity)getActivity()).goEvent(eventId, lng, lat);
    }

    private void doBack() {
        if (webView.canGoBack() && !isTop){
            webView.goBack();
        }
        else if (showBack) {
            Container.finishWithResult(AddActivity.SUCCESS);
        }
        else {
            AppCtx.exitApp();
        }
    }

    private void goBack() {
        if (showLocal) {
            doBack();
        }
        else {
            webView.loadUrl("javascript:AX.goBack()");
        }
    }

    private void onImageCapture() {
        new SheetDialog(AppCtx.getContext())
                .builder()
                .setCancelable(true)
                .setCanceledOnTouchOutside(true)
                .addItem("拍照",
                    new SheetDialog.OnClickListener() {
                        @Override
                        public void onClick(int which) {
                            // 调用Google的CameraView库拍照的图片不能正确旋转
                            //CaptureActivity.showPhoto(WebViewFragment.this);

                            /***
                             * 需要说明一下，以下操作使用照相机拍照，拍照后的图片会存放在相册中的
                             * 这里使用的这种方式有一个好处就是获取的图片是拍照后的原图
                             * 如果不实用ContentValues存放照片路径的话，拍照后获取的图片为缩略图不清晰
                             * 此时返回的data=null，直接从指定的URI中取出原图
                             */
                            Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            ContentValues values = new ContentValues();
                            currentUri = getActivity().getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
                            i.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, currentUri);
                            startActivityForResult(i, CaptureActivity.PHOTO_REQUEST);
                        }
                    })
                .addItem("去相册选择",
                    new SheetDialog.OnClickListener() {
                        @Override
                        public void onClick(int which) {
                            Intent i = new Intent(getActivity(), ImageSelectorActivity.class);
                            i.putExtra(ImageSelector.MAX_SELECT_COUNT, 3);
                            i.putExtra(ImageSelector.IS_SINGLE, false);
                            i.putExtra(ImageSelector.USE_CAMERA, true);
                            i.putExtra(ImageSelector.IS_CONFIRM, true);
                            i.putStringArrayListExtra(ImageSelector.SELECTED, null);
                            // 在fragement中要这么调用才能收到回调，不能用Builder
                            startActivityForResult(i, CaptureActivity.ALBUM_REQUEST);
                        }
                    })
                .setCancelListener(new SheetDialog.OnCancelListener() {
                    @Override
                    public void onCancel() {
                        // 一定要设置取消返回，否则不能再次弹出
                        cancelCallback();
                    }
                })
                .show();
    }

    private void cancelCallback() {
        if (callbacks != null) {
            callbacks.onReceiveValue(null);
            callbacks = null;
        }
        else if (callback != null) {
            callback.onReceiveValue(null);
            callback = null;
        }
    }

    private Uri getCompressPhoto(String filePath) {
        String folder = FileUtil.getExternalPictureFolder();
        File photo = ImageUtil.compressImage(filePath, folder);
        return Uri.fromFile(photo);
    }

    private Uri getCompressPhoto(Uri uri) {
        String filePath = FileUtil.getPathFromUri(uri);
        String folder = FileUtil.getExternalPictureFolder();
        File photo = ImageUtil.compressImage(filePath, folder);
        return Uri.fromFile(photo);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (null == callback && null == callbacks) {
            // 不是H5 File发起不响应
            return;
        }

        if (resultCode == RESULT_OK) {
            if (requestCode == CaptureActivity.ALBUM_REQUEST) {
                if (data != null) {
                    ArrayList<String> photos = data.getStringArrayListExtra(
                            ImageSelectorUtils.SELECT_RESULT);
                    Uri[] results = new Uri[photos.size()];
                    for (int i = 0; i < photos.size(); i++) {
                        results[i] = getCompressPhoto(photos.get(i));
                    }
                    if (callbacks != null) {
                        callbacks.onReceiveValue(results);
                        callbacks = null;
                    } else if (callback != null) {
                        callback.onReceiveValue(results[0]);
                        callback = null;
                    }
                }
                else {
                    cancelCallback();
                }
            }
            else {
                Uri result = getCompressPhoto(currentUri);
                if (requestCode == CaptureActivity.PHOTO_REQUEST) {
                    if (callbacks != null) {
                        Uri[] uris = new Uri[]{ result };
                        callbacks.onReceiveValue(uris);
                        callbacks = null;
                    }
                    else if (callback != null) {
                        callback.onReceiveValue(result);
                        callback = null;
                    }
                }
                else if (requestCode == CaptureActivity.VIDEO_REQUEST) {
                    if (callbacks != null) {
                        callbacks.onReceiveValue(new Uri[]{ result });
                        callbacks = null;
                    }
                    else if (callback != null) {
                        callback.onReceiveValue(result);
                        callback = null;
                    }
                }
            }
        }
        else {
            cancelCallback();
        }
    }

    private void logout() {
        Membership.logout();
    }

    @Override
    public void onResume() {
        super.onResume();
        webView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        webView.onPause();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onParentDestroy() {
        destroyWebView();
    }

    public void destroyWebView() {
        if (webView != null) {
            webView.loadUrl("about:blank");
            webView.clearHistory();
            webView.clearCache(true);
            webView.destroy();
            webView = null;
        }
    }
}