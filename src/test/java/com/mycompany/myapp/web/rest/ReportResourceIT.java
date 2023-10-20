package com.mycompany.myapp.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;

import com.mycompany.myapp.IntegrationTest;
import com.mycompany.myapp.domain.Report;
import com.mycompany.myapp.repository.EntityManager;
import com.mycompany.myapp.repository.ReportRepository;
import com.mycompany.myapp.service.dto.ReportDTO;
import com.mycompany.myapp.service.mapper.ReportMapper;
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
 * Integration tests for the {@link ReportResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class ReportResourceIT {

    private static final String DEFAULT_MAIL = "AAAAAAAAAA";
    private static final String UPDATED_MAIL = "BBBBBBBBBB";

    private static final String DEFAULT_MESSAGE = "AAAAAAAAAA";
    private static final String UPDATED_MESSAGE = "BBBBBBBBBB";

    private static final Instant DEFAULT_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String ENTITY_API_URL = "/api/reports";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ReportRepository reportRepository;

    @Autowired
    private ReportMapper reportMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private Report report;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Report createEntity(EntityManager em) {
        Report report = new Report().mail(DEFAULT_MAIL).message(DEFAULT_MESSAGE).date(DEFAULT_DATE);
        return report;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Report createUpdatedEntity(EntityManager em) {
        Report report = new Report().mail(UPDATED_MAIL).message(UPDATED_MESSAGE).date(UPDATED_DATE);
        return report;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(Report.class).block();
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
        report = createEntity(em);
    }

    @Test
    void createReport() throws Exception {
        int databaseSizeBeforeCreate = reportRepository.findAll().collectList().block().size();
        // Create the Report
        ReportDTO reportDTO = reportMapper.toDto(report);
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(reportDTO))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the Report in the database
        List<Report> reportList = reportRepository.findAll().collectList().block();
        assertThat(reportList).hasSize(databaseSizeBeforeCreate + 1);
        Report testReport = reportList.get(reportList.size() - 1);
        assertThat(testReport.getMail()).isEqualTo(DEFAULT_MAIL);
        assertThat(testReport.getMessage()).isEqualTo(DEFAULT_MESSAGE);
        assertThat(testReport.getDate()).isEqualTo(DEFAULT_DATE);
    }

    @Test
    void createReportWithExistingId() throws Exception {
        // Create the Report with an existing ID
        report.setId(1L);
        ReportDTO reportDTO = reportMapper.toDto(report);

        int databaseSizeBeforeCreate = reportRepository.findAll().collectList().block().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(reportDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Report in the database
        List<Report> reportList = reportRepository.findAll().collectList().block();
        assertThat(reportList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    void getAllReports() {
        // Initialize the database
        reportRepository.save(report).block();

        // Get all the reportList
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
            .value(hasItem(report.getId().intValue()))
            .jsonPath("$.[*].mail")
            .value(hasItem(DEFAULT_MAIL))
            .jsonPath("$.[*].message")
            .value(hasItem(DEFAULT_MESSAGE))
            .jsonPath("$.[*].date")
            .value(hasItem(DEFAULT_DATE.toString()));
    }

    @Test
    void getReport() {
        // Initialize the database
        reportRepository.save(report).block();

        // Get the report
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, report.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(report.getId().intValue()))
            .jsonPath("$.mail")
            .value(is(DEFAULT_MAIL))
            .jsonPath("$.message")
            .value(is(DEFAULT_MESSAGE))
            .jsonPath("$.date")
            .value(is(DEFAULT_DATE.toString()));
    }

    @Test
    void getNonExistingReport() {
        // Get the report
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingReport() throws Exception {
        // Initialize the database
        reportRepository.save(report).block();

        int databaseSizeBeforeUpdate = reportRepository.findAll().collectList().block().size();

        // Update the report
        Report updatedReport = reportRepository.findById(report.getId()).block();
        updatedReport.mail(UPDATED_MAIL).message(UPDATED_MESSAGE).date(UPDATED_DATE);
        ReportDTO reportDTO = reportMapper.toDto(updatedReport);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, reportDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(reportDTO))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Report in the database
        List<Report> reportList = reportRepository.findAll().collectList().block();
        assertThat(reportList).hasSize(databaseSizeBeforeUpdate);
        Report testReport = reportList.get(reportList.size() - 1);
        assertThat(testReport.getMail()).isEqualTo(UPDATED_MAIL);
        assertThat(testReport.getMessage()).isEqualTo(UPDATED_MESSAGE);
        assertThat(testReport.getDate()).isEqualTo(UPDATED_DATE);
    }

    @Test
    void putNonExistingReport() throws Exception {
        int databaseSizeBeforeUpdate = reportRepository.findAll().collectList().block().size();
        report.setId(count.incrementAndGet());

        // Create the Report
        ReportDTO reportDTO = reportMapper.toDto(report);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, reportDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(reportDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Report in the database
        List<Report> reportList = reportRepository.findAll().collectList().block();
        assertThat(reportList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchReport() throws Exception {
        int databaseSizeBeforeUpdate = reportRepository.findAll().collectList().block().size();
        report.setId(count.incrementAndGet());

        // Create the Report
        ReportDTO reportDTO = reportMapper.toDto(report);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(reportDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Report in the database
        List<Report> reportList = reportRepository.findAll().collectList().block();
        assertThat(reportList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamReport() throws Exception {
        int databaseSizeBeforeUpdate = reportRepository.findAll().collectList().block().size();
        report.setId(count.incrementAndGet());

        // Create the Report
        ReportDTO reportDTO = reportMapper.toDto(report);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(reportDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Report in the database
        List<Report> reportList = reportRepository.findAll().collectList().block();
        assertThat(reportList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateReportWithPatch() throws Exception {
        // Initialize the database
        reportRepository.save(report).block();

        int databaseSizeBeforeUpdate = reportRepository.findAll().collectList().block().size();

        // Update the report using partial update
        Report partialUpdatedReport = new Report();
        partialUpdatedReport.setId(report.getId());

        partialUpdatedReport.message(UPDATED_MESSAGE).date(UPDATED_DATE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedReport.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedReport))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Report in the database
        List<Report> reportList = reportRepository.findAll().collectList().block();
        assertThat(reportList).hasSize(databaseSizeBeforeUpdate);
        Report testReport = reportList.get(reportList.size() - 1);
        assertThat(testReport.getMail()).isEqualTo(DEFAULT_MAIL);
        assertThat(testReport.getMessage()).isEqualTo(UPDATED_MESSAGE);
        assertThat(testReport.getDate()).isEqualTo(UPDATED_DATE);
    }

    @Test
    void fullUpdateReportWithPatch() throws Exception {
        // Initialize the database
        reportRepository.save(report).block();

        int databaseSizeBeforeUpdate = reportRepository.findAll().collectList().block().size();

        // Update the report using partial update
        Report partialUpdatedReport = new Report();
        partialUpdatedReport.setId(report.getId());

        partialUpdatedReport.mail(UPDATED_MAIL).message(UPDATED_MESSAGE).date(UPDATED_DATE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedReport.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedReport))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Report in the database
        List<Report> reportList = reportRepository.findAll().collectList().block();
        assertThat(reportList).hasSize(databaseSizeBeforeUpdate);
        Report testReport = reportList.get(reportList.size() - 1);
        assertThat(testReport.getMail()).isEqualTo(UPDATED_MAIL);
        assertThat(testReport.getMessage()).isEqualTo(UPDATED_MESSAGE);
        assertThat(testReport.getDate()).isEqualTo(UPDATED_DATE);
    }

    @Test
    void patchNonExistingReport() throws Exception {
        int databaseSizeBeforeUpdate = reportRepository.findAll().collectList().block().size();
        report.setId(count.incrementAndGet());

        // Create the Report
        ReportDTO reportDTO = reportMapper.toDto(report);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, reportDTO.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(reportDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Report in the database
        List<Report> reportList = reportRepository.findAll().collectList().block();
        assertThat(reportList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchReport() throws Exception {
        int databaseSizeBeforeUpdate = reportRepository.findAll().collectList().block().size();
        report.setId(count.incrementAndGet());

        // Create the Report
        ReportDTO reportDTO = reportMapper.toDto(report);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(reportDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Report in the database
        List<Report> reportList = reportRepository.findAll().collectList().block();
        assertThat(reportList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamReport() throws Exception {
        int databaseSizeBeforeUpdate = reportRepository.findAll().collectList().block().size();
        report.setId(count.incrementAndGet());

        // Create the Report
        ReportDTO reportDTO = reportMapper.toDto(report);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(reportDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Report in the database
        List<Report> reportList = reportRepository.findAll().collectList().block();
        assertThat(reportList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteReport() {
        // Initialize the database
        reportRepository.save(report).block();

        int databaseSizeBeforeDelete = reportRepository.findAll().collectList().block().size();

        // Delete the report
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, report.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<Report> reportList = reportRepository.findAll().collectList().block();
        assertThat(reportList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
