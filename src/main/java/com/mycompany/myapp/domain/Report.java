package com.mycompany.myapp.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * A Report.
 */
@Table("report")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Report implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column("id")
    private Long id;

    @Column("mail")
    private String mail;

    @Column("message")
    private String message;

    @Column("date")
    private Instant date;

    @Transient
    @JsonIgnoreProperties(value = { "park", "equipement", "report" }, allowSetters = true)
    private Set<Media> media = new HashSet<>();

    @Transient
    @JsonIgnoreProperties(value = { "reports", "media", "park" }, allowSetters = true)
    private Equipement equipement;

    @Transient
    @JsonIgnoreProperties(value = { "equipements", "media", "reports" }, allowSetters = true)
    private Park park;

    @Column("equipement_id")
    private Long equipementId;

    @Column("park_id")
    private Long parkId;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Report id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMail() {
        return this.mail;
    }

    public Report mail(String mail) {
        this.setMail(mail);
        return this;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getMessage() {
        return this.message;
    }

    public Report message(String message) {
        this.setMessage(message);
        return this;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Instant getDate() {
        return this.date;
    }

    public Report date(Instant date) {
        this.setDate(date);
        return this;
    }

    public void setDate(Instant date) {
        this.date = date;
    }

    public Set<Media> getMedia() {
        return this.media;
    }

    public void setMedia(Set<Media> media) {
        if (this.media != null) {
            this.media.forEach(i -> i.setReport(null));
        }
        if (media != null) {
            media.forEach(i -> i.setReport(this));
        }
        this.media = media;
    }

    public Report media(Set<Media> media) {
        this.setMedia(media);
        return this;
    }

    public Report addMedia(Media media) {
        this.media.add(media);
        media.setReport(this);
        return this;
    }

    public Report removeMedia(Media media) {
        this.media.remove(media);
        media.setReport(null);
        return this;
    }

    public Equipement getEquipement() {
        return this.equipement;
    }

    public void setEquipement(Equipement equipement) {
        this.equipement = equipement;
        this.equipementId = equipement != null ? equipement.getId() : null;
    }

    public Report equipement(Equipement equipement) {
        this.setEquipement(equipement);
        return this;
    }

    public Park getPark() {
        return this.park;
    }

    public void setPark(Park park) {
        this.park = park;
        this.parkId = park != null ? park.getId() : null;
    }

    public Report park(Park park) {
        this.setPark(park);
        return this;
    }

    public Long getEquipementId() {
        return this.equipementId;
    }

    public void setEquipementId(Long equipement) {
        this.equipementId = equipement;
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
        if (!(o instanceof Report)) {
            return false;
        }
        return id != null && id.equals(((Report) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Report{" +
            "id=" + getId() +
            ", mail='" + getMail() + "'" +
            ", message='" + getMessage() + "'" +
            ", date='" + getDate() + "'" +
            "}";
    }
}
