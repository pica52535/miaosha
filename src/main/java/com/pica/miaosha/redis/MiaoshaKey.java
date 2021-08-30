package com.pica.miaosha.redis;

public class MiaoshaKey extends BasePrefix {


    public MiaoshaKey(int expireSeconds, String Prefix) {
        super(expireSeconds, Prefix);
    }

    public static MiaoshaKey isGoodsOver = new MiaoshaKey(0, "go");

    public static MiaoshaKey getMiaoshaPath = new MiaoshaKey(60, "mp");

    public static MiaoshaKey getMiaoshaVerifyCode = new MiaoshaKey(300, "vc");
}
