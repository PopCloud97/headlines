package com.popcloud.controller;

import com.popcloud.model.HostHolder;
import com.popcloud.model.News;
import com.popcloud.model.ViewObject;
import com.popcloud.service.LikeService;
import com.popcloud.service.NewsService;
import com.popcloud.service.UserService;
import com.popcloud.util.CommonUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import sun.misc.Contended;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Controller
public class HomeController {
    @Resource
    private NewsService newsService;

    @Resource
    private UserService userService;

    @Resource
    private HostHolder hostHolder;

    @Autowired
    private LikeService likeService;

    private List<ViewObject> getNews(int userId, int offset, int limit) {
        List<News> newsList = newsService.getLatestNews(userId, offset, limit);
        int localUserId = hostHolder.getUser() != null ? hostHolder.getUser().getId() : 0;
        List<ViewObject> vos = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String tmpDate = "0000-00-00";
        for (News news : newsList) {
            ViewObject vo = new ViewObject();
            vo.set("news", news);
            vo.set("user", userService.getUser(news.getUserId()));
            //看和上条新闻是否为同一天
            if (!sdf.format(news.getCreatedDate()).equals(tmpDate)) {
                news.setSameDate(false);
            }
            tmpDate = sdf.format(news.getCreatedDate());
            if (localUserId != 0) {
                vo.set("like", likeService.getLikeStatus(localUserId, CommonUtil.ENTITY_NEWS, news.getId()));
            } else {
                vo.set("like", 0);
            }
            vos.add(vo);
        }
        return vos;
    }

    @RequestMapping(path = {"/", "/index"})
    public String index(@RequestParam(value = "pop", defaultValue = "0") int pop, Model model) {
        if (hostHolder.getUser() != null) {
            pop = 0;   //pop==1时弹出登录页
        }
        model.addAttribute("vos", getNews(0,0,10));
        model.addAttribute("pop", pop);
        return "home";
    }

    @RequestMapping("/user/{userId}")
    public String userIndex(Model model, @PathVariable("userId") int userId) {
        model.addAttribute("vos", getNews(userId, 0, 10));
        return "home";
    }
}
