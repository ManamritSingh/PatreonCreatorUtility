package com.patreon.backend.chatbot;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/chat")
@CrossOrigin(origins = "*") // JavaFX app CORS support
public class ChatController {

    @Autowired
    private ChatService chatService;

    // Accept JSON: { "sessionId": "...", "userInput": "..." }
    @PostMapping
    public String chat(@RequestBody Map<String, String> payload) {
        String sessionId = payload.get("sessionId");
        String userInput = payload.get("userInput");

        if (sessionId == null || userInput == null) {
            return "⚠️ Missing sessionId or userInput.";
        }

        return chatService.getResponse(sessionId, userInput);
    }
}
