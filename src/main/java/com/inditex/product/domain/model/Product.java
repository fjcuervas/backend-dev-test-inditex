package com.inditex.product.domain.model;

import org.jspecify.annotations.NonNull;

import java.math.BigDecimal;

public record Product(
        @NonNull String id,
        @NonNull String name,
        @NonNull BigDecimal price,
        boolean availability
) {}