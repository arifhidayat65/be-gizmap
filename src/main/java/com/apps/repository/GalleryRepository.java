package com.apps.repository;

import com.apps.model.Gallery;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface GalleryRepository extends JpaRepository<Gallery, Long> {
    List<Gallery> findByImageType(String imageType);
    List<Gallery> findByTitleContainingIgnoreCase(String title);
    List<Gallery> findByOrderByCreatedAtDesc();
}
