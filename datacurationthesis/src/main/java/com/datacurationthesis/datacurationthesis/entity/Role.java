package com.datacurationthesis.datacurationthesis.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "roles")
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "role",nullable = false)
    private String role1;

    @Column(nullable = false)
    private Integer systemid;

    @Column(nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date timestamp;

    // Navigational Properties
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "systemid", insertable = false, updatable = false)
    @JsonIgnore
    private System system;

    @OneToMany(mappedBy = "role", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Contribution> contributions;

    public Role() {
    }

    public Role(Integer id, String role1, Integer systemid, Date timestamp, System system, List<Contribution> contributions) {
        this.id = id;
        this.role1 = role1;
        this.systemid = systemid;
        this.timestamp = timestamp;
        this.system = system;
        this.contributions = contributions;
    }
// Getters and Setters

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getRole1() {
        return role1;
    }

    public void setRole1(String role1) {
        this.role1 = role1;
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

    @JsonIgnore
    public System getSystem() {
        return system;
    }

    @JsonIgnore
    public void setSystem(System system) {
        this.system = system;
    }

    @JsonIgnore
    public List<Contribution> getContributions() {
        return contributions;
    }

    @JsonIgnore
    public void setContributions(List<Contribution> contributions) {
        this.contributions = contributions;
    }
}