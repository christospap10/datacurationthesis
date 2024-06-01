package com.datacurationthesis.datacurationthesis.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.Date;

@Entity
@Table(name = "persons")
public class Person {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column
	private Integer ID;

	@Column
	private String fullname;
	@Column
	private String systemid;
	@Column
	private Date timestamp;
	@Column
	private String haircolor;
	@Column
	private Integer height;
	@Column
	private String eyecolor;
	@Column
	private Integer weight;
	@Column
	private String languages;
	@Column
	private String description;
	@Column
	private Date birthdate;
	@Column
	private String roles;
	@Column
	private Boolean isclaimed;
	@Column
	private String claimingstatus;

	public Integer getId() {
		return this.ID;
	}

	public void setId(Integer ID) {
		this.ID = ID;
	}

	public String getFullname() {
		return this.fullname;
	}

	public void setFullname(String fullname) {
		this.fullname = fullname;
	}

	public String getSystemid() {
		return this.systemid;
	}

	public void setSystemid(String systemid) {
		this.systemid = systemid;
	}

	public Date getTimestamp() {
		return this.timestamp;
	}

	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}

	public String getHaircolor() {
		return this.haircolor;
	}

	public void setHaircolor(String haircolor) {
		this.haircolor = haircolor;
	}

	public Integer getHeight() {
		return this.height;
	}

	public void setHeight(Integer height) {
		this.height = height;
	}

	public String getEyecolor() {
		return this.eyecolor;
	}

	public void setEyecolor(String eyecolor) {
		this.eyecolor = eyecolor;
	}

	public Integer getWeight() {
		return this.weight;
	}

	public void setWeight(Integer weight) {
		this.weight = weight;
	}

	public String getLanguages() {
		return this.languages;
	}

	public void setLanguages(String languages) {
		this.languages = languages;
	}

	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Date getBirthdate() {
		return this.birthdate;
	}

	public void setBirthdate(Date birthdate) {
		this.birthdate = birthdate;
	}

	public String getRoles() {
		return this.roles;
	}

	public void setRoles(String roles) {
		this.roles = roles;
	}

	public Boolean isIsclaimed() {
		return this.isclaimed;
	}

	public Boolean getIsclaimed() {
		return this.isclaimed;
	}

	public void setIsclaimed(Boolean isclaimed) {
		this.isclaimed = isclaimed;
	}

	public String getClaimingstatus() {
		return this.claimingstatus;
	}

	public void setClaimingstatus(String claimingstatus) {
		this.claimingstatus = claimingstatus;
	}

}
