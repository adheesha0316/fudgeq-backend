package com.fudgeq.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductRequestDto {

    private String name;
    private String description;
    private String ingredients;
    private BigDecimal price;
    private int dailyCapacity;
    private Double weightGrams;
    private boolean isSignature;
    private List<MultipartFile> images; // 0 to 5 images
}
