package com.pulsechat.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

@Service
public class SupabaseService {

        private final WebClient client;
        private final String serviceKey;

        public SupabaseService(
                        @Value("${supabase.url}") String supabaseUrl,
                        @Value("${supabase.service-key}") String serviceKey) {
                this.serviceKey = serviceKey;

                if (supabaseUrl == null || supabaseUrl.isEmpty() || serviceKey == null || serviceKey.isEmpty()) {
                        System.err.println("************************************************************");
                        System.err.println("WARNING: SUPABASE_URL or SUPABASE_SERVICE_KEY is missing!");
                        System.err.println("The application will NOT be able to communicate with Supabase.");
                        System.err.println("Please set these environment variables in your Render Dashboard.");
                        System.err.println("************************************************************");

                        // Use a dummy URL if empty to allow the bean to be created without crash
                        supabaseUrl = (supabaseUrl == null || supabaseUrl.isEmpty()) ? "http://missing-supabase-url"
                                        : supabaseUrl;
                }

                this.client = WebClient.builder()
                                .baseUrl(supabaseUrl + "/rest/v1")
                                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                                .defaultHeader("apikey", serviceKey)
                                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + serviceKey)
                                .build();
        }

        /** Check if a username exists */
        public boolean isUsernameAvailable(String username) {
                try {
                        List<?> result = client.get()
                                        .uri(uriBuilder -> uriBuilder
                                                        .path("/profiles")
                                                        .queryParam("username", "eq." + username.toLowerCase().trim())
                                                        .queryParam("select", "id")
                                                        .build())
                                        .retrieve()
                                        .bodyToMono(List.class)
                                        .block();
                        return result == null || result.isEmpty();
                } catch (Exception e) {
                        return false;
                }
        }

        /** Find user profiles by username (supports partial matches) */
        public List<Map<?, ?>> findUsersByUsername(String username) {
                // Use ilike and wildcards for partial matches
                String query = "ilike.%" + username.toLowerCase().trim() + "%";

                try {
                        List<?> results = client.get()
                                        .uri(uriBuilder -> uriBuilder
                                                        .path("/profiles")
                                                        .queryParam("username", query)
                                                        .queryParam("select", "id,username,full_name,avatar_url")
                                                        .queryParam("limit", "10") // Return up to 10 matches
                                                        .build())
                                        .retrieve()
                                        .bodyToMono(List.class)
                                        .block();

                        return (List<Map<?, ?>>) results;
                } catch (Exception e) {
                        return List.of();
                }
        }

        /** Upsert a profile into Supabase */
        public boolean upsertProfile(Map<String, Object> profile) {
                try {
                        client.post()
                                        .uri("/profiles?on_conflict=id")
                                        .header("Prefer", "resolution=merge-duplicates")
                                        .bodyValue(profile)
                                        .retrieve()
                                        .bodyToMono(String.class)
                                        .block();
                        return true;
                } catch (Exception e) {
                        return false;
                }
        }

        /** Get an existing chat between two users */
        public String getExistingChatId(String userId1, String userId2) {
                // Find chats where both users are participants
                List<?> cp1 = client.get()
                                .uri(uriBuilder -> uriBuilder
                                                .path("/chat_participants")
                                                .queryParam("user_id", "eq." + userId1)
                                                .queryParam("select", "chat_id")
                                                .build())
                                .retrieve()
                                .bodyToMono(List.class)
                                .block();

                if (cp1 == null || cp1.isEmpty())
                        return null;

                List<String> chatIds = cp1.stream()
                                .map(item -> (String) ((Map<?, ?>) item).get("chat_id"))
                                .toList();

                List<?> cp2 = client.get()
                                .uri(uriBuilder -> uriBuilder
                                                .path("/chat_participants")
                                                .queryParam("user_id", "eq." + userId2)
                                                .queryParam("chat_id", "in.(" + String.join(",", chatIds) + ")")
                                                .queryParam("select", "chat_id")
                                                .build())
                                .retrieve()
                                .bodyToMono(List.class)
                                .block();

                if (cp2 == null || cp2.isEmpty())
                        return null;
                return (String) ((Map<?, ?>) cp2.get(0)).get("chat_id");
        }

        /** Create a new chat and add participants */
        public String createChat(String userId1, String userId2) {
                // Insert into chats table
                List<?> chatResult = client.post()
                                .uri("/chats?select=id")
                                .header("Prefer", "return=representation")
                                .bodyValue(Map.of())
                                .retrieve()
                                .bodyToMono(List.class)
                                .block();

                if (chatResult == null || chatResult.isEmpty())
                        throw new RuntimeException("Failed to create chat");
                String chatId = (String) ((Map<?, ?>) chatResult.get(0)).get("id");

                // Add participants
                List<Map<String, String>> participants = List.of(
                                Map.of("chat_id", chatId, "user_id", userId1),
                                Map.of("chat_id", chatId, "user_id", userId2));
                client.post()
                                .uri("/chat_participants")
                                .bodyValue(participants)
                                .retrieve()
                                .bodyToMono(String.class)
                                .block();

                return chatId;
        }
}
