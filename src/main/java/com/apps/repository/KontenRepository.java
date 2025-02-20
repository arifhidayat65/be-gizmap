package com.apps.repository;

import com.apps.model.Konten;
import com.apps.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface KontenRepository extends JpaRepository<Konten, Long> {
    List<Konten> findByAuthorOrderByCreatedAtDesc(User author);
    List<Konten> findByTypeOrderByCreatedAtDesc(String type);
}
