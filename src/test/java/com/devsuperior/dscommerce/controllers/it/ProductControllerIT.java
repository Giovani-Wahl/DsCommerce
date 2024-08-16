package com.devsuperior.dscommerce.controllers.it;


import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.devsuperior.dscommerce.dto.ProductDTO;
import com.devsuperior.dscommerce.entities.Category;
import com.devsuperior.dscommerce.entities.Product;
import com.devsuperior.dscommerce.tests.TokenUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class ProductControllerIT {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TokenUtil tokenUtil;

    @Autowired
    private ObjectMapper objectMapper;

    private String clientUserName,clientPassword,adminUserName,adminPassword;
    private String adminToken,clientToken,invalidToken;
    private Long existingProductId,nonExistingProductId,dependentProductId;
    private String productName;
    private Product product;
    private ProductDTO productDTO;

    @BeforeEach
    void setUp()throws Exception{
        clientUserName = "maria@gmail.com";
        clientPassword = "123456";
        adminUserName = "alex@gmail.com";
        adminPassword = "123456";

        adminToken = tokenUtil.obtainAccessToken(mockMvc,adminUserName,adminPassword);
        clientToken = tokenUtil.obtainAccessToken(mockMvc,clientUserName,clientPassword);
        invalidToken = adminToken +"invalid";

        existingProductId = 2L;
        nonExistingProductId = 100L;
        dependentProductId = 3L;

        Category category = new Category(2L,"Categoria Teste");
        product = new Product(null,"PS5 Pro","descrição do item cadastrado",3999.90,
                "uri da imagem do produto");
        product.getCategories().add(category);

        productDTO =new ProductDTO(product);

        productName = "Macbook";
    }

    @Test
    public void findAllShouldReturnPageWhenNameParamIsNotEmpty() throws Exception{
        ResultActions result =
                mockMvc.perform(get("/products?name={productName}",productName)
                        .accept(MediaType.APPLICATION_JSON));
        result.andExpect(status().isOk());
        result.andExpect(jsonPath("$.content[0].id").isNotEmpty());
        result.andExpect(jsonPath("$.content[0].id").value(3L));
        result.andExpect(jsonPath("$.content[0].name").isNotEmpty());
        result.andExpect(jsonPath("$.content[0].name").value("Macbook Pro"));
        result.andExpect(jsonPath("$.content[0].price").isNotEmpty());
        result.andExpect(jsonPath("$.content[0].imgUrl").isNotEmpty());
    }
    @Test
    public void findAllShouldReturnPageWhenNameParamIsEmpty()throws Exception{
        ResultActions result =
                mockMvc.perform(get("/products")
                        .accept(MediaType.APPLICATION_JSON));
        result.andExpect(status().isOk());
        result.andExpect(jsonPath("$.content[0].id").isNotEmpty());
        result.andExpect(jsonPath("$.content[0].id").value(1L));
        result.andExpect(jsonPath("$.content[0].name").isNotEmpty());
        result.andExpect(jsonPath("$.content[0].price").isNotEmpty());
        result.andExpect(jsonPath("$.content[0].imgUrl").isNotEmpty());
    }
    @Test
    public void findByIdShouldReturnProductDTOWhenIdExists() throws Exception {
        ResultActions result =
                mockMvc.perform(get("/products/{id}", existingProductId)
                        .accept(MediaType.APPLICATION_JSON));
        result.andExpect(status().isOk());
        result.andExpect(jsonPath("$.id").isNotEmpty());
        result.andExpect(jsonPath("$.name").isNotEmpty());
        result.andExpect(jsonPath("$.description").exists());
        result.andExpect(jsonPath("$.price").exists());
        result.andExpect(jsonPath("$.categories").exists());
    }
    @Test
    public void findByIdShouldReturnNotFoundWhenIdDoesNotExist() throws Exception {
        ResultActions result =
                mockMvc.perform(get("/products/{id}", nonExistingProductId)
                        .accept(MediaType.APPLICATION_JSON));
        result.andExpect(status().isNotFound());
    }
    @Test
    public void insertShouldReturnProductDtoWhenAdminLogged() throws Exception{
        String jsonBody = objectMapper.writeValueAsString(productDTO);
        ResultActions result =
                mockMvc.perform(post("/products")
                        .header("Authorization", "Bearer " + adminToken)
                        .content(jsonBody)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                        .andDo(MockMvcResultHandlers.print());
        result.andExpect(status().isCreated());
        result.andExpect(jsonPath("$.id").isNotEmpty());
        result.andExpect(jsonPath("$.name").value("PS5 Pro"));
        result.andExpect(jsonPath("$.description").value("descrição do item cadastrado"));
        result.andExpect(jsonPath("$.price").value(3999.90));
        result.andExpect(jsonPath("$.imgUrl").value("uri da imagem do produto"));
        result.andExpect(jsonPath("$.categories").isNotEmpty());
        result.andExpect(jsonPath("$.categories[0].id").value(2));
    }
    @Test
    public void insertShouldReturnUnprocessableEntityWhenAdminLoggedAndInvalidName() throws Exception{
        product.setName("ab");
        productDTO = new ProductDTO(product);

        String jsonBody = objectMapper.writeValueAsString(productDTO);
        ResultActions result =
                mockMvc.perform(post("/products")
                                .header("Authorization", "Bearer " + adminToken)
                                .content(jsonBody)
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON));
        result.andExpect(status().isUnprocessableEntity());
    }
    @Test
    public void insertShouldReturnUnprocessableEntityWhenAdminLoggedAndInvalidDescription() throws Exception{
        product.setDescription("ab");
        productDTO = new ProductDTO(product);

        String jsonBody = objectMapper.writeValueAsString(productDTO);
        ResultActions result =
                mockMvc.perform(post("/products")
                        .header("Authorization", "Bearer " + adminToken)
                        .content(jsonBody)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON));
        result.andExpect(status().isUnprocessableEntity());
    }
    @Test
    public void insertShouldReturnUnprocessableEntityWhenAdminLoggedAndPriceNegative() throws Exception{
        product.setPrice(-1.00);
        productDTO = new ProductDTO(product);

        String jsonBody = objectMapper.writeValueAsString(productDTO);
        ResultActions result =
                mockMvc.perform(post("/products")
                        .header("Authorization", "Bearer " + adminToken)
                        .content(jsonBody)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON));
        result.andExpect(status().isUnprocessableEntity());
    }
    @Test
    public void insertShouldReturnUnprocessableEntityWhenAdminLoggedAndPriceZero() throws Exception{
        product.setPrice(0.00);
        productDTO = new ProductDTO(product);

        String jsonBody = objectMapper.writeValueAsString(productDTO);
        ResultActions result =
                mockMvc.perform(post("/products")
                        .header("Authorization", "Bearer " + adminToken)
                        .content(jsonBody)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON));
        result.andExpect(status().isUnprocessableEntity());
    }
    @Test
    public void insertShouldReturnUnprocessableEntityWhenAdminLoggedAndHasNotCategory() throws Exception{
        product.getCategories().clear();
        productDTO = new ProductDTO(product);

        String jsonBody = objectMapper.writeValueAsString(productDTO);
        ResultActions result =
                mockMvc.perform(post("/products")
                        .header("Authorization", "Bearer " + adminToken)
                        .content(jsonBody)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON));
        result.andExpect(status().isUnprocessableEntity());
    }
    @Test
    public void insertShouldReturnForbiddenWhenClientLogged() throws Exception{
        String jsonBody = objectMapper.writeValueAsString(productDTO);
        ResultActions result =
                mockMvc.perform(post("/products")
                        .header("Authorization", "Bearer " + clientToken)
                        .content(jsonBody)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON));
        result.andExpect(status().isForbidden());
    }
    @Test
    public void insertShouldReturnUnauthorizedWhenInvalidToken() throws Exception{
        String jsonBody = objectMapper.writeValueAsString(productDTO);
        ResultActions result =
                mockMvc.perform(post("/products")
                        .header("Authorization", "Bearer " + invalidToken)
                        .content(jsonBody)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON));
        result.andExpect(status().isUnauthorized());
    }
    @Test
    public void updateShouldReturnProductDTOWhenIdExistsAndAdminLogged() throws Exception {
        String jsonBody = objectMapper.writeValueAsString(productDTO);
        ResultActions result =
                mockMvc.perform(put("/products/{id}", existingProductId)
                        .header("Authorization", "Bearer " + adminToken)
                        .content(jsonBody)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON));
        result.andExpect(status().isOk());
        result.andExpect(jsonPath("$.id").isNotEmpty());
        result.andExpect(jsonPath("$.name").isNotEmpty());
        result.andExpect(jsonPath("$.description").exists());
        result.andExpect(jsonPath("$.price").exists());
        result.andExpect(jsonPath("$.categories").exists());
        result.andExpect(jsonPath("$.imgUrl").exists());
        result.andExpect(jsonPath("$.categories[0].id").isNotEmpty());
    }
    @Test
    public void updateShouldReturnNotFoundWhenIdDoesNotExistAndAdminLogged() throws Exception {
        String jsonBody = objectMapper.writeValueAsString(productDTO);
        ResultActions result =
                mockMvc.perform(put("/products/{id}", nonExistingProductId)
                        .header("Authorization", "Bearer " + adminToken)
                        .content(jsonBody)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON));
        result.andExpect(status().isNotFound());
    }
    @Test
    public void updateShouldReturnUnprocessableEntityWhenIdExistsAndAdminLoggedAndInvalidName() throws Exception {
        product.setName("ab");
        productDTO = new ProductDTO(product);

        String jsonBody = objectMapper.writeValueAsString(productDTO);
        ResultActions result =
                mockMvc.perform(put("/products/{id}", existingProductId)
                        .header("Authorization", "Bearer " + adminToken)
                        .content(jsonBody)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON));
        result.andExpect(status().isUnprocessableEntity());
    }



    @Test
    public void deleteShouldReturnNoContentWhenIdExistsAndAdminLogged() throws Exception{
        ResultActions result =
                mockMvc.perform(delete("/products/{id}",existingProductId)
                        .header("Authorization", "Bearer " + adminToken)
                        .accept(MediaType.APPLICATION_JSON));
        result.andExpect(status().isNoContent());
    }
    @Test
    public void deleteShouldReturnNotFoundWhenIdDoesNotExistsAndAdminLogged() throws Exception{
        ResultActions result =
                mockMvc.perform(delete("/products/{id}", nonExistingProductId)
                        .header("Authorization", "Bearer " + adminToken)
                        .accept(MediaType.APPLICATION_JSON));
        result.andExpect(status().isNotFound());
    }
    @Test
    @Transactional(propagation = Propagation.SUPPORTS)
    public void deleteShouldReturnBabRequestWhenIdDependentAndAdminLogged() throws Exception{
        ResultActions result =
                mockMvc.perform(delete("/products/{id}", dependentProductId)
                        .header("Authorization", "Bearer " + adminToken)
                        .accept(MediaType.APPLICATION_JSON));
        result.andExpect(status().isBadRequest());
    }
    @Test
    public void deleteShouldReturnForbiddenWhenClientLogged() throws Exception{
        ResultActions result =
                mockMvc.perform(delete("/products/{id}", existingProductId)
                        .header("Authorization", "Bearer " + clientToken)
                        .accept(MediaType.APPLICATION_JSON));
        result.andExpect(status().isForbidden());
    }
    @Test
    public void deleteShouldReturnUnauthorizedWhenInvalidToken() throws Exception{
        ResultActions result =
                mockMvc.perform(delete("/products/{id}", existingProductId)
                        .header("Authorization", "Bearer " + invalidToken)
                        .accept(MediaType.APPLICATION_JSON));
        result.andExpect(status().isUnauthorized());
    }
}
