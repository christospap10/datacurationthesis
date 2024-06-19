package com.datacurationthesis.datacurationthesis.controller;

import com.datacurationthesis.datacurationthesis.dto.PersonDto;
import com.datacurationthesis.datacurationthesis.entity.Person;
import com.datacurationthesis.datacurationthesis.service.PersonService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/datacuration/persons")
public class PersonController {

	@Autowired
	private PersonService personService;

	@GetMapping("/all")
	public List<Person> getAllPersons() {
		return personService.getAllPersons();
	}

	@GetMapping("/firstten")
	public Page<Person> firstTen() {
		return personService.getFirstTenPersons();
	}

	@GetMapping("/persondto")
	public ResponseEntity<List<PersonDto>> getPersonsDto() {
		List<PersonDto> persons = personService.getPersonDto();
		return ResponseEntity.ok(persons);
	}


}
