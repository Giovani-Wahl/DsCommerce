package com.giovaniwahl.dscommerce.controllers;

import com.giovaniwahl.dscommerce.domain.dtos.UserDTO;
import com.giovaniwahl.dscommerce.domain.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService service;
    @Autowired
    public UserController(UserService service) {
        this.service = service;
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_CLIENT')")
    @GetMapping("/userlog")
    public ResponseEntity<UserDTO> getUserLogged(){
        return ResponseEntity.status(HttpStatus.OK).body(service.getUserLogged());
    }
}
