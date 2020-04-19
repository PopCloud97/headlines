package com.popcloud.controller;

import com.popcloud.model.Comment;
import com.popcloud.model.HostHolder;
import com.popcloud.model.News;
import com.popcloud.model.ViewObject;
import com.popcloud.service.*;
import com.popcloud.util.CommonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Controller
public class NewsController {
    private static final Logger logger = LoggerFactory.getLogger(NewsController.class);

    @Resource
    NewsService newsService;

    @Resource
    UserService userService;

    @Resource
    QiniuService qiniuService;

    @Resource
    HostHolder hostHolder;

    @Resource
    CommentService commentService;

    @Resource
    LikeService likeService;

    @PostMapping("/user/addNews/")
    @ResponseBody
    public String addNews(@RequestParam("image") String image,
                          @RequestParam("title") String title,
                          @RequestParam("link") String link) {
        try {
            News news = new News();
            news.setCreatedDate(new Date());
            news.setTitle(title);
            news.setImage(image);
            news.setLink(link);
            if (hostHolder.getUser() != null) {
                news.setUserId(hostHolder.getUser().getId());
            } else {
                // 设置一个匿名用户
                news.setUserId(3);
            }
            newsService.addNews(news);
            return CommonUtil.getJSONString(0);
        } catch (Exception e) {
            logger.error("添加资讯失败" + e.getMessage());
            return CommonUtil.getJSONString(1, "发布失败");
        }
    }

    @PostMapping("/uploadImage/")
    @ResponseBody
    public String uploadImage(@RequestParam("file") MultipartFile file) {
        try {
//            String fileUrl = newsService.saveImage(file);   //local
            String fileUrl = qiniuService.saveImage(file);  //qiniucloud
            if (fileUrl == null){
                return CommonUtil.getJSONString(1,"上传图片失败");
            }
            return CommonUtil.getJSONString(0, fileUrl);
        } catch (Exception e) {
            logger.error("上传图片失败"+e.getMessage());
            return CommonUtil.getJSONString(1,"上传失败");
        }
    }

    @GetMapping("/image")
    @ResponseBody
    public void getImage(@RequestParam("name") String imageName, HttpServletResponse response) {
        try {
            response.setContentType("image/jpeg");
            StreamUtils.copy(new FileInputStream(new File(
                    CommonUtil.IMAGE_DIR + imageName)), response.getOutputStream());
        } catch (Exception e) {
            logger.error("读取图片错误" + imageName + e.getMessage());
        }
    }

    @GetMapping("news/{newsId}")
    public String newsDetail(@PathVariable("newsId") int newsId, Model model){
        News news = newsService.getById(newsId);
        if (news!=null){
            int localUserId = hostHolder.getUser() != null ? hostHolder.getUser().getId() : 0;
            if (localUserId != 0) {
                model.addAttribute("like", likeService.getLikeStatus(localUserId, CommonUtil.ENTITY_NEWS, news.getId()));
            } else {
                model.addAttribute("like", 0);
            }
            List<Comment> comments = commentService.getCommentsByEntity(news.getId(), CommonUtil.ENTITY_NEWS);
            List<ViewObject> commentVOs = new ArrayList<>();
            for (Comment comment : comments) {
                ViewObject commentVO = new ViewObject();
                commentVO.set("comment", comment);
                commentVO.set("user", userService.getUser(comment.getUserId()));
                commentVOs.add(commentVO);
            }
            model.addAttribute("comments", commentVOs);
        }
        model.addAttribute("news", news);
        model.addAttribute("owner", userService.getUser(news.getUserId()));
        return "detail";
    }

    @PostMapping("/addComment")
    public String addComment(@RequestParam("newsId") int newsId,
                             @RequestParam("content") String content){
        try {
            Comment comment = new Comment();
            comment.setUserId(hostHolder.getUser().getId());
            comment.setContent(content);
            comment.setEntityType(CommonUtil.ENTITY_NEWS);
            comment.setEntityId(newsId);
            comment.setCreatedDate(new Date());
            comment.setStatus(0);
            commentService.addComment(comment);

            // 更新评论数量，以后用异步实现
            int count = commentService.getCommentCount(comment.getEntityId(), comment.getEntityType());
            newsService.updateCommentCount(newsId, count);

        } catch (Exception e) {
            logger.error("提交评论错误" + e.getMessage());
        }
        return "redirect:/news/" + newsId;
    }

    //TODO
    @GetMapping("/deleteComment/{entityType}/{entityId}")
    public String deleteComment(@PathVariable int entityId, @PathVariable int entityType){
        commentService.deleteComment(entityId, entityType);
        return "redirect:/news/" + entityId;
    }
}
