package fr.nordlys.shoploc.merchants.api.dto;

import fr.nordlys.shoploc.merchants.domain.model.Product;

import java.math.BigDecimal;

/** JSON returned for an article. */
public record ProductResponse(
        Long id,
        String name,
        String description,
        BigDecimal price,
        int stock,
        int lowStockThreshold,
        boolean available
) {

    public static ProductResponse from(Product product) {
        return new ProductResponse(product.id(), product.name(), product.description(), product.price(),
                product.stock(), product.lowStockThreshold(), product.available());
    }
}
