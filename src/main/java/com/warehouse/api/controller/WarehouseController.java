package com.warehouse.api.controller;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.warehouse.api.domain.Inventory;
import com.warehouse.api.domain.Products;
import com.warehouse.api.exception.FileStorageException;
import com.warehouse.api.response.AvailabilityResponse;
import com.warehouse.api.response.ProductStockResponse;
import com.warehouse.api.response.SellProductResponse;
import com.warehouse.api.response.UploadFileResponse;
import com.warehouse.api.service.WarehouseService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/v1")
public class WarehouseController {

    private static final Logger logger = LoggerFactory.getLogger(WarehouseController.class);

    private final WarehouseService warehouseService;

    @Autowired
    public WarehouseController(WarehouseService warehouseService) {
        this.warehouseService = warehouseService;
    }

    /**
     * To handle the POST request to upload a file
     *
     * @param file
     * @return
     */
    @PostMapping("/uploadFile")
    public UploadFileResponse uploadFile(@RequestParam("file") MultipartFile file) {
        String fileName = "";
        try {
            fileName = warehouseService.storeFile(file);
        } catch (FileStorageException fx) {
            logger.error("FileStorageException while saving file in directory", fx);
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR, "FileStorageException while saving json file");
        } catch (IOException ex) {
            logger.error("IOException while saving json file in directory", ex);
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR, "IOException while saving json file");
        } catch (Exception ex) {
            logger.error("Exception while saving json file in directory", ex);
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR, "Exception while saving json file");
        }
        return new UploadFileResponse(fileName, file.getContentType(), file.getSize(), "Success");
    }


    /**
     * To handle POST request to upload inventory.json and products.json together
     *
     * @param files
     * @return
     */
    @PostMapping("/uploadMultipleFiles")
    public List<UploadFileResponse> uploadMultipleFiles(@RequestParam("files") MultipartFile[] files) {
        return Arrays.asList(files)
                .stream()
                .map(file -> uploadFile(file))
                .collect(Collectors.toList());
    }


    /**
     * To handle the GET requests to get the products available for the customers from the available inventory
     *
     * @return
     */
    @GetMapping("/getProductAvailability")
    public AvailabilityResponse getAvailableProducts() {
        List<ProductStockResponse> stockList;
        try {
            Products products = warehouseService.readProducts();
            Inventory inventory = warehouseService.readInventory();
            Map<String, Integer> availability = warehouseService.findAvailability(products, inventory);
            stockList = warehouseService.formProductStockResponseList(availability);
        } catch (FileNotFoundException ex) {
            logger.error("Inventory/Products files not available to read availability", ex);
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR, "Failed. Inventory/Product file not found to read availability");
        } catch (JsonMappingException ex) {
            logger.error("JsonMappingException while reading Products/Inventory from json file", ex);
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR, "Failed due to JsonMappingException while reading Products/Inventory json files");
        } catch (IOException ex) {
            logger.error("IOException while reading Products/Inventory from json file", ex);
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR, "Failed due to exception while reading Products/Inventory json files");
        } catch (Exception ex) {
            logger.error("Exception while getting available products", ex);
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR, "Request to get availability failed");
        }
        return new AvailabilityResponse(stockList, "Success");
    }

    /**
     * To handle POST request to sell a product
     *
     * @param productName
     * @param quantity
     * @return
     */
    @PostMapping("/sellProduct")
    public SellProductResponse sellProduct(@RequestParam("productName") String productName,
                                           @RequestParam("quantity") String quantity) {
        String status = "";
        try {
            Products products = warehouseService.readProducts();
            Inventory inventory = warehouseService.readInventory();
            int sellQuantity = Integer.parseInt(quantity);
            status  = warehouseService.checkAndSellProduct(productName, sellQuantity, products, inventory);
        } catch (FileNotFoundException ex) {
            logger.error("Inventory/Products files not available to read availability", ex);
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR, "Failed. File not found to read availability before sell");
        } catch (JsonMappingException ex) {
            logger.error("JsonMappingException while reading Products/Inventory from json file", ex);
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR, "Failed due to JsonMappingException while reading Products/Inventory json files");
        } catch (IOException ex) {
            logger.error("IOException while reading Products/Inventory from json file", ex);
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR, "Failed due to exception while reading Products/Inventory json files");
        } catch (Exception ex) {
            logger.error("Exception while selling the product", ex);
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR, "Request to sell the product failed");
        }
        return new SellProductResponse(productName, quantity, status);
    }

}
