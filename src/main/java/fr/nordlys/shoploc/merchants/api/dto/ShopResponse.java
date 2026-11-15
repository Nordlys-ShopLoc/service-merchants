package fr.nordlys.shoploc.merchants.api.dto;

import fr.nordlys.shoploc.merchants.domain.model.Shop;

import java.time.LocalDateTime;

/** JSON returned for a shop. */
public record ShopResponse(
        Long id,
        String name,
        String address,
        String category,
        String description,
        String status,
        LocalDateTime createdAt
) {

    public static ShopResponse from(Shop shop) {
        return new ShopResponse(shop.id(), shop.name(), shop.address(), shop.category(),
                shop.description(), shop.status().name(), shop.createdAt());
    }
}
