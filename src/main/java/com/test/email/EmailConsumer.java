package com.test.email;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
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

    @Value("${email.app.redirect.link}")
    private String link;

    private EmailSender emailSender;
    private final Logger log = LoggerFactory.getLogger(EmailConsumer.class);

    public EmailConsumer(EmailSender emailSender) {
        this.emailSender = emailSender;
    }

    public void readEmail() throws Exception {
        Properties props = new Properties();

        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, host);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "email-app");

        JsonMapper mapper = JsonMapper.builder()
                .enable(JsonReadFeature.ALLOW_SINGLE_QUOTES)
                .enable(JsonReadFeature.ALLOW_UNQUOTED_PROPERTY_NAMES)
                .build();

        try(var consumer = new KafkaConsumer<String, String>(props)) {
            consumer.subscribe(List.of("email-input"));

            while(true) {
                for (var e : consumer.poll(Duration.ofSeconds(1))) {
                    try {
                        JsonNode node = mapper.readTree(e.value());
                        Assert.notNull(node, "Null node");
                        String to = Optional.of(node.get("to"))
                                .orElseThrow(() -> new NullPointerException("Null property")).asString();
                        String name = Optional.of(node.get("name"))
                                .orElseThrow(() -> new NullPointerException("Null property")).asString();
                        log.info("VALUE: {}, NAME: {}", to, name);
                        this.emailSender.sendEmail(to, name, this.link);
                    } catch (JacksonException | NullPointerException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}