package br.com.carloslonghi.api_chat_ai.memory;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.util.List;

@Repository
public class MemoryChatRepository {
    private final JdbcTemplate jdbcTemplate;

    public MemoryChatRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public String generateChatId(String userId, String description) {
        final String sql = "INSERT INTO chat_memory (user_id, description) VALUES (?, ?) RETURNING conversation_id";
        return jdbcTemplate.queryForObject(sql, String.class, userId, description);
    }

    public List<ChatView> getAllChatsByUser(String userId) {
        final String sql = "SELECT conversation_id, user_id, description FROM chat_memory WHERE user_id = ?";
        return jdbcTemplate.query(sql, (ResultSet rs, int __) ->
            new ChatView(
                    rs.getString("conversation_id"),
                    rs.getString("description"))
        , userId);
    }

    public List<ChatMessage> getChatMessages(String chatId) {
        final String sql = "SELECT content, type, timestamp FROM spring_ai_chat_memory WHERE conversation_id = ? ORDER BY timestamp ASC";
        return jdbcTemplate.query(sql, (ResultSet rs, int __) ->
                new ChatMessage(rs.getString("content"), rs.getString("type"), rs.getString("timestamp"))
        , chatId);
    }
}
