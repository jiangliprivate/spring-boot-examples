package com.neo.config;

import org.springframework.boot.web.embedded.tomcat.TomcatContextCustomizer;
import org.springframework.boot.web.embedded.tomcat.TomcatProtocolHandlerCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ThreadFactory;

/**
 *
 * Tomcat 配置
 *
 */
@Configuration
public class TomcatConfiguration {
    /*@Bean
    public TomcatProtocolHandlerCustomizer<?> protocolHandlerVirtualThreadExecutorCustomizer() {

        return protocolHandler -> {
            // 使用虚拟线程来处理每一个请求
            protocolHandler.setExecutor(Executors.newVirtualThreadPerTaskExecutor());
        };

        return protocolHandler -> {
            // 创建 OfVirtual，指定虚拟线程名称的前缀，以及线程编号起始值
            OfVirtual ofVirtual = Thread.ofVirtual().name("virtualthread#", 1);
            // 获取虚拟线程池工厂
            ThreadFactory factory = ofVirtual.factory();
            // 通过该工厂，创建 ExecutorService
            protocolHandler.setExecutor(Executors.newThreadPerTaskExecutor(factory));
        };
    }*/
}