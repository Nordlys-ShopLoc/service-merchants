package fr.nordlys.shoploc.merchants.domain.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class ShopTest {

    private Shop shop(ShopStatus status) {
        return new Shop(1L, 10L, "Le Fournil", "12 rue Solferino, Lille", "Boulangerie",
                "Pain au levain", status, LocalDateTime.now());
    }

    @Test
    void only_a_pending_shop_is_pending() {
        assertThat(shop(ShopStatus.PENDING).isPending()).isTrue();
        assertThat(shop(ShopStatus.APPROVED).isPending()).isFalse();
        assertThat(shop(ShopStatus.REJECTED).isPending()).isFalse();
    }

    @Test
    void changes_the_status_and_keeps_everything_else() {
        Shop approved = shop(ShopStatus.PENDING).withStatus(ShopStatus.APPROVED);

        assertThat(approved.status()).isEqualTo(ShopStatus.APPROVED);
        assertThat(approved.id()).isEqualTo(1L);
        assertThat(approved.name()).isEqualTo("Le Fournil");
        assertThat(approved.ownerAccountId()).isEqualTo(10L);
    }
}
