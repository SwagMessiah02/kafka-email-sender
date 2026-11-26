package com.test.email;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.KafkaMessageListenerContainer;
import java.util.Map;

@Configuration
public class KafkaListenerConfig {
    @Value("${kafka.config.host}")
    private String host;
    private final EmailListener emailListener;

    public KafkaListenerConfig(EmailListener emailListener) {
        this.emailListener = emailListener;
    }

    @Bean
    public KafkaMessageListenerContainer<String, EmailDTO> kafkaMessageListenerContainer() {
        ContainerProperties props = new ContainerProperties("email-input");
        props.setMessageListener(this.emailListener);

        Map<String, Object> coprops =
                Map.of(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, host,
                        ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest",
                        ConsumerConfig.GROUP_ID_CONFIG, "email-app");

        DefaultKafkaConsumerFactory<String, EmailDTO> cf =
                new DefaultKafkaConsumerFactory<>(coprops, new StringDeserializer(), new JsonDeserializer<>(EmailDTO.class));
        KafkaMessageListenerContainer<String, EmailDTO> container =
                new KafkaMessageListenerContainer<>(cf, props);

        return container;
    }
}