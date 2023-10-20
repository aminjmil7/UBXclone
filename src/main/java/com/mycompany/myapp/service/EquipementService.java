package com.mycompany.myapp.service;

import com.mycompany.myapp.service.dto.EquipementDTO;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Interface for managing {@link com.mycompany.myapp.domain.Equipement}.
 */
public interface EquipementService {
    /**
     * Save a equipement.
     *
     * @param equipementDTO the entity to save.
     * @return the persisted entity.
     */
    Mono<EquipementDTO> save(EquipementDTO equipementDTO);

    /**
     * Updates a equipement.
     *
     * @param equipementDTO the entity to update.
     * @return the persisted entity.
     */
    Mono<EquipementDTO> update(EquipementDTO equipementDTO);

    /**
     * Partially updates a equipement.
     *
     * @param equipementDTO the entity to update partially.
     * @return the persisted entity.
     */
    Mono<EquipementDTO> partialUpdate(EquipementDTO equipementDTO);

    /**
     * Get all the equipements.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Flux<EquipementDTO> findAll(Pageable pageable);

    /**
     * Returns the number of equipements available.
     * @return the number of entities in the database.
     *
     */
    Mono<Long> countAll();

    /**
     * Get the "id" equipement.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Mono<EquipementDTO> findOne(Long id);

    /**
     * Delete the "id" equipement.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    Mono<Void> delete(Long id);
}
