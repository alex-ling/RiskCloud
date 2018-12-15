package com.miniplat.auth;

import java.io.Serializable;

public class Token {
    public String access_token;
    public int expires_in;
    public String token_type;
    public String refresh_token;
}
