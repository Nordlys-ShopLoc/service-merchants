package fr.nordlys.shoploc.merchants.domain.service;

import fr.nordlys.shoploc.merchants.domain.exception.OperationNotAllowedException;
import fr.nordlys.shoploc.merchants.domain.model.OpeningHour;
import fr.nordlys.shoploc.merchants.domain.model.Shop;
import fr.nordlys.shoploc.merchants.domain.model.ShopStatus;
import fr.nordlys.shoploc.merchants.domain.port.OpeningHourPort;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class OpeningHoursServiceTest {

    private final OpeningHourPort port = mock(OpeningHourPort.class);
    private final ShopService shopService = mock(ShopService.class);
    private final OpeningHoursService service = new OpeningHoursService(port, shopService);

    private final Shop shop = new Shop(1L, 10L, "Le Fournil", "12 rue Solferino, Lille", "Boulangerie",
            null, ShopStatus.APPROVED, LocalDateTime.now());

    private OpeningHour slot(int day, String open, String close) {
        return new OpeningHour(null, null, day, LocalTime.parse(open), LocalTime.parse(close));
    }

    @Test
    void saves_two_slots_on_the_same_day() {
        when(shopService.requireOwnedApprovedShop(10L, "MERCHANT")).thenReturn(shop);

        service.replaceMine(10L, "MERCHANT", List.of(slot(2, "08:00", "12:30"), slot(2, "14:00", "19:00")));

        verify(port).replaceForShop(eq(1L), anyList());
    }

    @Test
    void refuses_a_slot_closing_before_opening() {
        when(shopService.requireOwnedApprovedShop(10L, "MERCHANT")).thenReturn(shop);

        assertThatThrownBy(() -> service.replaceMine(10L, "MERCHANT", List.of(slot(2, "19:00", "08:00"))))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void refuses_overlapping_slots() {
        when(shopService.requireOwnedApprovedShop(10L, "MERCHANT")).thenReturn(shop);

        assertThatThrownBy(() -> service.replaceMine(10L, "MERCHANT",
                List.of(slot(2, "08:00", "13:00"), slot(2, "12:00", "19:00"))))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void refuses_a_third_slot_on_the_same_day() {
        when(shopService.requireOwnedApprovedShop(10L, "MERCHANT")).thenReturn(shop);

        assertThatThrownBy(() -> service.replaceMine(10L, "MERCHANT",
                List.of(slot(2, "07:00", "09:00"), slot(2, "10:00", "12:00"), slot(2, "14:00", "19:00"))))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void refuses_a_day_outside_valid_range() {
        when(shopService.requireOwnedApprovedShop(10L, "MERCHANT")).thenReturn(shop);

        assertThatThrownBy(() -> service.replaceMine(10L, "MERCHANT", List.of(slot(8, "08:00", "12:00"))))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void refuses_if_shop_is_not_approved() {
        when(shopService.requireOwnedApprovedShop(10L, "MERCHANT"))
                .thenThrow(new OperationNotAllowedException("shop not approved"));

        assertThatThrownBy(() -> service.replaceMine(10L, "MERCHANT", List.of(slot(2, "08:00", "12:00"))))
                .isInstanceOf(OperationNotAllowedException.class);
    }
}
