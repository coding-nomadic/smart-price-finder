package com.example.product.price.configs;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;
import java.time.OffsetDateTime;

public class DateOnlyDeserializer extends JsonDeserializer<String> {
    @Override
    public String deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JsonProcessingException {
        String isoDateTime = jsonParser.getText();
        if (isoDateTime == null || isoDateTime.isEmpty()) return null;
        return OffsetDateTime.parse(isoDateTime).toLocalDate().toString();
    }
}