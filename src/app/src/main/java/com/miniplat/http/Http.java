package com.miniplat.http;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.miniplat.app.AppCtx;
import com.miniplat.auth.Membership;
import com.miniplat.auth.Token;
import com.miniplat.json.Json;
import com.miniplat.util.DialogUtil;
import com.miniplat.util.ToastUtil;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import java.util.List;

import cz.msebera.android.httpclient.Header;

public class Http {
    private static AsyncHttpClient client = new AsyncHttpClient();
    public static boolean showLoading = true;

    public static String getAbsoluteUrl(String api) {
        if (showLoading) {
            AppCtx.showLoading();
        }
        return AppCtx.URL + api;
    }

    public static String getUrlAndAppendToken(String api) {
        Token token = Membership.getToken();
        if (token != null) {
            client.addHeader("Authorization", "Bearer " + token.access_token);
        }
        return getAbsoluteUrl(api);
    }

    public static String getUrlAndRemoveToken(String api) {
        client.removeHeader("Authorization");
        return getAbsoluteUrl(api);
    }

    // GET T
    public static <T> void get(String api, RequestParams params, final Handler<T> handler) {
        client.get(getUrlAndAppendToken(api), params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                handlerSuccess(responseBody, handler);
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                handlerFailure(responseBody, handler);
            }
        });
    }

    // GET List<T>
    public static <T> void getList(String api, RequestParams params, final ListHandler<T> handler) {
        client.get(getUrlAndAppendToken(api), params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                handlerListSuccess(responseBody, handler);
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                handlerFailure(responseBody, handler);
            }
        });
    }

    // GET Response<T>
    public static <T> void getRes(String api, RequestParams params, final Handler<T> handler) {
        client.get(getUrlAndAppendToken(api), params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                handlerResponseSuccess(responseBody, handler);
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                handlerFailure(responseBody, handler);
            }
        });
    }

    // GET Response<JSONObject>
    public static void getResJson(String api, RequestParams params, final JsonHandler handler) {
        client.get(getUrlAndAppendToken(api), params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                handlerResponseJsonSuccess(responseBody, handler);
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                handlerFailure(responseBody, handler);
            }
        });
    }

    // GET Response<JSONArray>
    public static void getResJsonList(String api, RequestParams params, final JsonListHandler handler) {
        client.get(getUrlAndAppendToken(api), params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                handlerResponseJsonListSuccess(responseBody, handler);
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                handlerFailure(responseBody, handler);
            }
        });
    }

    // GET JSON
    public static void getJson(String api, RequestParams params, final JsonHandler handler) {
        client.get(getUrlAndAppendToken(api), params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                handlerJsonSuccess(responseBody, handler);
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                handlerFailure(responseBody, handler);
            }
        });
    }

    // GET JSON List
    public static void getJsonList(String api, RequestParams params, final JsonListHandler handler) {
        client.get(getUrlAndAppendToken(api), params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                handlerJsonListSuccess(responseBody, handler);
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                handlerFailure(responseBody, handler);
            }
        });
    }

    // GET String
    public static void getString(String api, RequestParams params, final StringHandler handler) {
        client.get(getUrlAndAppendToken(api), params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                handlerStringSuccess(responseBody, handler);
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                handlerFailure(responseBody, handler);
            }
        });
    }

    // POST T
    public static <T> void post(String api, RequestParams params, final Handler<T> handler) {
        post(api, params, handler, false);
    }

    // POST T
    public static <T> void post(String api, RequestParams params, final Handler<T> handler, boolean useJsonStreamer) {
        params.setUseJsonStreamer(useJsonStreamer);
        client.post(getUrlAndAppendToken(api), params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                handlerSuccess(responseBody, handler);
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                handlerFailure(responseBody, handler);
            }
        });
    }

    // POST T
    public static <T> void postList(String api, RequestParams params, final ListHandler<T> handler) {
        postList(api, params, handler, false);
    }

    // POST T
    public static <T> void postList(String api, RequestParams params, final ListHandler<T> handler, boolean useJsonStreamer) {
        params.setUseJsonStreamer(useJsonStreamer);
        client.post(getUrlAndAppendToken(api), params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                handlerListSuccess(responseBody, handler);
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                handlerFailure(responseBody, handler);
            }
        });
    }

    // POST Response<JSONObject>
    public static void postResJson(String api, RequestParams params, final JsonHandler handler) {
        postResJson(api, params, handler, false);
    }

    // POST Response<JSONObject>
    public static void postResJson(String api, RequestParams params, final JsonHandler handler, boolean useJsonStreamer) {
        params.setUseJsonStreamer(useJsonStreamer);
        client.post(getUrlAndAppendToken(api), params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                handlerResponseJsonSuccess(responseBody, handler);
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                handlerFailure(responseBody, handler);
            }
        });
    }

    // POST Response<JSONObject>
    public static void postResJsonList(String api, RequestParams params, final JsonListHandler handler) {
        postResJsonList(api, params, handler, false);
    }

    // POST Response<JSONObject>
    public static void postResJsonList(String api, RequestParams params, final JsonListHandler handler, boolean useJsonStreamer) {
        params.setUseJsonStreamer(useJsonStreamer);
        client.post(getUrlAndAppendToken(api), params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                handlerResponseJsonListSuccess(responseBody, handler);
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                handlerFailure(responseBody, handler);
            }
        });
    }

    // POST Response<T>
    public static <T> void postRes(String api, RequestParams params, final Handler<T> handler) {
        postRes(api, params, handler, false);
    }

    // POST Response<T>
    public static <T> void postRes(String api, RequestParams params, final Handler<T> handler, boolean useJsonStreamer) {
        params.setUseJsonStreamer(useJsonStreamer);
        client.post(getUrlAndAppendToken(api), params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                handlerResponseSuccess(responseBody, handler);
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                handlerFailure(responseBody, handler);
            }
        });
    }

    // POST Response<T>
    public static <T> void postResList(String api, RequestParams params, final ListHandler<T> handler) {
        postResList(api, params, handler, false);
    }

    // POST Response<T>
    public static <T> void postResList(String api, RequestParams params, final ListHandler<T> handler, boolean useJsonStreamer) {
        params.setUseJsonStreamer(useJsonStreamer);
        client.post(getUrlAndAppendToken(api), params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                handlerResponseListSuccess(responseBody, handler);
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                handlerFailure(responseBody, handler);
            }
        });
    }

    // POST JSON
    public static void postJson(String api, RequestParams params, final JsonHandler handler) {
        postJson(api, params, handler, false);
    }

    // POST JSON
    public static void postJson(String api, RequestParams params, final JsonHandler handler, boolean useJsonStreamer) {
        params.setUseJsonStreamer(useJsonStreamer);
        client.post(getUrlAndAppendToken(api), params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                handlerJsonSuccess(responseBody, handler);
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                handlerFailure(responseBody, handler);
            }
        });
    }

    // POST JSON
    public static void postJsonList(String api, RequestParams params, final JsonListHandler handler) {
        postJsonList(api, params, handler, false);
    }

    // POST JSON
    public static void postJsonList(String api, RequestParams params, final JsonListHandler handler, boolean useJsonStreamer) {
        params.setUseJsonStreamer(useJsonStreamer);
        client.post(getUrlAndAppendToken(api), params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                handlerJsonListSuccess(responseBody, handler);
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                handlerFailure(responseBody, handler);
            }
        });
    }

    // POST String
    public static void postString(String api, RequestParams params, final StringHandler handler) {
        postString(api, params, handler, false);
    }

    // POST String
    public static void postString(String api, RequestParams params, final StringHandler handler, boolean useJsonStreamer) {
        params.setUseJsonStreamer(useJsonStreamer);
        client.post(getUrlAndAppendToken(api), params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                handlerStringSuccess(responseBody, handler);
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                handlerFailure(responseBody, handler);
            }
        });
    }

    // Handler T
    private static <T> void handlerSuccess(byte[] body, Handler<T> handler) {
        AppCtx.closeLoading();
        T res = Json.fromJsonObject(new String(body), handler.getClazz());
        handler.success(res);
    }

    // Handler T
    private static <T> void handlerListSuccess(byte[] body, ListHandler<T> handler) {
        AppCtx.closeLoading();
        List<T> res = Json.fromJsonList(new String(body), handler.getClazz());
        handler.success(res);
    }

    // Handler Response<T>
    private static <T> void handlerResponseSuccess(byte[] body, Handler<T> handler) {
        AppCtx.closeLoading();
        Response<T> res = Json.fromJsonResponse(new String(body), handler.getClazz());
        handler.success(res.value);
    }

    // Handler Response<List<T>>
    private static <T> void handlerResponseListSuccess(byte[] body, ListHandler<T> handler) {
        AppCtx.closeLoading();
        Response<List<T>> res = Json.fromJsonResponseList(new String(body), handler.getClazz());
        handler.success(res.value);
    }

    // Handler Response<JSONObject>
    private static void handlerResponseJsonSuccess(byte[] body, JsonHandler handler) {
        AppCtx.closeLoading();
        Response<JSONObject> res = Json.fromJsonResponse(new String(body), JSONObject.class);
        handler.success(res.value);
    }

    // Handler Response<JSONArray>
    private static void handlerResponseJsonListSuccess(byte[] body, JsonListHandler handler) {
        AppCtx.closeLoading();
        Response<JSONArray> res = Json.fromJsonResponse(new String(body), JSONArray.class);
        handler.success(res.value);
    }

    // Handler JSON
    private static void handlerJsonSuccess(byte[] body, JsonHandler handler) {
        AppCtx.closeLoading();
        JSONObject res = Json.fromJson(new String(body));
        handler.success(res);
    }

    // Handler JSON List
    private static void handlerJsonListSuccess(byte[] body, JsonListHandler handler) {
        AppCtx.closeLoading();
        JSONArray res = Json.fromJsonList(new String(body));
        handler.success(res);
    }

    private static <T> void handlerStringSuccess(byte[] body, StringHandler handler) {
        AppCtx.closeLoading();
        handler.success(new String(body));
    }

    private static void handlerFailure(byte[] body, BaseHandler handler) {
        AppCtx.closeLoading();
        try {
            Response<String> res = Json.fromJsonResponse(new String(body), String.class);
            if (res != null) {
                ToastUtil.error(res.error_description);
            }
            else {
                ToastUtil.error("请检查网络是否连接");
            }
        }
        catch (Exception e){
            e.printStackTrace();
            ToastUtil.error("请检查网络是否连接");
        }
        handler.failure();
    }
}
