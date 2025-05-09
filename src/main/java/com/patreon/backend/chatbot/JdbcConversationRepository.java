package com.patreon.backend.chatbot;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class JdbcConversationRepository {

    private final JdbcTemplate jdbc;

    public JdbcConversationRepository(JdbcTemplate jdbcTemplate) {
        this.jdbc = jdbcTemplate;
    }

    private final RowMapper<ChatMessage> rowMapper = (rs, rowNum) -> {
        ChatMessage msg = new ChatMessage();
        msg.setId(rs.getLong("id"));
        msg.setSessionId(rs.getString("session_id"));
        msg.setRole(rs.getString("role"));
        msg.setMessage(rs.getString("message"));
        msg.setTimestamp(rs.getLong("timestamp"));
        return msg;
    };

    public void save(ChatMessage msg) {
        jdbc.update("""
            INSERT INTO conversation (session_id, role, message, timestamp)
            VALUES (?, ?, ?, ?)
        """, msg.getSessionId(), msg.getRole(), msg.getMessage(), msg.getTimestamp());
    }

    public List<ChatMessage> findBySessionIdOrderByTimestampAsc(String sessionId) {
        return jdbc.query("""
            SELECT * FROM conversation
            WHERE session_id = ?
            ORDER BY timestamp ASC
        """, rowMapper, sessionId);
    }
}
