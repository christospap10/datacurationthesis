package com.datacurationthesis.datacurationthesis.entity;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;


@Entity
@Table(name = "events")
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "productionid",nullable = false)
    private int productionId;

    @Column(name = "venueid",nullable = false)
    private int venueId;

    @Column(name = "dateevent",nullable = false)
    private LocalDateTime dateEvent;

    @Column(name = "pricerange",nullable = false)
    private String priceRange;

    @Column(name = "systemid",nullable = false)
    private int systemId;

    @Column(nullable = false)
    private LocalDateTime timestamp;

    @Column(name = "claimed", nullable = false)
    private boolean isClaimed;

    // Navigational Properties
    @ManyToOne
    @JoinColumn(name = "productionid", insertable = false, updatable = false)
    @JsonIgnore
    private Production production;

    @ManyToOne
    @JoinColumn(name = "systemid", insertable = false, updatable = false)
    @JsonIgnore
    private System system;

    @ManyToOne
    @JoinColumn(name = "venueid", insertable = false, updatable = false)
    @JsonIgnore
    private Venue venue;

    @OneToMany(mappedBy = "event")
    @JsonIgnore
    private List<UserEvent> userEvents;

    public Event() {
    }

    public Event(int id, int productionId, int venueId, LocalDateTime dateEvent, String priceRange, int systemId, LocalDateTime timestamp, boolean isClaimed, Production production, System system, Venue venue, List<UserEvent> userEvents) {
        this.id = id;
        this.productionId = productionId;
        this.venueId = venueId;
        this.dateEvent = dateEvent;
        this.priceRange = priceRange;
        this.systemId = systemId;
        this.timestamp = timestamp;
        this.isClaimed = isClaimed;
        this.production = production;
        this.system = system;
        this.venue = venue;
        this.userEvents = userEvents;
    }
// Getters and Setters

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getProductionId() {
        return productionId;
    }

    public void setProductionId(int productionId) {
        this.productionId = productionId;
    }

    public int getVenueId() {
        return venueId;
    }

    public void setVenueId(int venueId) {
        this.venueId = venueId;
    }

    public LocalDateTime getDateEvent() {
        return dateEvent;
    }

    public void setDateEvent(LocalDateTime dateEvent) {
        this.dateEvent = dateEvent;
    }

    public String getPriceRange() {
        return priceRange;
    }

    public void setPriceRange(String priceRange) {
        this.priceRange = priceRange;
    }

    public int getSystemId() {
        return systemId;
    }

    public void setSystemId(int systemId) {
        this.systemId = systemId;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public boolean isClaimed() {
        return isClaimed;
    }

    public void setClaimed(boolean claimed) {
        isClaimed = claimed;
    }

    @JsonIgnore
    public Production getProduction() {
        return production;
    }

    @JsonIgnore
    public void setProduction(Production production) {
        this.production = production;
    }

    @JsonIgnore
    public System getSystem() {
        return system;
    }

    @JsonIgnore
    public void setSystem(System system) {
        this.system = system;
    }
    @JsonIgnore
    public Venue getVenue() {
        return venue;
    }

    @JsonIgnore
    public void setVenue(Venue venue) {
        this.venue = venue;
    }

    @JsonIgnore
    public List<UserEvent> getUserEvents() {
        return userEvents;
    }

    @JsonIgnore
    public void setUserEvents(List<UserEvent> userEvents) {
        this.userEvents = userEvents;
    }
}