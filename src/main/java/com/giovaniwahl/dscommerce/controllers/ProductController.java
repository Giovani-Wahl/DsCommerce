package com.giovaniwahl.dscommerce.controllers;

import com.giovaniwahl.dscommerce.domain.dtos.ProductDTO;
import com.giovaniwahl.dscommerce.domain.services.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/products")
public class ProductController {
   private final ProductService productService;
   @Autowired
    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping("/{id}")
    public ProductDTO findById(@PathVariable Long id){
        return productService.findById(id);
    }
    @GetMapping
    public Page<ProductDTO> findAll(@PageableDefault(page = 0, size = 10, sort = "id", direction = Sort.Direction.ASC)
                                        Pageable pageable){
       return productService.findAll(pageable);
    }
    @PostMapping
    public ProductDTO insert(@RequestBody ProductDTO productDTO){
       productDTO = productService.insert(productDTO);
       return productDTO;
    }
}
