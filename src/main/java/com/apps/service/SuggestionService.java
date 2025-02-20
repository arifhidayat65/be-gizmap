package com.apps.service;

import com.apps.model.Suggestion;
import com.apps.model.User;
import com.apps.repository.SuggestionRepository;
import com.apps.repository.UserRepository;
import com.apps.payload.request.SuggestionRequest;
import com.apps.payload.response.SuggestionResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SuggestionService {

    @Autowired
    private SuggestionRepository suggestionRepository;

    @Autowired
    private UserRepository userRepository;

    @Transactional
    public SuggestionResponse createSuggestion(SuggestionRequest suggestionRequest) {
        User currentUser = getCurrentUser();
        
        Suggestion suggestion = new Suggestion();
        suggestion.setTitle(suggestionRequest.getTitle());
        suggestion.setContent(suggestionRequest.getContent());
        suggestion.setCategory(suggestionRequest.getCategory());
        suggestion.setUser(currentUser);
        
        Suggestion savedSuggestion = suggestionRepository.save(suggestion);
        return convertToResponse(savedSuggestion);
    }

    @Transactional(readOnly = true)
    public List<SuggestionResponse> getAllSuggestions() {
        return suggestionRepository.findAll().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public SuggestionResponse getSuggestionById(Long id) {
        Suggestion suggestion = suggestionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Suggestion not found with id: " + id));
        return convertToResponse(suggestion);
    }

    @Transactional(readOnly = true)
    public List<SuggestionResponse> getSuggestionsByCurrentUser() {
        User currentUser = getCurrentUser();
        return suggestionRepository.findByUserOrderByCreatedAtDesc(currentUser).stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<SuggestionResponse> getSuggestionsByStatus(String status) {
        return suggestionRepository.findByStatusOrderByCreatedAtDesc(status).stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<SuggestionResponse> getSuggestionsByCategory(String category) {
        return suggestionRepository.findByCategoryOrderByCreatedAtDesc(category).stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public SuggestionResponse updateSuggestion(Long id, SuggestionRequest suggestionRequest) {
        Suggestion suggestion = suggestionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Suggestion not found with id: " + id));

        // Check if the current user is the creator
        User currentUser = getCurrentUser();
        if (!suggestion.getUser().getId().equals(currentUser.getId())) {
            throw new IllegalStateException("You can only update your own suggestions");
        }

        suggestion.setTitle(suggestionRequest.getTitle());
        suggestion.setContent(suggestionRequest.getContent());
        suggestion.setCategory(suggestionRequest.getCategory());

        Suggestion updatedSuggestion = suggestionRepository.save(suggestion);
        return convertToResponse(updatedSuggestion);
    }

    @Transactional
    public SuggestionResponse updateSuggestionStatus(Long id, String status) {
        Suggestion suggestion = suggestionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Suggestion not found with id: " + id));

        suggestion.setStatus(status);
        Suggestion updatedSuggestion = suggestionRepository.save(suggestion);
        return convertToResponse(updatedSuggestion);
    }

    @Transactional
    public void deleteSuggestion(Long id) {
        Suggestion suggestion = suggestionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Suggestion not found with id: " + id));

        // Check if the current user is the creator
        User currentUser = getCurrentUser();
        if (!suggestion.getUser().getId().equals(currentUser.getId())) {
            throw new IllegalStateException("You can only delete your own suggestions");
        }

        suggestionRepository.deleteById(id);
    }

    private SuggestionResponse convertToResponse(Suggestion suggestion) {
        SuggestionResponse response = new SuggestionResponse();
        response.setId(suggestion.getId());
        response.setTitle(suggestion.getTitle());
        response.setContent(suggestion.getContent());
        response.setCategory(suggestion.getCategory());
        response.setStatus(suggestion.getStatus());
        response.setUsername(suggestion.getUser().getUsername());
        response.setCreatedAt(suggestion.getCreatedAt());
        response.setUpdatedAt(suggestion.getUpdatedAt());
        return response;
    }

    private User getCurrentUser() {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
    }
}
