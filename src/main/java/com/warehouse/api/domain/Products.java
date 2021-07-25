package com.warehouse.api.domain;

import lombok.*;

import java.util.List;

@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Products {

    @Setter
    @Getter
    @NonNull
    private List<Product> products;

}
