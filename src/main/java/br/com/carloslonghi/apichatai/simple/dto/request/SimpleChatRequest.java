package br.com.carloslonghi.apichatai.simple.dto.request;

import jakarta.validation.constraints.NotBlank;

public record SimpleChatRequest(@NotBlank String message) {
}
