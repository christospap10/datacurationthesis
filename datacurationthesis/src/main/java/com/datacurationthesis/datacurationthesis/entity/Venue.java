package com.datacurationthesis.datacurationthesis.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.util.Date;
import java.util.List;


@Entity
@Table(name = "venue", schema = "public")
public class Venue {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "title")
    private String title;

    @Column(name = "address")
    private String address;

    @Column(name = "systemid")
    private Integer systemid;

    @Column(name = "timestamp")
    @Temporal(TemporalType.TIMESTAMP)
    private Date timestamp;

    @Column(name = "claimed")
    private boolean isClaimed;

    // Navigational Properties
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "systemid", insertable = false, updatable = false)
    @JsonIgnore
    private System system;

    @OneToMany(mappedBy = "venue", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Event> events;

    @OneToMany(mappedBy = "venue", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<UserVenue> userVenues;

    public Venue() {
    }

    public Venue(Integer id, String title, String address, Integer systemid, Date timestamp, boolean isClaimed, System system, List<Event> events, List<UserVenue> userVenues) {
        this.id = id;
        this.title = title;
        this.address = address;
        this.systemid = systemid;
        this.timestamp = timestamp;
        this.isClaimed = isClaimed;
        this.system = system;
        this.events = events;
        this.userVenues = userVenues;
    }

    // Getters and Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Integer getSystemId() {
        return systemid;
    }

    public void setSystemId(Integer systemid) {
        this.systemid = systemid;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public boolean isClaimed() {
        return isClaimed;
    }

    public void setClaimed(boolean isClaimed) {
        this.isClaimed = isClaimed;
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
    public List<Event> getEvents() {
        return events;
    }

    @JsonIgnore
    public void setEvents(List<Event> events) {
        this.events = events;
    }

    @JsonIgnore
    public List<UserVenue> getUserVenues() {
        return userVenues;
    }

    @JsonIgnore
    public void setUserVenues(List<UserVenue> userVenues) {
        this.userVenues = userVenues;
    }
}
