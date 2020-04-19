package com.popcloud.util;

import javafx.util.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;

import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeUtility;
import java.io.File;
import java.util.List;
import java.util.Map;

@Service
public class MailUtil {
    private String emailSendFrom = "头条客服 <15600992150@163.com>";

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private SpringTemplateEngine thymeleaf;

    //发送简单邮件
    public void sendSimpleMail(String sendTo, String title, String content) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(emailSendFrom);
        message.setTo(sendTo);
        message.setSubject(title);
        message.setText(content);
        mailSender.send(message);

    }

    //发送带附件的简单邮件
    public void sendAttachmentsMail(String sendTo, String title, String content, List<Pair<String, File>> attachments) {
        MimeMessage mimeMessage = mailSender.createMimeMessage();

        try {
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage,true);
            helper.setFrom(emailSendFrom);
            helper.setTo(sendTo);
            helper.setSubject(title);
            helper.setText(content);

            for(Pair<String, File> pair : attachments){
                helper.addAttachment(pair.getKey(),new FileSystemResource(pair.getValue()));
            }

            mailSender.send(mimeMessage);
        } catch (Exception e){
            System.out.println("发送带附件邮件出现错误: " + e.toString());
            throw  new RuntimeException(e);
        }
    }

    //发送带附件的Inline邮件
    public void sendInlineMail(String sendTo, String title, String html, List<Pair<String, File>> attachments) {
        MimeMessage mimeMessage = mailSender.createMimeMessage();

        try {
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage,true);
            helper.setFrom(emailSendFrom);
            helper.setTo(sendTo);
            helper.setSubject(title);
            helper.setText(html,true);

            for(Pair<String,File> pair : attachments){
                helper.addAttachment(pair.getKey(),new FileSystemResource(pair.getValue()));
            }

            mailSender.send(mimeMessage);
        } catch (Exception e){
            System.out.println("发送带附件的Inline邮件出现错误: " + e.toString());
            throw  new RuntimeException(e);
        }
    }

    //发送带附件的模板邮件
    public void sendTemplateMail(String sendTo, String title, String template, Map<String, Object> content) {
        MimeMessage mimeMessage = mailSender.createMimeMessage();

        try {
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage,true);
            helper.setFrom(emailSendFrom);
            helper.setTo(sendTo);
            helper.setSubject(title);

            Context ctx = new Context();
            for(Map.Entry<String,Object> entry: content.entrySet()){
                ctx.setVariable(entry.getKey(), entry.getValue());
            }
            String emailText = thymeleaf.process(template, ctx);
            helper.setText(emailText,true);

            mailSender.send(mimeMessage);
        } catch (Exception e){
            System.out.println("发送带模板的邮件出现错误: " + e.toString());
            throw new RuntimeException(e);
        }
    }
}
