package com.devsuperior.dscommerce.services;

import com.devsuperior.dscommerce.dto.UserDTO;
import com.devsuperior.dscommerce.entities.User;
import com.devsuperior.dscommerce.projections.UserDetailsProjection;
import com.devsuperior.dscommerce.repositories.UserRepository;
import com.devsuperior.dscommerce.services.tests.UserDetailsFactory;
import com.devsuperior.dscommerce.services.tests.UserFactory;
import com.devsuperior.dscommerce.utils.CustomUserUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@ExtendWith(SpringExtension.class)
public class UserServiceTests {
    @InjectMocks
    private UserService userService;
    @Mock
    private UserRepository userRepository;
    @Mock
    private CustomUserUtil customUserUtil;

    private String existingUserNane,nonExistingUserNane;
    private User user;
    private List<UserDetailsProjection> userDetails;

    @BeforeEach
    void setUp() throws Exception{
        existingUserNane = "maria@gmail.com";
        nonExistingUserNane = "user@gmail.com";

        user = UserFactory.createCustomClientUser(1L,existingUserNane);
        userDetails = UserDetailsFactory.createCustomAdminUser(existingUserNane);

        Mockito.when(userRepository.searchUserAndRolesByEmail(existingUserNane)).thenReturn(userDetails);
        Mockito.when(userRepository.searchUserAndRolesByEmail(nonExistingUserNane)).thenReturn(new ArrayList<>());

        Mockito.when(userRepository.findByEmail(existingUserNane)).thenReturn(Optional.of(user));
        Mockito.when(userRepository.findByEmail(nonExistingUserNane)).thenReturn(Optional.empty());
    }

    @Test
    public void loadUserByUserNameShouldReturnUserDetailsWhenUserExists(){
        UserDetails result = userService.loadUserByUsername(existingUserNane);
        Assertions.assertNotNull(result);
        Assertions.assertEquals(result.getUsername(),existingUserNane);
    }
    @Test
    public void loadUserByUserNameShouldThrowUserNotFoundExceptionWhenUserDoesNotExists(){
        Assertions.assertThrows(UsernameNotFoundException.class,()->{
            userService.loadUserByUsername(nonExistingUserNane);
        });
    }
    @Test
    public void authenticatedShouldReturnUserWhenUserExists(){
        Mockito.when(customUserUtil.getProfile()).thenReturn(existingUserNane);
        User result = userService.authenticated();
        Assertions.assertNotNull(result);
        Assertions.assertEquals(result.getUsername(),existingUserNane);
    }
    @Test
    public void authenticatedShouldThrowUsernameNotFoundExceptionWhenUserDoesNotExists(){
        Mockito.doThrow(ClassCastException.class).when(customUserUtil).getProfile();
        Assertions.assertThrows(UsernameNotFoundException.class,()->{
            userService.authenticated();
        });
    }
    @Test
    public void getMeShouldReturnUserDtoWhenUserAuthenticated(){
        UserService spyUserService = Mockito.spy(userService);
        Mockito.doReturn(user).when(spyUserService).authenticated();

        UserDTO result = spyUserService.getMe();

        Assertions.assertNotNull(result);
        Assertions.assertEquals(result.getEmail(),existingUserNane);
    }
    @Test
    public void getMeShouldThrowUsernameNotFoundExceptionWhenUserDoesNotAuthenticated(){
        UserService spyUserService = Mockito.spy(userService);
        Mockito.doThrow(UsernameNotFoundException.class).when(spyUserService).authenticated();

        Assertions.assertThrows(UsernameNotFoundException.class,()->{
            UserDTO result = spyUserService.getMe();
        });
    }
}
