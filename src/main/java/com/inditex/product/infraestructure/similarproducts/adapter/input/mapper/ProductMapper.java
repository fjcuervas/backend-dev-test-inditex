package com.inditex.product.infraestructure.similarproducts.adapter.input.mapper;

import com.inditex.product.api.generated.model.ProductDetail;
import com.inditex.product.domain.model.Product;

public final class ProductMapper {

    public static ProductDetail toApiModel(Product product) {
        return new ProductDetail(
                product.id(),
                product.name(),
                product.price(),
                product.availability()
        );
    }
}