package com.mycompany.myapp.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;

import com.mycompany.myapp.IntegrationTest;
import com.mycompany.myapp.domain.Events;
import com.mycompany.myapp.repository.EntityManager;
import com.mycompany.myapp.repository.EventsRepository;
import com.mycompany.myapp.service.dto.EventsDTO;
import com.mycompany.myapp.service.mapper.EventsMapper;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.reactive.server.WebTestClient;

/**
 * Integration tests for the {@link EventsResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class EventsResourceIT {

    private static final String DEFAULT_EVENT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_EVENT_NAME = "BBBBBBBBBB";

    private static final Instant DEFAULT_EVENT_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_EVENT_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Integer DEFAULT_USER_ID = 1;
    private static final Integer UPDATED_USER_ID = 2;

    private static final String ENTITY_API_URL = "/api/events";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private EventsRepository eventsRepository;

    @Autowired
    private EventsMapper eventsMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private Events events;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Events createEntity(EntityManager em) {
        Events events = new Events().eventName(DEFAULT_EVENT_NAME).eventDate(DEFAULT_EVENT_DATE).user_id(DEFAULT_USER_ID);
        return events;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Events createUpdatedEntity(EntityManager em) {
        Events events = new Events().eventName(UPDATED_EVENT_NAME).eventDate(UPDATED_EVENT_DATE).user_id(UPDATED_USER_ID);
        return events;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(Events.class).block();
        } catch (Exception e) {
            // It can fail, if other entities are still referring this - it will be removed later.
        }
    }

    @AfterEach
    public void cleanup() {
        deleteEntities(em);
    }

    @BeforeEach
    public void initTest() {
        deleteEntities(em);
        events = createEntity(em);
    }

    @Test
    void createEvents() throws Exception {
        int databaseSizeBeforeCreate = eventsRepository.findAll().collectList().block().size();
        // Create the Events
        EventsDTO eventsDTO = eventsMapper.toDto(events);
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(eventsDTO))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the Events in the database
        List<Events> eventsList = eventsRepository.findAll().collectList().block();
        assertThat(eventsList).hasSize(databaseSizeBeforeCreate + 1);
        Events testEvents = eventsList.get(eventsList.size() - 1);
        assertThat(testEvents.getEventName()).isEqualTo(DEFAULT_EVENT_NAME);
        assertThat(testEvents.getEventDate()).isEqualTo(DEFAULT_EVENT_DATE);
        assertThat(testEvents.getUser_id()).isEqualTo(DEFAULT_USER_ID);
    }

    @Test
    void createEventsWithExistingId() throws Exception {
        // Create the Events with an existing ID
        events.setId(1L);
        EventsDTO eventsDTO = eventsMapper.toDto(events);

        int databaseSizeBeforeCreate = eventsRepository.findAll().collectList().block().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(eventsDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Events in the database
        List<Events> eventsList = eventsRepository.findAll().collectList().block();
        assertThat(eventsList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    void getAllEvents() {
        // Initialize the database
        eventsRepository.save(events).block();

        // Get all the eventsList
        webTestClient
            .get()
            .uri(ENTITY_API_URL + "?sort=id,desc")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.[*].id")
            .value(hasItem(events.getId().intValue()))
            .jsonPath("$.[*].eventName")
            .value(hasItem(DEFAULT_EVENT_NAME))
            .jsonPath("$.[*].eventDate")
            .value(hasItem(DEFAULT_EVENT_DATE.toString()))
            .jsonPath("$.[*].user_id")
            .value(hasItem(DEFAULT_USER_ID));
    }

    @Test
    void getEvents() {
        // Initialize the database
        eventsRepository.save(events).block();

        // Get the events
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, events.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(events.getId().intValue()))
            .jsonPath("$.eventName")
            .value(is(DEFAULT_EVENT_NAME))
            .jsonPath("$.eventDate")
            .value(is(DEFAULT_EVENT_DATE.toString()))
            .jsonPath("$.user_id")
            .value(is(DEFAULT_USER_ID));
    }

    @Test
    void getNonExistingEvents() {
        // Get the events
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingEvents() throws Exception {
        // Initialize the database
        eventsRepository.save(events).block();

        int databaseSizeBeforeUpdate = eventsRepository.findAll().collectList().block().size();

        // Update the events
        Events updatedEvents = eventsRepository.findById(events.getId()).block();
        updatedEvents.eventName(UPDATED_EVENT_NAME).eventDate(UPDATED_EVENT_DATE).user_id(UPDATED_USER_ID);
        EventsDTO eventsDTO = eventsMapper.toDto(updatedEvents);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, eventsDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(eventsDTO))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Events in the database
        List<Events> eventsList = eventsRepository.findAll().collectList().block();
        assertThat(eventsList).hasSize(databaseSizeBeforeUpdate);
        Events testEvents = eventsList.get(eventsList.size() - 1);
        assertThat(testEvents.getEventName()).isEqualTo(UPDATED_EVENT_NAME);
        assertThat(testEvents.getEventDate()).isEqualTo(UPDATED_EVENT_DATE);
        assertThat(testEvents.getUser_id()).isEqualTo(UPDATED_USER_ID);
    }

    @Test
    void putNonExistingEvents() throws Exception {
        int databaseSizeBeforeUpdate = eventsRepository.findAll().collectList().block().size();
        events.setId(count.incrementAndGet());

        // Create the Events
        EventsDTO eventsDTO = eventsMapper.toDto(events);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, eventsDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(eventsDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Events in the database
        List<Events> eventsList = eventsRepository.findAll().collectList().block();
        assertThat(eventsList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchEvents() throws Exception {
        int databaseSizeBeforeUpdate = eventsRepository.findAll().collectList().block().size();
        events.setId(count.incrementAndGet());

        // Create the Events
        EventsDTO eventsDTO = eventsMapper.toDto(events);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(eventsDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Events in the database
        List<Events> eventsList = eventsRepository.findAll().collectList().block();
        assertThat(eventsList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamEvents() throws Exception {
        int databaseSizeBeforeUpdate = eventsRepository.findAll().collectList().block().size();
        events.setId(count.incrementAndGet());

        // Create the Events
        EventsDTO eventsDTO = eventsMapper.toDto(events);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(eventsDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Events in the database
        List<Events> eventsList = eventsRepository.findAll().collectList().block();
        assertThat(eventsList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateEventsWithPatch() throws Exception {
        // Initialize the database
        eventsRepository.save(events).block();

        int databaseSizeBeforeUpdate = eventsRepository.findAll().collectList().block().size();

        // Update the events using partial update
        Events partialUpdatedEvents = new Events();
        partialUpdatedEvents.setId(events.getId());

        partialUpdatedEvents.eventName(UPDATED_EVENT_NAME).eventDate(UPDATED_EVENT_DATE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedEvents.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedEvents))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Events in the database
        List<Events> eventsList = eventsRepository.findAll().collectList().block();
        assertThat(eventsList).hasSize(databaseSizeBeforeUpdate);
        Events testEvents = eventsList.get(eventsList.size() - 1);
        assertThat(testEvents.getEventName()).isEqualTo(UPDATED_EVENT_NAME);
        assertThat(testEvents.getEventDate()).isEqualTo(UPDATED_EVENT_DATE);
        assertThat(testEvents.getUser_id()).isEqualTo(DEFAULT_USER_ID);
    }

    @Test
    void fullUpdateEventsWithPatch() throws Exception {
        // Initialize the database
        eventsRepository.save(events).block();

        int databaseSizeBeforeUpdate = eventsRepository.findAll().collectList().block().size();

        // Update the events using partial update
        Events partialUpdatedEvents = new Events();
        partialUpdatedEvents.setId(events.getId());

        partialUpdatedEvents.eventName(UPDATED_EVENT_NAME).eventDate(UPDATED_EVENT_DATE).user_id(UPDATED_USER_ID);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedEvents.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedEvents))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Events in the database
        List<Events> eventsList = eventsRepository.findAll().collectList().block();
        assertThat(eventsList).hasSize(databaseSizeBeforeUpdate);
        Events testEvents = eventsList.get(eventsList.size() - 1);
        assertThat(testEvents.getEventName()).isEqualTo(UPDATED_EVENT_NAME);
        assertThat(testEvents.getEventDate()).isEqualTo(UPDATED_EVENT_DATE);
        assertThat(testEvents.getUser_id()).isEqualTo(UPDATED_USER_ID);
    }

    @Test
    void patchNonExistingEvents() throws Exception {
        int databaseSizeBeforeUpdate = eventsRepository.findAll().collectList().block().size();
        events.setId(count.incrementAndGet());

        // Create the Events
        EventsDTO eventsDTO = eventsMapper.toDto(events);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, eventsDTO.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(eventsDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Events in the database
        List<Events> eventsList = eventsRepository.findAll().collectList().block();
        assertThat(eventsList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchEvents() throws Exception {
        int databaseSizeBeforeUpdate = eventsRepository.findAll().collectList().block().size();
        events.setId(count.incrementAndGet());

        // Create the Events
        EventsDTO eventsDTO = eventsMapper.toDto(events);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(eventsDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Events in the database
        List<Events> eventsList = eventsRepository.findAll().collectList().block();
        assertThat(eventsList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamEvents() throws Exception {
        int databaseSizeBeforeUpdate = eventsRepository.findAll().collectList().block().size();
        events.setId(count.incrementAndGet());

        // Create the Events
        EventsDTO eventsDTO = eventsMapper.toDto(events);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(eventsDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Events in the database
        List<Events> eventsList = eventsRepository.findAll().collectList().block();
        assertThat(eventsList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteEvents() {
        // Initialize the database
        eventsRepository.save(events).block();

        int databaseSizeBeforeDelete = eventsRepository.findAll().collectList().block().size();

        // Delete the events
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, events.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<Events> eventsList = eventsRepository.findAll().collectList().block();
        assertThat(eventsList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
