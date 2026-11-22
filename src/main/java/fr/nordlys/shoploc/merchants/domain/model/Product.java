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

    /** Same article with the details changed by the merchant. */
    public Product withDetails(String newName, String newDescription, BigDecimal newPrice, int newStock) {
        return new Product(id, shopId, newName, newDescription, newPrice, newStock, lowStockThreshold, available);
    }

    /** Same article, no longer offered to clients. */
    public Product withdrawn() {
        return new Product(id, shopId, name, description, price, stock, lowStockThreshold, false);
    }
}
