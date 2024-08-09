package com.datacurationthesis.datacurationthesis.repository;

import com.datacurationthesis.datacurationthesis.entity.Organizer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrganizerRepository extends JpaRepository<Organizer, Integer> {
    // Custom query to find organizers by name and address
    @Query("SELECT o FROM Organizer o WHERE o.name = :name AND o.address = :address")
    List<Organizer> findByNameAndAddress(String name, String address);
}
