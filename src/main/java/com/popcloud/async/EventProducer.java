package com.popcloud.async;

import com.alibaba.fastjson.JSONObject;
import com.popcloud.util.RedisUtil;
import com.popcloud.util.RedisKeyUtil;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Service
public class EventProducer {

    @Autowired
    RedisUtil redisUtil;

    public boolean fireEvent(EventModel eventModel) {
        try {
            String json = JSONObject.toJSONString(eventModel);
            String key = RedisKeyUtil.getEventQueueKey();
            redisUtil.lpush(key, json);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
