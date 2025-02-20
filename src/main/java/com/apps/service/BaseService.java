package com.apps.service;

import java.util.List;

/**
 * Generic base service interface for common CRUD operations
 * @param <T> The response DTO type
 * @param <R> The request DTO type
 * @param <ID> The type of the entity's ID
 */
public interface BaseService<T, R, ID> {
    /**
     * Create a new entity
     * @param request The request DTO containing entity data
     * @return The response DTO of the created entity
     */
    T create(R request);

    /**
     * Get all entities
     * @return List of response DTOs
     */
    List<T> getAll();

    /**
     * Get an entity by its ID
     * @param id The entity ID
     * @return The response DTO of the found entity
     */
    T getById(ID id);

    /**
     * Update an existing entity
     * @param id The entity ID
     * @param request The request DTO containing updated data
     * @return The response DTO of the updated entity
     */
    T update(ID id, R request);

    /**
     * Delete an entity by its ID
     * @param id The entity ID
     */
    void delete(ID id);
}
