package com.warehouse.api.response;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ProductStockResponse {
    @Setter
    @Getter
    private String productName;

    @Setter
    @Getter
    private String stock;
}
