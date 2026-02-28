package com.pulsechat.controller;

import com.pulsechat.service.SupabaseService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/chats")
public class ChatController {

    private final SupabaseService supabaseService;

    public ChatController(SupabaseService supabaseService) {
        this.supabaseService = supabaseService;
    }

    /** POST /api/chats — Get existing chat or create a new one between two users */
    @PostMapping
    public ResponseEntity<Map<String, Object>> getOrCreateChat(@RequestBody Map<String, Object> body) {
        String userId1 = (String) body.get("user_id_1");
        String userId2 = (String) body.get("user_id_2");

        if (userId1 == null || userId2 == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "user_id_1 and user_id_2 are required"));
        }

        // Try to find existing chat
        String chatId = supabaseService.getExistingChatId(userId1, userId2);

        if (chatId == null) {
            // Create a new one
            chatId = supabaseService.createChat(userId1, userId2);
        }

        return ResponseEntity.ok(Map.of("chat_id", chatId));
    }
}
