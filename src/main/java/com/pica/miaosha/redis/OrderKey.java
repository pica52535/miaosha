package com.pica.miaosha.redis;

public class OrderKey extends BasePrefix {
    private OrderKey(String Prefix) {
        super(Prefix);
    }

    public static OrderKey getMiaoshaOrderByUidGid = new OrderKey("moug");

}
