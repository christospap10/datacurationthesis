package com.datacurationthesis.datacurationthesis.service;

import com.datacurationthesis.datacurationthesis.entity.Person;
import com.datacurationthesis.datacurationthesis.logger.LoggerController;
import com.datacurationthesis.datacurationthesis.repository.PersonRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;


@Service
public class PersonService {

	@Autowired
	private PersonRepository personRepository;

	@Autowired
	private EntityManager entityManager;

	public List<Person> getTenId() {
		try {
			String query = "SELECT \"ID\", \"Birthdate\", \"Fullname\", \"HairColor\", \"Height\", \"EyeColor\", \"Weight\", \"Languages\", \"Description\", \"ClaimingStatus\", \"IsClaimed\", \"Roles\", \"SystemID\", \"timestamp\" FROM persons LIMIT 10";
			Query q = entityManager.createNativeQuery(query, Person.class);
			List<Person> persons = q.getResultList();
			LoggerController.info(persons.toString());
			return persons;
		} catch (Exception e) {
			LoggerController.logException("Exception while getting 10 ids", e);
			String message = e.getMessage();
			LoggerController.error(message);
			return null; 
		}
	}

	public List<Person> getAllPersons() {
		Pageable pageable = PageRequest.of(0, 10);
		return personRepository.findAll(pageable).getContent();
	}
}
