package com.apps.service;

import com.apps.model.Community;
import com.apps.model.User;
import com.apps.repository.CommunityRepository;
import com.apps.repository.UserRepository;
import com.apps.payload.request.CommunityRequest;
import com.apps.payload.response.CommunityResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CommunityService {

    @Autowired
    private CommunityRepository communityRepository;

    @Autowired
    private UserRepository userRepository;

    @Transactional
    public CommunityResponse createCommunity(CommunityRequest communityRequest) {
        User currentUser = getCurrentUser();
        
        Community community = new Community();
        community.setName(communityRequest.getName());
        community.setDescription(communityRequest.getDescription());
        community.setLocation(communityRequest.getLocation());
        community.setImageUrl(communityRequest.getImageUrl());
        community.setActive(communityRequest.isActive());
        community.setCreator(currentUser);
        
        Community savedCommunity = communityRepository.save(community);
        return convertToResponse(savedCommunity);
    }

    @Transactional(readOnly = true)
    public List<CommunityResponse> getAllCommunities() {
        return communityRepository.findAll().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public CommunityResponse getCommunityById(Long id) {
        Community community = communityRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Community not found with id: " + id));
        return convertToResponse(community);
    }

    @Transactional(readOnly = true)
    public List<CommunityResponse> getCommunitiesByCurrentUser() {
        User currentUser = getCurrentUser();
        return communityRepository.findByCreatorOrderByCreatedAtDesc(currentUser).stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<CommunityResponse> getActiveCommunities() {
        return communityRepository.findByIsActiveOrderByCreatedAtDesc(true).stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<CommunityResponse> getCommunitiesByLocation(String location) {
        return communityRepository.findByLocationOrderByCreatedAtDesc(location).stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<CommunityResponse> searchCommunitiesByName(String name) {
        return communityRepository.findByNameContainingIgnoreCaseOrderByCreatedAtDesc(name).stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public CommunityResponse updateCommunity(Long id, CommunityRequest communityRequest) {
        Community community = communityRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Community not found with id: " + id));

        // Check if the current user is the creator
        User currentUser = getCurrentUser();
        if (!community.getCreator().getId().equals(currentUser.getId())) {
            throw new IllegalStateException("You can only update communities you created");
        }

        community.setName(communityRequest.getName());
        community.setDescription(communityRequest.getDescription());
        community.setLocation(communityRequest.getLocation());
        community.setImageUrl(communityRequest.getImageUrl());
        community.setActive(communityRequest.isActive());

        Community updatedCommunity = communityRepository.save(community);
        return convertToResponse(updatedCommunity);
    }

    @Transactional
    public void deleteCommunity(Long id) {
        Community community = communityRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Community not found with id: " + id));

        // Check if the current user is the creator
        User currentUser = getCurrentUser();
        if (!community.getCreator().getId().equals(currentUser.getId())) {
            throw new IllegalStateException("You can only delete communities you created");
        }

        communityRepository.deleteById(id);
    }

    private CommunityResponse convertToResponse(Community community) {
        CommunityResponse response = new CommunityResponse();
        response.setId(community.getId());
        response.setName(community.getName());
        response.setDescription(community.getDescription());
        response.setLocation(community.getLocation());
        response.setImageUrl(community.getImageUrl());
        response.setActive(community.isActive());
        response.setCreatorUsername(community.getCreator().getUsername());
        response.setCreatedAt(community.getCreatedAt());
        response.setUpdatedAt(community.getUpdatedAt());
        return response;
    }

    private User getCurrentUser() {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
    }
}
