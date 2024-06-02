package com.datacurationthesis.datacurationthesis.repository;

import com.datacurationthesis.datacurationthesis.entity.Person;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PersonRepository extends JpaRepository<Person, Long> {
	Page<Person> findAll(Pageable pageable);
	@Query(value = "SELECT * FROM persons OFFSET :offset LIMIT :limit", nativeQuery = true)
    List<Person> findAllWithLimit(@Param("offset") int offset, @Param("limit") int limit);

}
