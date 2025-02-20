package com.apps.controller;

import com.apps.payload.request.SuggestionRequest;
import com.apps.payload.response.SuggestionResponse;
import com.apps.payload.response.MessageResponse;
import com.apps.service.SuggestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/suggestions")
public class SuggestionController {

    @Autowired
    private SuggestionService suggestionService;

    @PostMapping
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<SuggestionResponse> createSuggestion(@Valid @RequestBody SuggestionRequest suggestionRequest) {
        SuggestionResponse response = suggestionService.createSuggestion(suggestionRequest);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<SuggestionResponse>> getAllSuggestions() {
        List<SuggestionResponse> suggestions = suggestionService.getAllSuggestions();
        return ResponseEntity.ok(suggestions);
    }

    @GetMapping("/{id}")
    public ResponseEntity<SuggestionResponse> getSuggestionById(@PathVariable Long id) {
        SuggestionResponse suggestion = suggestionService.getSuggestionById(id);
        return ResponseEntity.ok(suggestion);
    }

    @GetMapping("/my-suggestions")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<List<SuggestionResponse>> getMySuggestions() {
        List<SuggestionResponse> suggestions = suggestionService.getSuggestionsByCurrentUser();
        return ResponseEntity.ok(suggestions);
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<SuggestionResponse>> getSuggestionsByStatus(@PathVariable String status) {
        List<SuggestionResponse> suggestions = suggestionService.getSuggestionsByStatus(status);
        return ResponseEntity.ok(suggestions);
    }

    @GetMapping("/category/{category}")
    public ResponseEntity<List<SuggestionResponse>> getSuggestionsByCategory(@PathVariable String category) {
        List<SuggestionResponse> suggestions = suggestionService.getSuggestionsByCategory(category);
        return ResponseEntity.ok(suggestions);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<SuggestionResponse> updateSuggestion(
            @PathVariable Long id,
            @Valid @RequestBody SuggestionRequest suggestionRequest) {
        SuggestionResponse response = suggestionService.updateSuggestion(id, suggestionRequest);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SuggestionResponse> updateSuggestionStatus(
            @PathVariable Long id,
            @RequestParam String status) {
        SuggestionResponse response = suggestionService.updateSuggestionStatus(id, status);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<MessageResponse> deleteSuggestion(@PathVariable Long id) {
        suggestionService.deleteSuggestion(id);
        return ResponseEntity.ok(new MessageResponse("Suggestion deleted successfully"));
    }
}
