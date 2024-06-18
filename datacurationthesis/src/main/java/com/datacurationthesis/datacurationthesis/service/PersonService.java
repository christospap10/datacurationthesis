package com.datacurationthesis.datacurationthesis.service;

import com.datacurationthesis.datacurationthesis.entity.Person;
import com.datacurationthesis.datacurationthesis.repository.PersonRepository;
import jakarta.persistence.EntityManager;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PersonService {

	@Autowired
	private PersonRepository personRepository;

	@Autowired
	private EntityManager entityManager;


	public List<Person> getAllPersons() {
		List<Person> persons = personRepository.findAll();	
		return persons;
	}

}
