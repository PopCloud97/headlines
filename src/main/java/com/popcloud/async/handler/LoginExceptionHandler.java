package com.popcloud.async.handler;

import com.popcloud.async.EventHandler;
import com.popcloud.async.EventModel;
import com.popcloud.async.EventType;
import com.popcloud.model.Message;
import com.popcloud.service.MessageService;
import com.popcloud.util.MailUtil;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.*;

@Component
public class LoginExceptionHandler implements EventHandler{
    @Resource
    MessageService messageService;

    @Resource
    MailUtil mailUtil;

    @Override
    public void doHandle(EventModel model) {
        //这里应该加上异常登录判断，比较复杂
        Message message = new Message();
        message.setToId(model.getActorId());
        message.setContent("你上次的登陆IP异常");
        //系统账号id=2
        message.setFromId(2);
        message.setCreatedDate(new Date());
        message.setConversationId(String.format("%d_%d", 2, model.getActorId()));
        messageService.addMessage(message);

        String sendTo = model.getExt("to");
        Map<String ,Object> map = new HashMap<>();
        map.put("username", model.getExt("username"));
        map.put("text","这封电子邮件生成是由于另一个地址试图登录您的帐户。该地址在试图登录时输入了您正确的帐户名称和密码。");
        mailUtil.sendTemplateMail(sendTo,"您的头条账户：来自新电脑的访问", "emails/warn", map);
    }

    @Override
    public List<EventType> getSupportEventTypes() {
        return Arrays.asList(EventType.LOGIN);
    }
}
