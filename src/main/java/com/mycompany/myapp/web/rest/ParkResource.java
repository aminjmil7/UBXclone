package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.repository.ParkRepository;
import com.mycompany.myapp.service.ParkService;
import com.mycompany.myapp.service.dto.ParkDTO;
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
 * REST controller for managing {@link com.mycompany.myapp.domain.Park}.
 */
@RestController
@RequestMapping("/api")
public class ParkResource {

    private final Logger log = LoggerFactory.getLogger(ParkResource.class);

    private static final String ENTITY_NAME = "park";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ParkService parkService;

    private final ParkRepository parkRepository;

    public ParkResource(ParkService parkService, ParkRepository parkRepository) {
        this.parkService = parkService;
        this.parkRepository = parkRepository;
    }

    /**
     * {@code POST  /parks} : Create a new park.
     *
     * @param parkDTO the parkDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new parkDTO, or with status {@code 400 (Bad Request)} if the park has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/parks")
    public Mono<ResponseEntity<ParkDTO>> createPark(@RequestBody ParkDTO parkDTO) throws URISyntaxException {
        log.debug("REST request to save Park : {}", parkDTO);
        if (parkDTO.getId() != null) {
            throw new BadRequestAlertException("A new park cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return parkService
            .save(parkDTO)
            .map(result -> {
                try {
                    return ResponseEntity
                        .created(new URI("/api/parks/" + result.getId()))
                        .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
                        .body(result);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    /**
     * {@code PUT  /parks/:id} : Updates an existing park.
     *
     * @param id the id of the parkDTO to save.
     * @param parkDTO the parkDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated parkDTO,
     * or with status {@code 400 (Bad Request)} if the parkDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the parkDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/parks/{id}")
    public Mono<ResponseEntity<ParkDTO>> updatePark(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody ParkDTO parkDTO
    ) throws URISyntaxException {
        log.debug("REST request to update Park : {}, {}", id, parkDTO);
        if (parkDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, parkDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return parkRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                return parkService
                    .update(parkDTO)
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
     * {@code PATCH  /parks/:id} : Partial updates given fields of an existing park, field will ignore if it is null
     *
     * @param id the id of the parkDTO to save.
     * @param parkDTO the parkDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated parkDTO,
     * or with status {@code 400 (Bad Request)} if the parkDTO is not valid,
     * or with status {@code 404 (Not Found)} if the parkDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the parkDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/parks/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<ParkDTO>> partialUpdatePark(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody ParkDTO parkDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update Park partially : {}, {}", id, parkDTO);
        if (parkDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, parkDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return parkRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<ParkDTO> result = parkService.partialUpdate(parkDTO);

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
     * {@code GET  /parks} : get all the parks.
     *
     * @param pageable the pagination information.
     * @param request a {@link ServerHttpRequest} request.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of parks in body.
     */
    @GetMapping("/parks")
    public Mono<ResponseEntity<List<ParkDTO>>> getAllParks(
        @org.springdoc.api.annotations.ParameterObject Pageable pageable,
        ServerHttpRequest request
    ) {
        log.debug("REST request to get a page of Parks");
        return parkService
            .countAll()
            .zipWith(parkService.findAll(pageable).collectList())
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
     * {@code GET  /parks/:id} : get the "id" park.
     *
     * @param id the id of the parkDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the parkDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/parks/{id}")
    public Mono<ResponseEntity<ParkDTO>> getPark(@PathVariable Long id) {
        log.debug("REST request to get Park : {}", id);
        Mono<ParkDTO> parkDTO = parkService.findOne(id);
        return ResponseUtil.wrapOrNotFound(parkDTO);
    }

    /**
     * {@code DELETE  /parks/:id} : delete the "id" park.
     *
     * @param id the id of the parkDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/parks/{id}")
    public Mono<ResponseEntity<Void>> deletePark(@PathVariable Long id) {
        log.debug("REST request to delete Park : {}", id);
        return parkService
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
