package fr.nordlys.shoploc.merchants.domain.model;

import java.time.LocalTime;

/**
 * An opening slot of a shop (US-S10). A day can have two slots (lunch break);
 * a day without any slot is a closed day.
 *
 * Domain object: no persistence annotation, no framework dependency.
 * dayOfWeek: 1 = Monday ... 7 = Sunday (ISO-8601).
 */
public record OpeningHour(
        Long id,
        Long shopId,
        int dayOfWeek,
        LocalTime opensAt,
        LocalTime closesAt
) {

    public OpeningHour forShop(Long newShopId) {
        return new OpeningHour(null, newShopId, dayOfWeek, opensAt, closesAt);
    }
}
