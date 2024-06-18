package com.datacurationthesis.datacurationthesis.entity;

import java.io.Serializable;
import java.util.Objects;

public class UserAuthorityId implements Serializable {

    private Integer userId;
    private Integer authorityId;

    // Default constructor
    public UserAuthorityId() {}

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

    // hashCode and equals

    @Override
    public int hashCode() {
        return Objects.hash(userId, authorityId);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        UserAuthorityId that = (UserAuthorityId) obj;
        return Objects.equals(userId, that.userId) && Objects.equals(authorityId, that.authorityId);
    }
}