package com.mycompany.myapp.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;

import com.mycompany.myapp.IntegrationTest;
import com.mycompany.myapp.domain.Equipement;
import com.mycompany.myapp.repository.EntityManager;
import com.mycompany.myapp.repository.EquipementRepository;
import com.mycompany.myapp.service.dto.EquipementDTO;
import com.mycompany.myapp.service.mapper.EquipementMapper;
import java.time.Duration;
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
 * Integration tests for the {@link EquipementResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class EquipementResourceIT {

    private static final String DEFAULT_MODEL_NAME = "AAAAAAAAAA";
    private static final String UPDATED_MODEL_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_MODEL_NUMBER = "AAAAAAAAAA";
    private static final String UPDATED_MODEL_NUMBER = "BBBBBBBBBB";

    private static final String DEFAULT_INSTRUCTION = "AAAAAAAAAA";
    private static final String UPDATED_INSTRUCTION = "BBBBBBBBBB";

    private static final Boolean DEFAULT_VERIFIED = false;
    private static final Boolean UPDATED_VERIFIED = true;

    private static final String ENTITY_API_URL = "/api/equipements";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private EquipementRepository equipementRepository;

    @Autowired
    private EquipementMapper equipementMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private Equipement equipement;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Equipement createEntity(EntityManager em) {
        Equipement equipement = new Equipement()
            .modelName(DEFAULT_MODEL_NAME)
            .modelNumber(DEFAULT_MODEL_NUMBER)
            .instruction(DEFAULT_INSTRUCTION)
            .verified(DEFAULT_VERIFIED);
        return equipement;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Equipement createUpdatedEntity(EntityManager em) {
        Equipement equipement = new Equipement()
            .modelName(UPDATED_MODEL_NAME)
            .modelNumber(UPDATED_MODEL_NUMBER)
            .instruction(UPDATED_INSTRUCTION)
            .verified(UPDATED_VERIFIED);
        return equipement;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(Equipement.class).block();
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
        equipement = createEntity(em);
    }

    @Test
    void createEquipement() throws Exception {
        int databaseSizeBeforeCreate = equipementRepository.findAll().collectList().block().size();
        // Create the Equipement
        EquipementDTO equipementDTO = equipementMapper.toDto(equipement);
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(equipementDTO))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the Equipement in the database
        List<Equipement> equipementList = equipementRepository.findAll().collectList().block();
        assertThat(equipementList).hasSize(databaseSizeBeforeCreate + 1);
        Equipement testEquipement = equipementList.get(equipementList.size() - 1);
        assertThat(testEquipement.getModelName()).isEqualTo(DEFAULT_MODEL_NAME);
        assertThat(testEquipement.getModelNumber()).isEqualTo(DEFAULT_MODEL_NUMBER);
        assertThat(testEquipement.getInstruction()).isEqualTo(DEFAULT_INSTRUCTION);
        assertThat(testEquipement.getVerified()).isEqualTo(DEFAULT_VERIFIED);
    }

    @Test
    void createEquipementWithExistingId() throws Exception {
        // Create the Equipement with an existing ID
        equipement.setId(1L);
        EquipementDTO equipementDTO = equipementMapper.toDto(equipement);

        int databaseSizeBeforeCreate = equipementRepository.findAll().collectList().block().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(equipementDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Equipement in the database
        List<Equipement> equipementList = equipementRepository.findAll().collectList().block();
        assertThat(equipementList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    void getAllEquipements() {
        // Initialize the database
        equipementRepository.save(equipement).block();

        // Get all the equipementList
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
            .value(hasItem(equipement.getId().intValue()))
            .jsonPath("$.[*].modelName")
            .value(hasItem(DEFAULT_MODEL_NAME))
            .jsonPath("$.[*].modelNumber")
            .value(hasItem(DEFAULT_MODEL_NUMBER))
            .jsonPath("$.[*].instruction")
            .value(hasItem(DEFAULT_INSTRUCTION))
            .jsonPath("$.[*].verified")
            .value(hasItem(DEFAULT_VERIFIED.booleanValue()));
    }

    @Test
    void getEquipement() {
        // Initialize the database
        equipementRepository.save(equipement).block();

        // Get the equipement
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, equipement.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(equipement.getId().intValue()))
            .jsonPath("$.modelName")
            .value(is(DEFAULT_MODEL_NAME))
            .jsonPath("$.modelNumber")
            .value(is(DEFAULT_MODEL_NUMBER))
            .jsonPath("$.instruction")
            .value(is(DEFAULT_INSTRUCTION))
            .jsonPath("$.verified")
            .value(is(DEFAULT_VERIFIED.booleanValue()));
    }

    @Test
    void getNonExistingEquipement() {
        // Get the equipement
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingEquipement() throws Exception {
        // Initialize the database
        equipementRepository.save(equipement).block();

        int databaseSizeBeforeUpdate = equipementRepository.findAll().collectList().block().size();

        // Update the equipement
        Equipement updatedEquipement = equipementRepository.findById(equipement.getId()).block();
        updatedEquipement
            .modelName(UPDATED_MODEL_NAME)
            .modelNumber(UPDATED_MODEL_NUMBER)
            .instruction(UPDATED_INSTRUCTION)
            .verified(UPDATED_VERIFIED);
        EquipementDTO equipementDTO = equipementMapper.toDto(updatedEquipement);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, equipementDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(equipementDTO))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Equipement in the database
        List<Equipement> equipementList = equipementRepository.findAll().collectList().block();
        assertThat(equipementList).hasSize(databaseSizeBeforeUpdate);
        Equipement testEquipement = equipementList.get(equipementList.size() - 1);
        assertThat(testEquipement.getModelName()).isEqualTo(UPDATED_MODEL_NAME);
        assertThat(testEquipement.getModelNumber()).isEqualTo(UPDATED_MODEL_NUMBER);
        assertThat(testEquipement.getInstruction()).isEqualTo(UPDATED_INSTRUCTION);
        assertThat(testEquipement.getVerified()).isEqualTo(UPDATED_VERIFIED);
    }

    @Test
    void putNonExistingEquipement() throws Exception {
        int databaseSizeBeforeUpdate = equipementRepository.findAll().collectList().block().size();
        equipement.setId(count.incrementAndGet());

        // Create the Equipement
        EquipementDTO equipementDTO = equipementMapper.toDto(equipement);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, equipementDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(equipementDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Equipement in the database
        List<Equipement> equipementList = equipementRepository.findAll().collectList().block();
        assertThat(equipementList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchEquipement() throws Exception {
        int databaseSizeBeforeUpdate = equipementRepository.findAll().collectList().block().size();
        equipement.setId(count.incrementAndGet());

        // Create the Equipement
        EquipementDTO equipementDTO = equipementMapper.toDto(equipement);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(equipementDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Equipement in the database
        List<Equipement> equipementList = equipementRepository.findAll().collectList().block();
        assertThat(equipementList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamEquipement() throws Exception {
        int databaseSizeBeforeUpdate = equipementRepository.findAll().collectList().block().size();
        equipement.setId(count.incrementAndGet());

        // Create the Equipement
        EquipementDTO equipementDTO = equipementMapper.toDto(equipement);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(equipementDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Equipement in the database
        List<Equipement> equipementList = equipementRepository.findAll().collectList().block();
        assertThat(equipementList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateEquipementWithPatch() throws Exception {
        // Initialize the database
        equipementRepository.save(equipement).block();

        int databaseSizeBeforeUpdate = equipementRepository.findAll().collectList().block().size();

        // Update the equipement using partial update
        Equipement partialUpdatedEquipement = new Equipement();
        partialUpdatedEquipement.setId(equipement.getId());

        partialUpdatedEquipement.modelName(UPDATED_MODEL_NAME).instruction(UPDATED_INSTRUCTION);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedEquipement.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedEquipement))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Equipement in the database
        List<Equipement> equipementList = equipementRepository.findAll().collectList().block();
        assertThat(equipementList).hasSize(databaseSizeBeforeUpdate);
        Equipement testEquipement = equipementList.get(equipementList.size() - 1);
        assertThat(testEquipement.getModelName()).isEqualTo(UPDATED_MODEL_NAME);
        assertThat(testEquipement.getModelNumber()).isEqualTo(DEFAULT_MODEL_NUMBER);
        assertThat(testEquipement.getInstruction()).isEqualTo(UPDATED_INSTRUCTION);
        assertThat(testEquipement.getVerified()).isEqualTo(DEFAULT_VERIFIED);
    }

    @Test
    void fullUpdateEquipementWithPatch() throws Exception {
        // Initialize the database
        equipementRepository.save(equipement).block();

        int databaseSizeBeforeUpdate = equipementRepository.findAll().collectList().block().size();

        // Update the equipement using partial update
        Equipement partialUpdatedEquipement = new Equipement();
        partialUpdatedEquipement.setId(equipement.getId());

        partialUpdatedEquipement
            .modelName(UPDATED_MODEL_NAME)
            .modelNumber(UPDATED_MODEL_NUMBER)
            .instruction(UPDATED_INSTRUCTION)
            .verified(UPDATED_VERIFIED);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedEquipement.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedEquipement))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Equipement in the database
        List<Equipement> equipementList = equipementRepository.findAll().collectList().block();
        assertThat(equipementList).hasSize(databaseSizeBeforeUpdate);
        Equipement testEquipement = equipementList.get(equipementList.size() - 1);
        assertThat(testEquipement.getModelName()).isEqualTo(UPDATED_MODEL_NAME);
        assertThat(testEquipement.getModelNumber()).isEqualTo(UPDATED_MODEL_NUMBER);
        assertThat(testEquipement.getInstruction()).isEqualTo(UPDATED_INSTRUCTION);
        assertThat(testEquipement.getVerified()).isEqualTo(UPDATED_VERIFIED);
    }

    @Test
    void patchNonExistingEquipement() throws Exception {
        int databaseSizeBeforeUpdate = equipementRepository.findAll().collectList().block().size();
        equipement.setId(count.incrementAndGet());

        // Create the Equipement
        EquipementDTO equipementDTO = equipementMapper.toDto(equipement);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, equipementDTO.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(equipementDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Equipement in the database
        List<Equipement> equipementList = equipementRepository.findAll().collectList().block();
        assertThat(equipementList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchEquipement() throws Exception {
        int databaseSizeBeforeUpdate = equipementRepository.findAll().collectList().block().size();
        equipement.setId(count.incrementAndGet());

        // Create the Equipement
        EquipementDTO equipementDTO = equipementMapper.toDto(equipement);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(equipementDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Equipement in the database
        List<Equipement> equipementList = equipementRepository.findAll().collectList().block();
        assertThat(equipementList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamEquipement() throws Exception {
        int databaseSizeBeforeUpdate = equipementRepository.findAll().collectList().block().size();
        equipement.setId(count.incrementAndGet());

        // Create the Equipement
        EquipementDTO equipementDTO = equipementMapper.toDto(equipement);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(equipementDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Equipement in the database
        List<Equipement> equipementList = equipementRepository.findAll().collectList().block();
        assertThat(equipementList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteEquipement() {
        // Initialize the database
        equipementRepository.save(equipement).block();

        int databaseSizeBeforeDelete = equipementRepository.findAll().collectList().block().size();

        // Delete the equipement
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, equipement.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<Equipement> equipementList = equipementRepository.findAll().collectList().block();
        assertThat(equipementList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
