package com.popcloud.controller;

import com.popcloud.model.HostHolder;
import com.popcloud.model.Message;
import com.popcloud.model.User;
import com.popcloud.model.ViewObject;
import com.popcloud.service.MessageService;
import com.popcloud.service.UserService;
import com.popcloud.util.CommonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Controller
public class MessageController {
    private static final Logger logger = LoggerFactory.getLogger(MessageController.class);

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private UserService userService;

    @Autowired
    private MessageService messageService;

    @GetMapping("/msg/detail")
    public String conversationDetail(Model model, @RequestParam("conversationId") String conversationId) {
        try {
            List<ViewObject> messages = new ArrayList<ViewObject>();
            List<Message> conversationList = messageService.getConversationDetail(conversationId, 0, 10);
            for (Message msg : conversationList) {
                ViewObject vo = new ViewObject();
                vo.set("message", msg);
                User user = userService.getUser(msg.getFromId());
                if (user == null) {
                    continue;
                }
                vo.set("headUrl", user.getHeadUrl());
                vo.set("userName", user.getName());
                vo.set("user",user);
                messages.add(vo);
            }
            model.addAttribute("messages", messages);
            messageService.resetUnreadCount(hostHolder.getUser().getId(), conversationId);
            return "letterDetail";
        } catch (Exception e) {
              logger.error("获取站内信列表失败" + e.getMessage());
        }
        return "letterDetail";
    }

    @GetMapping("/msg/list")
    public String conversationList(Model model) {
        try {
            int localUserId = hostHolder.getUser().getId();
            List<ViewObject> conversations = new ArrayList<>();
            List<Message> conversationList = messageService.getConversationList(localUserId, 0, 10);
            for (Message msg : conversationList) {
                ViewObject vo = new ViewObject();
                vo.set("conversation", msg);
                int targetId = msg.getFromId() == localUserId ? msg.getToId() : msg.getFromId();
                User user = userService.getUser(targetId);
                vo.set("user", user);
                vo.set("conversationsCount",messageService.getConversationCout(msg.getConversationId()));
                vo.set("unread", messageService.getUnreadCount(localUserId, msg.getConversationId()));
                conversations.add(vo);
            }
            model.addAttribute("conversations", conversations);
            return "letter";
        } catch (Exception e) {
            logger.error("获取站内信列表失败" + e.getMessage());
        }
        return "letter";
    }

    @RequestMapping("/msg/addMessage")
    @ResponseBody
    public String addMessage(@RequestParam("fromId") int fromId,
                             @RequestParam("toId") int toId,
                             @RequestParam("content") String content) {
//        fromId=hostHolder.getUser().getId();
        Message msg = new Message();
        msg.setContent(content);
        msg.setCreatedDate(new Date());
        msg.setToId(toId);
        msg.setFromId(fromId);
        msg.setConversationId(fromId < toId ? String.format("%d_%d", fromId, toId) : String.format("%d_%d", toId, fromId));
        messageService.addMessage(msg);
        return CommonUtil.getJSONString(0);
    }

    @GetMapping("/msg/deletemsg/{messageId}")
    public String deleteMessage(@PathVariable("messageId") int messageId) {
        String conversationId = messageService.getConversationByMessage(messageId);
        messageService.deleteMessage(messageId);
        return "redirect:/msg/detail?conversationId=" + conversationId;
    }

    @GetMapping("/msg/delconversation/{conversationId}")
    public String deleteConversation(@PathVariable("conversationId") String conversationId) {
        messageService.deleteConversation(conversationId);
        return "redirect:/msg/list";
    }
}
