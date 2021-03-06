package com.pica.miaosha.redis;


public class MiaoshaUserKey extends BasePrefix {
    private static final int TOKEN_EXPIRE = 3600 * 24 * 2;

    private MiaoshaUserKey(int expireSeconds, String Prefix) {

        super(expireSeconds, Prefix);
    }

    public static MiaoshaUserKey token = new MiaoshaUserKey(TOKEN_EXPIRE,"tk");

    public static MiaoshaUserKey getById = new MiaoshaUserKey(0,"id");
}
