package com.apps.repository;

import com.apps.model.Feedback;
import com.apps.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FeedbackRepository extends JpaRepository<Feedback, Long> {
    List<Feedback> findByUserOrderByCreatedAtDesc(User user);
    List<Feedback> findByStatusOrderByCreatedAtDesc(String status);
    List<Feedback> findByTypeOrderByCreatedAtDesc(String type);
    List<Feedback> findByPriorityOrderByCreatedAtDesc(String priority);
    List<Feedback> findByRatingOrderByCreatedAtDesc(Integer rating);
    List<Feedback> findByDescriptionContainingIgnoreCaseOrderByCreatedAtDesc(String description);
}
