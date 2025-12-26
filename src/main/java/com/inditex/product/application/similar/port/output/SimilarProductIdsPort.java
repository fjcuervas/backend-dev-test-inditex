package com.inditex.product.application.similar.port.output;

import org.jspecify.annotations.NonNull;
import reactor.core.publisher.Flux;

public interface SimilarProductIdsPort {
    Flux<@NonNull String> getSimilarProductIds(String productId);
}
