package com.mycompany.myapp.service.dto;

import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link com.mycompany.myapp.domain.Rating} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class RatingDTO implements Serializable {

    private Long id;

    private String comment;

    private Integer ratingNumber;

    private Instant ratingDate;

    private Integer user_id;

    private ParkDTO park;

    private EquipementDTO equipement;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Integer getRatingNumber() {
        return ratingNumber;
    }

    public void setRatingNumber(Integer ratingNumber) {
        this.ratingNumber = ratingNumber;
    }

    public Instant getRatingDate() {
        return ratingDate;
    }

    public void setRatingDate(Instant ratingDate) {
        this.ratingDate = ratingDate;
    }

    public Integer getUser_id() {
        return user_id;
    }

    public void setUser_id(Integer user_id) {
        this.user_id = user_id;
    }

    public ParkDTO getPark() {
        return park;
    }

    public void setPark(ParkDTO park) {
        this.park = park;
    }

    public EquipementDTO getEquipement() {
        return equipement;
    }

    public void setEquipement(EquipementDTO equipement) {
        this.equipement = equipement;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof RatingDTO)) {
            return false;
        }

        RatingDTO ratingDTO = (RatingDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, ratingDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "RatingDTO{" +
            "id=" + getId() +
            ", comment='" + getComment() + "'" +
            ", ratingNumber=" + getRatingNumber() +
            ", ratingDate='" + getRatingDate() + "'" +
            ", user_id=" + getUser_id() +
            ", park=" + getPark() +
            ", equipement=" + getEquipement() +
            "}";
    }
}
