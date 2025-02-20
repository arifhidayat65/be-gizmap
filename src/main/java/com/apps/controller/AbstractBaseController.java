package com.apps.controller;

import com.apps.service.BaseService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

/**
 * Abstract base implementation of BaseController that provides common CRUD functionality
 * @param <T> The response DTO type
 * @param <R> The request DTO type
 * @param <ID> The type of the entity's ID
 */
public abstract class AbstractBaseController<T, R, ID> implements BaseController<T, R, ID> {

    protected abstract BaseService<T, R, ID> getService();

    @Override
    public ResponseEntity<T> create(R request) {
        T response = getService().create(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity<List<T>> getAll() {
        List<T> responses = getService().getAll();
        return ResponseEntity.ok(responses);
    }

    @Override
    public ResponseEntity<T> getById(ID id) {
        T response = getService().getById(id);
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<T> update(ID id, R request) {
        T response = getService().update(id, request);
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<Void> delete(ID id) {
        getService().delete(id);
        return ResponseEntity.noContent().build();
    }
}
