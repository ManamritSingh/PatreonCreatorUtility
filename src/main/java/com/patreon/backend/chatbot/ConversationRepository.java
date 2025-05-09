package com.patreon.backend.chatbot;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ConversationRepository extends JpaRepository<ChatMessage, Long> {
    List<ChatMessage> findBySessionIdOrderByTimestampAsc(String sessionId);
}
