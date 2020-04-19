package com.popcloud.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class News {
    private int id;
    private String title;
    private String link;
    private String image;
    private int likeCount;
    private int commentCount;
    private Date createdDate;
    private int userId;
    private boolean sameDate = true;  //记录是否同一天的新闻
}
