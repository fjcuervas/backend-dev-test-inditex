package com.inditex.product.infraestructure.adapter.output.common;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.*;
import org.springframework.http.MediaType;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.test.StepVerifier;

import java.io.IOException;
import java.math.BigDecimal;

class ProductDetailClientTest {

    private MockWebServer mockWebServer;
    private ProductDetailClient client;

    @BeforeEach
    void setUp() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start();

        WebClient webClient = WebClient.builder()
                .baseUrl(mockWebServer.url("/").toString())
                .build();

        client = new ProductDetailClient(webClient);

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