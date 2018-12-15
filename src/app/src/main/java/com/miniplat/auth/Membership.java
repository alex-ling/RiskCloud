package com.miniplat.auth;

import com.miniplat._activity.LoginActivity;
import com.miniplat.app.AppCtx;
import com.miniplat.core.IHandler;
import com.miniplat.http.Handler;
import com.miniplat.http.Http;
import com.loopj.android.http.RequestParams;

public class Membership {
    private static Token token = null;
    private static User user = null;

    public static boolean getLogined() {
        //return false;
        return getUser() != null;
    }

    public static Token getToken() {
        if (token == null) {
            token = AppCtx.getCache().getObject("c_token", Token.class);
        }
        return token;
    }

    public static User getUser() {
        if (user == null) {
            user = AppCtx.getCache().getObject("c_user", User.class);
        }
        return user;
    }

    public static void login(String loginName, String password, final IHandler handler) {
        RequestParams params = new RequestParams();
        params.put("client_id", AppCtx.CLIENT_ID);
        params.put("client_secret", AppCtx.ClIENT_Secret);
        params.put("grant_type", AppCtx.GRANT_Password);
        params.put("username", loginName);
        params.put("password", password);
        Http.post("connect/token", params, new Handler<Token>() {
            @Override
            public Class<Token> getClazz() { return Token.class; }
            @Override
            public void success(Token res) {
                token = res;
                AppCtx.getCache().setObject("c_token", token);
                getLoginUser(handler);
            }
        });
    }

    public static void getLoginUser(final IHandler handler) {
        Http.getRes("api/auth/getUser", null, new Handler<User>() {
            @Override
            public Class<User> getClazz() { return User.class; }
            @Override
            public void success(User res) {
                user = res;
                AppCtx.getCache().setObject("c_user", user);
                handler.callback();
            }
        });
    }

    public static void logout() {
        AppCtx.getCache().remove("c_token");
        AppCtx.getCache().remove("c_user");
        token = null;
        user = null;

        LoginActivity.show();
    }
}
