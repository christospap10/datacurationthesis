package com.datacurationthesis.datacurationthesis.service;

import com.datacurationthesis.datacurationthesis.entity.Role;
import com.datacurationthesis.datacurationthesis.repository.RoleRepository;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;



@Service
public class RoleCurationService {
@Autowired
    private RoleRepository roleRepository;

    /**
     * Συγχωνεύει διπλές εγγραφές στον πίνακα Roles.
     */
    public void mergeDuplicateRoles() {
        List<Role> allRoles = roleRepository.findAll();

        Map<String, List<Role>> rolesByName = allRoles.stream()
                .collect(Collectors.groupingBy(role -> role.getRole1().toLowerCase()));

        rolesByName.forEach((roleName, roles) -> {
            if (roles.size() > 1) {
                Role primaryRole = roles.stream()
                        .max(Comparator.comparingInt(this::getActiveRelationsCount))
                        .orElse(roles.get(0));

                System.out.printf("Πρωτεύων ρόλος για '%s': %s%n", roleName, primaryRole.getId());

                roles.stream()
                        .filter(role -> role != primaryRole)
                        .forEach(duplicateRole -> {
                            mergeRelations(primaryRole, duplicateRole);
                            roleRepository.delete(duplicateRole);
                            System.out.printf("Διαγραφή διπλού ρόλου: %s%n", duplicateRole.getId());
                        });

                roleRepository.save(primaryRole);
            }
        });
    }

    /**
     * Υπολογίζει τον αριθμό ενεργών σχέσεων ενός ρόλου.
     */
    private int getActiveRelationsCount(Role role) {
        return role.getContributions().size();
    }

    /**
     * Συγχωνεύει τις σχέσεις ενός διπλού ρόλου στον πρωτεύοντα ρόλο.
     */
    private void mergeRelations(Role primaryRole, Role duplicateRole) {
        duplicateRole.getContributions().forEach(contribution -> {
            contribution.setRole(primaryRole);
        });
        if (duplicateRole.getContributions() != null) {
            if (primaryRole.getContributions() == null) {
                primaryRole.setContributions(new ArrayList<>());
            }
            primaryRole.getContributions().addAll(duplicateRole.getContributions());
        }
    }
}
