package com.apps.controller;

import com.apps.payload.request.GalleryRequest;
import com.apps.payload.response.GalleryResponse;
import com.apps.payload.response.MessageResponse;
import com.apps.service.GalleryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/galleries")
public class GalleryController {

    @Autowired
    private GalleryService galleryService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<GalleryResponse> createGallery(@Valid @RequestBody GalleryRequest galleryRequest) {
        GalleryResponse gallery = galleryService.createGallery(galleryRequest);
        return new ResponseEntity<>(gallery, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<GalleryResponse>> getAllGalleries() {
        List<GalleryResponse> galleries = galleryService.getAllGalleries();
        return ResponseEntity.ok(galleries);
    }

    @GetMapping("/{id}")
    public ResponseEntity<GalleryResponse> getGalleryById(@PathVariable Long id) {
        GalleryResponse gallery = galleryService.getGalleryById(id);
        return ResponseEntity.ok(gallery);
    }

    @GetMapping("/type/{imageType}")
    public ResponseEntity<List<GalleryResponse>> getGalleriesByType(@PathVariable String imageType) {
        List<GalleryResponse> galleries = galleryService.getGalleriesByType(imageType);
        return ResponseEntity.ok(galleries);
    }

    @GetMapping("/search")
    public ResponseEntity<List<GalleryResponse>> searchGalleries(@RequestParam String title) {
        List<GalleryResponse> galleries = galleryService.searchGalleriesByTitle(title);
        return ResponseEntity.ok(galleries);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<GalleryResponse> updateGallery(
            @PathVariable Long id,
            @Valid @RequestBody GalleryRequest galleryRequest) {
        GalleryResponse updatedGallery = galleryService.updateGallery(id, galleryRequest);
        return ResponseEntity.ok(updatedGallery);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MessageResponse> deleteGallery(@PathVariable Long id) {
        galleryService.deleteGallery(id);
        return ResponseEntity.ok(new MessageResponse("Gallery deleted successfully"));
    }
}
