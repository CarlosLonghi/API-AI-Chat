package br.com.carloslonghi.api_chat_ai.memory;

import br.com.carloslonghi.api_chat_ai.memory.dto.response.ChatHistoryResponse;
import br.com.carloslonghi.api_chat_ai.memory.dto.response.ChatSummaryResponse;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

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

    public List<ChatSummaryResponse> getAllChatsByUser(String userId) {
        final String sql = "SELECT conversation_id, user_id, description FROM chat_memory WHERE user_id = ?";
        return jdbcTemplate.query(sql, (rs, __) ->
            new ChatSummaryResponse(
                    rs.getString("conversation_id"),
                    rs.getString("description"))
        , userId);
    }

    public List<ChatHistoryResponse> getChatMessages(String chatId) {
        final String sql = "SELECT content, type, timestamp FROM spring_ai_chat_memory WHERE conversation_id = ? ORDER BY timestamp ASC";
        return jdbcTemplate.query(sql, (rs, __) ->
                new ChatHistoryResponse(
                        rs.getString("content"),
                        rs.getString("type"),
                        rs.getString("timestamp"))
        , chatId);
    }

    public boolean existsChat(String chatId) {
        final String sql = "SELECT COUNT(*) FROM spring_ai_chat_memory WHERE conversation_id = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, chatId);
        return count != null && count > 0;
    }
}
