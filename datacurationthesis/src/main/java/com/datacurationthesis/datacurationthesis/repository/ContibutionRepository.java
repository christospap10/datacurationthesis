package com.datacurationthesis.datacurationthesis.repository;

import com.datacurationthesis.datacurationthesis.entity.Contribution;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ContibutionRepository extends JpaRepository<Contribution, Integer> {
}