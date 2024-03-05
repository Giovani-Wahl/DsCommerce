package com.giovaniwahl.dscommerce.domain.repositories;

import com.giovaniwahl.dscommerce.domain.entities.OrderItem;
import com.giovaniwahl.dscommerce.domain.entities.OrderItemPK;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderItemRepository extends JpaRepository<OrderItem, OrderItemPK> {
}
