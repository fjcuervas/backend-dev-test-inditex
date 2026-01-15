package com.inditex.product.infraestructure.similarproducts.adapter.input.rest;

import com.inditex.product.api.generated.ProductApi;
import com.inditex.product.api.generated.model.ProductDetail;
import com.inditex.product.application.similarproducts.port.input.GetSimilarProductsUseCase;
import com.inditex.product.infraestructure.similarproducts.adapter.input.mapper.ProductMapper;
import org.jspecify.annotations.NonNull;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
public class SimilarProductsController implements ProductApi {

    private final GetSimilarProductsUseCase useCase;

    public SimilarProductsController(GetSimilarProductsUseCase useCase) {
        this.useCase = useCase;
    }

    @Override
    public Mono<@NonNull ResponseEntity<@NonNull Flux<@NonNull ProductDetail>>> getProductSimilar(
            String productId,
            ServerWebExchange exchange) {
        return useCase.getSimilarProducts(productId)
                .map(ProductMapper::toApiModel)
                .collectList()
                .map(list -> ResponseEntity.ok(Flux.fromIterable(list)));
    }

}