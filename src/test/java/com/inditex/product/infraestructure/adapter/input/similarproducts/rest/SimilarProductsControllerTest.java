package com.inditex.product.infraestructure.adapter.input.similarproducts.rest;

import com.inditex.product.api.generated.model.ProductDetail;
import com.inditex.product.application.similar.port.input.GetSimilarProductsUseCase;
import com.inditex.product.domain.model.Product;
import org.jspecify.annotations.NonNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.webflux.test.autoconfigure.WebFluxTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

@WebFluxTest(controllers = SimilarProductsController.class)
class SimilarProductsControllerTest {

    @MockitoBean
    private GetSimilarProductsUseCase useCase;

    @Mock
    private ServerWebExchange exchange;

    private SimilarProductsController controller;

    @BeforeEach
    void setUp() {
        controller = new SimilarProductsController(useCase);
    }

    @Test
    void shouldReturnFluxOfProducts() {
        String productId = "4";

        Product domainProduct1 = new Product("2", "T-Shirt", BigDecimal.valueOf(20.50), true);
        Product domainProduct2 = new Product("3", "Jeans", BigDecimal.valueOf(40), true);

        Mockito.when(useCase.getSimilarProducts(productId))
                .thenReturn(Flux.just(domainProduct1, domainProduct2));

        Mono<@NonNull ResponseEntity<@NonNull Flux<@NonNull ProductDetail>>> result =
                controller.getProductSimilar(productId, exchange);

        StepVerifier.create(result)
                .assertNext(response -> {
                    assertEquals(HttpStatus.OK, response.getStatusCode());
                    assert response.getBody() != null;
                    StepVerifier.create(response.getBody())
                            .expectNextMatches(p -> p.getId().equals("2"))
                            .expectNextMatches(p -> p.getId().equals("3"))
                            .verifyComplete();
                })
                .verifyComplete();
    }

    @Test
    void shouldReturnEmptyFluxWhenNoProducts() {
        String productId = "5";

        Mockito.when(useCase.getSimilarProducts(productId))
                .thenReturn(Flux.empty());

        Mono<@NonNull ResponseEntity<@NonNull Flux<@NonNull ProductDetail>>> result =
                controller.getProductSimilar(productId, exchange);

        StepVerifier.create(result)
                .assertNext(response -> {
                    assertEquals(200, response.getStatusCode().value());
                    assert response.getBody() != null;
                    StepVerifier.create(response.getBody())
                            .verifyComplete();
                })
                .verifyComplete();
    }

    @Test
    void shouldPropagateErrorFromUseCase() {
        String productId = "6";

        Mockito.when(useCase.getSimilarProducts(productId))
                .thenReturn(Flux.error(new RuntimeException("unexpected error")));

        Mono<@NonNull ResponseEntity<@NonNull Flux<@NonNull ProductDetail>>> result =
                controller.getProductSimilar(productId, exchange);

        StepVerifier.create(result)
                .expectErrorMatches(throwable ->
                        throwable instanceof RuntimeException &&
                                throwable.getMessage().equals("unexpected error"))
                .verify();
    }

}