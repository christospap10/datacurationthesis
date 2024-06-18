package com.datacurationthesis.datacurationthesis.entity;

import jakarta.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
public class Organizer {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@Column(nullable = false)
	private String name;

	@Column(nullable = false)
	private String address;

	@Column(nullable = false)
	private String town;

	@Column(nullable = false)
	private String postcode;

	@Column(nullable = false)
	private String phone;

	@Column(nullable = false)
	private String email;

	@Column(nullable = false)
	private String doy;

	@Column(nullable = false)
	private String afm;

	@Column(nullable = false)
	private Integer systemid;

	@Column(nullable = false)
	@Temporal(TemporalType.TIMESTAMP)
	private Date timestamp;

	// Navigational Properties
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "systemid", insertable = false, updatable = false)
	private System system;

	@OneToMany(mappedBy = "organizer", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private List<Production> productions;

	// Getters and Setters

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getTown() {
		return town;
	}

	public void setTown(String town) {
		this.town = town;
	}

	public String getPostcode() {
		return postcode;
	}

	public void setPostcode(String postcode) {
		this.postcode = postcode;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getDoy() {
		return doy;
	}

	public void setDoy(String doy) {
		this.doy = doy;
	}

	public String getAfm() {
		return afm;
	}

	public void setAfm(String afm) {
		this.afm = afm;
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

	public System getSystem() {
		return system;
	}

	public void setSystem(System system) {
		this.system = system;
	}

	public List<Production> getProductions() {
		return productions;
	}

	public void setProductions(List<Production> productions) {
		this.productions = productions;
	}

	@Override
	public String toString() {
		return "Organizer{" +
				"id=" + id +
				", name='" + name + '\'' +
				", address='" + address + '\'' +
				", town='" + town + '\'' +
				", postcode='" + postcode + '\'' +
				", phone='" + phone + '\'' +
				", email='" + email + '\'' +
				", doy='" + doy + '\'' +
				", afm='" + afm + '\'' +
				", systemid=" + systemid +
				", timestamp=" + timestamp +
				", system=" + system +
				", productions=" + productions +
				'}';
	}
}