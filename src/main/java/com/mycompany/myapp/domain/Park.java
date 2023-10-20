package com.mycompany.myapp.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * A Park.
 */
@Table("park")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Park implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column("id")
    private Long id;

    @Column("park_name")
    private String parkName;

    @Column("park_address")
    private String parkAddress;

    @Column("longtitude")
    private BigDecimal longtitude;

    @Column("latitude")
    private BigDecimal latitude;

    @Column("verified")
    private Boolean verified;

    @Column("date_install")
    private Instant dateInstall;

    @Column("date_open")
    private Instant dateOpen;

    @Column("date_close")
    private Instant dateClose;

    @Column("note")
    private String note;

    @Column("reseller")
    private String reseller;

    @Transient
    @JsonIgnoreProperties(value = { "reports", "media", "park" }, allowSetters = true)
    private Set<Equipement> equipements = new HashSet<>();

    @Transient
    @JsonIgnoreProperties(value = { "park", "equipement", "report" }, allowSetters = true)
    private Set<Media> media = new HashSet<>();

    @Transient
    @JsonIgnoreProperties(value = { "media", "equipement", "park" }, allowSetters = true)
    private Set<Report> reports = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Park id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getParkName() {
        return this.parkName;
    }

    public Park parkName(String parkName) {
        this.setParkName(parkName);
        return this;
    }

    public void setParkName(String parkName) {
        this.parkName = parkName;
    }

    public String getParkAddress() {
        return this.parkAddress;
    }

    public Park parkAddress(String parkAddress) {
        this.setParkAddress(parkAddress);
        return this;
    }

    public void setParkAddress(String parkAddress) {
        this.parkAddress = parkAddress;
    }

    public BigDecimal getLongtitude() {
        return this.longtitude;
    }

    public Park longtitude(BigDecimal longtitude) {
        this.setLongtitude(longtitude);
        return this;
    }

    public void setLongtitude(BigDecimal longtitude) {
        this.longtitude = longtitude != null ? longtitude.stripTrailingZeros() : null;
    }

    public BigDecimal getLatitude() {
        return this.latitude;
    }

    public Park latitude(BigDecimal latitude) {
        this.setLatitude(latitude);
        return this;
    }

    public void setLatitude(BigDecimal latitude) {
        this.latitude = latitude != null ? latitude.stripTrailingZeros() : null;
    }

    public Boolean getVerified() {
        return this.verified;
    }

    public Park verified(Boolean verified) {
        this.setVerified(verified);
        return this;
    }

    public void setVerified(Boolean verified) {
        this.verified = verified;
    }

    public Instant getDateInstall() {
        return this.dateInstall;
    }

    public Park dateInstall(Instant dateInstall) {
        this.setDateInstall(dateInstall);
        return this;
    }

    public void setDateInstall(Instant dateInstall) {
        this.dateInstall = dateInstall;
    }

    public Instant getDateOpen() {
        return this.dateOpen;
    }

    public Park dateOpen(Instant dateOpen) {
        this.setDateOpen(dateOpen);
        return this;
    }

    public void setDateOpen(Instant dateOpen) {
        this.dateOpen = dateOpen;
    }

    public Instant getDateClose() {
        return this.dateClose;
    }

    public Park dateClose(Instant dateClose) {
        this.setDateClose(dateClose);
        return this;
    }

    public void setDateClose(Instant dateClose) {
        this.dateClose = dateClose;
    }

    public String getNote() {
        return this.note;
    }

    public Park note(String note) {
        this.setNote(note);
        return this;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getReseller() {
        return this.reseller;
    }

    public Park reseller(String reseller) {
        this.setReseller(reseller);
        return this;
    }

    public void setReseller(String reseller) {
        this.reseller = reseller;
    }

    public Set<Equipement> getEquipements() {
        return this.equipements;
    }

    public void setEquipements(Set<Equipement> equipements) {
        if (this.equipements != null) {
            this.equipements.forEach(i -> i.setPark(null));
        }
        if (equipements != null) {
            equipements.forEach(i -> i.setPark(this));
        }
        this.equipements = equipements;
    }

    public Park equipements(Set<Equipement> equipements) {
        this.setEquipements(equipements);
        return this;
    }

    public Park addEquipement(Equipement equipement) {
        this.equipements.add(equipement);
        equipement.setPark(this);
        return this;
    }

    public Park removeEquipement(Equipement equipement) {
        this.equipements.remove(equipement);
        equipement.setPark(null);
        return this;
    }

    public Set<Media> getMedia() {
        return this.media;
    }

    public void setMedia(Set<Media> media) {
        if (this.media != null) {
            this.media.forEach(i -> i.setPark(null));
        }
        if (media != null) {
            media.forEach(i -> i.setPark(this));
        }
        this.media = media;
    }

    public Park media(Set<Media> media) {
        this.setMedia(media);
        return this;
    }

    public Park addMedia(Media media) {
        this.media.add(media);
        media.setPark(this);
        return this;
    }

    public Park removeMedia(Media media) {
        this.media.remove(media);
        media.setPark(null);
        return this;
    }

    public Set<Report> getReports() {
        return this.reports;
    }

    public void setReports(Set<Report> reports) {
        if (this.reports != null) {
            this.reports.forEach(i -> i.setPark(null));
        }
        if (reports != null) {
            reports.forEach(i -> i.setPark(this));
        }
        this.reports = reports;
    }

    public Park reports(Set<Report> reports) {
        this.setReports(reports);
        return this;
    }

    public Park addReport(Report report) {
        this.reports.add(report);
        report.setPark(this);
        return this;
    }

    public Park removeReport(Report report) {
        this.reports.remove(report);
        report.setPark(null);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Park)) {
            return false;
        }
        return id != null && id.equals(((Park) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Park{" +
            "id=" + getId() +
            ", parkName='" + getParkName() + "'" +
            ", parkAddress='" + getParkAddress() + "'" +
            ", longtitude=" + getLongtitude() +
            ", latitude=" + getLatitude() +
            ", verified='" + getVerified() + "'" +
            ", dateInstall='" + getDateInstall() + "'" +
            ", dateOpen='" + getDateOpen() + "'" +
            ", dateClose='" + getDateClose() + "'" +
            ", note='" + getNote() + "'" +
            ", reseller='" + getReseller() + "'" +
            "}";
    }
}
