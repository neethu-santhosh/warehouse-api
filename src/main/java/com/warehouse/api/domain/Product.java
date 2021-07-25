package com.warehouse.api.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.micrometer.core.instrument.util.StringUtils;
import lombok.*;

import java.util.List;
import java.util.Map;

@ToString
@NoArgsConstructor
@Builder
public class Product {

    @Setter
    @Getter
    @JsonProperty("name")
    private String productName;

    @Setter
    @Getter
    private String price;

    @Setter
    @Getter
    @JsonProperty("contain_articles")
    private List<Map<String, String>> containArticles;

    private Product(final String productName, final String price, final List<Map<String, String>> containArticles) {
        if (StringUtils.isBlank(productName)) {
            throw new IllegalArgumentException("product Name can't be blank/empty/null");
        }
        this.productName = productName;
        this.price = price;
        this.containArticles = containArticles;
    }
}
