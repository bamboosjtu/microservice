package me.bamboo.notification.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.bamboo.common.notification.EmailTriggered;
import me.bamboo.common.notification.NotificationDomainEvent;

@Service
@Slf4j
@RequiredArgsConstructor
public class Email163Service {
    private final JavaMailSender mailSender;
	
	private final TemplateEngine templateEngine;

    public void sendSimpleMail(NotificationDomainEvent event) {
    	EmailTriggered emailTriggered = (EmailTriggered) event.getPayload();
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("bamboo_pengkezhu@163.com");
        message.setTo("bamboopengkezhu@hotmail.com");
        message.setSubject("图书管理系统邮件");
        message.setText(emailTriggered.toString());
        mailSender.send(message);
    }
    
    public void sendEmailWithHtmlTemplate(NotificationDomainEvent event) {        
        EmailTriggered emailTriggered = (EmailTriggered) event.getPayload();
        
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "UTF-8");
        Context context = new Context();
        context.setVariable("firstName", emailTriggered.getFirstName());
        context.setVariable("lastName", emailTriggered.getLastName());
        context.setVariable("email", emailTriggered.getEmail());
        context.setVariable("title", emailTriggered.getTitle());
        context.setVariable("author", emailTriggered.getAuthor());

        try {
        	helper.setFrom("bamboo_pengkezhu@163.com");
            helper.setTo("bamboopengkezhu@hotmail.com");
            helper.setSubject("【最新优惠】图书" + emailTriggered.getTitle() + "已经上架");
            String htmlContent = this.templateEngine.process("email-template", context);
            helper.setText(htmlContent, true);
            mailSender.send(mimeMessage);
        } catch (MessagingException e) {
            log.error("邮件发送失败: {}", e.getMessage());
        }
    }

}
