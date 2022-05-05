package com.jing.utils;

public interface CommunityConstant {
    //激活成功
    int ACTIVATION_SUCCESS=0;
    //重复激活
    int ACTIVATION_REPEAT=1;
    //激活失败
    int ACTIVATION_FAILURE=2;
    //登录凭证默认过期时间 12h
    int DEFAULT_EXPIRED_SECONDS=3600*12;
    //登录凭证renmember过期时间 60DAYS
    int REMEMBER_EXPIRED_SECONDS=3600*24*60;
    //帖子实体
    int ENTITY_TYPE_POST = 1;
    //评论实体
    int ENTITY_TYPE_COMMENT = 2;
}
