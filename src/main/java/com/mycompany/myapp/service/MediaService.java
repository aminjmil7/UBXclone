package com.mycompany.myapp.service;

import com.mycompany.myapp.service.dto.MediaDTO;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Interface for managing {@link com.mycompany.myapp.domain.Media}.
 */
public interface MediaService {
    /**
     * Save a media.
     *
     * @param mediaDTO the entity to save.
     * @return the persisted entity.
     */
    Mono<MediaDTO> save(MediaDTO mediaDTO);

    /**
     * Updates a media.
     *
     * @param mediaDTO the entity to update.
     * @return the persisted entity.
     */
    Mono<MediaDTO> update(MediaDTO mediaDTO);

    /**
     * Partially updates a media.
     *
     * @param mediaDTO the entity to update partially.
     * @return the persisted entity.
     */
    Mono<MediaDTO> partialUpdate(MediaDTO mediaDTO);

    /**
     * Get all the media.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Flux<MediaDTO> findAll(Pageable pageable);

    /**
     * Returns the number of media available.
     * @return the number of entities in the database.
     *
     */
    Mono<Long> countAll();

    /**
     * Get the "id" media.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Mono<MediaDTO> findOne(Long id);

    /**
     * Delete the "id" media.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    Mono<Void> delete(Long id);
}
