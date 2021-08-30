package com.pica.miaosha.rabbitmq;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;


@Configuration
public class MQConfig {

    //队列的名称
    public static final String QUEUE = "queue";
    public static final String MIAOSHA_QUEUE = "queue";
    public static final String TOPIC_QUEUE1 = "topic.queue1";
    public static final String TOPIC_QUEUE2 = "topic.queue2";
    public static final String HEAD_QUEUE = "head.queue";

    public static final String TOPIC_EXCHANGE = "topic.Exchange";
    public static final String ROUTING_KEY1 = "topic.key1";
    public static final String ROUTING_KEY2 = "topic.#"; //#为通配符

    public static final String FANOUT_EXCHANGE = "fanout.Exchange";

    public static final String HEADERS_EXCHANGE = "headers.Exchange";


    /**
     * 交换机Exchange
     * Direct模式：
     */

    @Bean
    public Queue queue() {
        return new Queue(QUEUE, true);//true 是否做初始化
    }

    /**
     * Topic模式交换机
     */

    @Bean
    public Queue topicQueue1() {
        return new Queue(TOPIC_QUEUE1, true);//true 是否做持久化
    }

    @Bean
    public Queue topicQueue2() {
        return new Queue(TOPIC_QUEUE2, true);//true 是否做持久化
    }

    @Bean
    public TopicExchange topicExchange() {
        return new TopicExchange(TOPIC_EXCHANGE);
    }

    @Bean
    public Binding topBinding1() {
        return BindingBuilder.bind(topicQueue1()).to(topicExchange()).with(ROUTING_KEY1);
    }

    @Bean
    public Binding topBinding2() {
        return BindingBuilder.bind(topicQueue2()).to(topicExchange()).with(ROUTING_KEY2);
    }


    /**
     * fanout模式 ： 广播模式
     */
    @Bean
    public FanoutExchange fanoutExchange() {
        return new FanoutExchange(FANOUT_EXCHANGE);
    }

    @Bean
    public Binding fanoutBinding1() {
        return BindingBuilder.bind(topicQueue1()).to(fanoutExchange());
    }

    @Bean
    public Binding fanoutBinding2() {
        return BindingBuilder.bind(topicQueue2()).to(fanoutExchange());
    }


    /**
     * Header模式 ：
     */
    @Bean
    public HeadersExchange headersExchange() {
        return new HeadersExchange(HEADERS_EXCHANGE);
    }

    @Bean
    public Queue headerQueue() {
        return new Queue(HEAD_QUEUE, true);//true 是否做初始化
    }

    @Bean
    public Binding headerBinding() {
        Map<String,Object> map = new HashMap<>();

        map.put("head1","value1");
        map.put("head2","value2");

        return BindingBuilder.bind(headerQueue()).to(headersExchange()).whereAll(map).match();
    }

}
