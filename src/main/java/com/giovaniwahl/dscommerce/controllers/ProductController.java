package com.giovaniwahl.dscommerce.controllers;

import com.giovaniwahl.dscommerce.domain.entities.Product;
import com.giovaniwahl.dscommerce.domain.repositories.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/products")
public class ProductController {
    private final ProductRepository productRepository;
    @Autowired
    public ProductController(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @GetMapping
    public String teste(){
        Optional<Product> result = productRepository.findById(1L);
        Product product = result.get();
        return product.getName();
    }
}
