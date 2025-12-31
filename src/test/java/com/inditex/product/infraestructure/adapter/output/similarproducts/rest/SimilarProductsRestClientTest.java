package com.inditex.product.infraestructure.adapter.output.similarproducts.rest;

import com.inditex.product.infraestructure.similarproducts.adapter.output.rest.SimilarProductsRestClient;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.*;
import org.springframework.http.MediaType;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.reactive.function.client.WebClient;
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

        client = new SimilarProductsRestClient(webClient);

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
                .expectError()
                .verify();
    }

    @Test
    void shouldPropagate500ErrorWhenInternalServerErrorThrows() {
        mockWebServer.enqueue(
                new MockResponse()
                        .setResponseCode(500)
        );

        StepVerifier.create(client.getSimilarProductIds("1"))
                .expectError()
                .verify();
    }

}
