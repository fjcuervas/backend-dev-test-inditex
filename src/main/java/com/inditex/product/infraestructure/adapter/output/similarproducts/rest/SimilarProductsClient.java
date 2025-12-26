package com.inditex.product.infraestructure.adapter.output.similarproducts.rest;

import com.inditex.product.application.similar.port.output.SimilarProductIdsPort;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

@Component
public class SimilarProductsClient implements SimilarProductIdsPort {

    private final WebClient webClient;

    @Value("${external.api.similar-ids.url}")
    private String similarIdsUrl;

    public SimilarProductsClient(WebClient webClient) {
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