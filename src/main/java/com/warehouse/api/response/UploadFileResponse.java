package com.warehouse.api.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@AllArgsConstructor
@ToString
public class UploadFileResponse {

    @Setter
    @Getter
    private String fileName;

    @Setter
    @Getter
    private String fileType;

    @Setter
    @Getter
    private long size;

    @Setter
    @Getter
    private String status;
}
