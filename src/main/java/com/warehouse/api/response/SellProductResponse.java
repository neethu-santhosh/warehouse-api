package com.warehouse.api.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@AllArgsConstructor
@ToString
public class SellProductResponse {

    @Setter
    @Getter
    private String productName;

    @Setter
    @Getter
    private String quantity;

    @Setter
    @Getter
    private String status;
}
