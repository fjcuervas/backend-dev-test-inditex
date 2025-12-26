package com.inditex.product.application.similar;

import com.inditex.product.application.similar.port.output.ProductDetailPort;
import com.inditex.product.application.similar.port.output.SimilarProductIdsPort;
import com.inditex.product.domain.model.Product;
import org.jspecify.annotations.NonNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;

import static org.mockito.Mockito.*;

class GetSimilarProductsUseCaseImplTest {

    @Mock
    private SimilarProductIdsPort similarIdsPort;

    @Mock
    private ProductDetailPort productDetailPort;

    private GetSimilarProductsUseCaseImpl useCase;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        useCase = new GetSimilarProductsUseCaseImpl(similarIdsPort, productDetailPort);
    }

    @Test
    void shouldReturnProductsForGivenProductId() {
        String productId = "1";

        when(similarIdsPort.getSimilarProductIds(productId))
                .thenReturn(Flux.just("2", "3"));

        Product product2 = new Product("2", "T-Shirt", BigDecimal.valueOf(19.99), true);
        Product product3 = new Product("3", "Jeans", BigDecimal.valueOf(39.99), true);

        when(productDetailPort.getProductById("2")).thenReturn(Mono.just(product2));
        when(productDetailPort.getProductById("3")).thenReturn(Mono.just(product3));

        Flux<@NonNull Product> result = useCase.getSimilarProducts(productId);

        StepVerifier.create(result)
                .expectNext(product2)
                .expectNext(product3)
                .verifyComplete();

        verify(similarIdsPort, times(1)).getSimilarProductIds(productId);
        verify(productDetailPort, times(1)).getProductById("2");
        verify(productDetailPort, times(1)).getProductById("3");
    }

    @Test
    void shouldReturnEmptyWhenNoSimilarIds() {
        String productId = "1";

        when(similarIdsPort.getSimilarProductIds(productId))
                .thenReturn(Flux.empty());

        Flux<@NonNull Product> result = useCase.getSimilarProducts(productId);

        StepVerifier.create(result).verifyComplete();

        verify(similarIdsPort, times(1)).getSimilarProductIds(productId);
        verifyNoInteractions(productDetailPort);
    }

    @Test
    void shouldPropagateErrorFromSimilarIdsPort() {
        String productId = "1";

        when(similarIdsPort.getSimilarProductIds(productId))
                .thenReturn(Flux.error(new RuntimeException("Error fetching IDs")));

        Flux<@NonNull Product> result = useCase.getSimilarProducts(productId);

        StepVerifier.create(result)
                .expectErrorMatches(throwable ->
                        throwable instanceof RuntimeException &&
                                throwable.getMessage().equals("Error fetching IDs"))
                .verify();

        verify(similarIdsPort, times(1)).getSimilarProductIds(productId);
        verifyNoInteractions(productDetailPort);
    }

    @Test
    void shouldPropagateErrorFromProductDetailPort() {
        String productId = "1";

        when(similarIdsPort.getSimilarProductIds(productId))
                .thenReturn(Flux.just("2"));

        when(productDetailPort.getProductById("2"))
                .thenReturn(Mono.error(new RuntimeException("Error fetching product")));

        Flux<@NonNull Product> result = useCase.getSimilarProducts(productId);

        StepVerifier.create(result)
                .expectErrorMatches(throwable ->
                        throwable instanceof RuntimeException &&
                                throwable.getMessage().equals("Error fetching product"))
                .verify();

        verify(similarIdsPort, times(1)).getSimilarProductIds(productId);
        verify(productDetailPort, times(1)).getProductById("2");
    }
}
