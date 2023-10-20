package com.mycompany.myapp.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;

import com.mycompany.myapp.IntegrationTest;
import com.mycompany.myapp.domain.Rating;
import com.mycompany.myapp.repository.EntityManager;
import com.mycompany.myapp.repository.RatingRepository;
import com.mycompany.myapp.service.dto.RatingDTO;
import com.mycompany.myapp.service.mapper.RatingMapper;
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
 * Integration tests for the {@link RatingResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class RatingResourceIT {

    private static final String DEFAULT_COMMENT = "AAAAAAAAAA";
    private static final String UPDATED_COMMENT = "BBBBBBBBBB";

    private static final Integer DEFAULT_RATING_NUMBER = 1;
    private static final Integer UPDATED_RATING_NUMBER = 2;

    private static final Instant DEFAULT_RATING_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_RATING_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Integer DEFAULT_USER_ID = 1;
    private static final Integer UPDATED_USER_ID = 2;

    private static final String ENTITY_API_URL = "/api/ratings";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private RatingRepository ratingRepository;

    @Autowired
    private RatingMapper ratingMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private Rating rating;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Rating createEntity(EntityManager em) {
        Rating rating = new Rating()
            .comment(DEFAULT_COMMENT)
            .ratingNumber(DEFAULT_RATING_NUMBER)
            .ratingDate(DEFAULT_RATING_DATE)
            .user_id(DEFAULT_USER_ID);
        return rating;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Rating createUpdatedEntity(EntityManager em) {
        Rating rating = new Rating()
            .comment(UPDATED_COMMENT)
            .ratingNumber(UPDATED_RATING_NUMBER)
            .ratingDate(UPDATED_RATING_DATE)
            .user_id(UPDATED_USER_ID);
        return rating;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(Rating.class).block();
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
        rating = createEntity(em);
    }

    @Test
    void createRating() throws Exception {
        int databaseSizeBeforeCreate = ratingRepository.findAll().collectList().block().size();
        // Create the Rating
        RatingDTO ratingDTO = ratingMapper.toDto(rating);
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(ratingDTO))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the Rating in the database
        List<Rating> ratingList = ratingRepository.findAll().collectList().block();
        assertThat(ratingList).hasSize(databaseSizeBeforeCreate + 1);
        Rating testRating = ratingList.get(ratingList.size() - 1);
        assertThat(testRating.getComment()).isEqualTo(DEFAULT_COMMENT);
        assertThat(testRating.getRatingNumber()).isEqualTo(DEFAULT_RATING_NUMBER);
        assertThat(testRating.getRatingDate()).isEqualTo(DEFAULT_RATING_DATE);
        assertThat(testRating.getUser_id()).isEqualTo(DEFAULT_USER_ID);
    }

    @Test
    void createRatingWithExistingId() throws Exception {
        // Create the Rating with an existing ID
        rating.setId(1L);
        RatingDTO ratingDTO = ratingMapper.toDto(rating);

        int databaseSizeBeforeCreate = ratingRepository.findAll().collectList().block().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(ratingDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Rating in the database
        List<Rating> ratingList = ratingRepository.findAll().collectList().block();
        assertThat(ratingList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    void getAllRatings() {
        // Initialize the database
        ratingRepository.save(rating).block();

        // Get all the ratingList
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
            .value(hasItem(rating.getId().intValue()))
            .jsonPath("$.[*].comment")
            .value(hasItem(DEFAULT_COMMENT))
            .jsonPath("$.[*].ratingNumber")
            .value(hasItem(DEFAULT_RATING_NUMBER))
            .jsonPath("$.[*].ratingDate")
            .value(hasItem(DEFAULT_RATING_DATE.toString()))
            .jsonPath("$.[*].user_id")
            .value(hasItem(DEFAULT_USER_ID));
    }

    @Test
    void getRating() {
        // Initialize the database
        ratingRepository.save(rating).block();

        // Get the rating
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, rating.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(rating.getId().intValue()))
            .jsonPath("$.comment")
            .value(is(DEFAULT_COMMENT))
            .jsonPath("$.ratingNumber")
            .value(is(DEFAULT_RATING_NUMBER))
            .jsonPath("$.ratingDate")
            .value(is(DEFAULT_RATING_DATE.toString()))
            .jsonPath("$.user_id")
            .value(is(DEFAULT_USER_ID));
    }

    @Test
    void getNonExistingRating() {
        // Get the rating
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingRating() throws Exception {
        // Initialize the database
        ratingRepository.save(rating).block();

        int databaseSizeBeforeUpdate = ratingRepository.findAll().collectList().block().size();

        // Update the rating
        Rating updatedRating = ratingRepository.findById(rating.getId()).block();
        updatedRating.comment(UPDATED_COMMENT).ratingNumber(UPDATED_RATING_NUMBER).ratingDate(UPDATED_RATING_DATE).user_id(UPDATED_USER_ID);
        RatingDTO ratingDTO = ratingMapper.toDto(updatedRating);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, ratingDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(ratingDTO))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Rating in the database
        List<Rating> ratingList = ratingRepository.findAll().collectList().block();
        assertThat(ratingList).hasSize(databaseSizeBeforeUpdate);
        Rating testRating = ratingList.get(ratingList.size() - 1);
        assertThat(testRating.getComment()).isEqualTo(UPDATED_COMMENT);
        assertThat(testRating.getRatingNumber()).isEqualTo(UPDATED_RATING_NUMBER);
        assertThat(testRating.getRatingDate()).isEqualTo(UPDATED_RATING_DATE);
        assertThat(testRating.getUser_id()).isEqualTo(UPDATED_USER_ID);
    }

    @Test
    void putNonExistingRating() throws Exception {
        int databaseSizeBeforeUpdate = ratingRepository.findAll().collectList().block().size();
        rating.setId(count.incrementAndGet());

        // Create the Rating
        RatingDTO ratingDTO = ratingMapper.toDto(rating);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, ratingDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(ratingDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Rating in the database
        List<Rating> ratingList = ratingRepository.findAll().collectList().block();
        assertThat(ratingList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchRating() throws Exception {
        int databaseSizeBeforeUpdate = ratingRepository.findAll().collectList().block().size();
        rating.setId(count.incrementAndGet());

        // Create the Rating
        RatingDTO ratingDTO = ratingMapper.toDto(rating);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(ratingDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Rating in the database
        List<Rating> ratingList = ratingRepository.findAll().collectList().block();
        assertThat(ratingList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamRating() throws Exception {
        int databaseSizeBeforeUpdate = ratingRepository.findAll().collectList().block().size();
        rating.setId(count.incrementAndGet());

        // Create the Rating
        RatingDTO ratingDTO = ratingMapper.toDto(rating);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(ratingDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Rating in the database
        List<Rating> ratingList = ratingRepository.findAll().collectList().block();
        assertThat(ratingList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateRatingWithPatch() throws Exception {
        // Initialize the database
        ratingRepository.save(rating).block();

        int databaseSizeBeforeUpdate = ratingRepository.findAll().collectList().block().size();

        // Update the rating using partial update
        Rating partialUpdatedRating = new Rating();
        partialUpdatedRating.setId(rating.getId());

        partialUpdatedRating.comment(UPDATED_COMMENT).ratingNumber(UPDATED_RATING_NUMBER);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedRating.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedRating))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Rating in the database
        List<Rating> ratingList = ratingRepository.findAll().collectList().block();
        assertThat(ratingList).hasSize(databaseSizeBeforeUpdate);
        Rating testRating = ratingList.get(ratingList.size() - 1);
        assertThat(testRating.getComment()).isEqualTo(UPDATED_COMMENT);
        assertThat(testRating.getRatingNumber()).isEqualTo(UPDATED_RATING_NUMBER);
        assertThat(testRating.getRatingDate()).isEqualTo(DEFAULT_RATING_DATE);
        assertThat(testRating.getUser_id()).isEqualTo(DEFAULT_USER_ID);
    }

    @Test
    void fullUpdateRatingWithPatch() throws Exception {
        // Initialize the database
        ratingRepository.save(rating).block();

        int databaseSizeBeforeUpdate = ratingRepository.findAll().collectList().block().size();

        // Update the rating using partial update
        Rating partialUpdatedRating = new Rating();
        partialUpdatedRating.setId(rating.getId());

        partialUpdatedRating
            .comment(UPDATED_COMMENT)
            .ratingNumber(UPDATED_RATING_NUMBER)
            .ratingDate(UPDATED_RATING_DATE)
            .user_id(UPDATED_USER_ID);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedRating.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedRating))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Rating in the database
        List<Rating> ratingList = ratingRepository.findAll().collectList().block();
        assertThat(ratingList).hasSize(databaseSizeBeforeUpdate);
        Rating testRating = ratingList.get(ratingList.size() - 1);
        assertThat(testRating.getComment()).isEqualTo(UPDATED_COMMENT);
        assertThat(testRating.getRatingNumber()).isEqualTo(UPDATED_RATING_NUMBER);
        assertThat(testRating.getRatingDate()).isEqualTo(UPDATED_RATING_DATE);
        assertThat(testRating.getUser_id()).isEqualTo(UPDATED_USER_ID);
    }

    @Test
    void patchNonExistingRating() throws Exception {
        int databaseSizeBeforeUpdate = ratingRepository.findAll().collectList().block().size();
        rating.setId(count.incrementAndGet());

        // Create the Rating
        RatingDTO ratingDTO = ratingMapper.toDto(rating);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, ratingDTO.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(ratingDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Rating in the database
        List<Rating> ratingList = ratingRepository.findAll().collectList().block();
        assertThat(ratingList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchRating() throws Exception {
        int databaseSizeBeforeUpdate = ratingRepository.findAll().collectList().block().size();
        rating.setId(count.incrementAndGet());

        // Create the Rating
        RatingDTO ratingDTO = ratingMapper.toDto(rating);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(ratingDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Rating in the database
        List<Rating> ratingList = ratingRepository.findAll().collectList().block();
        assertThat(ratingList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamRating() throws Exception {
        int databaseSizeBeforeUpdate = ratingRepository.findAll().collectList().block().size();
        rating.setId(count.incrementAndGet());

        // Create the Rating
        RatingDTO ratingDTO = ratingMapper.toDto(rating);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(ratingDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Rating in the database
        List<Rating> ratingList = ratingRepository.findAll().collectList().block();
        assertThat(ratingList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteRating() {
        // Initialize the database
        ratingRepository.save(rating).block();

        int databaseSizeBeforeDelete = ratingRepository.findAll().collectList().block().size();

        // Delete the rating
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, rating.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<Rating> ratingList = ratingRepository.findAll().collectList().block();
        assertThat(ratingList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
