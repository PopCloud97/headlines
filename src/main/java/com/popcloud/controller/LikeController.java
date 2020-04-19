package com.popcloud.controller;

import com.popcloud.async.EventModel;
import com.popcloud.async.EventProducer;
import com.popcloud.async.EventType;
import com.popcloud.model.HostHolder;
import com.popcloud.model.News;
import com.popcloud.service.LikeService;
import com.popcloud.service.NewsService;
import com.popcloud.util.CommonUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class LikeController {
    @Autowired
    LikeService likeService;

    @Autowired
    HostHolder hostHolder;

    @Autowired
    NewsService newsService;

    @Autowired
    EventProducer eventProducer;

    @RequestMapping("/like")
    @ResponseBody
    public String like(@RequestParam("newsId") int newsId) {
        long likeCount = likeService.like(hostHolder.getUser().getId(), CommonUtil.ENTITY_NEWS, newsId);
        // 更新喜欢数
        News news = newsService.getById(newsId);
        newsService.updateLikeCount(newsId, (int) likeCount);
        eventProducer.fireEvent(new EventModel(EventType.LIKE)
                .setEntityOwnerId(news.getUserId())
                .setActorId(hostHolder.getUser().getId()).setEntityId(newsId));
        return CommonUtil.getJSONString(0, String.valueOf(likeCount));
    }

    @RequestMapping("/dislike")
    @ResponseBody
    public String dislike(@RequestParam("newsId") int newsId) {
        long likeCount = likeService.disLike(hostHolder.getUser().getId(), CommonUtil.ENTITY_NEWS, newsId);
        // 更新喜欢数
        newsService.updateLikeCount(newsId, (int) likeCount);
        return CommonUtil.getJSONString(0, String.valueOf(likeCount));
    }
}
