package com.pica.miaosha.access;

import com.pica.miaosha.domian.MiaoshaUser;

public class UserContext {
    //ThreadLocal与当前线程绑定，存储当前线程的数据
    private static ThreadLocal<MiaoshaUser> userHolder = new ThreadLocal<>();

    public static void setUser(MiaoshaUser user) {
        userHolder.set(user);
    }

    public static MiaoshaUser getUser() {
        return userHolder.get();
    }

}
