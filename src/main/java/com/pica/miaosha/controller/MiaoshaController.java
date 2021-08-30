package com.pica.miaosha.controller;

import com.pica.miaosha.access.AccessLimit;
import com.pica.miaosha.domian.MiaoshaUser;
import com.pica.miaosha.domian.MiaoshaoOrder;
import com.pica.miaosha.domian.OrderInfo;
import com.pica.miaosha.rabbitmq.MQSender;
import com.pica.miaosha.rabbitmq.MiaoshaMessage;
import com.pica.miaosha.redis.AccessKey;
import com.pica.miaosha.redis.GoodsKey;
import com.pica.miaosha.redis.MiaoshaKey;
import com.pica.miaosha.redis.RedisService;
import com.pica.miaosha.result.CodeMsg;
import com.pica.miaosha.result.Result;
import com.pica.miaosha.service.GoodsService;
import com.pica.miaosha.service.MiaoshaService;
import com.pica.miaosha.service.MiasohaUserService;
import com.pica.miaosha.service.OrderService;
import com.pica.miaosha.util.MD5Util;
import com.pica.miaosha.util.UUIDUtil;
import com.pica.miaosha.vo.GoodsVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Controller
@RequestMapping("/miaosha")
public class MiaoshaController implements InitializingBean {


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

    @Autowired
    MQSender sender;

    //使用一个map来存储库存到本地，减少查询redis的时间
    private Map<Long, Boolean> localOverMap = new HashMap<>();

    //系统初始化
    @Override
    public void afterPropertiesSet() throws Exception {
        List<GoodsVo> goosList = goodsService.listGoodsVo();

        if (goosList == null) {
            return;
        }
        //将商品库存加载到缓存
        for (GoodsVo goods : goosList) {
            //设置redis库存
            redisService.set(GoodsKey.getMiaoshaGoodsStock, "" + goods.getId(), goods.getStockCount());
            //设置本地库存
            localOverMap.put(goods.getId(), false);
        }

    }

    //实现 MiaoshaUser 自动注入 user中自带cookie
    /*@RequestMapping("/do_miaosha")
    public String toList(Model model, MiaoshaUser user,
                         @RequestParam("goodsId") long goodsId) {
        model.addAttribute("user", user);

        if(user == null){
            return "login";
        }
        //判断库存
        GoodsVo goods = goodsService.getGoodsVoByGoodsId(goodsId);
        Integer stock = goods.getStockCount();
        if(stock<=0){ //没库存
            model.addAttribute("errmsg", CodeMsg.MIAO_SHA_OVER.getMsg());
            return "miaosha_fail";
        }

        //不能重复秒杀
        MiaoshaoOrder order = orderService.getMiaoshaOrderByUserIdGoodsId(user.getId(),goodsId);

        if(order!=null){
            model.addAttribute("errmsg", CodeMsg.REPEATE_MIAOSHA.getMsg());
            return "miaosha_fail";
        }
        //减库存，下订单，写入秒杀订单--事务
        OrderInfo orderInfo = miaoshaService.miaosha(user,goods);
        model.addAttribute("orderInfo", orderInfo);
        model.addAttribute("goods", goods);
        return "order_detail";
    }*/

    /**
     * get POST的区别
     * get是幂等的 无论调用多少次 都相等
     * post 对服务端的数据产生影响
     */

    @RequestMapping(value = "/{path}/do_miaosha", method = RequestMethod.POST)
    @ResponseBody
    public Result<Integer> toList(Model model, MiaoshaUser user,
                                  @RequestParam("goodsId") long goodsId,
                                  @PathVariable("path") String path) {
        model.addAttribute("user", user);

        if (user == null) {
            return Result.error(CodeMsg.SESSION_ERROR);
        }

        //验证path
        boolean check = miaoshaService.checkPath(user, goodsId, path);

        if (!check) {
            return Result.error(CodeMsg.REQUEST_ILLEGAL);
        }

        //内存标记来减少redis访问
        Boolean over = localOverMap.get(goodsId);
        if (over) {
            return Result.error(CodeMsg.MIAO_SHA_OVER);
        }

        //减库存
        Long stock = redisService.decr(GoodsKey.getMiaoshaGoodsStock, "" + goodsId);
        if (stock < 0) {
            //当查询redis的库存小于0时，标记本地库存为 false
            localOverMap.put(goodsId, true);
            return Result.error(CodeMsg.MIAO_SHA_OVER);
        }

        //不能重复秒杀
        MiaoshaoOrder order = orderService.getMiaoshaOrderByUserIdGoodsId(user.getId(), goodsId);

        if (order != null) {
            return Result.error(CodeMsg.REPEATE_MIAOSHA);
        }

        //入队
        MiaoshaMessage mm = new MiaoshaMessage();
        mm.setUser(user);
        mm.setGoodsId(goodsId);
        sender.sendMiaoshaMessage(mm);

        return Result.success(0);


        /*
        //判断库存
        GoodsVo goods = goodsService.getGoodsVoByGoodsId(goodsId);
        Integer stock = goods.getStockCount();
        if (stock <= 0) { //没库存
            return Result.error(CodeMsg.MIAO_SHA_OVER);
        }

        //不能重复秒杀
        MiaoshaoOrder order = orderService.getMiaoshaOrderByUserIdGoodsId(user.getId(), goodsId);

        if (order != null) {
            return Result.error(CodeMsg.REPEATE_MIAOSHA);
        }
        //减库存，下订单，写入秒杀订单--事务
        OrderInfo orderInfo = miaoshaService.miaosha(user, goods);

        return Result.success(orderInfo);
        */

    }

    /**
     * 返回值 订单ID表示秒杀成功
     * 0 表示排队中
     * -1 表示秒杀失败
     */
    @AccessLimit(seconds=5,maxCount = 10,needLogin =true)
    @RequestMapping(value = "/result", method = RequestMethod.GET)
    @ResponseBody
    public Result<Long> miaoshaResult(Model model, MiaoshaUser user,
                                      @RequestParam("goodsId") long goodsId) {
        model.addAttribute("user", user);

        if (user == null) {
            return Result.error(CodeMsg.SESSION_ERROR);
        }

        long result = miaoshaService.getMiaoshaResult(user.getId(), goodsId);
        return Result.success(result);
    }


    //生成秒杀地址
    @AccessLimit(seconds=5,maxCount = 5,needLogin =true)
    @RequestMapping(value = "/path", method = RequestMethod.GET)
    @ResponseBody
    public Result<String> miaoshaPath(HttpServletRequest request, MiaoshaUser user,
                                      @RequestParam("goodsId") long goodsId,
                                      @RequestParam(value = "verifyCode", defaultValue = "0") int verifyCode) {


        if (user == null) {
            return Result.error(CodeMsg.SESSION_ERROR);
        }

        //检查验证码
        boolean check = miaoshaService.checkVerifyCode(user, goodsId, verifyCode);
        if (!check) {
            return Result.error(CodeMsg.REQUEST_ILLEGAL);
        }

        String path = miaoshaService.createMiaoshaPath(user, goodsId);

        return Result.success(path);
    }

    //verifyCode
    @RequestMapping(value = "/verifyCode", method = RequestMethod.GET)
    @ResponseBody
    public Result<String> verifyCode(HttpServletResponse response, Model model, MiaoshaUser user,
                                     @RequestParam("goodsId") long goodsId) {
        model.addAttribute("user", user);

        if (user == null) {
            return Result.error(CodeMsg.SESSION_ERROR);
        }

        try {
            BufferedImage image = miaoshaService.createMiaoshaVerifyCode(user, goodsId);
            ServletOutputStream out = response.getOutputStream();

            ImageIO.write(image, "JPEG", out);
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error(CodeMsg.MIAOSHA_FAIL);
        }


        return null;
    }


}
