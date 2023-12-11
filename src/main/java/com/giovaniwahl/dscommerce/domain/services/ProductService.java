package com.giovaniwahl.dscommerce.domain.services;

import com.giovaniwahl.dscommerce.domain.dtos.ProductDTO;
import com.giovaniwahl.dscommerce.domain.entities.Product;
import com.giovaniwahl.dscommerce.domain.repositories.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class ProductService {
    private final ProductRepository productRepository;
    @Autowired
    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Transactional(readOnly = true)
    public ProductDTO findById(Long id){
        Optional<Product> result = productRepository.findById(id);
        if (result.isPresent()){
            Product product = result.get();
            return new ProductDTO(product);
        }
        return null;
    }
    @Transactional(readOnly = true)
    public List<ProductDTO> findAll(){
        List<Product> productList = productRepository.findAll();
        return productList.stream().map(ProductDTO::new).toList();
    }
}
