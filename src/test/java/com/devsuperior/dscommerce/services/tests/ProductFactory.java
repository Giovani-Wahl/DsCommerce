package com.devsuperior.dscommerce.services.tests;

import com.devsuperior.dscommerce.entities.Category;
import com.devsuperior.dscommerce.entities.Product;

public class ProductFactory {
    public static Product createProduct(){
        Category category = CategoryFactory.createCategory();
        Product product = new Product(1L,"PS5-PRO","Descrição do item",3500.00,
                "url do produto-endereço da imagem do produto");
        product.getCategories().add(category);
        return product;
    }
    public static Product createProduct(String name){
        Product product = createProduct();
        product.setName(name);
        return product;
    }
}
