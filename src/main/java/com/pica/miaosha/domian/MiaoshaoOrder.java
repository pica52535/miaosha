package com.pica.miaosha.domian;


import lombok.Data;

@Data
public class MiaoshaoOrder {
    private Long id;
    private Long userId;
    private Long orderId;
    private Long goodsId;
}
