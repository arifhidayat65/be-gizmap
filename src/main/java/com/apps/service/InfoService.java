package com.apps.service;

import com.apps.model.Info;
import com.apps.model.User;
import com.apps.repository.InfoRepository;
import com.apps.repository.UserRepository;
import com.apps.payload.request.InfoRequest;
import com.apps.payload.response.InfoResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class InfoService {

    @Autowired
    private InfoRepository infoRepository;

    @Autowired
    private UserRepository userRepository;

    @Transactional
    public InfoResponse createInfo(InfoRequest infoRequest) {
        User currentUser = getCurrentUser();
        
        Info info = new Info();
        info.setTitle(infoRequest.getTitle());
        info.setDescription(infoRequest.getDescription());
        info.setCategory(infoRequest.getCategory());
        info.setImageUrl(infoRequest.getImageUrl());
        info.setActive(infoRequest.isActive());
        info.setAuthor(currentUser);
        
        Info savedInfo = infoRepository.save(info);
        return convertToResponse(savedInfo);
    }

    @Transactional(readOnly = true)
    public List<InfoResponse> getAllInfos() {
        return infoRepository.findAll().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public InfoResponse getInfoById(Long id) {
        Info info = infoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Info not found with id: " + id));
        return convertToResponse(info);
    }

    @Transactional(readOnly = true)
    public List<InfoResponse> getInfosByCurrentUser() {
        User currentUser = getCurrentUser();
        return infoRepository.findByAuthorOrderByCreatedAtDesc(currentUser).stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<InfoResponse> getInfosByCategory(String category) {
        return infoRepository.findByCategoryOrderByCreatedAtDesc(category).stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<InfoResponse> getActiveInfos() {
        return infoRepository.findByIsActiveOrderByCreatedAtDesc(true).stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public InfoResponse updateInfo(Long id, InfoRequest infoRequest) {
        Info info = infoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Info not found with id: " + id));

        // Check if the current user is the author
        User currentUser = getCurrentUser();
        if (!info.getAuthor().getId().equals(currentUser.getId())) {
            throw new IllegalStateException("You can only update your own info");
        }

        info.setTitle(infoRequest.getTitle());
        info.setDescription(infoRequest.getDescription());
        info.setCategory(infoRequest.getCategory());
        info.setImageUrl(infoRequest.getImageUrl());
        info.setActive(infoRequest.isActive());

        Info updatedInfo = infoRepository.save(info);
        return convertToResponse(updatedInfo);
    }

    @Transactional
    public void deleteInfo(Long id) {
        Info info = infoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Info not found with id: " + id));

        // Check if the current user is the author
        User currentUser = getCurrentUser();
        if (!info.getAuthor().getId().equals(currentUser.getId())) {
            throw new IllegalStateException("You can only delete your own info");
        }

        infoRepository.deleteById(id);
    }

    private InfoResponse convertToResponse(Info info) {
        InfoResponse response = new InfoResponse();
        response.setId(info.getId());
        response.setTitle(info.getTitle());
        response.setDescription(info.getDescription());
        response.setCategory(info.getCategory());
        response.setImageUrl(info.getImageUrl());
        response.setActive(info.isActive());
        response.setAuthorUsername(info.getAuthor().getUsername());
        response.setCreatedAt(info.getCreatedAt());
        response.setUpdatedAt(info.getUpdatedAt());
        return response;
    }

    private User getCurrentUser() {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
    }
}
