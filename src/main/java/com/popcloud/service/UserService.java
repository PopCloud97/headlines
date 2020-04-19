package com.popcloud.service;

import com.popcloud.dao.LoginTicketDAO;
import com.popcloud.dao.UserDAO;
import com.popcloud.model.User;
import com.popcloud.model.LoginTicket;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import org.thymeleaf.util.StringUtils;

import javax.annotation.Resource;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import static java.util.UUID.randomUUID;

@Service
public class UserService {
    @Resource
    private UserDAO userDAO;

    @Resource
    private LoginTicketDAO loginTicketDAO;

    public User getUser(int id) {
        return userDAO.selectById(id);
    }

    public  Map<String, Object> register(String username, String password){
        //Map用来存放校验信息
        Map<String,Object> map = new HashMap<>();
        if(StringUtils.isEmpty(username)){
            map.put("msgname","用户名不能为空");
            return map;
        }
        if(StringUtils.isEmpty(password)){
            map.put("msgpwd","密码不能为空");
            return map;
        }
        User user = userDAO.selectByName(username);
        if(user!=null) {
            map.put("msgname","用户名已被注册");
            return map;
        }
        user = new User();
        user.setName(username);
        user.setSalt(randomUUID().toString().substring(0, 5));
        String head = String.format("http://images.nowcoder.com/head/%dt.png", new Random().nextInt(1000));
        user.setHeadUrl(head);
        String base = password+user.getSalt();
        user.setPassword(DigestUtils.md5DigestAsHex(base.getBytes()));
        userDAO.addUser(user);

        String ticket = addLoginTicket(user.getId());    //user.getId()==0,而数据库id自增,ticket和user对应不上
        map.put("ticket", ticket);
        return map;
    }

    public Map<String, Object> login(String username, String password) {
        Map<String,Object> map= new HashMap<>();
        if(StringUtils.isEmpty(username))
        {
            map.put("msgname","用户名不能为空");
            return map;
        }
        if(StringUtils.isEmpty(password))
        {
            map.put("msgpwd","密码不能为空");
            return map;
        }
        User user = userDAO.selectByName(username);
        if (user == null) {
            map.put("msgname", "用户名不存在");
            return map;
        }

        String base = password+user.getSalt();
        if (!DigestUtils.md5DigestAsHex(base.getBytes()).equals(user.getPassword())) {
            map.put("msgpwd", "密码不正确");
            return map;
        }
        map.put("userId", user.getId());
        String ticket = addLoginTicket(user.getId());
        map.put("ticket", ticket);
        return map;
    }

    private String addLoginTicket(int userId) {
        LoginTicket ticket = new LoginTicket();
        ticket.setUserId(userId);
        Date date = new Date();
        date.setTime(date.getTime() + 1000*3600*24);
        ticket.setExpired(date);
        ticket.setStatus(0);
        ticket.setTicket(randomUUID().toString().replaceAll("-", ""));
        loginTicketDAO.addTicket(ticket);
        return ticket.getTicket();
    }

    public void logout(String ticket) {
        loginTicketDAO.updateStatus(ticket, 1);
    }
}
