package fr.nordlys.shoploc.merchants.domain.model;

import java.time.LocalDateTime;

/**
 * A shop of the partner program.
 *
 * Domain object: no persistence annotation, no framework dependency.
 * ownerAccountId is the id of the merchant's account in service-accounts.
 */
public record Shop(
        Long id,
        Long ownerAccountId,
        String name,
        String address,
        String category,
        String description,
        ShopStatus status,
        LocalDateTime createdAt
) {

    public boolean isPending() {
        return status == ShopStatus.PENDING;
    }

    /** Same shop with another status: used when the administrator decides (US-A01). */
    public Shop withStatus(ShopStatus newStatus) {
        return new Shop(id, ownerAccountId, name, address, category, description, newStatus, createdAt);
    }
}
