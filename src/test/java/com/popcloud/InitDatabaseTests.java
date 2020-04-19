package com.popcloud;


import com.popcloud.dao.CommentDAO;
import com.popcloud.dao.LoginTicketDAO;
import com.popcloud.dao.NewsDAO;
import com.popcloud.dao.UserDAO;
import com.popcloud.model.Comment;
import com.popcloud.model.LoginTicket;
import com.popcloud.model.News;
import com.popcloud.model.User;
import com.popcloud.util.CommonUtil;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.util.Date;
import java.util.Random;

@RunWith(SpringRunner.class)
@SpringBootTest
@Sql("/init-schema.sql")
public class InitDatabaseTests {
    @Autowired
    UserDAO userDAO;

    @Autowired
    NewsDAO newsDAO;

    @Autowired
    LoginTicketDAO loginTicketDAO;

    @Autowired
    CommentDAO commentDAO;

    @Test
    public void initData() {
        Random random = new Random();
        for (int i = 0; i < 11; ++i) {
            User user = new User();
            user.setHeadUrl(String.format("http://images.nowcoder.com/head/%dt.png", random.nextInt(1000)));
            user.setName(String.format("用户 %d", i));
            user.setPassword("");
            user.setSalt("");
            userDAO.addUser(user);

            News news = new News();
            news.setCommentCount(3);
            Date date = new Date();
            date.setTime(date.getTime() + 1000*3600*5*i);
            news.setCreatedDate(date);
            news.setImage(String.format("http://images.nowcoder.com/head/%dm.png", random.nextInt(1000)));
            news.setLikeCount(0);
            news.setUserId(i+1);
            news.setTitle(String.format("资讯标题 %d", i));
            news.setLink(String.format("http://www.nowcoder.com/%d.html", i));
            newsDAO.addNews(news);

            for (int j=0; j<3;++j) {
                Comment comment = new Comment();
                comment.setUserId(i+1);
                comment.setEntityId(news.getId());   //addNews(news)通过@Option返回自增id (否则newsId=null)
                comment.setEntityType(CommonUtil.ENTITY_NEWS);
                comment.setStatus(0);
                comment.setCreatedDate(new Date());
                comment.setContent("第 "+j+" 名");
                commentDAO.addcomment(comment);
            }

            user.setPassword("newpassword");
            userDAO.updatePassword(user);   //addUser(user)通过@Option返回自增id (否则userId=null)

            LoginTicket ticket = new LoginTicket();
            ticket.setStatus(0);
            ticket.setUserId(i+1);
            ticket.setExpired(date);
            ticket.setTicket(String.format("TICKET%d", i+1));
            loginTicketDAO.addTicket(ticket);

            loginTicketDAO.updateStatus(ticket.getTicket(), 2);
        }

        Assert.assertEquals("newpassword", userDAO.selectById(1).getPassword());   //检查密码是否更新
        userDAO.deleteById(1);
        Assert.assertNull(userDAO.selectById(1));

        Assert.assertEquals(1, loginTicketDAO.selectByTicket("TICKET1").getUserId());
        Assert.assertEquals(2, loginTicketDAO.selectByTicket("TICKET1").getStatus());

        Assert.assertNotNull(commentDAO.selectByEntity(1,CommonUtil.ENTITY_NEWS).get(0));
    }

}
