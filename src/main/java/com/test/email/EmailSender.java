package com.test.email;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.mail.outbound.MailSendingMessageHandler;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.messaging.MessageHandler;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.UnsupportedEncodingException;
import java.util.Properties;

@Configuration
public class EmailSender {
    @Value("${email.host}")
    private String emailHost;
    @Value("${email.username}")
    private String username;
    @Value("${email.password}")
    private String password;
    @Value("${email.port}")
    private int port;

    private final TemplateEngine templateEngine;

    public EmailSender(TemplateEngine templateEngine) {
        this.templateEngine = templateEngine;
    }

    @Bean
    public JavaMailSender mailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();

        mailSender.setHost(emailHost);
        mailSender.setUsername(username);
        mailSender.setPassword(password);
        mailSender.setPort(port);

        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        mailSender.setJavaMailProperties(props);

        return mailSender;
    }

    @Bean
    @ServiceActivator(inputChannel = "outboundMail")
    public MessageHandler outboundMailMessageHandler(JavaMailSender mailSender) {
        return new MailSendingMessageHandler(mailSender);
    }

    public void sendEmail(EmailDTO email) {
        Context ctx = new Context();
        ctx.setVariable("nome", email.name());
        ctx.setVariable("link", email.link());
        String html = templateEngine.process("email", ctx);

        String text = "Hello, " + email.name() + "! Your registration was received. Confirm your account: " + email.link();

        try {
            MimeMessage mime = mailSender().createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mime, true, "UTF-8");
            helper.setFrom(username, "Email Sender App");
            helper.setTo(email.to());
            helper.setSubject("Registration Confirmation");
            helper.setText(text, html);
            mailSender().send(mime);
        } catch (MailException | MessagingException | UnsupportedEncodingException ex) {
            ex.printStackTrace();
        }
    }
}