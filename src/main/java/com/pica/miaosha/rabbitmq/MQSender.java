package com.pica.miaosha.rabbitmq;

import com.pica.miaosha.redis.RedisService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class MQSender {

    @Autowired
    AmqpTemplate amqpTemplate;

    public void sendMiaoshaMessage(MiaoshaMessage mm) {
        //把bean转为string
        String msg = RedisService.beanToString(mm);
        log.info("send message " + msg);
        //
        amqpTemplate.convertAndSend(MQConfig.MIAOSHA_QUEUE, msg);
    }

    /*public void send(Object message) {
        //把bean转为string
        String msg = RedisService.beanToString(message);
        log.info("send message " + msg);
        //
        amqpTemplate.convertAndSend(MQConfig.QUEUE, msg);
    }

    public void sendTopic(Object message) {
        //把bean转为string
        String msg = RedisService.beanToString(message);
        log.info("send topic message " + msg);
        //
        amqpTemplate.convertAndSend(MQConfig.TOPIC_EXCHANGE, "topic.key1", msg + "1");
        amqpTemplate.convertAndSend(MQConfig.TOPIC_EXCHANGE, "topic.key2", msg + "2");
    }*/

    /*public void sendFanout(Object message) {
        //把bean转为string
        String msg = RedisService.beanToString(message);
        log.info("send fanout message " + msg);
        //
        amqpTemplate.convertAndSend(MQConfig.FANOUT_EXCHANGE, "", msg);
    }*/
    /*
    public void sendHeader(Object message) {
        //把bean转为string
        String msg = RedisService.beanToString(message);
        log.info("send header message " + msg);

        MessageProperties properties = new MessageProperties();

        properties.setHeader("head1","value1");
        properties.setHeader("head2","value2");

        Message obj = new Message(msg.getBytes(),properties);

        //
        amqpTemplate.convertAndSend(MQConfig.HEADERS_EXCHANGE, "", obj);
    }*/


}
