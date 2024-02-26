package com.giovaniwahl.dscommerce.domain.services;

import com.giovaniwahl.dscommerce.domain.dtos.ProductDTO;
import com.giovaniwahl.dscommerce.domain.dtos.ProductMinDTO;
import com.giovaniwahl.dscommerce.domain.entities.Product;
import com.giovaniwahl.dscommerce.domain.repositories.ProductRepository;
import com.giovaniwahl.dscommerce.domain.services.exceptions.DatabaseException;
import com.giovaniwahl.dscommerce.domain.services.exceptions.ResourceNotFoundException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
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
        Product product = productRepository.findById(id).orElseThrow(
                ()-> new ResourceNotFoundException("Id Not Found."));
        return new ProductDTO(product);
    }
    @Transactional(readOnly = true)
    public Page<ProductMinDTO> findAll(String name, Pageable pageable){
        Page<Product> productList = productRepository.searchByName(name, pageable);
        return productList.map(ProductMinDTO::new);
    }

    @Transactional
    public ProductDTO insert(ProductDTO productDTO){
        Product product = new Product();
        copyDtoToEntity(productDTO,product);
        product = productRepository.save(product);
        return new ProductDTO(product);
    }
    @Transactional
    public ProductDTO update(Long id,ProductDTO productDTO){
        try {Product product = productRepository.getReferenceById(id);
            copyDtoToEntity(productDTO,product);
            product = productRepository.save(product);
            return new ProductDTO(product);
        }
        catch (EntityNotFoundException e){
            throw new ResourceNotFoundException("Resource not found!");
        }

    }
    @Transactional(propagation = Propagation.SUPPORTS)
    public void  delete(Long id){
        if (!productRepository.existsById(id)) {
            throw new ResourceNotFoundException("Resource not found !");
        }
        try {
            productRepository.deleteById(id);
        }
        catch (DataIntegrityViolationException e){
            throw new DatabaseException("Referential integrity failure !");
        }
    }

    private void copyDtoToEntity(ProductDTO productDTO, Product product) {
        product.setName(productDTO.getName());
        product.setDescription(productDTO.getDescription());
        product.setPrice(productDTO.getPrice());
        product.setImgUrl(productDTO.getImgUrl());
    }
}
