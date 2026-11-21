package com.test.email;

import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Component
public class CommandLine implements CommandLineRunner {
    private EmailConsumer consumer;

    public CommandLine(EmailConsumer consumer) {
        this.consumer = consumer;
    }

    @Override
    public void run(String... args) throws Exception {
        consumer.readEmail();
    }
}