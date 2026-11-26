package fr.nordlys.shoploc.merchants.api.dto;

import fr.nordlys.shoploc.merchants.domain.model.Product;

/** JSON returned for an article that needs the merchant's attention (US-S04). */
public record StockAlertResponse(
        Long id,
        String name,
        int stock,
        int lowStockThreshold,
        String level
) {

    public static StockAlertResponse from(Product product) {
        String level = product.isOutOfStock() ? "OUT_OF_STOCK" : "LOW_STOCK";
        return new StockAlertResponse(product.id(), product.name(), product.stock(),
                product.lowStockThreshold(), level);
    }
}