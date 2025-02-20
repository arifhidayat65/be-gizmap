package com.apps.service;

import com.apps.model.Konten;
import com.apps.model.User;
import com.apps.repository.KontenRepository;
import com.apps.repository.UserRepository;
import com.apps.payload.request.KontenRequest;
import com.apps.payload.response.KontenResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class KontenService {

    @Autowired
    private KontenRepository kontenRepository;

    @Autowired
    private UserRepository userRepository;

    @Transactional
    public KontenResponse createKonten(KontenRequest kontenRequest) {
        User currentUser = getCurrentUser();
        
        Konten konten = new Konten();
        konten.setTitle(kontenRequest.getTitle());
        konten.setContent(kontenRequest.getContent());
        konten.setType(kontenRequest.getType());
        konten.setImageUrl(kontenRequest.getImageUrl());
        konten.setAuthor(currentUser);
        
        Konten savedKonten = kontenRepository.save(konten);
        return convertToResponse(savedKonten);
    }

    @Transactional(readOnly = true)
    public List<KontenResponse> getAllKontens() {
        return kontenRepository.findAll().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public KontenResponse getKontenById(Long id) {
        Konten konten = kontenRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Konten not found with id: " + id));
        return convertToResponse(konten);
    }

    @Transactional(readOnly = true)
    public List<KontenResponse> getKontensByCurrentUser() {
        User currentUser = getCurrentUser();
        return kontenRepository.findByAuthorOrderByCreatedAtDesc(currentUser).stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<KontenResponse> getKontensByType(String type) {
        return kontenRepository.findByTypeOrderByCreatedAtDesc(type).stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public KontenResponse updateKonten(Long id, KontenRequest kontenRequest) {
        Konten konten = kontenRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Konten not found with id: " + id));

        // Check if the current user is the author
        User currentUser = getCurrentUser();
        if (!konten.getAuthor().getId().equals(currentUser.getId())) {
            throw new IllegalStateException("You can only update your own content");
        }

        konten.setTitle(kontenRequest.getTitle());
        konten.setContent(kontenRequest.getContent());
        konten.setType(kontenRequest.getType());
        konten.setImageUrl(kontenRequest.getImageUrl());

        Konten updatedKonten = kontenRepository.save(konten);
        return convertToResponse(updatedKonten);
    }

    @Transactional
    public void deleteKonten(Long id) {
        Konten konten = kontenRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Konten not found with id: " + id));

        // Check if the current user is the author
        User currentUser = getCurrentUser();
        if (!konten.getAuthor().getId().equals(currentUser.getId())) {
            throw new IllegalStateException("You can only delete your own content");
        }

        kontenRepository.deleteById(id);
    }

    private KontenResponse convertToResponse(Konten konten) {
        KontenResponse response = new KontenResponse();
        response.setId(konten.getId());
        response.setTitle(konten.getTitle());
        response.setContent(konten.getContent());
        response.setType(konten.getType());
        response.setImageUrl(konten.getImageUrl());
        response.setAuthorUsername(konten.getAuthor().getUsername());
        response.setCreatedAt(konten.getCreatedAt());
        response.setUpdatedAt(konten.getUpdatedAt());
        return response;
    }

    private User getCurrentUser() {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
    }
}
