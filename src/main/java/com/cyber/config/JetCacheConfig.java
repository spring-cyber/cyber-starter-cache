package com.cyber.config;

import com.alicp.jetcache.anno.JsonSerialPolicy;
import com.alicp.jetcache.anno.support.SpringConfigProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.function.Function;

@Configuration
public class JetCacheConfig {

    @Bean
    public JsonSerialPolicy jsonSerialPolicy() {
        return new JsonSerialPolicy();
    }

    @Bean
    public SpringConfigProvider springConfigProvider() {
        return new SpringConfigProvider() {
            @Override
            public Function<byte[], Object> parseValueDecoder(String valueDecoder) {
                if (valueDecoder.equalsIgnoreCase("json")) {
                    return jsonSerialPolicy().decoder();
                }
                return super.parseValueDecoder(valueDecoder);
            }

            @Override
            public Function<Object, byte[]> parseValueEncoder(String valueEncoder) {
                if (valueEncoder.equalsIgnoreCase("json")) {
                    return jsonSerialPolicy().encoder();
                }
                return super.parseValueEncoder(valueEncoder);
            }
        };
    }
}
