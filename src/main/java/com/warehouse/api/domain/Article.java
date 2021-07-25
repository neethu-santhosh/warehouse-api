package com.warehouse.api.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.micrometer.core.instrument.util.StringUtils;
import lombok.*;

@ToString
@NoArgsConstructor
@Builder
public class Article {

    @Setter
    @Getter
    @NonNull
    @JsonProperty("art_id")
    private String articleId;

    @Setter
    @Getter
    @JsonProperty("name")
    private String articleName;

    @Setter
    @Getter
    private String stock;

    public Article(final String articleId, final String articleName, final String stock) {
        if (StringUtils.isBlank(articleId)) {
            throw new IllegalArgumentException("Article ID can't be blank/empty/null");
        }
        if (StringUtils.isBlank(articleName)) {
            throw new IllegalArgumentException("Article Name can't be blank/empty/null");
        }
        if (StringUtils.isBlank(stock)) {
            throw new IllegalArgumentException("Article Stock can't be blank/empty/null");
        }
        this.articleId = articleId;
        this.articleName = articleName;
        this.stock = stock;
    }
}
