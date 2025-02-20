package com.apps.repository;

import com.apps.model.Suggestion;
import com.apps.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SuggestionRepository extends JpaRepository<Suggestion, Long> {
    List<Suggestion> findByUserOrderByCreatedAtDesc(User user);
    List<Suggestion> findByStatusOrderByCreatedAtDesc(String status);
    List<Suggestion> findByCategoryOrderByCreatedAtDesc(String category);
    List<Suggestion> findByTitleContainingIgnoreCaseOrderByCreatedAtDesc(String title);
}
