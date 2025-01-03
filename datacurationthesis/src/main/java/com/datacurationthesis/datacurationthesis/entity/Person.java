package com.datacurationthesis.datacurationthesis.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;


@Entity
@Table(name = "persons", schema = "public")
public class Person {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String fullname;

    @Column(name = "systemid", nullable = false)
    private int systemId;

    @Column(nullable = false)
    private LocalDateTime timestamp;

    private String haircolor;
    private String height;
    private String eyecolor;
    private String weight;

    private List<String> languages;

    private String description;
    private String bio;
    private LocalDateTime birthdate;

    private List<String> roles;

    @Column(nullable = false)
    private boolean isclaimed;

    @Enumerated(EnumType.ORDINAL)
    private ClaimingStatus claimingstatus;

    // Navigational Properties
    @ManyToOne
    @JoinColumn(name = "systemid", insertable = false, updatable = false)
    @JsonIgnore
    private System system;

    @OneToMany(mappedBy = "personId")
    @JsonIgnore
    private List<Contribution> contributions;

    @OneToMany(mappedBy = "person")
    @JsonIgnore
    private List<Image> images;

    public Person() {
    }

    public Person(Integer id, String fullname, int systemId, LocalDateTime timestamp, String haircolor, String height,
            String eyecolor, String weight, List<String> languages, String description, String bio,
            LocalDateTime birthdate, List<String> roles, boolean isclaimed, ClaimingStatus claimingstatus,
            System system, List<Contribution> contributions, List<Image> images) {
        this.id = id;
        this.fullname = fullname;
        this.systemId = systemId;
        this.timestamp = timestamp;
        this.haircolor = haircolor;
        this.height = height;
        this.eyecolor = eyecolor;
        this.weight = weight;
        this.languages = languages;
        this.description = description;
        this.bio = bio;
        this.birthdate = birthdate;
        this.roles = roles;
        this.isclaimed = isclaimed;
        this.claimingstatus = claimingstatus;
        this.system = system;
        this.contributions = contributions;
        this.images = images;
    }

    public Integer getId() {
        return this.id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getFullname() {
        return this.fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public int getSystemId() {
        return this.systemId;
    }

    public void setSystemId(int systemId) {
        this.systemId = systemId;
    }

    public LocalDateTime getTimestamp() {
        return this.timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public String getHaircolor() {
        return this.haircolor;
    }

    public void setHaircolor(String haircolor) {
        this.haircolor = haircolor;
    }

    public String getHeight() {
        return this.height;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public String getEyecolor() {
        return this.eyecolor;
    }

    public void setEyecolor(String eyecolor) {
        this.eyecolor = eyecolor;
    }

    public String getWeight() {
        return this.weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public List<String> getLanguages() {
        return this.languages;
    }

    public void setLanguages(List<String> languages) {
        this.languages = languages;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getBio() {
        return this.bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public LocalDateTime getBirthdate() {
        return this.birthdate;
    }

    public void setBirthdate(LocalDateTime birthdate) {
        this.birthdate = birthdate;
    }

    public List<String> getRoles() {
        return this.roles;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }

    public boolean isIsclaimed() {
        return this.isclaimed;
    }

    public boolean getIsclaimed() {
        return this.isclaimed;
    }

    public void setIsclaimed(boolean isclaimed) {
        this.isclaimed = isclaimed;
    }

    public ClaimingStatus getClaimingstatus() {
        return this.claimingstatus;
    }

    public void setClaimingstatus(ClaimingStatus claimingstatus) {
        this.claimingstatus = claimingstatus;
    }

    public System getSystem() {
        return this.system;
    }

    public void setSystem(System system) {
        this.system = system;
    }

    public List<Contribution> getContributions() {
        return this.contributions;
    }

    public void setContributions(List<Contribution> contributions) {
        this.contributions = contributions;
    }

    public List<Image> getImages() {
        return this.images;
    }

    public void setImages(List<Image> images) {
        this.images = images;
    }

    @Override
    public String toString() {
        return "Person{" +
                "id=" + id +
                ", fullname='" + fullname + '\'' +
                ", systemId=" + systemId +
                ", timestamp=" + timestamp +
                ", haircolor='" + haircolor + '\'' +
                ", height='" + height + '\'' +
                ", eyecolor='" + eyecolor + '\'' +
                ", weight='" + weight + '\'' +
                ", languages=" + languages +
                ", description='" + description + '\'' +
                ", bio='" + bio + '\'' +
                ", birthdate=" + birthdate +
                ", roles=" + roles +
                ", isclaimed=" + isclaimed +
                ", claimingstatus=" + claimingstatus +
                // Avoid recursion by only including the ID of the system and contributions
                // ", systemId=" + (system != null ? system.getId() : "null") +
                // ", contributionsCount=" + (contributions != null ? contributions.size() : 0) +
                // ", imagesCount=" + (images != null ? images.size() : 0) +
                '}';
    }

}