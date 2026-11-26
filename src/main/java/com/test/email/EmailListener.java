package com.test.email;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.listener.MessageListener;
import org.springframework.stereotype.Component;
import tools.jackson.core.JacksonException;

@Component
public class EmailListener implements MessageListener<String, EmailDTO> {
    private final Logger log = LoggerFactory.getLogger(EmailListener.class);
    private final EmailSender emailSender;

    public EmailListener(EmailSender emailSender) {
        this.emailSender = emailSender;
    }

    @Override
    public void onMessage(ConsumerRecord<String, EmailDTO> consumer) {
        try {
            EmailDTO email = consumer.value();
            if (email.to() != null && email.name() != null && email.link() != null) {
                log.info("EMAIL: {}, NAME: {}, LINK: {}", email.to(), email.name(), email.link());
                this.emailSender.sendEmail(email);
            }
        } catch (JacksonException | NullPointerException ex) {
            ex.printStackTrace();
        }
    }
}