package com.dev.org.config;

import com.dev.org.client.AstroClient;
import com.dev.org.client.impl.AstroClientImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class RestClientConfig {

    private static final Logger log = LoggerFactory.getLogger(RestClientConfig.class);

    private final RestClient.Builder builder;

    public RestClientConfig(RestClient.Builder builder) {
        this.builder = builder.requestInterceptor(new LoggingInterceptor());
    }

    @Bean
    public AstroClient productClient() {
        return new AstroClientImpl(buildRestClient("http://api.open-notify.org"));
    }

    private RestClient buildRestClient(String baseUrl) {
        log.info("base url: {}", baseUrl);
        return this.builder.baseUrl(baseUrl).build();
    }
}
