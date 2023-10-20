package com.mycompany.myapp.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * A Equipement.
 */
@Table("equipement")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Equipement implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column("id")
    private Long id;

    @Column("model_name")
    private String modelName;

    @Column("model_number")
    private String modelNumber;

    @Column("instruction")
    private String instruction;

    @Column("verified")
    private Boolean verified;

    @Transient
    @JsonIgnoreProperties(value = { "media", "equipement", "park" }, allowSetters = true)
    private Set<Report> reports = new HashSet<>();

    @Transient
    @JsonIgnoreProperties(value = { "park", "equipement", "report" }, allowSetters = true)
    private Set<Media> media = new HashSet<>();

    @Transient
    @JsonIgnoreProperties(value = { "equipements", "media", "reports" }, allowSetters = true)
    private Park park;

    @Column("park_id")
    private Long parkId;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Equipement id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getModelName() {
        return this.modelName;
    }

    public Equipement modelName(String modelName) {
        this.setModelName(modelName);
        return this;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    public String getModelNumber() {
        return this.modelNumber;
    }

    public Equipement modelNumber(String modelNumber) {
        this.setModelNumber(modelNumber);
        return this;
    }

    public void setModelNumber(String modelNumber) {
        this.modelNumber = modelNumber;
    }

    public String getInstruction() {
        return this.instruction;
    }

    public Equipement instruction(String instruction) {
        this.setInstruction(instruction);
        return this;
    }

    public void setInstruction(String instruction) {
        this.instruction = instruction;
    }

    public Boolean getVerified() {
        return this.verified;
    }

    public Equipement verified(Boolean verified) {
        this.setVerified(verified);
        return this;
    }

    public void setVerified(Boolean verified) {
        this.verified = verified;
    }

    public Set<Report> getReports() {
        return this.reports;
    }

    public void setReports(Set<Report> reports) {
        if (this.reports != null) {
            this.reports.forEach(i -> i.setEquipement(null));
        }
        if (reports != null) {
            reports.forEach(i -> i.setEquipement(this));
        }
        this.reports = reports;
    }

    public Equipement reports(Set<Report> reports) {
        this.setReports(reports);
        return this;
    }

    public Equipement addReport(Report report) {
        this.reports.add(report);
        report.setEquipement(this);
        return this;
    }

    public Equipement removeReport(Report report) {
        this.reports.remove(report);
        report.setEquipement(null);
        return this;
    }

    public Set<Media> getMedia() {
        return this.media;
    }

    public void setMedia(Set<Media> media) {
        if (this.media != null) {
            this.media.forEach(i -> i.setEquipement(null));
        }
        if (media != null) {
            media.forEach(i -> i.setEquipement(this));
        }
        this.media = media;
    }

    public Equipement media(Set<Media> media) {
        this.setMedia(media);
        return this;
    }

    public Equipement addMedia(Media media) {
        this.media.add(media);
        media.setEquipement(this);
        return this;
    }

    public Equipement removeMedia(Media media) {
        this.media.remove(media);
        media.setEquipement(null);
        return this;
    }

    public Park getPark() {
        return this.park;
    }

    public void setPark(Park park) {
        this.park = park;
        this.parkId = park != null ? park.getId() : null;
    }

    public Equipement park(Park park) {
        this.setPark(park);
        return this;
    }

    public Long getParkId() {
        return this.parkId;
    }

    public void setParkId(Long park) {
        this.parkId = park;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Equipement)) {
            return false;
        }
        return id != null && id.equals(((Equipement) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Equipement{" +
            "id=" + getId() +
            ", modelName='" + getModelName() + "'" +
            ", modelNumber='" + getModelNumber() + "'" +
            ", instruction='" + getInstruction() + "'" +
            ", verified='" + getVerified() + "'" +
            "}";
    }
}
