package fr.nordlys.shoploc.merchants.domain.service;

import fr.nordlys.shoploc.merchants.domain.exception.OperationNotAllowedException;
import fr.nordlys.shoploc.merchants.domain.exception.ShopAlreadyReviewedException;
import fr.nordlys.shoploc.merchants.domain.exception.ShopNotFoundException;
import fr.nordlys.shoploc.merchants.domain.model.Shop;
import fr.nordlys.shoploc.merchants.domain.model.ShopStatus;
import fr.nordlys.shoploc.merchants.domain.port.ShopPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/** US-S13 (registration) and US-A01 (approval by the administrator). */
class ShopServiceTest {

    private static final Long MERCHANT_ID = 10L;

    private final ShopPort shopPort = mock(ShopPort.class);
    private final ShopService service = new ShopService(shopPort);

    @BeforeEach
    void setUp() {
        // The fake database gives back the shop it receives
        when(shopPort.save(any(Shop.class))).thenAnswer(invocation -> invocation.getArgument(0));
    }

    private static Shop shop(Long id, ShopStatus status) {
        return new Shop(id, MERCHANT_ID, "Le Fournil", "12 rue Solferino, Lille", "Boulangerie",
                null, status, LocalDateTime.now());
    }

    /** Gets the shop that the service gave to shopPort.save(). */
    private Shop savedShop() {
        ArgumentCaptor<Shop> captor = ArgumentCaptor.forClass(Shop.class);
        verify(shopPort).save(captor.capture());
        return captor.getValue();
    }

    // ---------- Partner list ----------

    @Test
    void shows_only_approved_shops_to_clients() {
        when(shopPort.findByStatus(ShopStatus.APPROVED)).thenReturn(List.of(shop(1L, ShopStatus.APPROVED)));

        assertThat(service.findPartners()).hasSize(1);
        verify(shopPort).findByStatus(ShopStatus.APPROVED);
    }

    // ---------- US-S13 ----------

    @Test
    void registers_a_shop_waiting_for_the_administrator() {
        service.register(MERCHANT_ID, "MERCHANT", "Le Fournil", "12 rue Solferino, Lille", "Boulangerie", "Pain au levain");

        Shop shop = savedShop();
        assertThat(shop.status()).isEqualTo(ShopStatus.PENDING);
        assertThat(shop.ownerAccountId()).isEqualTo(MERCHANT_ID);
        assertThat(shop.name()).isEqualTo("Le Fournil");
        assertThat(shop.description()).isEqualTo("Pain au levain");
    }

    @Test
    void removes_the_spaces_around_the_fields() {
        service.register(MERCHANT_ID, "MERCHANT", "  Le Fournil ", " 12 rue Solferino ", " Boulangerie ", "   ");

        Shop shop = savedShop();
        assertThat(shop.name()).isEqualTo("Le Fournil");
        assertThat(shop.address()).isEqualTo("12 rue Solferino");
        assertThat(shop.category()).isEqualTo("Boulangerie");
        assertThat(shop.description()).isNull();
    }

    @Test
    void requires_a_name_an_address_and_a_category() {
        assertThatThrownBy(() -> service.register(MERCHANT_ID, "MERCHANT", " ", "12 rue Solferino", "Boulangerie", null))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> service.register(MERCHANT_ID, "MERCHANT", "Le Fournil", null, "Boulangerie", null))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> service.register(MERCHANT_ID, "MERCHANT", "Le Fournil", "12 rue Solferino", "", null))
                .isInstanceOf(IllegalArgumentException.class);
        verify(shopPort, never()).save(any());
    }

    @Test
    void only_a_merchant_can_register_a_shop() {
        assertThatThrownBy(() -> service.register(MERCHANT_ID, "CLIENT", "Le Fournil", "12 rue Solferino", "Boulangerie", null))
                .isInstanceOf(OperationNotAllowedException.class);
        assertThatThrownBy(() -> service.register(null, "MERCHANT", "Le Fournil", "12 rue Solferino", "Boulangerie", null))
                .isInstanceOf(OperationNotAllowedException.class);
        verify(shopPort, never()).save(any());
    }

    // ---------- US-A01 ----------

    @Test
    void lists_the_shops_waiting_for_a_decision() {
        when(shopPort.findByStatus(ShopStatus.PENDING)).thenReturn(List.of(shop(1L, ShopStatus.PENDING)));

        assertThat(service.findPending("ADMIN")).extracting(Shop::id).containsExactly(1L);
    }

    @Test
    void approves_a_pending_shop() {
        when(shopPort.findById(1L)).thenReturn(Optional.of(shop(1L, ShopStatus.PENDING)));

        Shop shop = service.approve(1L, "ADMIN");

        assertThat(shop.status()).isEqualTo(ShopStatus.APPROVED);
        assertThat(savedShop().status()).isEqualTo(ShopStatus.APPROVED);
    }

    @Test
    void rejects_a_pending_shop() {
        when(shopPort.findById(1L)).thenReturn(Optional.of(shop(1L, ShopStatus.PENDING)));

        assertThat(service.reject(1L, "ADMIN").status()).isEqualTo(ShopStatus.REJECTED);
    }

    @Test
    void refuses_to_review_a_shop_twice() {
        when(shopPort.findById(1L)).thenReturn(Optional.of(shop(1L, ShopStatus.APPROVED)));

        assertThatThrownBy(() -> service.reject(1L, "ADMIN"))
                .isInstanceOf(ShopAlreadyReviewedException.class);
        verify(shopPort, never()).save(any());
    }

    @Test
    void refuses_an_unknown_shop() {
        // Nothing is configured for id 99: the fake database returns an empty Optional
        assertThatThrownBy(() -> service.approve(99L, "ADMIN"))
                .isInstanceOf(ShopNotFoundException.class);
    }

    @Test
    void only_the_administrator_can_review_shops() {
        when(shopPort.findById(1L)).thenReturn(Optional.of(shop(1L, ShopStatus.PENDING)));

        assertThatThrownBy(() -> service.findPending("MERCHANT"))
                .isInstanceOf(OperationNotAllowedException.class);
        assertThatThrownBy(() -> service.approve(1L, "MERCHANT"))
                .isInstanceOf(OperationNotAllowedException.class);
        assertThatThrownBy(() -> service.reject(1L, "CLIENT"))
                .isInstanceOf(OperationNotAllowedException.class);
        verify(shopPort, never()).save(any());
    }
}
