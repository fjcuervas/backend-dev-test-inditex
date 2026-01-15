package com.inditex.product.infraestructure.adapter.output.common;

import com.inditex.product.infraestructure.similarproducts.adapter.output.rest.ProductDetailRestClient;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.retry.RetryConfig;
import io.github.resilience4j.retry.RetryRegistry;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.test.StepVerifier;

import java.io.IOException;
import java.math.BigDecimal;

class ProductDetailRestClientTest {

    private MockWebServer mockWebServer;
    private ProductDetailRestClient client;

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

        client = new ProductDetailRestClient(cbRegistry, retryRegistry, webClient);

        ReflectionTestUtils.setField(
                client,
                "productDetailUrl",
                "/product/{productId}"
        );
    }

    @AfterEach
    void tearDown() throws IOException {
        mockWebServer.shutdown();
    }

    @Test
    void shouldReturnProductById() {
        mockWebServer.enqueue(
                new MockResponse()
                        .setBody("""
                            {
                              "id": "2",
                              "name": "T-Shirt",
                              "price": 20.55,
                              "availability": true
                            }
                        """)
                        .addHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
        );

        StepVerifier.create(client.getProductById("2"))
                .assertNext(product -> {
                    Assertions.assertEquals("2", product.id());
                    Assertions.assertEquals("T-Shirt", product.name());
                    Assertions.assertEquals(BigDecimal.valueOf(20.55), product.price());
                    Assertions.assertTrue(product.availability());
                })
                .verifyComplete();
    }

    @Test
    void shouldPropagate404ErrorWhenProductIdDoesNotExist() {
        mockWebServer.enqueue(
                new MockResponse().setResponseCode(404)
        );

        StepVerifier.create(client.getProductById("99"))
                .expectError()
                .verify();
    }

    @Test
    void shouldPropagate500ErrorWhenInternalServerErrorThrows() {
        mockWebServer.enqueue(
                new MockResponse().setResponseCode(500)
        );

        StepVerifier.create(client.getProductById("2"))
                .expectError()
                .verify();
    }

}