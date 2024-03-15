package com.giovaniwahl.dscommerce.domain.services;

import com.giovaniwahl.dscommerce.domain.dtos.OrderDTO;
import com.giovaniwahl.dscommerce.domain.dtos.OrderItemDTO;
import com.giovaniwahl.dscommerce.domain.entities.*;
import com.giovaniwahl.dscommerce.domain.repositories.OrderItemRepository;
import com.giovaniwahl.dscommerce.domain.repositories.OrderRepository;
import com.giovaniwahl.dscommerce.domain.repositories.ProductRepository;
import com.giovaniwahl.dscommerce.domain.services.exceptions.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
public class OrderService {
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private OrderItemRepository orderItemRepository;
    @Autowired
    private UserService userService;
    @Autowired
    private AuthService authService;


    @Transactional(readOnly = true)
    public OrderDTO findById(Long id) {
        Order order = orderRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Id Not Found."));
        authService.validateSelfOrAdmin(order.getClient().getId());
        return new OrderDTO(order);
    }
    @Transactional
    public OrderDTO insert(OrderDTO dto) {
        Order order = new Order();
        order.setMoment(Instant.now());
        order.setStatus(OrderStatus.WAITING_PAYMENT);
        User user = userService.authenticated();
        order.setClient(user);

        for (OrderItemDTO itemDto : dto.getItems()) {
            Product product = productRepository.getReferenceById(itemDto.getProductId());
            OrderItem item = new OrderItem(order, product, itemDto.getQuantity(), product.getPrice());
            order.getItems().add(item);
        }

        orderRepository.save(order);
        orderItemRepository.saveAll(order.getItems());

        return new OrderDTO(order);
    }
}
