package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.repository.EquipementRepository;
import com.mycompany.myapp.service.EquipementService;
import com.mycompany.myapp.service.dto.EquipementDTO;
import com.mycompany.myapp.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.reactive.ResponseUtil;

/**
 * REST controller for managing {@link com.mycompany.myapp.domain.Equipement}.
 */
@RestController
@RequestMapping("/api")
public class EquipementResource {

    private final Logger log = LoggerFactory.getLogger(EquipementResource.class);

    private static final String ENTITY_NAME = "equipement";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final EquipementService equipementService;

    private final EquipementRepository equipementRepository;

    public EquipementResource(EquipementService equipementService, EquipementRepository equipementRepository) {
        this.equipementService = equipementService;
        this.equipementRepository = equipementRepository;
    }

    /**
     * {@code POST  /equipements} : Create a new equipement.
     *
     * @param equipementDTO the equipementDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new equipementDTO, or with status {@code 400 (Bad Request)} if the equipement has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/equipements")
    public Mono<ResponseEntity<EquipementDTO>> createEquipement(@RequestBody EquipementDTO equipementDTO) throws URISyntaxException {
        log.debug("REST request to save Equipement : {}", equipementDTO);
        if (equipementDTO.getId() != null) {
            throw new BadRequestAlertException("A new equipement cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return equipementService
            .save(equipementDTO)
            .map(result -> {
                try {
                    return ResponseEntity
                        .created(new URI("/api/equipements/" + result.getId()))
                        .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
                        .body(result);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    /**
     * {@code PUT  /equipements/:id} : Updates an existing equipement.
     *
     * @param id the id of the equipementDTO to save.
     * @param equipementDTO the equipementDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated equipementDTO,
     * or with status {@code 400 (Bad Request)} if the equipementDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the equipementDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/equipements/{id}")
    public Mono<ResponseEntity<EquipementDTO>> updateEquipement(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody EquipementDTO equipementDTO
    ) throws URISyntaxException {
        log.debug("REST request to update Equipement : {}, {}", id, equipementDTO);
        if (equipementDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, equipementDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return equipementRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                return equipementService
                    .update(equipementDTO)
                    .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                    .map(result ->
                        ResponseEntity
                            .ok()
                            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
                            .body(result)
                    );
            });
    }

    /**
     * {@code PATCH  /equipements/:id} : Partial updates given fields of an existing equipement, field will ignore if it is null
     *
     * @param id the id of the equipementDTO to save.
     * @param equipementDTO the equipementDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated equipementDTO,
     * or with status {@code 400 (Bad Request)} if the equipementDTO is not valid,
     * or with status {@code 404 (Not Found)} if the equipementDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the equipementDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/equipements/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<EquipementDTO>> partialUpdateEquipement(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody EquipementDTO equipementDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update Equipement partially : {}, {}", id, equipementDTO);
        if (equipementDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, equipementDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return equipementRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<EquipementDTO> result = equipementService.partialUpdate(equipementDTO);

                return result
                    .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                    .map(res ->
                        ResponseEntity
                            .ok()
                            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, res.getId().toString()))
                            .body(res)
                    );
            });
    }

    /**
     * {@code GET  /equipements} : get all the equipements.
     *
     * @param pageable the pagination information.
     * @param request a {@link ServerHttpRequest} request.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of equipements in body.
     */
    @GetMapping("/equipements")
    public Mono<ResponseEntity<List<EquipementDTO>>> getAllEquipements(
        @org.springdoc.api.annotations.ParameterObject Pageable pageable,
        ServerHttpRequest request
    ) {
        log.debug("REST request to get a page of Equipements");
        return equipementService
            .countAll()
            .zipWith(equipementService.findAll(pageable).collectList())
            .map(countWithEntities ->
                ResponseEntity
                    .ok()
                    .headers(
                        PaginationUtil.generatePaginationHttpHeaders(
                            UriComponentsBuilder.fromHttpRequest(request),
                            new PageImpl<>(countWithEntities.getT2(), pageable, countWithEntities.getT1())
                        )
                    )
                    .body(countWithEntities.getT2())
            );
    }

    /**
     * {@code GET  /equipements/:id} : get the "id" equipement.
     *
     * @param id the id of the equipementDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the equipementDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/equipements/{id}")
    public Mono<ResponseEntity<EquipementDTO>> getEquipement(@PathVariable Long id) {
        log.debug("REST request to get Equipement : {}", id);
        Mono<EquipementDTO> equipementDTO = equipementService.findOne(id);
        return ResponseUtil.wrapOrNotFound(equipementDTO);
    }

    /**
     * {@code DELETE  /equipements/:id} : delete the "id" equipement.
     *
     * @param id the id of the equipementDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/equipements/{id}")
    public Mono<ResponseEntity<Void>> deleteEquipement(@PathVariable Long id) {
        log.debug("REST request to delete Equipement : {}", id);
        return equipementService
            .delete(id)
            .then(
                Mono.just(
                    ResponseEntity
                        .noContent()
                        .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
                        .build()
                )
            );
    }
}
