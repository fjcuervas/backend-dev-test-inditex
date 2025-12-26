package com.inditex.product.infraestructure.adapter.output.common;

import com.inditex.product.application.similar.port.output.ProductDetailPort;
import com.inditex.product.domain.model.Product;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
public class ProductDetailClient implements ProductDetailPort {

    private final WebClient webClient;

    @Value("${external.api.product-detail.url}")
    private String productDetailUrl;

    public ProductDetailClient(WebClient webClient) {
        this.webClient = webClient;
    }

    @Override
    public Mono<@NonNull Product> getProductById(String productId) {
        return webClient.get()
                .uri(productDetailUrl, productId)
                .retrieve()
                .bodyToMono(Product.class);
    }
}