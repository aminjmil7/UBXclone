package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.repository.EventsRepository;
import com.mycompany.myapp.service.EventsService;
import com.mycompany.myapp.service.dto.EventsDTO;
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
 * REST controller for managing {@link com.mycompany.myapp.domain.Events}.
 */
@RestController
@RequestMapping("/api")
public class EventsResource {

    private final Logger log = LoggerFactory.getLogger(EventsResource.class);

    private static final String ENTITY_NAME = "events";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final EventsService eventsService;

    private final EventsRepository eventsRepository;

    public EventsResource(EventsService eventsService, EventsRepository eventsRepository) {
        this.eventsService = eventsService;
        this.eventsRepository = eventsRepository;
    }

    /**
     * {@code POST  /events} : Create a new events.
     *
     * @param eventsDTO the eventsDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new eventsDTO, or with status {@code 400 (Bad Request)} if the events has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/events")
    public Mono<ResponseEntity<EventsDTO>> createEvents(@RequestBody EventsDTO eventsDTO) throws URISyntaxException {
        log.debug("REST request to save Events : {}", eventsDTO);
        if (eventsDTO.getId() != null) {
            throw new BadRequestAlertException("A new events cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return eventsService
            .save(eventsDTO)
            .map(result -> {
                try {
                    return ResponseEntity
                        .created(new URI("/api/events/" + result.getId()))
                        .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
                        .body(result);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    /**
     * {@code PUT  /events/:id} : Updates an existing events.
     *
     * @param id the id of the eventsDTO to save.
     * @param eventsDTO the eventsDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated eventsDTO,
     * or with status {@code 400 (Bad Request)} if the eventsDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the eventsDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/events/{id}")
    public Mono<ResponseEntity<EventsDTO>> updateEvents(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody EventsDTO eventsDTO
    ) throws URISyntaxException {
        log.debug("REST request to update Events : {}, {}", id, eventsDTO);
        if (eventsDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, eventsDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return eventsRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                return eventsService
                    .update(eventsDTO)
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
     * {@code PATCH  /events/:id} : Partial updates given fields of an existing events, field will ignore if it is null
     *
     * @param id the id of the eventsDTO to save.
     * @param eventsDTO the eventsDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated eventsDTO,
     * or with status {@code 400 (Bad Request)} if the eventsDTO is not valid,
     * or with status {@code 404 (Not Found)} if the eventsDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the eventsDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/events/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<EventsDTO>> partialUpdateEvents(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody EventsDTO eventsDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update Events partially : {}, {}", id, eventsDTO);
        if (eventsDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, eventsDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return eventsRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<EventsDTO> result = eventsService.partialUpdate(eventsDTO);

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
     * {@code GET  /events} : get all the events.
     *
     * @param pageable the pagination information.
     * @param request a {@link ServerHttpRequest} request.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of events in body.
     */
    @GetMapping("/events")
    public Mono<ResponseEntity<List<EventsDTO>>> getAllEvents(
        @org.springdoc.api.annotations.ParameterObject Pageable pageable,
        ServerHttpRequest request
    ) {
        log.debug("REST request to get a page of Events");
        return eventsService
            .countAll()
            .zipWith(eventsService.findAll(pageable).collectList())
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
     * {@code GET  /events/:id} : get the "id" events.
     *
     * @param id the id of the eventsDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the eventsDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/events/{id}")
    public Mono<ResponseEntity<EventsDTO>> getEvents(@PathVariable Long id) {
        log.debug("REST request to get Events : {}", id);
        Mono<EventsDTO> eventsDTO = eventsService.findOne(id);
        return ResponseUtil.wrapOrNotFound(eventsDTO);
    }

    /**
     * {@code DELETE  /events/:id} : delete the "id" events.
     *
     * @param id the id of the eventsDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/events/{id}")
    public Mono<ResponseEntity<Void>> deleteEvents(@PathVariable Long id) {
        log.debug("REST request to delete Events : {}", id);
        return eventsService
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
