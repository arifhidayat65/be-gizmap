package com.apps.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * Base controller interface defining standard REST endpoints
 * @param <T> The response DTO type
 * @param <R> The request DTO type
 * @param <ID> The type of the entity's ID
 */
public interface BaseController<T, R, ID> {
    /**
     * Create a new entity
     * @param request The request DTO containing entity data
     * @return ResponseEntity containing the created entity's response DTO
     */
    @PostMapping
    ResponseEntity<T> create(@Valid @RequestBody R request);

    /**
     * Get all entities
     * @return ResponseEntity containing a list of response DTOs
     */
    @GetMapping
    ResponseEntity<List<T>> getAll();

    /**
     * Get an entity by its ID
     * @param id The entity ID
     * @return ResponseEntity containing the found entity's response DTO
     */
    @GetMapping("/{id}")
    ResponseEntity<T> getById(@PathVariable ID id);

    /**
     * Update an existing entity
     * @param id The entity ID
     * @param request The request DTO containing updated data
     * @return ResponseEntity containing the updated entity's response DTO
     */
    @PutMapping("/{id}")
    ResponseEntity<T> update(@PathVariable ID id, @Valid @RequestBody R request);

    /**
     * Delete an entity by its ID
     * @param id The entity ID
     * @return ResponseEntity with no content
     */
    @DeleteMapping("/{id}")
    ResponseEntity<Void> delete(@PathVariable ID id);
}
