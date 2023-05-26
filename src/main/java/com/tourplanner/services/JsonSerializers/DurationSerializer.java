package com.tourplanner.services.JsonSerializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.time.Duration;

public class DurationSerializer extends JsonSerializer<Duration> {

    @Override
    public void serialize(Duration duration, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        gen.writeStartObject();
        gen.writeNumberField("hours", duration.getSeconds() / 3600);
        gen.writeNumberField("minutes", (duration.getSeconds() % 3600) / 60);
        gen.writeEndObject();
    }
}
