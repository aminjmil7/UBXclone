package com.mycompany.myapp.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.mycompany.myapp.domain.enumeration.AuthType;
import java.io.Serializable;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * A Media.
 */
@Table("media")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Media implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column("id")
    private Long id;

    @Column("file_name")
    private String fileName;

    @Column("file_path")
    private String filePath;

    @Column("file_type")
    private String fileType;

    @Column("auth_type")
    private AuthType authType;

    @Transient
    @JsonIgnoreProperties(value = { "equipements", "media", "reports" }, allowSetters = true)
    private Park park;

    @Transient
    @JsonIgnoreProperties(value = { "reports", "media", "park" }, allowSetters = true)
    private Equipement equipement;

    @Transient
    @JsonIgnoreProperties(value = { "media", "equipement", "park" }, allowSetters = true)
    private Report report;

    @Column("park_id")
    private Long parkId;

    @Column("equipement_id")
    private Long equipementId;

    @Column("report_id")
    private Long reportId;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Media id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFileName() {
        return this.fileName;
    }

    public Media fileName(String fileName) {
        this.setFileName(fileName);
        return this;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFilePath() {
        return this.filePath;
    }

    public Media filePath(String filePath) {
        this.setFilePath(filePath);
        return this;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getFileType() {
        return this.fileType;
    }

    public Media fileType(String fileType) {
        this.setFileType(fileType);
        return this;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public AuthType getAuthType() {
        return this.authType;
    }

    public Media authType(AuthType authType) {
        this.setAuthType(authType);
        return this;
    }

    public void setAuthType(AuthType authType) {
        this.authType = authType;
    }

    public Park getPark() {
        return this.park;
    }

    public void setPark(Park park) {
        this.park = park;
        this.parkId = park != null ? park.getId() : null;
    }

    public Media park(Park park) {
        this.setPark(park);
        return this;
    }

    public Equipement getEquipement() {
        return this.equipement;
    }

    public void setEquipement(Equipement equipement) {
        this.equipement = equipement;
        this.equipementId = equipement != null ? equipement.getId() : null;
    }

    public Media equipement(Equipement equipement) {
        this.setEquipement(equipement);
        return this;
    }

    public Report getReport() {
        return this.report;
    }

    public void setReport(Report report) {
        this.report = report;
        this.reportId = report != null ? report.getId() : null;
    }

    public Media report(Report report) {
        this.setReport(report);
        return this;
    }

    public Long getParkId() {
        return this.parkId;
    }

    public void setParkId(Long park) {
        this.parkId = park;
    }

    public Long getEquipementId() {
        return this.equipementId;
    }

    public void setEquipementId(Long equipement) {
        this.equipementId = equipement;
    }

    public Long getReportId() {
        return this.reportId;
    }

    public void setReportId(Long report) {
        this.reportId = report;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Media)) {
            return false;
        }
        return id != null && id.equals(((Media) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Media{" +
            "id=" + getId() +
            ", fileName='" + getFileName() + "'" +
            ", filePath='" + getFilePath() + "'" +
            ", fileType='" + getFileType() + "'" +
            ", authType='" + getAuthType() + "'" +
            "}";
    }
}
