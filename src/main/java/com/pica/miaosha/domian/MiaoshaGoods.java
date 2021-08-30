package com.pica.miaosha.domian;

import lombok.Data;

import java.sql.Date;

@Data
public class MiaoshaGoods {
    private Long id;
    private Long goodsId;
    private Double miaoshaPrice;
    private Integer stockCount;
    private Date startDate;
    private Date endDate;
}
