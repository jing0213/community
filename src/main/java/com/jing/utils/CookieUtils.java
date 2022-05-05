package com.jing.utils;

import org.springframework.stereotype.Component;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;


public class CookieUtils {
    public static String getValue(HttpServletRequest request,String name){
        if(request==null||name == null){
            throw new IllegalArgumentException("参数为空");
        }
        Cookie[] cookies = request.getCookies();
        if(cookies!=null){
            for (Cookie cookie : cookies) {
                if(cookie.getName().equals(name))
                    return cookie.getValue().toString();
            }
        }
        return null;
    }
}
