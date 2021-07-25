package com.warehouse.api.property;

import lombok.*;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "file")
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class FileStorageProperties {

    @Setter
    @Getter
    private String uploadDir;
}
