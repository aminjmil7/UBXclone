package com.mycompany.myapp.web.rest;

import static com.mycompany.myapp.web.rest.TestUtil.sameNumber;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;

import com.mycompany.myapp.IntegrationTest;
import com.mycompany.myapp.domain.Park;
import com.mycompany.myapp.repository.EntityManager;
import com.mycompany.myapp.repository.ParkRepository;
import com.mycompany.myapp.service.dto.ParkDTO;
import com.mycompany.myapp.service.mapper.ParkMapper;
import java.math.BigDecimal;
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
 * Integration tests for the {@link ParkResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class ParkResourceIT {

    private static final String DEFAULT_PARK_NAME = "AAAAAAAAAA";
    private static final String UPDATED_PARK_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_PARK_ADDRESS = "AAAAAAAAAA";
    private static final String UPDATED_PARK_ADDRESS = "BBBBBBBBBB";

    private static final BigDecimal DEFAULT_LONGTITUDE = new BigDecimal(1);
    private static final BigDecimal UPDATED_LONGTITUDE = new BigDecimal(2);

    private static final BigDecimal DEFAULT_LATITUDE = new BigDecimal(1);
    private static final BigDecimal UPDATED_LATITUDE = new BigDecimal(2);

    private static final Boolean DEFAULT_VERIFIED = false;
    private static final Boolean UPDATED_VERIFIED = true;

    private static final Instant DEFAULT_DATE_INSTALL = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_DATE_INSTALL = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Instant DEFAULT_DATE_OPEN = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_DATE_OPEN = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Instant DEFAULT_DATE_CLOSE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_DATE_CLOSE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String DEFAULT_NOTE = "AAAAAAAAAA";
    private static final String UPDATED_NOTE = "BBBBBBBBBB";

    private static final String DEFAULT_RESELLER = "AAAAAAAAAA";
    private static final String UPDATED_RESELLER = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/parks";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ParkRepository parkRepository;

    @Autowired
    private ParkMapper parkMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private Park park;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Park createEntity(EntityManager em) {
        Park park = new Park()
            .parkName(DEFAULT_PARK_NAME)
            .parkAddress(DEFAULT_PARK_ADDRESS)
            .longtitude(DEFAULT_LONGTITUDE)
            .latitude(DEFAULT_LATITUDE)
            .verified(DEFAULT_VERIFIED)
            .dateInstall(DEFAULT_DATE_INSTALL)
            .dateOpen(DEFAULT_DATE_OPEN)
            .dateClose(DEFAULT_DATE_CLOSE)
            .note(DEFAULT_NOTE)
            .reseller(DEFAULT_RESELLER);
        return park;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Park createUpdatedEntity(EntityManager em) {
        Park park = new Park()
            .parkName(UPDATED_PARK_NAME)
            .parkAddress(UPDATED_PARK_ADDRESS)
            .longtitude(UPDATED_LONGTITUDE)
            .latitude(UPDATED_LATITUDE)
            .verified(UPDATED_VERIFIED)
            .dateInstall(UPDATED_DATE_INSTALL)
            .dateOpen(UPDATED_DATE_OPEN)
            .dateClose(UPDATED_DATE_CLOSE)
            .note(UPDATED_NOTE)
            .reseller(UPDATED_RESELLER);
        return park;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(Park.class).block();
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
        park = createEntity(em);
    }

    @Test
    void createPark() throws Exception {
        int databaseSizeBeforeCreate = parkRepository.findAll().collectList().block().size();
        // Create the Park
        ParkDTO parkDTO = parkMapper.toDto(park);
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(parkDTO))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the Park in the database
        List<Park> parkList = parkRepository.findAll().collectList().block();
        assertThat(parkList).hasSize(databaseSizeBeforeCreate + 1);
        Park testPark = parkList.get(parkList.size() - 1);
        assertThat(testPark.getParkName()).isEqualTo(DEFAULT_PARK_NAME);
        assertThat(testPark.getParkAddress()).isEqualTo(DEFAULT_PARK_ADDRESS);
        assertThat(testPark.getLongtitude()).isEqualByComparingTo(DEFAULT_LONGTITUDE);
        assertThat(testPark.getLatitude()).isEqualByComparingTo(DEFAULT_LATITUDE);
        assertThat(testPark.getVerified()).isEqualTo(DEFAULT_VERIFIED);
        assertThat(testPark.getDateInstall()).isEqualTo(DEFAULT_DATE_INSTALL);
        assertThat(testPark.getDateOpen()).isEqualTo(DEFAULT_DATE_OPEN);
        assertThat(testPark.getDateClose()).isEqualTo(DEFAULT_DATE_CLOSE);
        assertThat(testPark.getNote()).isEqualTo(DEFAULT_NOTE);
        assertThat(testPark.getReseller()).isEqualTo(DEFAULT_RESELLER);
    }

    @Test
    void createParkWithExistingId() throws Exception {
        // Create the Park with an existing ID
        park.setId(1L);
        ParkDTO parkDTO = parkMapper.toDto(park);

        int databaseSizeBeforeCreate = parkRepository.findAll().collectList().block().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(parkDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Park in the database
        List<Park> parkList = parkRepository.findAll().collectList().block();
        assertThat(parkList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    void getAllParks() {
        // Initialize the database
        parkRepository.save(park).block();

        // Get all the parkList
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
            .value(hasItem(park.getId().intValue()))
            .jsonPath("$.[*].parkName")
            .value(hasItem(DEFAULT_PARK_NAME))
            .jsonPath("$.[*].parkAddress")
            .value(hasItem(DEFAULT_PARK_ADDRESS))
            .jsonPath("$.[*].longtitude")
            .value(hasItem(sameNumber(DEFAULT_LONGTITUDE)))
            .jsonPath("$.[*].latitude")
            .value(hasItem(sameNumber(DEFAULT_LATITUDE)))
            .jsonPath("$.[*].verified")
            .value(hasItem(DEFAULT_VERIFIED.booleanValue()))
            .jsonPath("$.[*].dateInstall")
            .value(hasItem(DEFAULT_DATE_INSTALL.toString()))
            .jsonPath("$.[*].dateOpen")
            .value(hasItem(DEFAULT_DATE_OPEN.toString()))
            .jsonPath("$.[*].dateClose")
            .value(hasItem(DEFAULT_DATE_CLOSE.toString()))
            .jsonPath("$.[*].note")
            .value(hasItem(DEFAULT_NOTE))
            .jsonPath("$.[*].reseller")
            .value(hasItem(DEFAULT_RESELLER));
    }

    @Test
    void getPark() {
        // Initialize the database
        parkRepository.save(park).block();

        // Get the park
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, park.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(park.getId().intValue()))
            .jsonPath("$.parkName")
            .value(is(DEFAULT_PARK_NAME))
            .jsonPath("$.parkAddress")
            .value(is(DEFAULT_PARK_ADDRESS))
            .jsonPath("$.longtitude")
            .value(is(sameNumber(DEFAULT_LONGTITUDE)))
            .jsonPath("$.latitude")
            .value(is(sameNumber(DEFAULT_LATITUDE)))
            .jsonPath("$.verified")
            .value(is(DEFAULT_VERIFIED.booleanValue()))
            .jsonPath("$.dateInstall")
            .value(is(DEFAULT_DATE_INSTALL.toString()))
            .jsonPath("$.dateOpen")
            .value(is(DEFAULT_DATE_OPEN.toString()))
            .jsonPath("$.dateClose")
            .value(is(DEFAULT_DATE_CLOSE.toString()))
            .jsonPath("$.note")
            .value(is(DEFAULT_NOTE))
            .jsonPath("$.reseller")
            .value(is(DEFAULT_RESELLER));
    }

    @Test
    void getNonExistingPark() {
        // Get the park
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingPark() throws Exception {
        // Initialize the database
        parkRepository.save(park).block();

        int databaseSizeBeforeUpdate = parkRepository.findAll().collectList().block().size();

        // Update the park
        Park updatedPark = parkRepository.findById(park.getId()).block();
        updatedPark
            .parkName(UPDATED_PARK_NAME)
            .parkAddress(UPDATED_PARK_ADDRESS)
            .longtitude(UPDATED_LONGTITUDE)
            .latitude(UPDATED_LATITUDE)
            .verified(UPDATED_VERIFIED)
            .dateInstall(UPDATED_DATE_INSTALL)
            .dateOpen(UPDATED_DATE_OPEN)
            .dateClose(UPDATED_DATE_CLOSE)
            .note(UPDATED_NOTE)
            .reseller(UPDATED_RESELLER);
        ParkDTO parkDTO = parkMapper.toDto(updatedPark);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, parkDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(parkDTO))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Park in the database
        List<Park> parkList = parkRepository.findAll().collectList().block();
        assertThat(parkList).hasSize(databaseSizeBeforeUpdate);
        Park testPark = parkList.get(parkList.size() - 1);
        assertThat(testPark.getParkName()).isEqualTo(UPDATED_PARK_NAME);
        assertThat(testPark.getParkAddress()).isEqualTo(UPDATED_PARK_ADDRESS);
        assertThat(testPark.getLongtitude()).isEqualByComparingTo(UPDATED_LONGTITUDE);
        assertThat(testPark.getLatitude()).isEqualByComparingTo(UPDATED_LATITUDE);
        assertThat(testPark.getVerified()).isEqualTo(UPDATED_VERIFIED);
        assertThat(testPark.getDateInstall()).isEqualTo(UPDATED_DATE_INSTALL);
        assertThat(testPark.getDateOpen()).isEqualTo(UPDATED_DATE_OPEN);
        assertThat(testPark.getDateClose()).isEqualTo(UPDATED_DATE_CLOSE);
        assertThat(testPark.getNote()).isEqualTo(UPDATED_NOTE);
        assertThat(testPark.getReseller()).isEqualTo(UPDATED_RESELLER);
    }

    @Test
    void putNonExistingPark() throws Exception {
        int databaseSizeBeforeUpdate = parkRepository.findAll().collectList().block().size();
        park.setId(count.incrementAndGet());

        // Create the Park
        ParkDTO parkDTO = parkMapper.toDto(park);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, parkDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(parkDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Park in the database
        List<Park> parkList = parkRepository.findAll().collectList().block();
        assertThat(parkList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchPark() throws Exception {
        int databaseSizeBeforeUpdate = parkRepository.findAll().collectList().block().size();
        park.setId(count.incrementAndGet());

        // Create the Park
        ParkDTO parkDTO = parkMapper.toDto(park);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(parkDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Park in the database
        List<Park> parkList = parkRepository.findAll().collectList().block();
        assertThat(parkList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamPark() throws Exception {
        int databaseSizeBeforeUpdate = parkRepository.findAll().collectList().block().size();
        park.setId(count.incrementAndGet());

        // Create the Park
        ParkDTO parkDTO = parkMapper.toDto(park);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(parkDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Park in the database
        List<Park> parkList = parkRepository.findAll().collectList().block();
        assertThat(parkList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateParkWithPatch() throws Exception {
        // Initialize the database
        parkRepository.save(park).block();

        int databaseSizeBeforeUpdate = parkRepository.findAll().collectList().block().size();

        // Update the park using partial update
        Park partialUpdatedPark = new Park();
        partialUpdatedPark.setId(park.getId());

        partialUpdatedPark
            .parkName(UPDATED_PARK_NAME)
            .parkAddress(UPDATED_PARK_ADDRESS)
            .verified(UPDATED_VERIFIED)
            .dateOpen(UPDATED_DATE_OPEN)
            .dateClose(UPDATED_DATE_CLOSE)
            .note(UPDATED_NOTE)
            .reseller(UPDATED_RESELLER);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedPark.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedPark))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Park in the database
        List<Park> parkList = parkRepository.findAll().collectList().block();
        assertThat(parkList).hasSize(databaseSizeBeforeUpdate);
        Park testPark = parkList.get(parkList.size() - 1);
        assertThat(testPark.getParkName()).isEqualTo(UPDATED_PARK_NAME);
        assertThat(testPark.getParkAddress()).isEqualTo(UPDATED_PARK_ADDRESS);
        assertThat(testPark.getLongtitude()).isEqualByComparingTo(DEFAULT_LONGTITUDE);
        assertThat(testPark.getLatitude()).isEqualByComparingTo(DEFAULT_LATITUDE);
        assertThat(testPark.getVerified()).isEqualTo(UPDATED_VERIFIED);
        assertThat(testPark.getDateInstall()).isEqualTo(DEFAULT_DATE_INSTALL);
        assertThat(testPark.getDateOpen()).isEqualTo(UPDATED_DATE_OPEN);
        assertThat(testPark.getDateClose()).isEqualTo(UPDATED_DATE_CLOSE);
        assertThat(testPark.getNote()).isEqualTo(UPDATED_NOTE);
        assertThat(testPark.getReseller()).isEqualTo(UPDATED_RESELLER);
    }

    @Test
    void fullUpdateParkWithPatch() throws Exception {
        // Initialize the database
        parkRepository.save(park).block();

        int databaseSizeBeforeUpdate = parkRepository.findAll().collectList().block().size();

        // Update the park using partial update
        Park partialUpdatedPark = new Park();
        partialUpdatedPark.setId(park.getId());

        partialUpdatedPark
            .parkName(UPDATED_PARK_NAME)
            .parkAddress(UPDATED_PARK_ADDRESS)
            .longtitude(UPDATED_LONGTITUDE)
            .latitude(UPDATED_LATITUDE)
            .verified(UPDATED_VERIFIED)
            .dateInstall(UPDATED_DATE_INSTALL)
            .dateOpen(UPDATED_DATE_OPEN)
            .dateClose(UPDATED_DATE_CLOSE)
            .note(UPDATED_NOTE)
            .reseller(UPDATED_RESELLER);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedPark.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedPark))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Park in the database
        List<Park> parkList = parkRepository.findAll().collectList().block();
        assertThat(parkList).hasSize(databaseSizeBeforeUpdate);
        Park testPark = parkList.get(parkList.size() - 1);
        assertThat(testPark.getParkName()).isEqualTo(UPDATED_PARK_NAME);
        assertThat(testPark.getParkAddress()).isEqualTo(UPDATED_PARK_ADDRESS);
        assertThat(testPark.getLongtitude()).isEqualByComparingTo(UPDATED_LONGTITUDE);
        assertThat(testPark.getLatitude()).isEqualByComparingTo(UPDATED_LATITUDE);
        assertThat(testPark.getVerified()).isEqualTo(UPDATED_VERIFIED);
        assertThat(testPark.getDateInstall()).isEqualTo(UPDATED_DATE_INSTALL);
        assertThat(testPark.getDateOpen()).isEqualTo(UPDATED_DATE_OPEN);
        assertThat(testPark.getDateClose()).isEqualTo(UPDATED_DATE_CLOSE);
        assertThat(testPark.getNote()).isEqualTo(UPDATED_NOTE);
        assertThat(testPark.getReseller()).isEqualTo(UPDATED_RESELLER);
    }

    @Test
    void patchNonExistingPark() throws Exception {
        int databaseSizeBeforeUpdate = parkRepository.findAll().collectList().block().size();
        park.setId(count.incrementAndGet());

        // Create the Park
        ParkDTO parkDTO = parkMapper.toDto(park);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, parkDTO.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(parkDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Park in the database
        List<Park> parkList = parkRepository.findAll().collectList().block();
        assertThat(parkList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchPark() throws Exception {
        int databaseSizeBeforeUpdate = parkRepository.findAll().collectList().block().size();
        park.setId(count.incrementAndGet());

        // Create the Park
        ParkDTO parkDTO = parkMapper.toDto(park);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(parkDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Park in the database
        List<Park> parkList = parkRepository.findAll().collectList().block();
        assertThat(parkList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamPark() throws Exception {
        int databaseSizeBeforeUpdate = parkRepository.findAll().collectList().block().size();
        park.setId(count.incrementAndGet());

        // Create the Park
        ParkDTO parkDTO = parkMapper.toDto(park);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(parkDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Park in the database
        List<Park> parkList = parkRepository.findAll().collectList().block();
        assertThat(parkList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void deletePark() {
        // Initialize the database
        parkRepository.save(park).block();

        int databaseSizeBeforeDelete = parkRepository.findAll().collectList().block().size();

        // Delete the park
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, park.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<Park> parkList = parkRepository.findAll().collectList().block();
        assertThat(parkList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
