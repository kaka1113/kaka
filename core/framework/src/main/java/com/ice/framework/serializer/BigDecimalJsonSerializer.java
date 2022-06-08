package com.ice.framework.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.ice.framework.util.ArithUtil;

import java.io.IOException;
import java.math.BigDecimal;

/**
 * @author hubo
 * @since 2020/3/6
 */
public class BigDecimalJsonSerializer extends JsonSerializer<BigDecimal> {

    @Override
    public void serialize(BigDecimal value, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        if (null != value) {
            jsonGenerator.writeString(ArithUtil.formatTowScale(value));
        }
    }
}
