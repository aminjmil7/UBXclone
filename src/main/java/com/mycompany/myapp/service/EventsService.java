package com.mycompany.myapp.service;

import com.mycompany.myapp.service.dto.EventsDTO;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Interface for managing {@link com.mycompany.myapp.domain.Events}.
 */
public interface EventsService {
    /**
     * Save a events.
     *
     * @param eventsDTO the entity to save.
     * @return the persisted entity.
     */
    Mono<EventsDTO> save(EventsDTO eventsDTO);

    /**
     * Updates a events.
     *
     * @param eventsDTO the entity to update.
     * @return the persisted entity.
     */
    Mono<EventsDTO> update(EventsDTO eventsDTO);

    /**
     * Partially updates a events.
     *
     * @param eventsDTO the entity to update partially.
     * @return the persisted entity.
     */
    Mono<EventsDTO> partialUpdate(EventsDTO eventsDTO);

    /**
     * Get all the events.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Flux<EventsDTO> findAll(Pageable pageable);

    /**
     * Returns the number of events available.
     * @return the number of entities in the database.
     *
     */
    Mono<Long> countAll();

    /**
     * Get the "id" events.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Mono<EventsDTO> findOne(Long id);

    /**
     * Delete the "id" events.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    Mono<Void> delete(Long id);
}
