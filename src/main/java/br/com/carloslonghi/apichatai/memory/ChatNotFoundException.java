package br.com.carloslonghi.apichatai.memory;

public class ChatNotFoundException extends RuntimeException {
    public ChatNotFoundException(String message) {
        super("Chat nao encontrado: " + message);
    }
}
