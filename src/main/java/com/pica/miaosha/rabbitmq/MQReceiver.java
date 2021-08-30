package com.pica.miaosha.rabbitmq;

import com.pica.miaosha.domian.MiaoshaUser;
import com.pica.miaosha.domian.MiaoshaoOrder;
import com.pica.miaosha.redis.RedisService;
import com.pica.miaosha.result.CodeMsg;
import com.pica.miaosha.result.Result;
import com.pica.miaosha.service.GoodsService;
import com.pica.miaosha.service.MiaoshaService;
import com.pica.miaosha.service.MiasohaUserService;
import com.pica.miaosha.service.OrderService;
import com.pica.miaosha.vo.GoodsVo;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class MQReceiver {



    @Autowired
    MiasohaUserService userService;

    @Autowired
    RedisService redisService;

    @Autowired
    GoodsService goodsService;

    @Autowired
    OrderService orderService;

    @Autowired
    MiaoshaService miaoshaService;


    @RabbitListener(queues = MQConfig.MIAOSHA_QUEUE)
    public void receive(String message) {

        log.info("receive message " + message);
        MiaoshaMessage mm = RedisService.StringToBean(message, MiaoshaMessage.class);
        MiaoshaUser user = mm.getUser();
        Long goodsId = mm.getGoodsId();

        //判断库存
        GoodsVo goods = goodsService.getGoodsVoByGoodsId(goodsId);
        Integer stock = goods.getStockCount();
        if (stock <= 0) { //没库存
            return;
        }

        //不能重复秒杀
        MiaoshaoOrder order = orderService.getMiaoshaOrderByUserIdGoodsId(user.getId(), goodsId);

        if (order != null) {
            return;
        }

        //生成秒杀订单
        miaoshaService.miaosha(user, goods);

    }


    /*
    //为什么header的消息这个方法可以接收到

    @RabbitListener(queues = MQConfig.QUEUE)
    public void receive(String message) {
        log.info("receive message " + message);
    }

    @RabbitListener(queues = MQConfig.TOPIC_QUEUE1)
    public void receiveTopic1(String message) {
        log.info("receive topic queue1 message " + message);
    }

    @RabbitListener(queues = MQConfig.TOPIC_QUEUE2)
    public void receiveTopic2(String message) {
        log.info("receive topic queue2 message " + message);
    }

    @RabbitListener(queues = MQConfig.HEAD_QUEUE)
    public void receiveHeader(byte[] message) {
        log.info("receive header queue message " + new String(message));
    }*/
}
