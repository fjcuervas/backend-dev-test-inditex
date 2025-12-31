package com.inditex.product.application.similarproducts.port.input;

import com.inditex.product.domain.model.Product;
import org.jspecify.annotations.NonNull;
import reactor.core.publisher.Flux;

public interface GetSimilarProductsUseCase {
    Flux<@NonNull Product> getSimilarProducts(String productId);
}

