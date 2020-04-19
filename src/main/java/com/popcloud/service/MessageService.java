package com.popcloud.service;

import com.popcloud.dao.MessageDAO;
import com.popcloud.model.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class MessageService {
    @Resource
    private MessageDAO messageDAO;

    public int addMessage(Message message) {
        return messageDAO.addMessage(message);
    }

    public List<Message> getConversationList(int userId, int offset, int limit) {
        // 每个conversation的消息总条数存在id里
        return messageDAO.getConversationList(userId, offset, limit);
    }

    public List<Message> getConversationDetail(String conversationId, int offset, int limit) {
        // 每个conversation的消息总条数存在id里
        return messageDAO.getConversationDetail(conversationId, offset, limit);
    }

    public int getUnreadCount(int userId, String conversationId) {
        return messageDAO.getConversationUnReadCount(userId, conversationId);
    }

    public void resetUnreadCount(int userId, String conversationId) {
        messageDAO.resetConversationUnReadCount(userId, conversationId);
    }

    public int getConversationCout(String conversationId) {
        return messageDAO.getConversationCount(conversationId);
    }

    public void deleteMessage(int messageId) {
        messageDAO.deleteMessage(messageId);
    }

    public String getConversationByMessage(int messageId) {
        return messageDAO.getConversationByMessage(messageId);
    }

    public void deleteConversation(String conversationId) {
        messageDAO.deleteConversation(conversationId);
    }
}
