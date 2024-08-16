package com.devsuperior.dscommerce.controllers.it;


import com.devsuperior.dscommerce.dto.OrderDTO;
import com.devsuperior.dscommerce.entities.*;
import com.devsuperior.dscommerce.services.tests.ProductFactory;
import com.devsuperior.dscommerce.services.tests.UserFactory;
import com.devsuperior.dscommerce.tests.TokenUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class OrderControllerIT {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TokenUtil tokenUtil;

    @Autowired
    private ObjectMapper objectMapper;

    private String clientUserName,clientPassword,adminUserName,adminPassword;
    private String adminToken,clientToken,invalidToken;
    private Long existingOrderId,nonExistingOrderId,otherOrderId;

    private User user;
    private Order order;
    private OrderDTO orderDTO;
    private Product product;
    private OrderItem orderItem;

    @BeforeEach
    void setUp()throws Exception{
        clientUserName = "maria@gmail.com";
        clientPassword = "123456";
        adminUserName = "alex@gmail.com";
        adminPassword = "123456";

        adminToken = tokenUtil.obtainAccessToken(mockMvc,adminUserName,adminPassword);
        clientToken = tokenUtil.obtainAccessToken(mockMvc,clientUserName,clientPassword);
        invalidToken = adminToken +"invalid";

        user = UserFactory.createClientUser();

        order = new Order(null, Instant.now(), OrderStatus.WAITING_PAYMENT,user,null);
        product = ProductFactory.createProduct();
        orderItem = new OrderItem(order,product,2,10.0);
        order.getItems().add(orderItem);

        existingOrderId = 1L;
        nonExistingOrderId = 1000L;
        otherOrderId = 2L;

    }

    @Test
    public void findByIdShouldReturnOrderDtoWhenIdExistsAndAdminLogged() throws Exception {
        ResultActions result =
                mockMvc.perform(get("/orders/{id}", existingOrderId)
                        .header("Authorization", "Bearer " + adminToken)
                        .accept(MediaType.APPLICATION_JSON))
                        .andDo(MockMvcResultHandlers.print());
        result.andExpect(status().isOk());
        result.andExpect(jsonPath("$.id").isNotEmpty());
        result.andExpect(jsonPath("$.moment").isNotEmpty());
        result.andExpect(jsonPath("$.status").isNotEmpty());
        result.andExpect(jsonPath("$.client").exists());
        result.andExpect(jsonPath("$.payment").exists());
        result.andExpect(jsonPath("$.items").exists());
    }
    @Test
    public void findByIdShouldReturnOrderDtoWhenIdExistsAndClientLogged() throws Exception {
        ResultActions result =
                mockMvc.perform(get("/orders/{id}", existingOrderId)
                                .header("Authorization", "Bearer " + clientToken)
                                .accept(MediaType.APPLICATION_JSON))
                        .andDo(MockMvcResultHandlers.print());
        result.andExpect(status().isOk());
        result.andExpect(jsonPath("$.id").isNotEmpty());
        result.andExpect(jsonPath("$.moment").isNotEmpty());
        result.andExpect(jsonPath("$.status").isNotEmpty());
        result.andExpect(jsonPath("$.client").exists());
        result.andExpect(jsonPath("$.payment").exists());
        result.andExpect(jsonPath("$.items").exists());
    }
    @Test
    public void findByIdShouldReturnForbiddenWhenIdExistsAndOtherClientLogged() throws Exception {
        ResultActions result =
                mockMvc.perform(get("/orders/{id}", otherOrderId)
                                .header("Authorization", "Bearer " + clientToken)
                                .accept(MediaType.APPLICATION_JSON))
                        .andDo(MockMvcResultHandlers.print());
       result.andExpect(status().isForbidden());
    }
    @Test
    public void findByIdShouldReturnNotFoundWhenIdDoesNotExistsAndAdminLogged() throws Exception {
        ResultActions result =
                mockMvc.perform(get("/orders/{id}", nonExistingOrderId)
                                .header("Authorization", "Bearer " + adminToken)
                                .accept(MediaType.APPLICATION_JSON))
                        .andDo(MockMvcResultHandlers.print());
        result.andExpect(status().isNotFound());
    }
    @Test
    public void findByIdShouldReturnNotFoundWhenIdDoesNotExistsAndClientLogged() throws Exception {
        ResultActions result =
                mockMvc.perform(get("/orders/{id}", nonExistingOrderId)
                                .header("Authorization", "Bearer " + clientToken)
                                .accept(MediaType.APPLICATION_JSON))
                        .andDo(MockMvcResultHandlers.print());
        result.andExpect(status().isNotFound());
    }
}
