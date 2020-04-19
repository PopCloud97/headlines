package com.popcloud.controller;

import com.popcloud.async.EventModel;
import com.popcloud.async.EventProducer;
import com.popcloud.async.EventType;
import com.popcloud.service.UserService;
import com.popcloud.util.CommonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

@Controller
public class LoginController {
    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);

    @Resource
    UserService userService;

    @Resource
    EventProducer eventProducer;

    @RequestMapping("/reg/")
    @ResponseBody
    public String reg(@RequestParam("username") String username, @RequestParam("password") String password,
                      @RequestParam(value="rember", defaultValue = "0") int rememberme, HttpServletResponse response) {
        try {
            Map<String, Object> map = userService.register(username, password);
            if (map.containsKey("ticket")) {
                Cookie cookie = new Cookie("ticket", map.get("ticket").toString());
                cookie.setPath("/");
                if (rememberme > 0) {
                    cookie.setMaxAge(3600*24*5);
                }
                response.addCookie(cookie);
                return CommonUtil.getJSONString(0, "注册成功");
            } else {
                return CommonUtil.getJSONString(1, map);
            }

        } catch (Exception e) {
            logger.error("注册异常" + e.getMessage());
            return CommonUtil.getJSONString(1, "注册异常");
        }
    }

    @RequestMapping("/login/")
    @ResponseBody
    public String login(@RequestParam("username") String username, @RequestParam("password") String password,
                        @RequestParam(value="rember", defaultValue = "0") int rememberme, HttpServletResponse response) {
        try {
            Map<String, Object> map = userService.login(username, password);
            if (map.containsKey("ticket")) {
                Cookie cookie = new Cookie("ticket", map.get("ticket").toString());
                cookie.setPath("/");
                if (rememberme > 0) {
                    cookie.setMaxAge(3600*24*5);
                }
                response.addCookie(cookie);
                //发邮件事件队列
                eventProducer.fireEvent(new EventModel(EventType.LOGIN).setActorId((int) map.get("userId"))
                        .setExt("to", username).setExt("username", username));
                return CommonUtil.getJSONString(0, "登录成功");
            } else {
                return CommonUtil.getJSONString(1, map);
            }

        } catch (Exception e) {
            logger.error("登录异常" + e.getMessage());
            return CommonUtil.getJSONString(1, "登录异常");
        }
    }

    @RequestMapping("/logout")
    public String logout(@CookieValue("ticket") String ticket) {
        userService.logout(ticket);
        return "redirect:/";
    }

}
