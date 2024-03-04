package com.giovaniwahl.dscommerce.domain.repositories;

import com.giovaniwahl.dscommerce.domain.entities.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {
}
