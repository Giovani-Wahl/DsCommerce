package com.giovaniwahl.dscommerce.domain.services;

import com.giovaniwahl.dscommerce.domain.entities.User;
import com.giovaniwahl.dscommerce.domain.services.exceptions.ForbidenException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    @Autowired
    private UserService userService;

    public void validateSelfOrAdmin(long userId){
        User userLogged = userService.authenticated();
        if (!userLogged.hasRole("ROLE_ADMIN") && !userLogged.getId().equals(userId)){
            throw new ForbidenException("Access Denied");
        }
    }
}
