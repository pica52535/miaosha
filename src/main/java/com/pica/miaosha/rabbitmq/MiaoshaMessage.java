package com.pica.miaosha.rabbitmq;

import com.pica.miaosha.domian.MiaoshaUser;
import lombok.Data;

@Data
public class MiaoshaMessage {

    private MiaoshaUser user;
    private Long goodsId;
}
