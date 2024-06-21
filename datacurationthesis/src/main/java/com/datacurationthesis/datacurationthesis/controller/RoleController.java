package com.datacurationthesis.datacurationthesis.controller;

import com.datacurationthesis.datacurationthesis.entity.Role;
import com.datacurationthesis.datacurationthesis.logger.LoggerController;
import com.datacurationthesis.datacurationthesis.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = "/api/datacuration/roles")
public class RoleController {

    @Autowired
    private RoleService roleService;

    @GetMapping("/all")
    public List<Role> getAllRoles() {
        try {
            LoggerController.info("Getting all roles");
            return roleService.getAllRoles();
        } catch (Exception e) {
            LoggerController.formattedError("Exception while getting all roles", e.getMessage());
            return null;
        }
    }
}
