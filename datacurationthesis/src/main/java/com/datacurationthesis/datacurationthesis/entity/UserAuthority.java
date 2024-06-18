package com.datacurationthesis.datacurationthesis.entity;

import jakarta.persistence.*;

@Entity
@IdClass(UserAuthorityId.class)
@Table(name = "user_authorities")
public class UserAuthority {

    @Id
    @Column(name = "user_id")
    private Integer userId;

    @Id
    @Column(name = "authority_id")
    private Integer authorityId;

    // Navigational Properties
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "authority_id", insertable = false, updatable = false)
    private Authority authority;

    // Getters and Setters

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getAuthorityId() {
        return authorityId;
    }

    public void setAuthorityId(Integer authorityId) {
        this.authorityId = authorityId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Authority getAuthority() {
        return authority;
    }

    public void setAuthority(Authority authority) {
        this.authority = authority;
    }
}