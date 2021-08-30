package com.pica.miaosha.vo;

import com.pica.miaosha.domian.MiaoshaUser;
import lombok.Data;

@Data
public class GoodsDetailVo {

    private int miaoshaStatus = 0;
    private int remainSeconds = 0;
    private GoodsVo goods;
    private MiaoshaUser user;
}
