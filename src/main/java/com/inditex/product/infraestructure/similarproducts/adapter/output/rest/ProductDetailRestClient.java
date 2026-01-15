package com.inditex.product.infraestructure.similarproducts.adapter.output.rest;

import com.inditex.product.domain.model.Product;
import com.inditex.product.domain.port.output.ProductDetailPort;
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
import reactor.core.publisher.Mono;

@Component
public class ProductDetailRestClient implements ProductDetailPort {

    private final CircuitBreaker circuitBreaker;
    private final Retry retry;
    private final WebClient webClient;

    @Value("${external.api.product-detail.url}")
    private String productDetailUrl;

    public ProductDetailRestClient(
            CircuitBreakerRegistry cbRegistry,
            RetryRegistry retryRegistry,
            WebClient webClient
    ) {
        this.circuitBreaker = cbRegistry.circuitBreaker("productDetail");
        this.retry = retryRegistry.retry("productDetail");
        this.webClient = webClient;
    }

    @Override
    public Mono<@NonNull Product> getProductById(String productId) {
        return webClient.get()
                .uri(productDetailUrl, productId)
                .retrieve()
                .bodyToMono(Product.class)
                .transformDeferred(CircuitBreakerOperator.of(circuitBreaker))
                .transformDeferred(RetryOperator.of(retry));
    }

}