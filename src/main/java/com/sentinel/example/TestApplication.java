package com.sentinel.example;

import com.alibaba.csp.sentinel.annotation.aspectj.SentinelResourceAspect;
import com.eeeffff.hasentinel.web.WebConfigManager;
import com.sentinel.example.interceptor.RequestOriginParserDefinition;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
//@ComponentScan(basePackages = {"com.sentinel.example","com.eeeffff.hasentinel"})
public class TestApplication {
    public static void main(String[] args) {
        WebConfigManager.setOriginParser(new RequestOriginParserDefinition());
        SpringApplication.run(TestApplication.class, args);
    }

    @Bean
    public SentinelResourceAspect sentinelResourceAspect() {
        return new SentinelResourceAspect();
    }


}