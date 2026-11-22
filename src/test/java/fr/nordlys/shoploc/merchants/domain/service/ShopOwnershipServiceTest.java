package fr.nordlys.shoploc.merchants.domain.service;

import fr.nordlys.shoploc.merchants.domain.exception.MerchantHasNoShopException;
import fr.nordlys.shoploc.merchants.domain.exception.OperationNotAllowedException;
import fr.nordlys.shoploc.merchants.domain.model.Shop;
import fr.nordlys.shoploc.merchants.domain.model.ShopStatus;
import fr.nordlys.shoploc.merchants.domain.port.ShopPort;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/** Socle M2: the merchant's own shop, and the check before changing it (US-S03, S10, S15). */
class ShopOwnershipServiceTest {

    private static final Long MERCHANT_ID = 10L;

    private final ShopPort shopPort = mock(ShopPort.class);
    private final ShopService service = new ShopService(shopPort);

    private static Shop shop(ShopStatus status) {
        return new Shop(1L, MERCHANT_ID, "Le Fournil", "12 rue Solferino, Lille", "Boulangerie",
                null, status, LocalDateTime.now());
    }

    @Test
    void finds_the_shop_of_the_merchant_whatever_its_status() {
        when(shopPort.findLatestByOwner(MERCHANT_ID)).thenReturn(Optional.of(shop(ShopStatus.PENDING)));

        assertThat(service.findMine(MERCHANT_ID, "MERCHANT").status()).isEqualTo(ShopStatus.PENDING);
    }

    @Test
    void refuses_a_user_who_is_not_a_merchant() {
        assertThatThrownBy(() -> service.findMine(MERCHANT_ID, "CLIENT"))
                .isInstanceOf(OperationNotAllowedException.class);
        verify(shopPort, never()).findLatestByOwner(any());
    }

    @Test
    void refuses_a_call_without_identity() {
        assertThatThrownBy(() -> service.findMine(null, null))
                .isInstanceOf(OperationNotAllowedException.class);
    }

    @Test
    void tells_when_the_merchant_has_no_shop() {
        when(shopPort.findLatestByOwner(MERCHANT_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.findMine(MERCHANT_ID, "MERCHANT"))
                .isInstanceOf(MerchantHasNoShopException.class);
    }

    @Test
    void gives_the_approved_shop_to_its_owner() {
        when(shopPort.findLatestByOwner(MERCHANT_ID)).thenReturn(Optional.of(shop(ShopStatus.APPROVED)));

        assertThat(service.requireOwnedApprovedShop(MERCHANT_ID, "MERCHANT").id()).isEqualTo(1L);
    }

    @Test
    void refuses_changes_while_the_shop_waits_for_approval() {
        when(shopPort.findLatestByOwner(MERCHANT_ID)).thenReturn(Optional.of(shop(ShopStatus.PENDING)));

        assertThatThrownBy(() -> service.requireOwnedApprovedShop(MERCHANT_ID, "MERCHANT"))
                .isInstanceOf(OperationNotAllowedException.class);
    }

    @Test
    void refuses_changes_to_a_rejected_shop() {
        when(shopPort.findLatestByOwner(MERCHANT_ID)).thenReturn(Optional.of(shop(ShopStatus.REJECTED)));

        assertThatThrownBy(() -> service.requireOwnedApprovedShop(MERCHANT_ID, "MERCHANT"))
                .isInstanceOf(OperationNotAllowedException.class);
    }
}
