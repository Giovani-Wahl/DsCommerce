package com.giovaniwahl.dscommerce.domain.repositories;

import com.giovaniwahl.dscommerce.domain.entities.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product,Long> {
    @Query("SELECT obj FROM Product obj " +
            "WHERE UPPER(obj.name) " +
            "LIKE UPPER (CONCAT('%',:name,'%'))")
    Page<Product> searchByName(String name, Pageable pageable);
}
