package com.mycompany.myapp.service;

import com.mycompany.myapp.service.dto.RatingDTO;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Interface for managing {@link com.mycompany.myapp.domain.Rating}.
 */
public interface RatingService {
    /**
     * Save a rating.
     *
     * @param ratingDTO the entity to save.
     * @return the persisted entity.
     */
    Mono<RatingDTO> save(RatingDTO ratingDTO);

    /**
     * Updates a rating.
     *
     * @param ratingDTO the entity to update.
     * @return the persisted entity.
     */
    Mono<RatingDTO> update(RatingDTO ratingDTO);

    /**
     * Partially updates a rating.
     *
     * @param ratingDTO the entity to update partially.
     * @return the persisted entity.
     */
    Mono<RatingDTO> partialUpdate(RatingDTO ratingDTO);

    /**
     * Get all the ratings.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Flux<RatingDTO> findAll(Pageable pageable);

    /**
     * Returns the number of ratings available.
     * @return the number of entities in the database.
     *
     */
    Mono<Long> countAll();

    /**
     * Get the "id" rating.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Mono<RatingDTO> findOne(Long id);

    /**
     * Delete the "id" rating.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    Mono<Void> delete(Long id);
}
