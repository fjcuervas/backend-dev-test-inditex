package com.inditex.product.domain.port.output;

import org.jspecify.annotations.NonNull;
import reactor.core.publisher.Flux;

public interface SimilarProductIdsPort {
    Flux<@NonNull String> getSimilarProductIds(String productId);
}
