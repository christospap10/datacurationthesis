package com.datacurationthesis.datacurationthesis.entity;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String username;

    @Column(nullable = false)
    private String email;

    private String password;

    private Boolean enabled;

    private String verificationCode;

    @Column(name = "2FA_enabled", nullable = false)
    private boolean twoFAEnabled;

    @Column(name = "2FA_code")
    private String twoFACode;

    private String userSecret;

    @ElementCollection
    private List<String> performerRoles;

    private String facebook;

    private String youtube;

    private String instagram;

    private String bioPdfLocation;

    private String phoneNumber;

    private Boolean phoneNumberVerified;

    // Navigational Properties
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<UserImage> userImages;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<UserAuthority> userAuthorities;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Transaction> userTransactions;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<UserVenue> userVenues;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<UserEvent> userEvents;

    // Getters and Setters

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public String getVerificationCode() {
        return verificationCode;
    }

    public void setVerificationCode(String verificationCode) {
        this.verificationCode = verificationCode;
    }

    public boolean isTwoFAEnabled() {
        return twoFAEnabled;
    }

    public void setTwoFAEnabled(boolean twoFAEnabled) {
        this.twoFAEnabled = twoFAEnabled;
    }

    public String getTwoFACode() {
        return twoFACode;
    }

    public void setTwoFACode(String twoFACode) {
        this.twoFACode = twoFACode;
    }

    public String getUserSecret() {
        return userSecret;
    }

    public void setUserSecret(String userSecret) {
        this.userSecret = userSecret;
    }

    public List<String> getPerformerRoles() {
        return performerRoles;
    }

    public void setPerformerRoles(List<String> performerRoles) {
        this.performerRoles = performerRoles;
    }

    public String getFacebook() {
        return facebook;
    }

    public void setFacebook(String facebook) {
        this.facebook = facebook;
    }

    public String getYoutube() {
        return youtube;
    }

    public void setYoutube(String youtube) {
        this.youtube = youtube;
    }

    public String getInstagram() {
        return instagram;
    }

    public void setInstagram(String instagram) {
        this.instagram = instagram;
    }

    public String getBioPdfLocation() {
        return bioPdfLocation;
    }

    public void setBioPdfLocation(String bioPdfLocation) {
        this.bioPdfLocation = bioPdfLocation;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public Boolean getPhoneNumberVerified() {
        return phoneNumberVerified;
    }

    public void setPhoneNumberVerified(Boolean phoneNumberVerified) {
        this.phoneNumberVerified = phoneNumberVerified;
    }

    public List<UserImage> getUserImages() {
        return userImages;
    }

    public void setUserImages(List<UserImage> userImages) {
        this.userImages = userImages;
    }

    public List<UserAuthority> getUserAuthorities() {
        return userAuthorities;
    }

    public void setUserAuthorities(List<UserAuthority> userAuthorities) {
        this.userAuthorities = userAuthorities;
    }

    public List<Transaction> getUserTransactions() {
        return userTransactions;
    }

    public void setUserTransactions(List<Transaction> userTransactions) {
        this.userTransactions = userTransactions;
    }

    public List<UserVenue> getUserVenues() {
        return userVenues;
    }

    public void setUserVenues(List<UserVenue> userVenues) {
        this.userVenues = userVenues;
    }

    public List<UserEvent> getUserEvents() {
        return userEvents;
    }

    public void setUserEvents(List<UserEvent> userEvents) {
        this.userEvents = userEvents;
    }
}