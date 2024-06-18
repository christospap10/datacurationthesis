package com.datacurationthesis.datacurationthesis.entity;

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
    private Person person;

    @ManyToOne
    @JoinColumn(name = "productionid", insertable = false, updatable = false)
    private Production production;

    @ManyToOne
    @JoinColumn(name = "roleid", insertable = false, updatable = false)
    private Role role;

    @ManyToOne
    @JoinColumn(name = "systemid", insertable = false, updatable = false)
    private System system;

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

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }

    public Production getProduction() {
        return production;
    }

    public void setProduction(Production production) {
        this.production = production;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public System getSystem() {
        return system;
    }

    public void setSystem(System system) {
        this.system = system;
    }
}
