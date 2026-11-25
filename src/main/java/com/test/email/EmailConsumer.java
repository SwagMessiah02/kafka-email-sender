package com.test.email;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import tools.jackson.core.JacksonException;
import tools.jackson.core.json.JsonReadFeature;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;
import java.time.Duration;
import java.util.List;
import java.util.Optional;
import java.util.Properties;

@Component
public class EmailConsumer {
    @Value("${kafka.config.host}")
    private String host;

    private final EmailSender emailSender;
    private final Logger log = LoggerFactory.getLogger(EmailConsumer.class);

    public EmailConsumer(EmailSender emailSender) {
        this.emailSender = emailSender;
    }

    public void readEmail() throws Exception {
        Properties props = new Properties();

        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, host);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "email-app");

        try(var consumer = new KafkaConsumer<String, EmailDTO>(
                props,
                new StringDeserializer(),
                new JsonDeserializer<>(EmailDTO.class))) {
            consumer.subscribe(List.of("email-input"));

            while(true) {
                for (var e : consumer.poll(Duration.ofSeconds(1))) {
                    try {
                        EmailDTO email = e.value();
                        if(email.to() != null && email.name() != null && email.link() != null) {
                            log.info("EMAIL: {}, NAME: {}, LINK: {}", email.to(), email.name(), email.link());
                            this.emailSender.sendEmail(email);
                        }
                    } catch (JacksonException | NullPointerException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        }
    }
}