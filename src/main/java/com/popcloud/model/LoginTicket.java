package com.popcloud.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginTicket {
    private int id;
    private int userId;
    private Date expired;
    private int status; // 0有效，1无效
    private String ticket;
}
