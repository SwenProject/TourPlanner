package com.tourplanner.services.JsonSerializers;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.node.IntNode;

import java.io.IOException;
import java.time.Duration;

public class DurationDeserializer extends JsonDeserializer<Duration> {

    @Override
    public Duration deserialize(JsonParser parser, DeserializationContext ctx) throws IOException, JacksonException {

        JsonNode node = parser.getCodec().readTree(parser);
        Integer hours = (Integer) ((IntNode) node.get("hours")).numberValue();
        Integer minutes = (Integer) ((IntNode) node.get("minutes")).numberValue();

        return Duration.ofHours(hours).plusMinutes(minutes);
    }
}
