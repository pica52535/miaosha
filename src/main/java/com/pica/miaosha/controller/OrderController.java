package com.pica.miaosha.controller;

import com.pica.miaosha.domian.MiaoshaUser;
import com.pica.miaosha.domian.OrderInfo;
import com.pica.miaosha.redis.RedisService;
import com.pica.miaosha.result.CodeMsg;
import com.pica.miaosha.result.Result;
import com.pica.miaosha.service.GoodsService;
import com.pica.miaosha.service.MiasohaUserService;
import com.pica.miaosha.service.OrderService;
import com.pica.miaosha.vo.GoodsVo;
import com.pica.miaosha.vo.OrderDetailVo;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/order")
public class OrderController {
    @Autowired
    MiasohaUserService userService;

    @Autowired
    RedisService redisService;

    @Autowired
    GoodsService goodsService;

    @Autowired
    OrderService orderService;

    @RequestMapping("/detail")
    @ResponseBody
    public Result<OrderDetailVo> info(Model model, MiaoshaUser user,
                                      @RequestParam("orderId") long orderId) {

        if (user == null) {
            return Result.error(CodeMsg.SESSION_ERROR);
        }
        OrderInfo order = orderService.getOrderById(orderId);

        if (order == null) {
            return Result.error(CodeMsg.ORDER_NOT_EXIST);
        }
        Long goodsId = order.getGoodsId();
        GoodsVo goods = goodsService.getGoodsVoByGoodsId(goodsId);
        OrderDetailVo orderDetailVo = new OrderDetailVo();
        orderDetailVo.setGoods(goods);
        orderDetailVo.setOrder(order);
        return Result.success(orderDetailVo);
    }
}
