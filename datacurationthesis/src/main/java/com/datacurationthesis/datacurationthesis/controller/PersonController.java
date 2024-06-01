package com.datacurationthesis.datacurationthesis.controller;

import com.datacurationthesis.datacurationthesis.entity.Person;
import com.datacurationthesis.datacurationthesis.service.PersonService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
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
	public List<Person> getFirstTen() {
		return personService.getTenId();
	}

}
