package com.cursoudemy.backendcursoudemy.security;

import com.cursoudemy.backendcursoudemy.SpringApplicationContext;

public class SecurityConstants {
    public static final long EXPIRATION_DATE = 864000000; // 10 dias 
    public static final String TOKEN_PREFIX = "Bearer "; 
    public static final String HEADER_STRING = "Authorization"; 
    public static final String SIGN_UP_URL = "/users"; 

    public static String getSecretToken(){
        AppProperties appProperties = (AppProperties) SpringApplicationContext.getBeans("AppProperties");
        return appProperties.getSecretToken();
    }
}
