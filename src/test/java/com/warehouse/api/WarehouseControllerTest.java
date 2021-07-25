package com.warehouse.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.warehouse.api.controller.WarehouseController;
import com.warehouse.api.domain.Article;
import com.warehouse.api.domain.Inventory;
import com.warehouse.api.domain.Product;
import com.warehouse.api.domain.Products;
import com.warehouse.api.service.WarehouseService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(WarehouseController.class)
public class WarehouseControllerTest {
    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper mapper;

    @MockBean
    WarehouseService warehouseService;

    @Test
    public void getProductAvailability() throws Exception {
        String uri = "/v1/getProductAvailability";
        mockMvc.perform(MockMvcRequestBuilders
                .get(uri)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("Success")));
    }

    @Test
    public void getProductAvailabilityWithFileNotFoundException() throws Exception {
        String uri = "/v1/getProductAvailability";
        Mockito.when(warehouseService.readProducts()).thenThrow(FileNotFoundException.class);
        mockMvc.perform(MockMvcRequestBuilders
                .get(uri)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is5xxServerError());
    }

    @Test
    public void sellProductWithFileNotFoundException() throws Exception {
        String uri = "/v1/sellProduct";
        Mockito.when(warehouseService.readProducts()).thenThrow(FileNotFoundException.class);
        mockMvc.perform(MockMvcRequestBuilders
                .post(uri)
                .contentType(MediaType.APPLICATION_JSON)
                .param("productName", "Dining Chair")
                .param("quantity", "2"))
                .andExpect(status().is5xxServerError());
    }

    @Test
    public void sellProductWithNoAvailability() throws Exception {
        String uri = "/v1/sellProduct";
        Mockito.when(warehouseService.readInventory()).thenReturn(getMockInventory());
        Mockito.when(warehouseService.readProducts()).thenReturn(getMockProducts());
        mockMvc.perform(MockMvcRequestBuilders
                .post(uri)
                .contentType(MediaType.APPLICATION_JSON)
                .param("productName", "Dining Chair")
                .param("quantity", "1"))
                .andExpect(status().isOk())
                .andReturn();
    }


    public Inventory getMockInventory() {
        Article article1 = Article.builder().articleId("1").articleName("leg").stock("12").build();
        Article article2 = Article.builder().articleId("2").articleName("screw").stock("17").build();
        Article article3 = Article.builder().articleId("3").articleName("seat").stock("2").build();
        Article article4 = Article.builder().articleId("4").articleName("table top").stock("1").build();
        List<Article> articleList = new ArrayList<Article>();
        articleList.add(article1);
        articleList.add(article2);
        articleList.add(article3);
        articleList.add(article4);
        Inventory inventory = Inventory.builder().inventory(articleList).build();
        return inventory;
    }

    public Products getMockProducts() {
        Map<String, String> mockContainArticle1 = new HashMap<>();
        mockContainArticle1.put("art_id", "1");
        mockContainArticle1.put("amount_of", "4");

        Map<String, String> mockContainArticle2 = new HashMap<>();
        mockContainArticle2.put("art_id", "2");
        mockContainArticle2.put("amount_of", "8");

        Map<String, String> mockContainArticle3 = new HashMap<>();
        mockContainArticle3.put("art_id", "4");
        mockContainArticle3.put("amount_of", "1");

        List<Map<String, String>> mockContainArticles = new ArrayList<>();
        mockContainArticles.add(mockContainArticle1);
        mockContainArticles.add(mockContainArticle2);
        mockContainArticles.add(mockContainArticle1);

        Product product1 = Product.builder().productName("Dinning Table").price("100sek")
                .containArticles(mockContainArticles).build();

        List<Product> mockProduct = new ArrayList<>();
        mockProduct.add(product1);
        Products products = Products.builder().products(mockProduct).build();
        return products;
    }

}
