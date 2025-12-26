package com.inditex.product.application.similar.port.output;

import com.inditex.product.domain.model.Product;
import org.jspecify.annotations.NonNull;
import reactor.core.publisher.Mono;

public interface ProductDetailPort {
    Mono<@NonNull Product> getProductById(String productId);
}