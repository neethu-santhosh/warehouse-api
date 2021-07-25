package com.warehouse.api.response;

import lombok.*;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@ToString
public class AvailabilityResponse {

    @Setter
    @Getter
    private List<ProductStockResponse> availability;

    @Setter
    @Getter
    private String status;

}
