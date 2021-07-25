package com.warehouse.api.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.warehouse.api.domain.Article;
import com.warehouse.api.domain.Inventory;
import com.warehouse.api.domain.Product;
import com.warehouse.api.domain.Products;
import com.warehouse.api.exception.FileStorageException;
import com.warehouse.api.property.FileStorageProperties;
import com.warehouse.api.response.ProductStockResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class WarehouseServiceImpl implements WarehouseService {
    private static final Logger logger = LoggerFactory.getLogger(WarehouseServiceImpl.class);
    private final Path fileStorageLocation;

    /**
     * Constructor
     */
    @Autowired
    public WarehouseServiceImpl(FileStorageProperties fileStorageProperties) throws FileStorageException {
        this.fileStorageLocation = Paths.get(fileStorageProperties.getUploadDir())
                .toAbsolutePath().normalize();
        try {
            Files.createDirectories(this.fileStorageLocation);
        } catch (Exception ex) {
            throw new FileStorageException("Could not create the directory where the uploaded files will be stored.", ex);
        }
    }

    /**
     * To validate the file and save it in the target location set in the FileStorageProperties
     */
    public String storeFile(MultipartFile file) throws FileStorageException, IOException {
        // Normalize file name
        String fileName = StringUtils.cleanPath(file.getOriginalFilename());

        // Check if the file's name contains invalid characters
        if (fileName.contains("..")) {
            throw new FileStorageException("Sorry! Filename contains invalid path sequence " + fileName);
        }

        // Check if the file type is JSON
        if (!"application/json".equalsIgnoreCase(file.getContentType())) {
            throw new FileStorageException("Sorry! Invalid file type " + fileName);
        }

        // Copy file to the target location (Replacing existing file with the same name)
        Path targetLocation = this.fileStorageLocation.resolve(fileName);
        Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

        return fileName;

    }

    /**
     * To read the data from inventory.json and map it to Inventory object
     */
    public Inventory readInventory() throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        Inventory inventory = objectMapper.readValue(new File("src/main/resources/json/inventory.json"), Inventory.class);
        return inventory;
    }

    /**
     * To read the data from products.json and map it to Products object
     */
    public Products readProducts() throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        Products products = objectMapper.readValue(new File("src/main/resources/json/products.json"), Products.class);
        return products;
    }

    /**
     * To update the inventory data in inventory.json file after a successful product sale
     */
    public void writeInventory(Inventory inventory) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        //configure objectMapper for pretty input
        objectMapper.configure(SerializationFeature.INDENT_OUTPUT, true);
        objectMapper.writeValue(new File("src/main/resources/json/inventory.json"), inventory);
    }

    /**
     * To calculate the available products from the current inventory
     */
    public Map<String, Integer> findAvailability(Products products, Inventory inventory) {
        Map<String, Integer> availabilityResult = new HashMap<>();
        //Iterate through the list to find the availability for each product
        for (Product product : products.getProducts()) {
            String prodName = product.getProductName();
            List<Integer> stockProfile = new ArrayList<>();
            //Iterate through the articles needed for that product and
            // check if the amount needed is available in the current inventory
            for (Map<String, String> containArticle : product.getContainArticles()) {
                String requiredArtId = containArticle.get("art_id");
                int requiredQuantity = Integer.parseInt(containArticle.get("amount_of"));
                for (Article article : inventory.getInventory()) {
                    if (article.getArticleId().equalsIgnoreCase(requiredArtId)) {
                        int stock = Integer.parseInt(article.getStock());
                        int numberOfProd = stock / requiredQuantity;
                        stockProfile.add(numberOfProd);
                    }
                }
            }
            availabilityResult.put(prodName, stockProfile.stream().min(Integer::compare).get());
            logger.info("Getting availability completed for the product:" + product.getProductName());
        }

        return availabilityResult;
    }

    /**
     * To check the inventory is enough and proceed with sale
     */
    public String checkAndSellProduct(String sellProductName, int sellQuantity, Products products, Inventory inventory) throws IOException {
        Boolean canSell = findAvailabilityBeforeSell(sellProductName, sellQuantity, products, inventory);
        String status = "";
        if (canSell) {
            status = sellProduct(sellProductName, sellQuantity, products, inventory);
        } else {
            status = "Failed!!! Product not available to sell";
        }
        return status;
    }

    /**
     * To sell the product and update the available inventory after sale
     */
    public String sellProduct(String sellProductName, int sellQuantity, Products products, Inventory inventory) throws IOException {
        for (Product product : products.getProducts()) {
            String prodName = product.getProductName();
            if (prodName.equalsIgnoreCase(sellProductName)) {
                //To update the inventory after selling a product
                for (Map<String, String> containArticle : product.getContainArticles()) {
                    String sellArtId = containArticle.get("art_id");
                    int sellArtQuantity = Integer.parseInt(containArticle.get("amount_of"));
                    for (Article article : inventory.getInventory()) {
                        if (article.getArticleId().equalsIgnoreCase(sellArtId)) {
                            int stock = Integer.parseInt(article.getStock());
                            int sell = sellArtQuantity * sellQuantity;
                            if (stock >= sell) {
                                article.setStock(String.valueOf(stock - sell));
                            }
                        }
                    }
                }
            }
        }
        //To write the inventory.json file with the updated inventory
        writeInventory(inventory);
        return "Success";
    }

    /**
     * Availability check to make sure enough inventory is there before proceeding with sale
     */
    public Boolean findAvailabilityBeforeSell(String sellProductName, int sellQuantity, Products products, Inventory inventory) {
        Map<String, Integer> availability = findAvailability(products, inventory);
        for (Map.Entry<String, Integer> result : availability.entrySet()) {
            if (result.getKey().equalsIgnoreCase(sellProductName)) {
                if (result.getValue() >= sellQuantity) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Get a list of ProductStockResponse to form the AvailabilityResponse
     */
    public List<ProductStockResponse> formProductStockResponseList(Map<String, Integer> availability) {
        List<ProductStockResponse> result = new ArrayList<>();
        for (Map.Entry<String, Integer> pair : availability.entrySet()) {
            ProductStockResponse prodStock = new ProductStockResponse();
            prodStock.setProductName(pair.getKey());
            prodStock.setStock(String.valueOf(pair.getValue()));
            result.add(prodStock);
        }
        return result;
    }
}
