package br.com.carloslonghi.apichatai.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI getOpenAPI() {

        Contact contact = new Contact();
        contact.setName("Carlos Longhi");
        contact.setEmail("carloslonghi.cl@gmail.com");
        contact.setUrl("https://carloslonghi.com.br/");

        Info info = new Info();
        info.title("API Chat AI");
        info.version("v1");
        info.description("""
                API de chat com um LLM via Spring AI. Dois modos: chat simples \
                (uma chamada, sem histórico) e chat com memória (histórico persistido, \
                identificado por um chatId, com múltiplos turnos de conversa).""");
        info.contact(contact);

        return new OpenAPI().info(info);
    }
}
