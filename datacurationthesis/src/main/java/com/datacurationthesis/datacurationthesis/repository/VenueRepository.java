package com.datacurationthesis.datacurationthesis.repository;

import com.datacurationthesis.datacurationthesis.entity.Organizer;
import com.datacurationthesis.datacurationthesis.entity.Venue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VenueRepository extends JpaRepository<Venue, Integer> {

    @Query("SELECT v FROM Venue v WHERE v.title = :title ORDER BY LENGTH(v.address) DESC")
    List<Venue> findByTitle(@Param("title") String title);
}
