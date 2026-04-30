package br.com.carloslonghi.api_chat_ai.config;

import br.com.carloslonghi.api_chat_ai.memory.ChatNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ChatNotFoundException.class)
    ProblemDetail handleChatNotFound(ChatNotFoundException exception) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, exception.getMessage());
    }
}
