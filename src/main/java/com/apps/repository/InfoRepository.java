package com.apps.repository;

import com.apps.model.Info;
import com.apps.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InfoRepository extends JpaRepository<Info, Long> {
    List<Info> findByAuthorOrderByCreatedAtDesc(User author);
    List<Info> findByCategoryOrderByCreatedAtDesc(String category);
    List<Info> findByIsActiveOrderByCreatedAtDesc(boolean isActive);
}
