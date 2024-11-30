package com.datacurationthesis.datacurationthesis.repository;

import com.datacurationthesis.datacurationthesis.entity.Role;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;



@Repository
public interface RoleRepository extends JpaRepository<Role, Integer> {
	List<Role> findByRole1(String role1);
}
