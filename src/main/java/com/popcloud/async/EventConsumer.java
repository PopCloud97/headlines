package com.popcloud.async;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.popcloud.util.RedisUtil;
import com.popcloud.util.RedisKeyUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Created by popcloud on 2016/7/14.
 */
@Service
public class EventConsumer implements InitializingBean, ApplicationContextAware {
    private static final Logger logger = LoggerFactory.getLogger(EventConsumer.class);
    private Map<EventType, List<EventHandler>> config = new HashMap<>();
    private ApplicationContext applicationContext;
    @Autowired
    private RedisUtil redisUtil;

    @Override
    public void afterPropertiesSet() throws Exception {
        Map<String, EventHandler> beans = applicationContext.getBeansOfType(EventHandler.class);
        for (Map.Entry<String, EventHandler> entry : beans.entrySet()) {
            List<EventType> eventTypes = entry.getValue().getSupportEventTypes();
            for (EventType type : eventTypes) {
                if (!config.containsKey(type)) {
                    config.put(type, new ArrayList<EventHandler>());
                }

                // 注册每个事件的处理函数
                config.get(type).add(entry.getValue());
            }
        }

        // 启动线程去消费事件
        Thread thread = new Thread(() -> {
            // 从队列一直消费
            while (true) {
                String key = RedisKeyUtil.getEventQueueKey();
                String message = redisUtil.rpop(key, 0, TimeUnit.SECONDS);
                EventModel eventModel = JSON.parseObject(message, EventModel.class);
                // 找到这个事件的处理handler列表
                if (!config.containsKey(eventModel.getType())) {
                    logger.error("不能识别的事件");
                    continue;
                }
                for (EventHandler handler : config.get(eventModel.getType())) {
                    handler.doHandle(eventModel);
                }
            }
        });
        thread.start();
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
