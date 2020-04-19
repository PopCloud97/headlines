package com.popcloud.async.handler;

import com.popcloud.async.EventHandler;
import com.popcloud.async.EventModel;
import com.popcloud.async.EventType;
import com.popcloud.model.Message;
import com.popcloud.model.User;
import com.popcloud.service.MessageService;
import com.popcloud.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Date;
import java.util.List;


@Component
public class LikeHandler implements EventHandler {
    @Autowired
    MessageService messageService;

    @Autowired
    UserService userService;

    @Override
    public void doHandle(EventModel model) {
        Message message = new Message();
        User user = userService.getUser(model.getActorId());
        message.setToId(model.getEntityOwnerId());
        message.setContent("用户 " + user.getName() +
                " 赞了你的资讯， http://127.0.0.1:8080/news/"
                + model.getEntityId());
        //系统账号id=2
        message.setFromId(2);
        message.setCreatedDate(new Date());
        message.setConversationId(String.format("%d_%d", 2, model.getEntityOwnerId()));
        messageService.addMessage(message);
    }

    @Override
    public List<EventType> getSupportEventTypes() {
        return Arrays.asList(EventType.LIKE);
    }
}
