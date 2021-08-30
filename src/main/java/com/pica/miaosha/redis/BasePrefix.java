package com.pica.miaosha.redis;


import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
public abstract class BasePrefix implements KeyPrefix {

    private int expireSeconds;

    private String Prefix;


    @Override
    public int expireSeconds() {//默认0代表永不过期

        return expireSeconds;
    }

    @Override
    public String getPrefix() {
        //把redis的key加上前缀 作为识别
        String className = getClass().getSimpleName();

        return className + ":" + Prefix;
    }


    public BasePrefix(String prefix) {
        this(0, prefix);
    }
}
