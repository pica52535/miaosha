package com.pica.miaosha.vo;

import com.pica.miaosha.domian.OrderInfo;
import lombok.Data;

@Data
public class OrderDetailVo {
    private GoodsVo goods;
    private OrderInfo order;
}
