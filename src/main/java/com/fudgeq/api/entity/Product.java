package com.fudgeq.api.entity;

import com.fudgeq.api.enums.ProductStatus;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true)
public class Product extends BaseEntity {

    @Id
    @Column(name = "product_id", nullable = false, updatable = false)
    private String productId;

    @Column(nullable = false)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(columnDefinition = "TEXT")
    private String ingredients;

    @Column(nullable = false)
    private BigDecimal price;

    @Column(name = "daily_capacity", nullable = false)
    private int dailyCapacity;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProductStatus status = ProductStatus.PENDING_APPROVAL;

    @Column(name = "added_by", nullable = false)
    private String addedBy;

    @ElementCollection
    @CollectionTable(name = "product_images", joinColumns = @JoinColumn(name = "product_id"))
    @Column(name = "image_url")
    private List<String> imageUrls;

    @Column(name = "is_available")
    private boolean isAvailable = true;

    @Column(name = "is_signature")
    private boolean isSignature = false;

    @Column(name = "weight_grams")
    private Double weightGrams;
}
