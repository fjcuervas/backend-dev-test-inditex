package com.inditex.product.infraestructure.adapter.input.similarproducts.rest;

import com.inditex.product.application.similar.port.output.ProductDetailPort;
import com.inditex.product.application.similar.port.output.SimilarProductIdsPort;
import com.inditex.product.domain.model.Product;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webtestclient.autoconfigure.AutoConfigureWebTestClient;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;

import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
class SimilarProductsControllerIT {

    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private SimilarProductIdsPort similarProductIdsPort;

    @MockitoBean
    private ProductDetailPort productDetailPort;

    @Test
    void shouldReturnSimilarProductsOKWhenAllProductsIdsExist() {
        Mockito.when(similarProductIdsPort.getSimilarProductIds("4"))
                .thenReturn(Flux.just("2", "3"));

        Mockito.when(productDetailPort.getProductById("2"))
                .thenReturn(Mono.just(
                        new Product("2", "T-Shirt", BigDecimal.valueOf(25.50), true)
                ));

        Mockito.when(productDetailPort.getProductById("3"))
                .thenReturn(Mono.just(
                        new Product("3", "Jeans", BigDecimal.valueOf(25.50), true)
                ));

        webTestClient.get()
                .uri("/product/4/similar")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$[0].id").isEqualTo("2")
                .jsonPath("$[1].id").isEqualTo("3");
    }

    @Test
    void shouldReturn404WhenProductDoesNotExist() {
        Mockito.when(similarProductIdsPort.getSimilarProductIds("999"))
                .thenThrow(WebClientResponseException.NotFound.class);

        webTestClient.get()
                .uri("/product/999/similar")
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void shouldReturn500WhenExternalApiFails() {
        Mockito.when(similarProductIdsPort.getSimilarProductIds("4"))
                .thenReturn(Flux.just("2"));

        Mockito.when(productDetailPort.getProductById("2"))
                .thenReturn(Mono.error(new RuntimeException("External API failure")));

        webTestClient.get()
                .uri("/product/4/similar")
                .exchange()
                .expectStatus().is5xxServerError();
    }

    @Test
    void shouldReturn500IfOneSimilarProductFails() {
        Mockito.when(similarProductIdsPort.getSimilarProductIds("4"))
                .thenReturn(Flux.just("2", "3"));

        Mockito.when(productDetailPort.getProductById("3"))
                .thenReturn(Mono.error(new RuntimeException("External API failure")));

        Mockito.when(productDetailPort.getProductById("2"))
                .thenReturn(Mono.just(
                        new Product("2", "T-Shirt", BigDecimal.valueOf(25.50), true)
                ));

        webTestClient.get()
                .uri("/product/4/similar")
                .exchange()
                .expectStatus().is5xxServerError();
    }

}