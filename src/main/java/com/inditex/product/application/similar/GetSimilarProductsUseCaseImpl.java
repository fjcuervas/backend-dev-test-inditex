package com.inditex.product.application.similar;

import com.inditex.product.application.similar.port.input.GetSimilarProductsUseCase;
import com.inditex.product.application.similar.port.output.ProductDetailPort;
import com.inditex.product.application.similar.port.output.SimilarProductIdsPort;
import com.inditex.product.domain.model.Product;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Service
public class GetSimilarProductsUseCaseImpl implements GetSimilarProductsUseCase {

    private final SimilarProductIdsPort similarIdsPort;
    private final ProductDetailPort productDetailPort;

    public GetSimilarProductsUseCaseImpl(
            SimilarProductIdsPort similarIdsPort,
            ProductDetailPort productDetailPort) {
        this.similarIdsPort = similarIdsPort;
        this.productDetailPort = productDetailPort;
    }

    @Override
    public Flux<@NonNull Product> getSimilarProducts(String productId) {
        return similarIdsPort.getSimilarProductIds(productId)
                .flatMap(productDetailPort::getProductById);
    }

}
