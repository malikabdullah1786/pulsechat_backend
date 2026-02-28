package com.pulsechat.controller;

import com.pulsechat.service.SupabaseService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final SupabaseService supabaseService;

    public UserController(SupabaseService supabaseService) {
        this.supabaseService = supabaseService;
    }

    /** GET /api/users/check?username=john — Check if username is available */
    @GetMapping("/check")
    public ResponseEntity<Map<String, Object>> checkUsername(@RequestParam String username) {
        if (username == null || username.length() < 3) {
            return ResponseEntity.badRequest().body(Map.of(
                    "available", false,
                    "message", "Username must be at least 3 characters"));
        }
        // Validate format: only letters, numbers, underscores, dots
        if (!username.matches("^[a-z0-9_.]{3,30}$")) {
            return ResponseEntity.badRequest().body(Map.of(
                    "available", false,
                    "message", "Username can only contain letters, numbers, underscores and dots"));
        }
        boolean available = supabaseService.isUsernameAvailable(username.toLowerCase());
        return ResponseEntity.ok(Map.of(
                "available", available,
                "message", available ? "Username is available" : "Username is already taken"));
    }

    /** GET /api/users/find?username=john — Find users by partial username */
    @GetMapping("/find")
    public ResponseEntity<?> findUsers(@RequestParam String username) {
        List<Map<?, ?>> users = supabaseService.findUsersByUsername(username.toLowerCase().trim());
        return ResponseEntity.ok(users != null ? users : List.of());
    }
}
