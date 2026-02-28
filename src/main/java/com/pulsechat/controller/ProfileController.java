package com.pulsechat.controller;

import com.pulsechat.service.SupabaseService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/profiles")
public class ProfileController {

    private final SupabaseService supabaseService;

    public ProfileController(SupabaseService supabaseService) {
        this.supabaseService = supabaseService;
    }

    /** POST /api/profiles — Create or update a user profile */
    @PostMapping
    public ResponseEntity<Map<String, Object>> createProfile(@RequestBody Map<String, Object> body) {
        String id = (String) body.get("id");
        String username = (String) body.get("username");
        String fullName = (String) body.get("full_name");
        String email = (String) body.get("email");
        String phone = (String) body.get("phone");
        String gender = (String) body.get("gender");

        if (id == null || username == null || fullName == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "id, username, full_name are required"));
        }

        // Final uniqueness check before saving
        if (!supabaseService.isUsernameAvailable(username.toLowerCase())) {
            return ResponseEntity.status(409).body(Map.of("error", "Username is already taken"));
        }

        Map<String, Object> profile = Map.of(
                "id", id,
                "username", username.toLowerCase(),
                "full_name", fullName,
                "email", email != null ? email : "",
                "phone", phone != null ? phone : "",
                "gender", gender != null ? gender : "prefer_not_to_say"
        );

        boolean success = supabaseService.upsertProfile(profile);
        if (!success) {
            return ResponseEntity.internalServerError().body(Map.of("error", "Failed to save profile"));
        }

        return ResponseEntity.ok(Map.of("success", true, "message", "Profile saved successfully"));
    }
}
