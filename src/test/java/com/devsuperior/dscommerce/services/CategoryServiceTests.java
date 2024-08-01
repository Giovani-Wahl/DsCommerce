package com.devsuperior.dscommerce.services;

import com.devsuperior.dscommerce.dto.CategoryDTO;
import com.devsuperior.dscommerce.entities.Category;
import com.devsuperior.dscommerce.repositories.CategoryRepository;
import com.devsuperior.dscommerce.services.tests.CategoryFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.List;

@ExtendWith(SpringExtension.class)
public class CategoryServiceTests {
    @InjectMocks
    private CategoryService categoryService;
    @Mock
    private CategoryRepository categoryRepository;

    private Category category;
    private List<Category> categoryList;

    @BeforeEach
    void setUp()throws Exception{
        category = CategoryFactory.createCategory();
        categoryList = new ArrayList<>();
        categoryList.add(category);

        Mockito.when(categoryRepository.findAll()).thenReturn(categoryList);
    }

    @Test
    public void findAllShouldReturnListCategoryDto(){
        List<CategoryDTO> dtoList = categoryService.findAll();
        Assertions.assertEquals(dtoList.size(),1);
        Assertions.assertEquals(dtoList.get(0).getId(),category.getId());
        Assertions.assertEquals(dtoList.get(0).getName(),category.getName());
    }
}
