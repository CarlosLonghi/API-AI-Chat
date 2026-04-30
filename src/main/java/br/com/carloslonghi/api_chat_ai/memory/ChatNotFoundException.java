package br.com.carloslonghi.api_chat_ai.memory;

public class ChatNotFoundException extends RuntimeException {
    public ChatNotFoundException(String message) {
        super("Chat nao encontrado: " + message);
    }
}
