package com.apps.controller;

import com.apps.payload.request.FeedbackRequest;
import com.apps.payload.response.FeedbackResponse;
import com.apps.payload.response.MessageResponse;
import com.apps.service.FeedbackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/feedbacks")
public class FeedbackController {

    @Autowired
    private FeedbackService feedbackService;

    @PostMapping
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<FeedbackResponse> createFeedback(@Valid @RequestBody FeedbackRequest feedbackRequest) {
        FeedbackResponse response = feedbackService.createFeedback(feedbackRequest);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<FeedbackResponse>> getAllFeedbacks() {
        List<FeedbackResponse> feedbacks = feedbackService.getAllFeedbacks();
        return ResponseEntity.ok(feedbacks);
    }

    @GetMapping("/{id}")
    public ResponseEntity<FeedbackResponse> getFeedbackById(@PathVariable Long id) {
        FeedbackResponse feedback = feedbackService.getFeedbackById(id);
        return ResponseEntity.ok(feedback);
    }

    @GetMapping("/my-feedbacks")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<List<FeedbackResponse>> getMyFeedbacks() {
        List<FeedbackResponse> feedbacks = feedbackService.getFeedbacksByCurrentUser();
        return ResponseEntity.ok(feedbacks);
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<FeedbackResponse>> getFeedbacksByStatus(@PathVariable String status) {
        List<FeedbackResponse> feedbacks = feedbackService.getFeedbacksByStatus(status);
        return ResponseEntity.ok(feedbacks);
    }

    @GetMapping("/type/{type}")
    public ResponseEntity<List<FeedbackResponse>> getFeedbacksByType(@PathVariable String type) {
        List<FeedbackResponse> feedbacks = feedbackService.getFeedbacksByType(type);
        return ResponseEntity.ok(feedbacks);
    }

    @GetMapping("/priority/{priority}")
    public ResponseEntity<List<FeedbackResponse>> getFeedbacksByPriority(@PathVariable String priority) {
        List<FeedbackResponse> feedbacks = feedbackService.getFeedbacksByPriority(priority);
        return ResponseEntity.ok(feedbacks);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<FeedbackResponse> updateFeedback(
            @PathVariable Long id,
            @Valid @RequestBody FeedbackRequest feedbackRequest) {
        FeedbackResponse response = feedbackService.updateFeedback(id, feedbackRequest);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<FeedbackResponse> updateFeedbackStatus(
            @PathVariable Long id,
            @RequestParam String status,
            @RequestParam(required = false) String resolution) {
        FeedbackResponse response = feedbackService.updateFeedbackStatus(id, status, resolution);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<MessageResponse> deleteFeedback(@PathVariable Long id) {
        feedbackService.deleteFeedback(id);
        return ResponseEntity.ok(new MessageResponse("Feedback deleted successfully"));
    }
}
