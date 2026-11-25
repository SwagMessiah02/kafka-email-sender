package com.test.email;

import org.apache.kafka.common.serialization.Deserializer;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

public class JsonDeserializer<T> implements Deserializer<T> {
    private final ObjectMapper mapper = new ObjectMapper();
    private final Class<T> type;

    public JsonDeserializer(Class<T> type) {
        this.type = type;
    }

    @Override
    public T deserialize(String topic, byte[] data) {
        if(topic != null && data != null) {
            try {
                return mapper.readValue(data, type);
            } catch (JacksonException ex) {
                ex.printStackTrace();
            }
        }

        return null;
    }
}
