package com.apps.controller;

import com.apps.payload.request.CommunityRequest;
import com.apps.payload.response.CommunityResponse;
import com.apps.payload.response.MessageResponse;
import com.apps.service.CommunityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/communities")
public class CommunityController {

    @Autowired
    private CommunityService communityService;

    @PostMapping
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<CommunityResponse> createCommunity(@Valid @RequestBody CommunityRequest communityRequest) {
        CommunityResponse response = communityService.createCommunity(communityRequest);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<CommunityResponse>> getAllCommunities() {
        List<CommunityResponse> communities = communityService.getAllCommunities();
        return ResponseEntity.ok(communities);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CommunityResponse> getCommunityById(@PathVariable Long id) {
        CommunityResponse community = communityService.getCommunityById(id);
        return ResponseEntity.ok(community);
    }

    @GetMapping("/my-communities")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<List<CommunityResponse>> getMyCommunities() {
        List<CommunityResponse> communities = communityService.getCommunitiesByCurrentUser();
        return ResponseEntity.ok(communities);
    }

    @GetMapping("/active")
    public ResponseEntity<List<CommunityResponse>> getActiveCommunities() {
        List<CommunityResponse> communities = communityService.getActiveCommunities();
        return ResponseEntity.ok(communities);
    }

    @GetMapping("/location/{location}")
    public ResponseEntity<List<CommunityResponse>> getCommunitiesByLocation(@PathVariable String location) {
        List<CommunityResponse> communities = communityService.getCommunitiesByLocation(location);
        return ResponseEntity.ok(communities);
    }

    @GetMapping("/search")
    public ResponseEntity<List<CommunityResponse>> searchCommunities(@RequestParam String name) {
        List<CommunityResponse> communities = communityService.searchCommunitiesByName(name);
        return ResponseEntity.ok(communities);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<CommunityResponse> updateCommunity(
            @PathVariable Long id,
            @Valid @RequestBody CommunityRequest communityRequest) {
        CommunityResponse response = communityService.updateCommunity(id, communityRequest);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<MessageResponse> deleteCommunity(@PathVariable Long id) {
        communityService.deleteCommunity(id);
        return ResponseEntity.ok(new MessageResponse("Community deleted successfully"));
    }
}
