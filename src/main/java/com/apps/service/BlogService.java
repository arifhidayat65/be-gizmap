package com.apps.service;

import com.apps.model.Blog;
import com.apps.model.User;
import com.apps.repository.BlogRepository;
import com.apps.repository.UserRepository;
import com.apps.payload.request.BlogRequest;
import com.apps.payload.response.BlogResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BlogService {

    @Autowired
    private BlogRepository blogRepository;

    @Autowired
    private UserRepository userRepository;

    @Transactional
    public BlogResponse createBlog(BlogRequest blogRequest) {
        User currentUser = getCurrentUser();
        
        Blog blog = new Blog();
        blog.setTitle(blogRequest.getTitle());
        blog.setContent(blogRequest.getContent());
        blog.setAuthor(currentUser);
        
        Blog savedBlog = blogRepository.save(blog);
        return convertToResponse(savedBlog);
    }

    @Transactional(readOnly = true)
    public List<BlogResponse> getAllBlogs() {
        return blogRepository.findAll().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public BlogResponse getBlogById(Long id) {
        Blog blog = blogRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Blog not found with id: " + id));
        return convertToResponse(blog);
    }

    @Transactional(readOnly = true)
    public List<BlogResponse> getBlogsByCurrentUser() {
        User currentUser = getCurrentUser();
        return blogRepository.findByAuthorOrderByCreatedAtDesc(currentUser).stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public BlogResponse updateBlog(Long id, BlogRequest blogRequest) {
        Blog blog = blogRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Blog not found with id: " + id));

        // Check if the current user is the author
        User currentUser = getCurrentUser();
        if (!blog.getAuthor().getId().equals(currentUser.getId())) {
            throw new IllegalStateException("You can only update your own blogs");
        }

        blog.setTitle(blogRequest.getTitle());
        blog.setContent(blogRequest.getContent());

        Blog updatedBlog = blogRepository.save(blog);
        return convertToResponse(updatedBlog);
    }

    @Transactional
    public void deleteBlog(Long id) {
        Blog blog = blogRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Blog not found with id: " + id));

        // Check if the current user is the author
        User currentUser = getCurrentUser();
        if (!blog.getAuthor().getId().equals(currentUser.getId())) {
            throw new IllegalStateException("You can only delete your own blogs");
        }

        blogRepository.deleteById(id);
    }

    private BlogResponse convertToResponse(Blog blog) {
        BlogResponse response = new BlogResponse();
        response.setId(blog.getId());
        response.setTitle(blog.getTitle());
        response.setContent(blog.getContent());
        response.setAuthorUsername(blog.getAuthor().getUsername());
        response.setCreatedAt(blog.getCreatedAt());
        response.setUpdatedAt(blog.getUpdatedAt());
        return response;
    }

    private User getCurrentUser() {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
    }
}
