package com.mycompany.myapp.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.time.Instant;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * A Events.
 */
@Table("Events")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Events implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column("id")
    private Long id;

    @Column("event_name")
    private String eventName;

    @Column("event_date")
    private Instant eventDate;

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

    public Events id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEventName() {
        return this.eventName;
    }

    public Events eventName(String eventName) {
        this.setEventName(eventName);
        return this;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public Instant getEventDate() {
        return this.eventDate;
    }

    public Events eventDate(Instant eventDate) {
        this.setEventDate(eventDate);
        return this;
    }

    public void setEventDate(Instant eventDate) {
        this.eventDate = eventDate;
    }

    public Integer getUser_id() {
        return this.user_id;
    }

    public Events user_id(Integer user_id) {
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

    public Events park(Park park) {
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

    public Events equipement(Equipement equipement) {
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
        if (!(o instanceof Events)) {
            return false;
        }
        return id != null && id.equals(((Events) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Events{" +
            "id=" + getId() +
            ", eventName='" + getEventName() + "'" +
            ", eventDate='" + getEventDate() + "'" +
            ", user_id=" + getUser_id() +
            "}";
    }
}
