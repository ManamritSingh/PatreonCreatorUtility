package com.patreon.backend.chatbot;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "conversation")
public class ChatMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String sessionId;
    private String role;

    @Column(columnDefinition = "TEXT")
    private String message;

    private LocalDateTime timestamp = LocalDateTime.now();

    // Getters and setters
    public Long getId() { return id; }
    public String getSessionId() { return sessionId; }
    public void setSessionId(String sessionId) { this.sessionId = sessionId; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
}
