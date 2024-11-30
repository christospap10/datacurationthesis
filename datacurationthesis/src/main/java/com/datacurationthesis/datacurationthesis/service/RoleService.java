package com.datacurationthesis.datacurationthesis.service;

import com.datacurationthesis.datacurationthesis.entity.Role;
import com.datacurationthesis.datacurationthesis.logger.LoggerController;
import com.datacurationthesis.datacurationthesis.repository.ContibutionRepository;
import com.datacurationthesis.datacurationthesis.repository.RoleRepository;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RoleService {

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private ContibutionRepository contributionRepository;

    public List<Role> getAllRoles() {
        try {
            return roleRepository.findAll();
        } catch (Exception e) {
            LoggerController.formattedError("Exception while getting all roles", e.getMessage());
            return null;
        }
    }

    /**
     * Merges and deletes inactive roles (those without active relations).
     *
     * @param duplicates  the list of duplicate roles.
     * @param primaryRole the primary role to merge into, or null if no merge is
     *                    needed.
     */
    private void mergeInacticeRoles(List<Role> duplicates, Role primaryRole) {
        for (Role duplicate : duplicates) {
            if (!hasActiveRelations(duplicate)) {
                if (primaryRole != null) {
                    primaryRole = mergeRoleFields(primaryRole, duplicate);
                    LoggerController.formattedInfo("Merged inactive role with ID: %d into primary role",
                            duplicate.getId());
                }
                // Delete the inactive duplicate
                roleRepository.delete(duplicate);
                LoggerController.formattedInfo("Deleted inactive role with ID: %d", duplicate.getId());
            }
        }
    }

    /**
     * Merges duplicate roles based on the role field.
     *
     * @param role the role object to be checked and merged with any duplicates.
     * @return the merged role.
     */
    public Role mergeDuplicates(Role role) {
        // Find all roles with the same 'role1' value (case-sensitive comparison)
        List<Role> duplicates = roleRepository.findByRole1(role.getRole1());
        LoggerController.formattedInfo("Found %d duplicates for role: %s", duplicates.size(), role.getRole1());

        if (duplicates.size() > 1) {
            // Separate roles that have active relations (e.g., Contributions)
            List<Role> activeRoles = duplicates.stream().filter(this::hasActiveRelations).collect(Collectors.toList());
            LoggerController.formattedInfo("Active roles count: %d for role: %s", activeRoles.size(), role.getRole1());
            // If there's only one active role, it becomes the primary role
            if (activeRoles.size() == 1) {
                Role primaryRole = activeRoles.get(0);
                mergeInacticeRoles(duplicates, primaryRole);
                return roleRepository.save(primaryRole);
            } else if (activeRoles.size() > 1) {
                // If multiple roles have active relations, keep them and remove inactive roles
                for (Role activeRole : activeRoles) {
                    LoggerController.formattedInfo("Keeping active role with ID: %d", activeRole.getId());

                }
                // Remove inactive roles without active relations
                mergeInacticeRoles(duplicates, null); // We don't merge into any primary role in this case
                return activeRoles.get(0); // return any active role
            }
        }
        LoggerController.formattedInfo("No duplicates found for role: %s", role.getRole1());
        return role; // No duplicates or all are inactive, return the original role
    }

    /**
     * Merges fields of two roles, favoring the primary role.
     *
     * @param primary   the primary role that will be retained.
     * @param duplicate the duplicate role that will be merged into the primary
     *                  role.
     * @return the merged primary role.
     */
    private Role mergeRoleFields(Role primary, Role duplicate) {
        if (primary.getContributions() == null && duplicate.getContributions() != null) {
            primary.setContributions(duplicate.getContributions());
        }
        return primary;
    }

    /**
     * Checks if the given role has any active relations (e.g., with contributions).
     *
     * @param role the role to check.
     * @return true if the role has active relations, false otherwise.
     */
    private boolean hasActiveRelations(Role role) {
        long contributionCount = contributionRepository.countByRole(role);
        return contributionCount > 0;
    }
}
