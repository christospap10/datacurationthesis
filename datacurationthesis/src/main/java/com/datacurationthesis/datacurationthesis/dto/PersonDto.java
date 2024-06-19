package com.datacurationthesis.datacurationthesis.dto;

public class PersonDto {
	private Integer id;
	private String fullname;

	public PersonDto() {
	}

	public PersonDto(Integer id, String fullname) {
		this.id = id;
		this.fullname = fullname;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getFullname() {
		return fullname;
	}

	public void setFullname(String fullname) {
		this.fullname = fullname;
	}

	@Override
	public String toString() {
		return "PersonDTO [id=" + id + ", fullname=" + fullname + "]";
	}

}
