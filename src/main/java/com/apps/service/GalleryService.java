package com.apps.service;

import com.apps.model.Gallery;
import com.apps.repository.GalleryRepository;
import com.apps.payload.request.GalleryRequest;
import com.apps.payload.response.GalleryResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class GalleryService {

    @Autowired
    private GalleryRepository galleryRepository;

    @Transactional
    public GalleryResponse createGallery(GalleryRequest galleryRequest) {
        Gallery gallery = new Gallery();
        gallery.setTitle(galleryRequest.getTitle());
        gallery.setDescription(galleryRequest.getDescription());
        gallery.setImageUrl(galleryRequest.getImageUrl());
        gallery.setImageType(galleryRequest.getImageType());

        Gallery savedGallery = galleryRepository.save(gallery);
        return convertToResponse(savedGallery);
    }

    @Transactional(readOnly = true)
    public List<GalleryResponse> getAllGalleries() {
        return galleryRepository.findByOrderByCreatedAtDesc().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public GalleryResponse getGalleryById(Long id) {
        Gallery gallery = galleryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Gallery not found with id: " + id));
        return convertToResponse(gallery);
    }

    @Transactional(readOnly = true)
    public List<GalleryResponse> getGalleriesByType(String imageType) {
        return galleryRepository.findByImageType(imageType).stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<GalleryResponse> searchGalleriesByTitle(String title) {
        return galleryRepository.findByTitleContainingIgnoreCase(title).stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public GalleryResponse updateGallery(Long id, GalleryRequest galleryRequest) {
        Gallery gallery = galleryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Gallery not found with id: " + id));

        gallery.setTitle(galleryRequest.getTitle());
        gallery.setDescription(galleryRequest.getDescription());
        gallery.setImageUrl(galleryRequest.getImageUrl());
        gallery.setImageType(galleryRequest.getImageType());

        Gallery updatedGallery = galleryRepository.save(gallery);
        return convertToResponse(updatedGallery);
    }

    @Transactional
    public void deleteGallery(Long id) {
        if (!galleryRepository.existsById(id)) {
            throw new EntityNotFoundException("Gallery not found with id: " + id);
        }
        galleryRepository.deleteById(id);
    }

    private GalleryResponse convertToResponse(Gallery gallery) {
        return new GalleryResponse(
            gallery.getId(),
            gallery.getTitle(),
            gallery.getDescription(),
            gallery.getImageUrl(),
            gallery.getImageType(),
            gallery.getCreatedAt(),
            gallery.getUpdatedAt()
        );
    }
}
