package com.mycompany.myapp.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;

import com.mycompany.myapp.IntegrationTest;
import com.mycompany.myapp.domain.Media;
import com.mycompany.myapp.domain.enumeration.AuthType;
import com.mycompany.myapp.repository.EntityManager;
import com.mycompany.myapp.repository.MediaRepository;
import com.mycompany.myapp.service.dto.MediaDTO;
import com.mycompany.myapp.service.mapper.MediaMapper;
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
 * Integration tests for the {@link MediaResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class MediaResourceIT {

    private static final String DEFAULT_FILE_NAME = "AAAAAAAAAA";
    private static final String UPDATED_FILE_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_FILE_PATH = "AAAAAAAAAA";
    private static final String UPDATED_FILE_PATH = "BBBBBBBBBB";

    private static final String DEFAULT_FILE_TYPE = "AAAAAAAAAA";
    private static final String UPDATED_FILE_TYPE = "BBBBBBBBBB";

    private static final AuthType DEFAULT_AUTH_TYPE = AuthType.LEARN;
    private static final AuthType UPDATED_AUTH_TYPE = AuthType.TECHFILE;

    private static final String ENTITY_API_URL = "/api/media";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private MediaRepository mediaRepository;

    @Autowired
    private MediaMapper mediaMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private Media media;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Media createEntity(EntityManager em) {
        Media media = new Media()
            .fileName(DEFAULT_FILE_NAME)
            .filePath(DEFAULT_FILE_PATH)
            .fileType(DEFAULT_FILE_TYPE)
            .authType(DEFAULT_AUTH_TYPE);
        return media;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Media createUpdatedEntity(EntityManager em) {
        Media media = new Media()
            .fileName(UPDATED_FILE_NAME)
            .filePath(UPDATED_FILE_PATH)
            .fileType(UPDATED_FILE_TYPE)
            .authType(UPDATED_AUTH_TYPE);
        return media;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(Media.class).block();
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
        media = createEntity(em);
    }

    @Test
    void createMedia() throws Exception {
        int databaseSizeBeforeCreate = mediaRepository.findAll().collectList().block().size();
        // Create the Media
        MediaDTO mediaDTO = mediaMapper.toDto(media);
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(mediaDTO))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the Media in the database
        List<Media> mediaList = mediaRepository.findAll().collectList().block();
        assertThat(mediaList).hasSize(databaseSizeBeforeCreate + 1);
        Media testMedia = mediaList.get(mediaList.size() - 1);
        assertThat(testMedia.getFileName()).isEqualTo(DEFAULT_FILE_NAME);
        assertThat(testMedia.getFilePath()).isEqualTo(DEFAULT_FILE_PATH);
        assertThat(testMedia.getFileType()).isEqualTo(DEFAULT_FILE_TYPE);
        assertThat(testMedia.getAuthType()).isEqualTo(DEFAULT_AUTH_TYPE);
    }

    @Test
    void createMediaWithExistingId() throws Exception {
        // Create the Media with an existing ID
        media.setId(1L);
        MediaDTO mediaDTO = mediaMapper.toDto(media);

        int databaseSizeBeforeCreate = mediaRepository.findAll().collectList().block().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(mediaDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Media in the database
        List<Media> mediaList = mediaRepository.findAll().collectList().block();
        assertThat(mediaList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    void getAllMedia() {
        // Initialize the database
        mediaRepository.save(media).block();

        // Get all the mediaList
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
            .value(hasItem(media.getId().intValue()))
            .jsonPath("$.[*].fileName")
            .value(hasItem(DEFAULT_FILE_NAME))
            .jsonPath("$.[*].filePath")
            .value(hasItem(DEFAULT_FILE_PATH))
            .jsonPath("$.[*].fileType")
            .value(hasItem(DEFAULT_FILE_TYPE))
            .jsonPath("$.[*].authType")
            .value(hasItem(DEFAULT_AUTH_TYPE.toString()));
    }

    @Test
    void getMedia() {
        // Initialize the database
        mediaRepository.save(media).block();

        // Get the media
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, media.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(media.getId().intValue()))
            .jsonPath("$.fileName")
            .value(is(DEFAULT_FILE_NAME))
            .jsonPath("$.filePath")
            .value(is(DEFAULT_FILE_PATH))
            .jsonPath("$.fileType")
            .value(is(DEFAULT_FILE_TYPE))
            .jsonPath("$.authType")
            .value(is(DEFAULT_AUTH_TYPE.toString()));
    }

    @Test
    void getNonExistingMedia() {
        // Get the media
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingMedia() throws Exception {
        // Initialize the database
        mediaRepository.save(media).block();

        int databaseSizeBeforeUpdate = mediaRepository.findAll().collectList().block().size();

        // Update the media
        Media updatedMedia = mediaRepository.findById(media.getId()).block();
        updatedMedia.fileName(UPDATED_FILE_NAME).filePath(UPDATED_FILE_PATH).fileType(UPDATED_FILE_TYPE).authType(UPDATED_AUTH_TYPE);
        MediaDTO mediaDTO = mediaMapper.toDto(updatedMedia);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, mediaDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(mediaDTO))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Media in the database
        List<Media> mediaList = mediaRepository.findAll().collectList().block();
        assertThat(mediaList).hasSize(databaseSizeBeforeUpdate);
        Media testMedia = mediaList.get(mediaList.size() - 1);
        assertThat(testMedia.getFileName()).isEqualTo(UPDATED_FILE_NAME);
        assertThat(testMedia.getFilePath()).isEqualTo(UPDATED_FILE_PATH);
        assertThat(testMedia.getFileType()).isEqualTo(UPDATED_FILE_TYPE);
        assertThat(testMedia.getAuthType()).isEqualTo(UPDATED_AUTH_TYPE);
    }

    @Test
    void putNonExistingMedia() throws Exception {
        int databaseSizeBeforeUpdate = mediaRepository.findAll().collectList().block().size();
        media.setId(count.incrementAndGet());

        // Create the Media
        MediaDTO mediaDTO = mediaMapper.toDto(media);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, mediaDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(mediaDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Media in the database
        List<Media> mediaList = mediaRepository.findAll().collectList().block();
        assertThat(mediaList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchMedia() throws Exception {
        int databaseSizeBeforeUpdate = mediaRepository.findAll().collectList().block().size();
        media.setId(count.incrementAndGet());

        // Create the Media
        MediaDTO mediaDTO = mediaMapper.toDto(media);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(mediaDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Media in the database
        List<Media> mediaList = mediaRepository.findAll().collectList().block();
        assertThat(mediaList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamMedia() throws Exception {
        int databaseSizeBeforeUpdate = mediaRepository.findAll().collectList().block().size();
        media.setId(count.incrementAndGet());

        // Create the Media
        MediaDTO mediaDTO = mediaMapper.toDto(media);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(mediaDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Media in the database
        List<Media> mediaList = mediaRepository.findAll().collectList().block();
        assertThat(mediaList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateMediaWithPatch() throws Exception {
        // Initialize the database
        mediaRepository.save(media).block();

        int databaseSizeBeforeUpdate = mediaRepository.findAll().collectList().block().size();

        // Update the media using partial update
        Media partialUpdatedMedia = new Media();
        partialUpdatedMedia.setId(media.getId());

        partialUpdatedMedia.fileName(UPDATED_FILE_NAME).fileType(UPDATED_FILE_TYPE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedMedia.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedMedia))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Media in the database
        List<Media> mediaList = mediaRepository.findAll().collectList().block();
        assertThat(mediaList).hasSize(databaseSizeBeforeUpdate);
        Media testMedia = mediaList.get(mediaList.size() - 1);
        assertThat(testMedia.getFileName()).isEqualTo(UPDATED_FILE_NAME);
        assertThat(testMedia.getFilePath()).isEqualTo(DEFAULT_FILE_PATH);
        assertThat(testMedia.getFileType()).isEqualTo(UPDATED_FILE_TYPE);
        assertThat(testMedia.getAuthType()).isEqualTo(DEFAULT_AUTH_TYPE);
    }

    @Test
    void fullUpdateMediaWithPatch() throws Exception {
        // Initialize the database
        mediaRepository.save(media).block();

        int databaseSizeBeforeUpdate = mediaRepository.findAll().collectList().block().size();

        // Update the media using partial update
        Media partialUpdatedMedia = new Media();
        partialUpdatedMedia.setId(media.getId());

        partialUpdatedMedia.fileName(UPDATED_FILE_NAME).filePath(UPDATED_FILE_PATH).fileType(UPDATED_FILE_TYPE).authType(UPDATED_AUTH_TYPE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedMedia.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedMedia))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Media in the database
        List<Media> mediaList = mediaRepository.findAll().collectList().block();
        assertThat(mediaList).hasSize(databaseSizeBeforeUpdate);
        Media testMedia = mediaList.get(mediaList.size() - 1);
        assertThat(testMedia.getFileName()).isEqualTo(UPDATED_FILE_NAME);
        assertThat(testMedia.getFilePath()).isEqualTo(UPDATED_FILE_PATH);
        assertThat(testMedia.getFileType()).isEqualTo(UPDATED_FILE_TYPE);
        assertThat(testMedia.getAuthType()).isEqualTo(UPDATED_AUTH_TYPE);
    }

    @Test
    void patchNonExistingMedia() throws Exception {
        int databaseSizeBeforeUpdate = mediaRepository.findAll().collectList().block().size();
        media.setId(count.incrementAndGet());

        // Create the Media
        MediaDTO mediaDTO = mediaMapper.toDto(media);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, mediaDTO.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(mediaDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Media in the database
        List<Media> mediaList = mediaRepository.findAll().collectList().block();
        assertThat(mediaList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchMedia() throws Exception {
        int databaseSizeBeforeUpdate = mediaRepository.findAll().collectList().block().size();
        media.setId(count.incrementAndGet());

        // Create the Media
        MediaDTO mediaDTO = mediaMapper.toDto(media);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(mediaDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Media in the database
        List<Media> mediaList = mediaRepository.findAll().collectList().block();
        assertThat(mediaList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamMedia() throws Exception {
        int databaseSizeBeforeUpdate = mediaRepository.findAll().collectList().block().size();
        media.setId(count.incrementAndGet());

        // Create the Media
        MediaDTO mediaDTO = mediaMapper.toDto(media);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(mediaDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Media in the database
        List<Media> mediaList = mediaRepository.findAll().collectList().block();
        assertThat(mediaList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteMedia() {
        // Initialize the database
        mediaRepository.save(media).block();

        int databaseSizeBeforeDelete = mediaRepository.findAll().collectList().block().size();

        // Delete the media
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, media.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<Media> mediaList = mediaRepository.findAll().collectList().block();
        assertThat(mediaList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
