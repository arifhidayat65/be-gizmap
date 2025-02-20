package com.apps.service;

import com.apps.model.Feedback;
import com.apps.model.User;
import com.apps.repository.FeedbackRepository;
import com.apps.repository.UserRepository;
import com.apps.payload.request.FeedbackRequest;
import com.apps.payload.response.FeedbackResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FeedbackService {

    @Autowired
    private FeedbackRepository feedbackRepository;

    @Autowired
    private UserRepository userRepository;

    @Transactional
    public FeedbackResponse createFeedback(FeedbackRequest feedbackRequest) {
        User currentUser = getCurrentUser();
        
        Feedback feedback = new Feedback();
        feedback.setType(feedbackRequest.getType());
        feedback.setDescription(feedbackRequest.getDescription());
        feedback.setRating(feedbackRequest.getRating());
        feedback.setPriority(feedbackRequest.getPriority());
        feedback.setUser(currentUser);
        
        Feedback savedFeedback = feedbackRepository.save(feedback);
        return convertToResponse(savedFeedback);
    }

    @Transactional(readOnly = true)
    public List<FeedbackResponse> getAllFeedbacks() {
        return feedbackRepository.findAll().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public FeedbackResponse getFeedbackById(Long id) {
        Feedback feedback = feedbackRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Feedback not found with id: " + id));
        return convertToResponse(feedback);
    }

    @Transactional(readOnly = true)
    public List<FeedbackResponse> getFeedbacksByCurrentUser() {
        User currentUser = getCurrentUser();
        return feedbackRepository.findByUserOrderByCreatedAtDesc(currentUser).stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<FeedbackResponse> getFeedbacksByStatus(String status) {
        return feedbackRepository.findByStatusOrderByCreatedAtDesc(status).stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<FeedbackResponse> getFeedbacksByType(String type) {
        return feedbackRepository.findByTypeOrderByCreatedAtDesc(type).stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<FeedbackResponse> getFeedbacksByPriority(String priority) {
        return feedbackRepository.findByPriorityOrderByCreatedAtDesc(priority).stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public FeedbackResponse updateFeedback(Long id, FeedbackRequest feedbackRequest) {
        Feedback feedback = feedbackRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Feedback not found with id: " + id));

        // Check if the current user is the creator
        User currentUser = getCurrentUser();
        if (!feedback.getUser().getId().equals(currentUser.getId())) {
            throw new IllegalStateException("You can only update your own feedback");
        }

        feedback.setType(feedbackRequest.getType());
        feedback.setDescription(feedbackRequest.getDescription());
        feedback.setRating(feedbackRequest.getRating());
        feedback.setPriority(feedbackRequest.getPriority());

        Feedback updatedFeedback = feedbackRepository.save(feedback);
        return convertToResponse(updatedFeedback);
    }

    @Transactional
    public FeedbackResponse updateFeedbackStatus(Long id, String status, String resolution) {
        Feedback feedback = feedbackRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Feedback not found with id: " + id));

        feedback.setStatus(status);
        feedback.setResolution(resolution);
        Feedback updatedFeedback = feedbackRepository.save(feedback);
        return convertToResponse(updatedFeedback);
    }

    @Transactional
    public void deleteFeedback(Long id) {
        Feedback feedback = feedbackRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Feedback not found with id: " + id));

        // Check if the current user is the creator
        User currentUser = getCurrentUser();
        if (!feedback.getUser().getId().equals(currentUser.getId())) {
            throw new IllegalStateException("You can only delete your own feedback");
        }

        feedbackRepository.deleteById(id);
    }

    private FeedbackResponse convertToResponse(Feedback feedback) {
        FeedbackResponse response = new FeedbackResponse();
        response.setId(feedback.getId());
        response.setType(feedback.getType());
        response.setDescription(feedback.getDescription());
        response.setRating(feedback.getRating());
        response.setPriority(feedback.getPriority());
        response.setStatus(feedback.getStatus());
        response.setResolution(feedback.getResolution());
        response.setUsername(feedback.getUser().getUsername());
        response.setCreatedAt(feedback.getCreatedAt());
        response.setUpdatedAt(feedback.getUpdatedAt());
        return response;
    }

    private User getCurrentUser() {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
    }
}
