package com.apps.service;

import org.springframework.data.jpa.repository.JpaRepository;
import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Abstract base implementation of BaseService that provides common CRUD functionality
 * @param <E> The entity type
 * @param <T> The response DTO type
 * @param <R> The request DTO type
 * @param <ID> The type of the entity's ID
 */
public abstract class AbstractBaseService<E, T, R, ID> implements BaseService<T, R, ID> {

    protected abstract JpaRepository<E, ID> getRepository();
    protected abstract E createEntity(R request);
    protected abstract void updateEntity(E entity, R request);
    protected abstract T convertToResponse(E entity);
    protected abstract String getEntityName();

    @Override
    public T create(R request) {
        E entity = createEntity(request);
        E savedEntity = getRepository().save(entity);
        return convertToResponse(savedEntity);
    }

    @Override
    public List<T> getAll() {
        return getRepository().findAll().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public T getById(ID id) {
        E entity = findEntityById(id);
        return convertToResponse(entity);
    }

    @Override
    public T update(ID id, R request) {
        E entity = findEntityById(id);
        updateEntity(entity, request);
        E updatedEntity = getRepository().save(entity);
        return convertToResponse(updatedEntity);
    }

    @Override
    public void delete(ID id) {
        if (!getRepository().existsById(id)) {
            throw new EntityNotFoundException(getEntityName() + " not found with id: " + id);
        }
        getRepository().deleteById(id);
    }

    protected E findEntityById(ID id) {
        return getRepository().findById(id)
                .orElseThrow(() -> new EntityNotFoundException(getEntityName() + " not found with id: " + id));
    }

    protected <V> List<T> convertList(List<E> entities, Function<E, T> converter) {
        return entities.stream()
                .map(converter)
                .collect(Collectors.toList());
    }
}
