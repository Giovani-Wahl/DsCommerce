package com.devsuperior.dscommerce.services;

import com.devsuperior.dscommerce.dto.OrderDTO;
import com.devsuperior.dscommerce.entities.Order;
import com.devsuperior.dscommerce.entities.Product;
import com.devsuperior.dscommerce.entities.User;
import com.devsuperior.dscommerce.repositories.OrderItemRepository;
import com.devsuperior.dscommerce.repositories.OrderRepository;
import com.devsuperior.dscommerce.repositories.ProductRepository;
import com.devsuperior.dscommerce.services.exceptions.ForbiddenException;
import com.devsuperior.dscommerce.services.exceptions.ResourceNotFoundException;
import com.devsuperior.dscommerce.services.tests.OrderFactory;
import com.devsuperior.dscommerce.services.tests.ProductFactory;
import com.devsuperior.dscommerce.services.tests.UserFactory;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;

@ExtendWith(SpringExtension.class)
public class OrderServiceTests {
    @InjectMocks
    private OrderService orderService;

    @Mock
    private OrderRepository orderRepository;
    @Mock
    private AuthService authService;
    @Mock
    private ProductRepository productRepository;
    @Mock
    private OrderItemRepository orderItemRepository;
    @Mock
    private UserService userService;

    private Long existingOrderId, nonExistingOrderId;
    private Long existingProductId, nonExistingProductId;
    private Order order;
    private OrderDTO dto;
    private User admin,client;
    private Product product;

    @BeforeEach
    void setUp() throws Exception{
        existingOrderId = 1L;
        nonExistingOrderId = 2L;
        existingProductId = 1L;
        nonExistingProductId = 2L;
        admin = UserFactory.createCustomAdminUser(1L,"Jef");
        client = UserFactory.createCustomClientUser(2L,"Bob");
        order = OrderFactory.createOrder(client);
        dto = new OrderDTO(order);
        product = ProductFactory.createProduct();

        Mockito.when(orderRepository.findById(existingOrderId)).thenReturn(Optional.of(order));
        Mockito.when(orderRepository.findById(nonExistingOrderId)).thenReturn(Optional.empty());

        Mockito.when(productRepository.getReferenceById(existingProductId)).thenReturn(product);
        Mockito.when(productRepository.getReferenceById(nonExistingProductId)).thenThrow(EntityNotFoundException.class);

        Mockito.when(orderRepository.save(any())).thenAnswer(invocation -> {
            Order order = invocation.getArgument(0);
            order.setId(existingOrderId);
            return order;
        });


        Mockito.when(orderItemRepository.saveAll(any())).thenReturn(new ArrayList<>(order.getItems()));
    }

    @Test
    public void findByIdShouldReturnOrderDtoWhenIdExistsAndAdminLogged(){
        Mockito.doNothing().when(authService).validateSelfOrAdmin(any());
        OrderDTO result = orderService.findById(existingOrderId);
        Assertions.assertNotNull(result);
        Assertions.assertEquals(result.getId(),existingOrderId);
    }
    @Test
    public void findByIdShouldReturnOrderDtoWhenIdExistsAndSelfClientLogged(){
        Mockito.doNothing().when(authService).validateSelfOrAdmin(any());
        OrderDTO result = orderService.findById(existingOrderId);
        Assertions.assertNotNull(result);
        Assertions.assertEquals(result.getId(),existingOrderId);
    }
    @Test
    public void findByIdShouldThrowsForbiddenExceptionWhenIdExistsAndOtherClientLogged(){
        Mockito.doThrow(ForbiddenException.class).when(authService).validateSelfOrAdmin(any());
        Assertions.assertThrows(ForbiddenException.class,()->{
            OrderDTO result = orderService.findById(existingOrderId);
        });
    }
    @Test
    public void findByIdShouldThrowsResourceNotFoundExceptionWhenIdDoesNotExists(){
        Mockito.doNothing().when(authService).validateSelfOrAdmin(any());
        Assertions.assertThrows(ResourceNotFoundException.class,()->{
            OrderDTO result = orderService.findById(nonExistingOrderId);
        });
    }
    @Test
    public void insertShouldReturnOrderDtoWhenAdminLogged(){
        Mockito.when(userService.authenticated()).thenReturn(admin);
        OrderDTO result = orderService.insert(dto);
        Assertions.assertNotNull(result);
        Assertions.assertEquals(result.getId(),existingOrderId);
    }
    @Test
    public void insertShouldReturnOrderDtoWhenClientLogged(){
        Mockito.when(userService.authenticated()).thenReturn(client);
        OrderDTO result = orderService.insert(dto);
        Assertions.assertNotNull(result);
        Assertions.assertEquals(result.getId(),existingOrderId);
    }
    @Test
    public void insertShouldThrowsUsernameNotFoundExceptionWhenUserNotLogged(){
        Mockito.doThrow(UsernameNotFoundException.class).when(userService).authenticated();
        order.setClient(new User());
        dto = new OrderDTO(order);
        Assertions.assertThrows(UsernameNotFoundException.class,()->{
            orderService.insert(dto);
        });
    }
}
