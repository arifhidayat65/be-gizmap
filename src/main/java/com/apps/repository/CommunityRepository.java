package com.apps.repository;

import com.apps.model.Community;
import com.apps.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommunityRepository extends JpaRepository<Community, Long> {
    List<Community> findByCreatorOrderByCreatedAtDesc(User creator);
    List<Community> findByIsActiveOrderByCreatedAtDesc(boolean isActive);
    List<Community> findByLocationOrderByCreatedAtDesc(String location);
    List<Community> findByNameContainingIgnoreCaseOrderByCreatedAtDesc(String name);
}
