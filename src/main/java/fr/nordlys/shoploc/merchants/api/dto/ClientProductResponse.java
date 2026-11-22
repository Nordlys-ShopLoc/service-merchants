package fr.nordlys.shoploc.merchants.api.dto;

import fr.nordlys.shoploc.merchants.domain.model.Product;
import java.math.BigDecimal;

public record ClientProductResponse(
        Long id,
        String name,
        String description,
        BigDecimal price,
        boolean outOfStock
) {
    public static ClientProductResponse from(Product product) {
        return new ClientProductResponse(
                product.id(),
                product.name(),
                product.description(),
                product.price(),
                product.stock() == 0
        );
    }
}
