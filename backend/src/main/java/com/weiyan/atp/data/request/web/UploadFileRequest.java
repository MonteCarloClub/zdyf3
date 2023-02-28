package com.weiyan.atp.data.request.web;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class UploadFileRequest {
    @NotEmpty
    private String userName;

    @NotNull
    private List<String> tags;

    @NotEmpty
    private String plainContent;

    /**
     * (A AND B AND (C OR D))
     */
    @NotEmpty
    private String policy;

    @NotEmpty
    private MultipartFile file;
}
