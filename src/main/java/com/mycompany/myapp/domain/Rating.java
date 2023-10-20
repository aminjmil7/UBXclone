package com.mycompany.myapp.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.time.Instant;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * A Rating.
 */
@Table("rating")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Rating implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column("id")
    private Long id;

    @Column("comment")
    private String comment;

    @Column("rating_number")
    private Integer ratingNumber;

    @Column("rating_date")
    private Instant ratingDate;

    @Column("user_id")
    private Integer user_id;

    @Transient
    @JsonIgnoreProperties(value = { "equipements", "media", "reports" }, allowSetters = true)
    private Park park;

    @Transient
    @JsonIgnoreProperties(value = { "reports", "media", "park" }, allowSetters = true)
    private Equipement equipement;

    @Column("park_id")
    private Long parkId;

    @Column("equipement_id")
    private Long equipementId;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Rating id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getComment() {
        return this.comment;
    }

    public Rating comment(String comment) {
        this.setComment(comment);
        return this;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Integer getRatingNumber() {
        return this.ratingNumber;
    }

    public Rating ratingNumber(Integer ratingNumber) {
        this.setRatingNumber(ratingNumber);
        return this;
    }

    public void setRatingNumber(Integer ratingNumber) {
        this.ratingNumber = ratingNumber;
    }

    public Instant getRatingDate() {
        return this.ratingDate;
    }

    public Rating ratingDate(Instant ratingDate) {
        this.setRatingDate(ratingDate);
        return this;
    }

    public void setRatingDate(Instant ratingDate) {
        this.ratingDate = ratingDate;
    }

    public Integer getUser_id() {
        return this.user_id;
    }

    public Rating user_id(Integer user_id) {
        this.setUser_id(user_id);
        return this;
    }

    public void setUser_id(Integer user_id) {
        this.user_id = user_id;
    }

    public Park getPark() {
        return this.park;
    }

    public void setPark(Park park) {
        this.park = park;
        this.parkId = park != null ? park.getId() : null;
    }

    public Rating park(Park park) {
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

    public Rating equipement(Equipement equipement) {
        this.setEquipement(equipement);
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

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Rating)) {
            return false;
        }
        return id != null && id.equals(((Rating) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Rating{" +
            "id=" + getId() +
            ", comment='" + getComment() + "'" +
            ", ratingNumber=" + getRatingNumber() +
            ", ratingDate='" + getRatingDate() + "'" +
            ", user_id=" + getUser_id() +
            "}";
    }
}
