package com.datacurationthesis.datacurationthesis.service;

import com.datacurationthesis.datacurationthesis.dto.PersonDto;
import com.datacurationthesis.datacurationthesis.entity.Person;
import com.datacurationthesis.datacurationthesis.repository.PersonRepository;
import jakarta.persistence.EntityManager;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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

	public Page<Person> getFirstTenPersons() {
		Pageable pageable = PageRequest.of(0, 10);
		Page<Person> persons = personRepository.findAll(pageable);
		return persons;
	}

	public List<PersonDto> getPersonDto() {
		List<Person> persons = personRepository.findAll();
		return  persons.stream().map(this::convertToDto).collect(Collectors.toList());
	}

	private PersonDto convertToDto(Person person) {
		return new PersonDto(person.getId(), person.getFullname());
	}

}
