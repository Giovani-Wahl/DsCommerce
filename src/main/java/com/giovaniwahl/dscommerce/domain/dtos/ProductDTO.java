package com.giovaniwahl.dscommerce.domain.dtos;

import com.giovaniwahl.dscommerce.domain.entities.Category;
import com.giovaniwahl.dscommerce.domain.entities.Product;
import jakarta.validation.constraints.*;

import java.util.ArrayList;
import java.util.List;

public class ProductDTO {
    private Long id;
    @Size(min = 3,max = 80, message = "Nome entre 3 e 8 caracteres.")
    @NotBlank(message = "Campo Obrigatório.")
    private String name;
    @Size(min = 10,message = "minimo 10 caracteres.")
    @NotBlank
    private String description;
    @NotNull(message = "Required field.")
    @Positive(message = "o Valor deve ser positivo.")
    private Double price;
    private String imgUrl;
    @NotEmpty(message = "Must contain one or more categories.")
    private List<CategoryDTO> categories = new ArrayList<>();

    public ProductDTO(){
    }

    public ProductDTO(Long id, String name, String description, Double price, String imgUrl) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.imgUrl = imgUrl;
    }
    public ProductDTO(Product entity) {
        id = entity.getId();
        name = entity.getName();
        description = entity.getDescription();
        price = entity.getPrice();
        imgUrl = entity.getImgUrl();
        for (Category cat : entity.getCategories()){
            categories.add(new CategoryDTO(cat));
        }
    }

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }

    public Double getPrice() {
        return price;
    }
    public void setPrice(Double price) {
        this.price = price;
    }

    public String getImgUrl() {
        return imgUrl;
    }
    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public List<CategoryDTO> getCategories() {
        return categories;
    }
}
