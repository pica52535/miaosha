package com.pica.miaosha.service;

import com.pica.miaosha.dao.OrderDao;
import com.pica.miaosha.domian.MiaoshaUser;
import com.pica.miaosha.domian.MiaoshaoOrder;
import com.pica.miaosha.domian.OrderInfo;
import com.pica.miaosha.redis.OrderKey;
import com.pica.miaosha.redis.RedisService;
import com.pica.miaosha.vo.GoodsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Service
public class OrderService {
    @Autowired
    OrderDao orderDao;

    @Autowired
    RedisService redisService;


    public MiaoshaoOrder getMiaoshaOrderByUserIdGoodsId(Long userId, long goodsId) {

        return redisService.get(OrderKey.getMiaoshaOrderByUidGid, "" + userId + "" + goodsId, MiaoshaoOrder.class);
    }

    @Transactional
    public OrderInfo createOrder(MiaoshaUser user, GoodsVo goods) {
        OrderInfo orderInfo = new OrderInfo();

        orderInfo.setCreateDate(new Date());
        orderInfo.setDeliveryAddrId(0L);
        orderInfo.setGoodsCount(1);
        orderInfo.setGoodsId(goods.getId());
        orderInfo.setGoodsName(goods.getGoodsName());
        orderInfo.setGoodsPrice(goods.getMiaoshaPrice());
        orderInfo.setOrderChannel(1);
        orderInfo.setStatus(0);
        orderInfo.setUserId(user.getId());

        orderDao.insert(orderInfo);

        MiaoshaoOrder miaoshaoOrder = new MiaoshaoOrder();

        miaoshaoOrder.setGoodsId(goods.getId());
        miaoshaoOrder.setOrderId(orderInfo.getId());
        miaoshaoOrder.setUserId(user.getId());

        orderDao.insertMiaoshaOrder(miaoshaoOrder);

        //把秒杀订单放入缓存，用来后续判断是否重复下单的问题
        redisService.set(OrderKey.getMiaoshaOrderByUidGid, "" + user.getId() + "" + goods.getId(), miaoshaoOrder);

        return orderInfo;

    }

    public OrderInfo getOrderById(long orderId) {

        return orderDao.getOrderById(orderId);
    }
}
