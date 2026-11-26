package fr.nordlys.shoploc.merchants.domain.model;

import java.math.BigDecimal;

/**
 * An article sold in click and collect by a shop (US-S03).
 *
 * Domain object: no persistence annotation, no framework dependency.
 * A withdrawn article is kept (available = false) so that past orders still point to it.
 */
public record Product(
        Long id,
        Long shopId,
        String name,
        String description,
        BigDecimal price,
        int stock,
        int lowStockThreshold,
        boolean available
) {

    public boolean belongsTo(Shop shop) {
        return shopId.equals(shop.id());
    }

    /** Same article with the details changed by the merchant (US-S03, alert threshold US-S04). */
    public Product withDetails(String newName, String newDescription, BigDecimal newPrice, int newStock,
                               int newLowStockThreshold) {
        return new Product(id, shopId, newName, newDescription, newPrice, newStock, newLowStockThreshold, available);
    }
    /** Same article, no longer offered to clients. */
    public Product withdrawn() {
        return new Product(id, shopId, name, description, price, stock, lowStockThreshold, false);
    }

    /** US-S04: no article left. */
    public boolean isOutOfStock() {
        return stock == 0;
    }

    /** US-S04: still in stock, but at or under the alert threshold chosen by the merchant. */
    public boolean isLowStock() {
        return stock > 0 && stock <= lowStockThreshold;
    }

    /** US-S04: the merchant is warned only for the articles still offered to clients. */
    public boolean needsStockAlert() {
        return available && (isOutOfStock() || isLowStock());
    }
}
