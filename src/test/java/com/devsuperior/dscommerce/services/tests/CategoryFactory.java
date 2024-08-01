package com.devsuperior.dscommerce.services.tests;

import com.devsuperior.dscommerce.entities.Category;

public class CategoryFactory {
    public static Category createdCategory(){
        return new Category(1L,"Games");
    }
    public static Category createdCategory(Long id,String name){
        return new Category(id,name);
    }
}
