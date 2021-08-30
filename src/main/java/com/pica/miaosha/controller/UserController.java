package com.pica.miaosha.controller;

import com.pica.miaosha.domian.MiaoshaUser;
import com.pica.miaosha.redis.RedisService;
import com.pica.miaosha.result.Result;
import com.pica.miaosha.service.GoodsService;
import com.pica.miaosha.service.MiasohaUserService;
import com.pica.miaosha.vo.GoodsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
@RequestMapping("/user")
public class UserController {
    @Autowired
    MiasohaUserService userService;

    @Autowired
    RedisService redisService;

    @Autowired
    GoodsService goodsService;

    @RequestMapping("/info")
    @ResponseBody
    public Result<MiaoshaUser> info(Model model, MiaoshaUser user) {
        return Result.success(user);
    }
}
