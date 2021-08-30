package com.pica.miaosha.controller;

import com.pica.miaosha.domian.User;
import com.pica.miaosha.rabbitmq.MQSender;
import com.pica.miaosha.redis.RedisService;
import com.pica.miaosha.redis.UserKey;
import com.pica.miaosha.result.CodeMsg;
import com.pica.miaosha.result.Result;
import com.pica.miaosha.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/demo")
public class SampleController {

    @Autowired
    UserService service;

    @Autowired
    RedisService redisService;

    @Autowired
    MQSender sender;

    /*@RequestMapping("/mq")
    @ResponseBody
    public Result<String> mq() {
        sender.send("hello,pica!");
        return Result.success("Hello，world");
    }

    @RequestMapping("/mq/topic")
    @ResponseBody
    public Result<String> topic() {
        sender.sendTopic("hello,pica!");
        return Result.success("Hello，world");
    }


    @RequestMapping("/mq/fanout")
    @ResponseBody
    public Result<String> fanout() {
        sender.sendFanout("hello,pica!");
        return Result.success("Hello，world");
    }

    @RequestMapping("/mq/header")
    @ResponseBody
    public Result<String> header() {
        sender.sendHeader("hello,pica!");
        return Result.success("Hello，world");
    }*/

    @RequestMapping("/hello")
    @ResponseBody
    public Result<String> home() {
        return Result.success("Hello，world");
    }

    @RequestMapping("/error")
    @ResponseBody
    public Result<String> error() {
        return Result.error(CodeMsg.SESSION_ERROR);
    }

    @GetMapping("/thymeleaf")
    public String thymeleaf(Model model) {
        model.addAttribute("name", "pica!");
        return "hello";
    }

    @RequestMapping("/db/Get")
    @ResponseBody
    public Result<User> dbGet() {
        User user = service.getById(1);

        return Result.success(user);
    }

    @RequestMapping("/db/tx")
    @ResponseBody
    public Result<Boolean> dbTx() {

        service.tx();
        return Result.success(true);
    }

    @RequestMapping("/redis/get")
    @ResponseBody
    public Result<User> redisGet() {

        User user = redisService.get(UserKey.getById, "" + 1, User.class);
        return Result.success(user);
    }

    @RequestMapping("/redis/set")
    @ResponseBody
    public Result<Boolean> redisSet() {

        User user = new User();
        user.setId(2);
        user.setName("2222");

        Boolean ret = redisService.set(UserKey.getById, "" + 1, user);
        return Result.success(ret);
    }

}
