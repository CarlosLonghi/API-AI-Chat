package br.com.carloslonghi.apichatai.memory.dto.request;

import jakarta.validation.constraints.NotBlank;

public record ChatMessageRequest(@NotBlank String message) {
}
