package com.datacurationthesis.datacurationthesis.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
	@JsonIgnore
	private System system;

	@OneToMany(mappedBy = "organizer", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	@JsonIgnore
	private List<Production> productions;

	public Organizer() {
	}

	public Organizer(Integer id, String name, String address, String town, String postcode, String phone, String email, String doy, String afm, Integer systemid, Date timestamp, System system, List<Production> productions) {
		this.id = id;
		this.name = name;
		this.address = address;
		this.town = town;
		this.postcode = postcode;
		this.phone = phone;
		this.email = email;
		this.doy = doy;
		this.afm = afm;
		this.systemid = systemid;
		this.timestamp = timestamp;
		this.system = system;
		this.productions = productions;
	}
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

	@JsonIgnore
	public System getSystem() {
		return system;
	}

	@JsonIgnore
	public void setSystem(System system) {
		this.system = system;
	}

	@JsonIgnore
	public List<Production> getProductions() {
		return productions;
	}

	@JsonIgnore
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