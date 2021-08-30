package com.pica.miaosha.controller;

import com.pica.miaosha.domian.MiaoshaUser;
import com.pica.miaosha.redis.GoodsKey;
import com.pica.miaosha.redis.RedisService;
import com.pica.miaosha.result.Result;
import com.pica.miaosha.service.GoodsService;
import com.pica.miaosha.service.MiasohaUserService;
import com.pica.miaosha.vo.GoodsDetailVo;
import com.pica.miaosha.vo.GoodsVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.thymeleaf.context.IWebContext;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.spring5.view.ThymeleafViewResolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@Slf4j
@Controller
@RequestMapping("/goods")
public class GoodsController {


    @Autowired
    MiasohaUserService userService;

    @Autowired
    RedisService redisService;

    @Autowired
    GoodsService goodsService;

    @Autowired
    ThymeleafViewResolver thymeleafViewResolver;

    @Autowired
    ApplicationContext applicationContext;

    /*@RequestMapping("/to_list")
    public String toList(HttpServletResponse response, Model model,
//                         @CookieValue(value = MiasohaUserService.COOKIE_NAME_TOKEN, required = false) String cookieToken,
//                         @RequestParam(value = MiasohaUserService.COOKIE_NAME_TOKEN, required = false) String paramToken
                         MiaoshaUser user) {

        *//*if (StringUtils.isEmpty(cookieToken) && StringUtils.isEmpty(paramToken)) {
            return "login";
        }
        String token = StringUtils.isEmpty(paramToken) ? cookieToken : paramToken;
        MiaoshaUser user = userService.getByToken(response, token);*//*
        model.addAttribute("user", user);
        return "goods_list";
    }*/


    //实现 MiaoshaUser 自动注入 user中自带cookie
    @RequestMapping(value = "/to_list", produces = "text/html")
    @ResponseBody
    public String toList(HttpServletRequest request, HttpServletResponse response, Model model, MiaoshaUser user) {
        model.addAttribute("user", user);

        //从redis中取页面缓存
        String html = redisService.get(GoodsKey.getGoodsList, "", String.class);
        if (!StringUtils.isEmpty(html)) {
            return html;
        }

        //查询商品列表
        List<GoodsVo> goodsList = goodsService.listGoodsVo();
        model.addAttribute("goodsList", goodsList);


        IWebContext ctx = new WebContext(request, response, request.getServletContext(), request.getLocale(), model.asMap());

        //手动渲染
        html = thymeleafViewResolver.getTemplateEngine().process("goods_list", ctx);
        if (!StringUtils.isEmpty(html)) {
            redisService.set(GoodsKey.getGoodsList, "", html);
        }
        return html;
    }

    // /goods/to_detail/

    @RequestMapping(value = "/to_detail/{goodsId}", produces = "text/html")
    @ResponseBody
    public String detail(HttpServletRequest request, HttpServletResponse response,
                         Model model, MiaoshaUser user,
                         @PathVariable("goodsId") long goodsId) {
        model.addAttribute("user", user);

        //从redis中取页面缓存
        String html = redisService.get(GoodsKey.getGoodsDetail, "" + goodsId, String.class);
        if (!StringUtils.isEmpty(html)) {
            return html;
        }

        GoodsVo goods = goodsService.getGoodsVoByGoodsId(goodsId);
        model.addAttribute("goods", goods);

        //
        long startTime = goods.getStartDate().getTime();
        long endTime = goods.getEndDate().getTime();
        long now = System.currentTimeMillis();

        int miaoshaStatus = 0;
        int remainSeconds = 0;

        if (now < startTime) { //秒杀未开始，倒计时
            miaoshaStatus = 0;
            remainSeconds = (int) (startTime - now) / 1000;

        } else if (now > endTime) {//秒杀结束
            miaoshaStatus = 2;
            remainSeconds = -1;

        } else {//秒杀进行中
            miaoshaStatus = 1;
            remainSeconds = 0;
        }

        model.addAttribute("miaoshaStatus", miaoshaStatus);
        model.addAttribute("remainSeconds", remainSeconds);

        IWebContext ctx = new WebContext(request, response, request.getServletContext(), request.getLocale(), model.asMap());

        //手动渲染
        html = thymeleafViewResolver.getTemplateEngine().process("goods_detail", ctx);
        if (!StringUtils.isEmpty(html)) {
            redisService.set(GoodsKey.getGoodsDetail, "" + goodsId, html);
        }

        return html;
    }

    @RequestMapping(value = "/detail2/{goodsId}")
    @ResponseBody
    public Result<GoodsDetailVo> detail2(HttpServletRequest request, HttpServletResponse response,
                                         Model model, MiaoshaUser user,
                                         @PathVariable("goodsId") long goodsId) {


        GoodsVo goods = goodsService.getGoodsVoByGoodsId(goodsId);

        long startTime = goods.getStartDate().getTime();
        long endTime = goods.getEndDate().getTime();
        long now = System.currentTimeMillis();

        int miaoshaStatus = 0;
        int remainSeconds = 0;

        if (now < startTime) { //秒杀未开始，倒计时
            miaoshaStatus = 0;
            remainSeconds = (int) (startTime - now) / 1000;

        } else if (now > endTime) {//秒杀结束
            miaoshaStatus = 2;
            remainSeconds = -1;

        } else {//秒杀进行中
            miaoshaStatus = 1;
            remainSeconds = 0;
        }

        GoodsDetailVo vo = new GoodsDetailVo();
        vo.setGoods(goods);
        vo.setMiaoshaStatus(miaoshaStatus);
        vo.setRemainSeconds(remainSeconds);
        vo.setUser(user);
        return Result.success(vo);
    }

}
