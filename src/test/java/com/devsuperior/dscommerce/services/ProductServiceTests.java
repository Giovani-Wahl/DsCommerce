package com.devsuperior.dscommerce.services;

import com.devsuperior.dscommerce.dto.ProductDTO;
import com.devsuperior.dscommerce.dto.ProductMinDTO;
import com.devsuperior.dscommerce.entities.Product;
import com.devsuperior.dscommerce.repositories.ProductRepository;
import com.devsuperior.dscommerce.services.exceptions.ResourceNotFoundException;
import com.devsuperior.dscommerce.services.tests.ProductFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;

@ExtendWith(SpringExtension.class)
public class ProductServiceTests {
    @InjectMocks
    private ProductService productService;
    @Mock
    private ProductRepository productRepository;

    private Product product;
    private Long existingId,nonExistingId;
    private String productName;
    private PageImpl<Product> page;

    @BeforeEach
    void setUp() throws Exception{
        existingId = 1L;
        nonExistingId = 2L;
        productName = "PlayStation 5";
        product = ProductFactory.createProduct(productName);
        page = new PageImpl<>(List.of(product));

        Mockito.when(productRepository.findById(existingId)).thenReturn(Optional.of(product));
        Mockito.when(productRepository.findById(nonExistingId)).thenReturn(Optional.empty());

        Mockito.when(productRepository.searchByName(any(),(Pageable)any())).thenReturn(page);
    }

    @Test
    public void findByIdShouldReturnProductDtoWhenIdExists(){
        ProductDTO result = productService.findById(existingId);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(result.getId(),existingId);
        Assertions.assertEquals(result.getName(),product.getName());
    }
    @Test
    public void findByIdShouldReturnResourceNotFoundExceptionWhenIdDoesNotExists(){

        Assertions.assertThrows(ResourceNotFoundException.class,()->{
            productService.findById(nonExistingId);
        });
    }
    @Test
    public void findAllShouldReturnPagedProductMinDto(){
        Pageable pageable = PageRequest.of(0,12);
        String name = "PlayStation 5";
        Page<ProductMinDTO> result = productService.findAll(name,pageable);
        Assertions.assertNotNull(result);
        Assertions.assertEquals(result.getSize(),1);
        Assertions.assertEquals(result.iterator().next().getName(),productName);
    }
}
