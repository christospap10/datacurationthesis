package com.datacurationthesis.datacurationthesis.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "assigned_users")  // Ensure the table name is correct
public class AssignedUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "userid", nullable = false)
    private int userId;

    @Column(name = "personid", nullable = false)
    private int personId;

    @Column(name = "requestid", nullable = false)
    private int requestId;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    // Navigational Properties
    @ManyToOne
    @JoinColumn(name = "userid", insertable = false, updatable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "personid", insertable = false, updatable = false)
    private Person person;

    @ManyToOne
    @JoinColumn(name = "requestid", insertable = false, updatable = false)
    private AccountRequest accountRequest;

    // Getters and Setters

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getPersonId() {
        return personId;
    }

    public void setPersonId(int personId) {
        this.personId = personId;
    }

    public int getRequestId() {
        return requestId;
    }

    public void setRequestId(int requestId) {
        this.requestId = requestId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }

    public AccountRequest getAccountRequest() {
        return accountRequest;
    }

    public void setAccountRequest(AccountRequest accountRequest) {
        this.accountRequest = accountRequest;
    }
}
