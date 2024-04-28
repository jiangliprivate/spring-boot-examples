package com.neo.config;

import com.google.gson.*;
import org.springframework.boot.autoconfigure.gson.GsonBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Configuration
public class GsonConfiguration {

    @Bean
    public GsonBuilder gsonBuilder(List<GsonBuilderCustomizer> customizers) { // customizers 是读取到的配置文件中关于 Gson 的配置

        // 创建 GsonBuilder
        GsonBuilder builder = new GsonBuilder();

        // 加载配置文件中的配置属性
        customizers.forEach((c) -> c.customize(builder));

        /**
         * 编程式自定义
         */
        DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        // LocalDateTime 类型的格式化
        builder.registerTypeHierarchyAdapter(LocalDateTime.class, new JsonSerializer<LocalDateTime>() {
            @Override
            public JsonElement serialize(LocalDateTime src, Type typeOfSrc, JsonSerializationContext context) {
                return new JsonPrimitive(DATE_TIME_FORMATTER.format(src));
            }
        });

        return builder;
    }
}