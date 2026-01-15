package com.inditex.product.infraestructure.adapter.output.similarproducts.rest;

import com.inditex.product.infraestructure.similarproducts.adapter.output.rest.SimilarProductsRestClient;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.retry.RetryConfig;
import io.github.resilience4j.retry.RetryRegistry;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.test.StepVerifier;

import java.io.IOException;

class SimilarProductsRestClientTest {

    private MockWebServer mockWebServer;
    private SimilarProductsRestClient client;

    @BeforeEach
    void setUp() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start();

        WebClient webClient = WebClient.builder()
                .baseUrl(mockWebServer.url("/").toString())
                .build();

        CircuitBreakerRegistry cbRegistry = CircuitBreakerRegistry.of(
                CircuitBreakerConfig.custom()
                        .failureRateThreshold(100)
                        .slidingWindowSize(1)
                        .build()
        );

        RetryRegistry retryRegistry = RetryRegistry.of(
                RetryConfig.custom()
                        .maxAttempts(1)
                        .build()
        );

        client = new SimilarProductsRestClient(cbRegistry, retryRegistry, webClient);

        ReflectionTestUtils.setField(
                client,
                "similarIdsUrl",
                "/product/{productId}/similarids"
        );
    }

    @AfterEach
    void tearDown() throws IOException {
        mockWebServer.shutdown();
    }

    @Test
    void shouldReturnSimilarProductIds() {
        mockWebServer.enqueue(
                new MockResponse()
                        .setBody("[\"2\",\"3\",\"4\"]")
                        .addHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
        );

        StepVerifier.create(client.getSimilarProductIds("1"))
                .expectNext("2")
                .expectNext("3")
                .expectNext("4")
                .verifyComplete();
    }

    @Test
    void shouldPropagate404ErrorWhenProductIdDoesNotExist() {
        mockWebServer.enqueue(
                new MockResponse()
                        .setResponseCode(404)
        );

        StepVerifier.create(client.getSimilarProductIds("1"))
                .expectError(WebClientResponseException.NotFound.class)
                .verify();
    }

    @Test
    void shouldPropagate500ErrorWhenInternalServerErrorThrows() {
        mockWebServer.enqueue(
                new MockResponse()
                        .setResponseCode(500)
        );

        StepVerifier.create(client.getSimilarProductIds("1"))
                .expectError(WebClientResponseException.InternalServerError.class)
                .verify();
    }

}
