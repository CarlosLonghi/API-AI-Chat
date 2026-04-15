package br.com.carloslonghi.api_chat_ai.config;

import org.springframework.boot.restclient.RestClientCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.JdkClientHttpRequestFactory;

import java.time.Duration;

@Configuration
public class AIConfig {

    @Bean
    public RestClientCustomizer restClientCustomizer() {
        return restClientBuilder -> {
            JdkClientHttpRequestFactory factory = new JdkClientHttpRequestFactory();
            factory.setReadTimeout(Duration.ofSeconds(60));
            restClientBuilder.requestFactory(factory);
        };
    }
}
