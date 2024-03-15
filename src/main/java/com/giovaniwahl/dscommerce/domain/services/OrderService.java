package com.giovaniwahl.dscommerce.domain.services;

import com.giovaniwahl.dscommerce.domain.dtos.OrderDTO;
import com.giovaniwahl.dscommerce.domain.entities.Order;
import com.giovaniwahl.dscommerce.domain.repositories.OrderRepository;
import com.giovaniwahl.dscommerce.domain.services.exceptions.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrderService {
    @Autowired
    private OrderRepository orderRepository;


    @Transactional(readOnly = true)
    public OrderDTO findById(Long id) {
        Order order = orderRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Id Not Found."));
        return new OrderDTO(order);
    }
}
