package com.warehouse.api.service;

import com.warehouse.api.domain.Inventory;
import com.warehouse.api.domain.Products;
import com.warehouse.api.exception.FileStorageException;
import com.warehouse.api.response.ProductStockResponse;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface WarehouseService {

    String storeFile(MultipartFile file) throws FileStorageException, IOException;

    Inventory readInventory() throws IOException;

    Products readProducts() throws IOException;

    void writeInventory(Inventory inventory) throws IOException;

    Map<String, Integer> findAvailability(Products products, Inventory inventory);

    String checkAndSellProduct(String sellProductName, int sellQuantity, Products products, Inventory inventory) throws IOException;

    List<ProductStockResponse> formProductStockResponseList(Map<String, Integer> availability);
}
