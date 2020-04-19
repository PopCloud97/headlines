package com.popcloud.service;

import com.popcloud.dao.NewsDAO;
import com.popcloud.model.News;
import com.popcloud.util.CommonUtil;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Objects;

import static java.util.UUID.randomUUID;

@Service
public class NewsService {
    @Resource
    private NewsDAO newsDAO;

    public List<News> getLatestNews(int userId, int offset, int limit) {
        return newsDAO.selectByUserIdAndOffset(userId, offset, limit);
    }
    public News getById(int id) {
        return newsDAO.selectbyId(id);
    }

    public int addNews(News news) {
        return newsDAO.addNews(news);
    }

    public  int  updateCommentCount(int id,int count) {
        return newsDAO.updateCommentCount(id,count);
    }

    public int updateLikeCount(int id, int count) {
        return newsDAO.updateLikeCount(id, count);
    }

    public String saveImage(MultipartFile file) throws IOException {
        int dotPos = Objects.requireNonNull(file.getOriginalFilename()).lastIndexOf(".");
        if (dotPos < 0) {
            return null;
        }
        String fileExt = file.getOriginalFilename().substring(dotPos+1).toLowerCase();
        if(!CommonUtil.isFileAllowed(fileExt)) {    //判断扩展名
            return null;
        }
        String fileName = randomUUID().toString().replaceAll("-","") + "." + fileExt;
        Files.copy(file.getInputStream(), new File(CommonUtil.IMAGE_DIR + fileName).toPath(),
                StandardCopyOption.REPLACE_EXISTING);
        return CommonUtil.TOUTIAO_DOMAIN + "image?name=" + fileName;
    }
}
