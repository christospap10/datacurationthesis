package com.datacurationthesis.datacurationthesis.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "contributions")  // Ensure the table name is correct
public class Contribution {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "peopleid", nullable = false)
    private int personId;

    @Column(name = "productionid", nullable = false)
    private int productionId;

    @Column(name = "roleid", nullable = false)
    private int roleId;

    private String subrole;

    @Column(name = "systemid", nullable = false)
    private int systemId;

    @Column(nullable = false)
    private LocalDateTime timestamp;

    // Navigational Properties
    @ManyToOne
    @JoinColumn(name = "peopleid", insertable = false, updatable = false)
    @JsonIgnore
    private Person person;

    @ManyToOne
    @JoinColumn(name = "productionid", insertable = false, updatable = false)
    @JsonIgnore
    private Production production;

    @ManyToOne
    @JoinColumn(name = "roleid", insertable = false, updatable = false)
    @JsonIgnore
    private Role role;

    @ManyToOne
    @JoinColumn(name = "systemid", insertable = false, updatable = false)
    @JsonIgnore
    private System system;

    public Contribution() {
    }

    public Contribution(int id, int personId, int productionId, int roleId, String subrole, int systemId, LocalDateTime timestamp, Person person, Production production, Role role, System system) {
        this.id = id;
        this.personId = personId;
        this.productionId = productionId;
        this.roleId = roleId;
        this.subrole = subrole;
        this.systemId = systemId;
        this.timestamp = timestamp;
        this.person = person;
        this.production = production;
        this.role = role;
        this.system = system;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getPersonId() {
        return personId;
    }

    public void setPersonId(int personId) {
        this.personId = personId;
    }

    public int getProductionId() {
        return productionId;
    }

    public void setProductionId(int productionId) {
        this.productionId = productionId;
    }

    public int getRoleId() {
        return roleId;
    }

    public void setRoleId(int roleId) {
        this.roleId = roleId;
    }

    public String getSubRole() {
        return subrole;
    }

    public void setSubRole(String subrole) {
        this.subrole = subrole;
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

    @JsonIgnore
    public Person getPerson() {
        return person;
    }

    @JsonIgnore
    public void setPerson(Person person) {
        this.person = person;
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
    public Role getRole() {
        return role;
    }

    @JsonIgnore
    public void setRole(Role role) {
        this.role = role;
    }

    @JsonIgnore
    public System getSystem() {
        return system;
    }

    @JsonIgnore
    public void setSystem(System system) {
        this.system = system;
    }
}
