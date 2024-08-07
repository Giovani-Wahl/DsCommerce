package com.devsuperior.dscommerce.services;

import com.devsuperior.dscommerce.entities.User;
import com.devsuperior.dscommerce.services.exceptions.ForbiddenException;
import com.devsuperior.dscommerce.services.tests.UserFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
public class AuthServiceTests {
    @InjectMocks
    private AuthService authService;
    @Mock
    private UserService userService;

    private User admin,selClient,otherClient;

    @BeforeEach
    void setUp()throws Exception{
        admin = UserFactory.createAdminUser();
        selClient = UserFactory.createCustomClientUser(1L,"Bob");
        otherClient = UserFactory.createCustomClientUser(2L,"Ana");
    }

    @Test
    public void validateSelfOrAdminShouldDoNotingWhenAdminLogged(){
        Mockito.when(userService.authenticated()).thenReturn(admin);

        Assertions.assertDoesNotThrow(()->{authService.validateSelfOrAdmin(admin.getId());});
    }
    @Test
    public void validateSelfOrAdminShouldDoNotingWhenSelfLogged(){
        Mockito.when(userService.authenticated()).thenReturn(selClient);

        Assertions.assertDoesNotThrow(()->{authService.validateSelfOrAdmin(selClient.getId());});
    }
    @Test
    public void validateSelfOrAdminThrowsForbiddenExceptionWhenOtherClientLogged(){
        Mockito.when(userService.authenticated()).thenReturn(selClient);

        Assertions.assertThrows(ForbiddenException.class,()->{
            authService.validateSelfOrAdmin(otherClient.getId());
        });
    }
}
