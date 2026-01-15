package com.inditex.product.infraestructure.similarproducts.adapter.output.rest;

import com.inditex.product.domain.port.output.SimilarProductIdsPort;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.reactor.circuitbreaker.operator.CircuitBreakerOperator;
import io.github.resilience4j.reactor.retry.RetryOperator;
import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryRegistry;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

@Component
public class SimilarProductsRestClient implements SimilarProductIdsPort {

    private final CircuitBreaker circuitBreaker;
    private final Retry retry;
    private final WebClient webClient;

    @Value("${external.api.similar-ids.url}")
    private String similarIdsUrl;

    public SimilarProductsRestClient(
            CircuitBreakerRegistry cbRegistry,
            RetryRegistry retryRegistry,
            WebClient webClient
    ) {
        this.circuitBreaker = cbRegistry.circuitBreaker("similarIds");
        this.retry = retryRegistry.retry("similarIds");
        this.webClient = webClient;
    }

    @Override
    public Flux<@NonNull String> getSimilarProductIds(String productId) {
        return webClient.get()
                .uri(similarIdsUrl, productId)
                .retrieve()
                .bodyToMono(String[].class)
                .flatMapMany(Flux::fromArray)
                .transformDeferred(CircuitBreakerOperator.of(circuitBreaker))
                .transformDeferred(RetryOperator.of(retry));
    }

}