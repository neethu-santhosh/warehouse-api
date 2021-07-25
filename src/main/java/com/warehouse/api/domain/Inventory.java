package com.warehouse.api.domain;

import lombok.*;

import java.util.List;

@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Inventory {

    @Setter
    @Getter
    @NonNull
    private List<Article> inventory;
}
