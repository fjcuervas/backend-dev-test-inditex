package com.inditex.product.infraestructure.adapter.input.similarproducts.mapper;

import com.inditex.product.api.generated.model.ProductDetail;
import com.inditex.product.domain.model.Product;
import com.inditex.product.infraestructure.similarproducts.adapter.input.mapper.ProductMapper;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class ProductMapperTest {

    @Test
    void shouldMapProductToProductDetail() {
        Product domainProduct = new Product(
                "2",
                "T-Shirt",
                BigDecimal.valueOf(20.55),
                true
        );

        ProductDetail apiModel = ProductMapper.toApiModel(domainProduct);

        assertNotNull(apiModel);
        assertEquals("2", apiModel.getId());
        assertEquals("T-Shirt", apiModel.getName());
        assertEquals(BigDecimal.valueOf(20.55), apiModel.getPrice());
        assertTrue(apiModel.getAvailability());
    }

    @Test
    void shouldMapAvailabilityFalse() {
        Product domainProduct = new Product(
                "3",
                "Jeans",
                BigDecimal.valueOf(39.99),
                false
        );

        ProductDetail apiModel = ProductMapper.toApiModel(domainProduct);

        assertFalse(apiModel.getAvailability());
    }
}