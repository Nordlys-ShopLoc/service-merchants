package fr.nordlys.shoploc.merchants.domain.service;

import fr.nordlys.shoploc.merchants.domain.model.OpeningHour;
import fr.nordlys.shoploc.merchants.domain.model.Shop;
import fr.nordlys.shoploc.merchants.domain.port.OpeningHourPort;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** US-S10: the merchant sets the opening hours of her approved shop; everybody can read them. */
public class OpeningHoursService {

    static final int MAX_SLOTS_PER_DAY = 2;

    private final OpeningHourPort openingHourPort;
    private final ShopService shopService;

    public OpeningHoursService(OpeningHourPort openingHourPort, ShopService shopService) {
        this.openingHourPort = openingHourPort;
        this.shopService = shopService;
    }

    public List<OpeningHour> findPublic(Long shopId) {
        shopService.requireApprovedShop(shopId);
        return openingHourPort.findByShop(shopId);
    }

    public List<OpeningHour> findMine(Long merchantId, String role) {
        Shop shop = shopService.requireOwnedApprovedShop(merchantId, role);
        return openingHourPort.findByShop(shop.id());
    }

    public List<OpeningHour> replaceMine(Long merchantId, String role, List<OpeningHour> slots) {
        Shop shop = shopService.requireOwnedApprovedShop(merchantId, role);
        validateSlots(slots);

        List<OpeningHour> hours = slots.stream()
                .map(slot -> slot.forShop(shop.id()))
                .toList();
        openingHourPort.replaceForShop(shop.id(), hours);
        return openingHourPort.findByShop(shop.id());
    }

    private void validateSlots(List<OpeningHour> slots) {
        if (slots == null) {
            throw new IllegalArgumentException("Opening hours cannot be null");
        }

        Map<Integer, List<OpeningHour>> slotsByDay = new HashMap<>();
        for (OpeningHour slot : slots) {
            validateSlot(slot);
            slotsByDay.computeIfAbsent(slot.dayOfWeek(), k -> new ArrayList<>()).add(slot);
        }

        for (List<OpeningHour> daySlots : slotsByDay.values()) {
            if (daySlots.size() > MAX_SLOTS_PER_DAY) {
                throw new IllegalArgumentException("Too many slots for a single day (max " + MAX_SLOTS_PER_DAY + ")");
            }
            daySlots.sort(Comparator.comparing(OpeningHour::opensAt));
            for (int i = 1; i < daySlots.size(); i++) {
                if (daySlots.get(i).opensAt().isBefore(daySlots.get(i - 1).closesAt())) {
                    throw new IllegalArgumentException("Overlapping slots on day " + daySlots.get(i).dayOfWeek());
                }
            }
        }
    }

    private void validateSlot(OpeningHour slot) {
        if (slot == null) {
            throw new IllegalArgumentException("Slot cannot be null");
        }
        if (slot.dayOfWeek() < 1 || slot.dayOfWeek() > 7) {
            throw new IllegalArgumentException("Invalid day of week: " + slot.dayOfWeek());
        }
        if (slot.opensAt() == null || slot.closesAt() == null) {
            throw new IllegalArgumentException("Opening and closing times are required");
        }
        if (!slot.opensAt().isBefore(slot.closesAt())) {
            throw new IllegalArgumentException("Closing time must be after opening time");
        }
    }
}