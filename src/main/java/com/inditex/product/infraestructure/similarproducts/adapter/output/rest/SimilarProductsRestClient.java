package com.inditex.product.infraestructure.similarproducts.adapter.output.rest;

import com.inditex.product.domain.port.output.SimilarProductIdsPort;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

@Component
public class SimilarProductsRestClient implements SimilarProductIdsPort {

    private final WebClient webClient;

    @Value("${external.api.similar-ids.url}")
    private String similarIdsUrl;

    public SimilarProductsRestClient(WebClient webClient) {
        this.webClient = webClient;
    }

    @Override
    public Flux<@NonNull String> getSimilarProductIds(String productId) {
        return webClient.get()
                .uri(similarIdsUrl, productId)
                .retrieve()
                .bodyToFlux(Object.class)
                .map(String::valueOf);
    }

}