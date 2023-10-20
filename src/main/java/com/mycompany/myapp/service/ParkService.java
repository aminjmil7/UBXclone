package com.mycompany.myapp.service;

import com.mycompany.myapp.service.dto.ParkDTO;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Interface for managing {@link com.mycompany.myapp.domain.Park}.
 */
public interface ParkService {
    /**
     * Save a park.
     *
     * @param parkDTO the entity to save.
     * @return the persisted entity.
     */
    Mono<ParkDTO> save(ParkDTO parkDTO);

    /**
     * Updates a park.
     *
     * @param parkDTO the entity to update.
     * @return the persisted entity.
     */
    Mono<ParkDTO> update(ParkDTO parkDTO);

    /**
     * Partially updates a park.
     *
     * @param parkDTO the entity to update partially.
     * @return the persisted entity.
     */
    Mono<ParkDTO> partialUpdate(ParkDTO parkDTO);

    /**
     * Get all the parks.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Flux<ParkDTO> findAll(Pageable pageable);

    /**
     * Returns the number of parks available.
     * @return the number of entities in the database.
     *
     */
    Mono<Long> countAll();

    /**
     * Get the "id" park.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Mono<ParkDTO> findOne(Long id);

    /**
     * Delete the "id" park.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    Mono<Void> delete(Long id);
}
