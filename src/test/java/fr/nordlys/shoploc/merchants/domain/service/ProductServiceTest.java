package fr.nordlys.shoploc.merchants.domain.service;

import fr.nordlys.shoploc.merchants.domain.exception.OperationNotAllowedException;
import fr.nordlys.shoploc.merchants.domain.exception.ProductNotFoundException;
import fr.nordlys.shoploc.merchants.domain.model.Product;
import fr.nordlys.shoploc.merchants.domain.model.Shop;
import fr.nordlys.shoploc.merchants.domain.model.ShopStatus;
import fr.nordlys.shoploc.merchants.domain.port.ProductPort;
import fr.nordlys.shoploc.merchants.domain.port.ShopPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
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

/** US-S03: the merchant manages the articles of her shop. */
class ProductServiceTest {

    private static final Long MERCHANT_ID = 10L;
    private static final Long SHOP_ID = 1L;

    private final ShopPort shopPort = mock(ShopPort.class);
    private final ProductPort productPort = mock(ProductPort.class);
    private final ProductService service = new ProductService(productPort, new ShopService(shopPort));

    @BeforeEach
    void the_merchant_owns_an_approved_shop() {
        givenShop(ShopStatus.APPROVED);
        // save gives back the article it receives
        when(productPort.save(any(Product.class))).thenAnswer(call -> call.getArgument(0));
    }

    private void givenShop(ShopStatus status) {
        when(shopPort.findLatestByOwner(MERCHANT_ID)).thenReturn(Optional.of(new Shop(SHOP_ID, MERCHANT_ID,
                "Le Fournil", "12 rue Solferino, Lille", "Boulangerie", null, status, LocalDateTime.now())));
    }

    private static Product baguette(Long shopId) {
        return new Product(5L, shopId, "Baguette tradition", null, new BigDecimal("1.30"), 40, 10, true);
    }

    @Test
    void adds_an_article_with_a_stock_of_zero_by_default() {
        Product product = service.add(MERCHANT_ID, "MERCHANT", "  Pain de seigle ", " ", new BigDecimal("2.50"), null, null);

        assertThat(product.shopId()).isEqualTo(SHOP_ID);
        assertThat(product.name()).isEqualTo("Pain de seigle");
        assertThat(product.description()).isNull();
        assertThat(product.stock()).isZero();
        assertThat(product.available()).isTrue();
    }

    @Test
    void refuses_a_price_of_zero() {
        assertThatThrownBy(() -> service.add(MERCHANT_ID, "MERCHANT", "Pain", null, BigDecimal.ZERO, 5, null))
                .isInstanceOf(IllegalArgumentException.class);
        verify(productPort, never()).save(any());
    }

    @Test
    void refuses_a_negative_price() {
        assertThatThrownBy(() -> service.add(MERCHANT_ID, "MERCHANT", "Pain", null, new BigDecimal("-1.00"), 5, null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void refuses_a_negative_stock() {
        assertThatThrownBy(() -> service.add(MERCHANT_ID, "MERCHANT", "Pain", null, new BigDecimal("2.00"), -1, null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void refuses_an_article_without_name() {
        assertThatThrownBy(() -> service.add(MERCHANT_ID, "MERCHANT", " ", null, new BigDecimal("2.00"), 5, null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void lists_the_articles_of_the_merchant_shop() {
        when(productPort.findByShop(SHOP_ID)).thenReturn(List.of(baguette(SHOP_ID)));

        assertThat(service.findMine(MERCHANT_ID, "MERCHANT")).extracting(Product::name)
                .containsExactly("Baguette tradition");
    }

    @Test
    void updates_an_article() {
        when(productPort.findById(5L)).thenReturn(Optional.of(baguette(SHOP_ID)));

        Product updated = service.update(MERCHANT_ID, "MERCHANT", 5L, "Baguette", "Farine locale",
                new BigDecimal("1.40"), 25, null);

        assertThat(updated.name()).isEqualTo("Baguette");
        assertThat(updated.description()).isEqualTo("Farine locale");
        assertThat(updated.price()).isEqualByComparingTo("1.40");
        assertThat(updated.stock()).isEqualTo(25);
        assertThat(updated.lowStockThreshold()).isEqualTo(10);
    }

    @Test
    void keeps_the_stock_when_it_is_not_sent() {
        when(productPort.findById(5L)).thenReturn(Optional.of(baguette(SHOP_ID)));

        Product updated = service.update(MERCHANT_ID, "MERCHANT", 5L, "Baguette", null, new BigDecimal("1.40"), null, null);

        assertThat(updated.stock()).isEqualTo(40);
    }

    @Test
    void withdraws_an_article_without_deleting_it() {
        when(productPort.findById(5L)).thenReturn(Optional.of(baguette(SHOP_ID)));

        Product withdrawn = service.withdraw(MERCHANT_ID, "MERCHANT", 5L);

        assertThat(withdrawn.available()).isFalse();
        assertThat(withdrawn.name()).isEqualTo("Baguette tradition");
        verify(productPort).save(withdrawn);
    }

    @Test
    void hides_the_articles_of_another_shop() {
        when(productPort.findById(5L)).thenReturn(Optional.of(baguette(99L)));

        assertThatThrownBy(() -> service.withdraw(MERCHANT_ID, "MERCHANT", 5L))
                .isInstanceOf(ProductNotFoundException.class);
        verify(productPort, never()).save(any());
    }

    @Test
    void tells_when_the_article_does_not_exist() {
        when(productPort.findById(42L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.update(MERCHANT_ID, "MERCHANT", 42L, "Pain", null, new BigDecimal("2.00"), 5, null))
                .isInstanceOf(ProductNotFoundException.class);
    }

    @Test
    void refuses_a_shop_waiting_for_approval() {
        givenShop(ShopStatus.PENDING);

        assertThatThrownBy(() -> service.add(MERCHANT_ID, "MERCHANT", "Pain", null, new BigDecimal("2.00"), 5, null))
                .isInstanceOf(OperationNotAllowedException.class);
    }

    @Test
    void refuses_a_user_who_is_not_a_merchant() {
        assertThatThrownBy(() -> service.findMine(7L, "CLIENT"))
                .isInstanceOf(OperationNotAllowedException.class);
        verify(productPort, never()).findByShop(any());
    }

    // ---------- US-S04 ----------

    @Test
    void lists_the_articles_to_restock_most_urgent_first() {
        when(productPort.findByShop(SHOP_ID)).thenReturn(List.of(
                new Product(1L, SHOP_ID, "Pain de campagne", null, new BigDecimal("3.20"), 15, 5, true),
                new Product(2L, SHOP_ID, "Croissant", null, new BigDecimal("1.10"), 4, 10, true),
                new Product(3L, SHOP_ID, "Brioche", null, new BigDecimal("4.00"), 0, 0, true),
                new Product(4L, SHOP_ID, "Galette", null, new BigDecimal("6.00"), 0, 0, false)));

        assertThat(service.findStockAlerts(MERCHANT_ID, "MERCHANT"))
                .extracting(Product::name)
                .containsExactly("Brioche", "Croissant");
    }

    @Test
    void refuses_the_stock_alerts_to_a_client() {
        assertThatThrownBy(() -> service.findStockAlerts(MERCHANT_ID, "CLIENT"))
                .isInstanceOf(OperationNotAllowedException.class);
    }

    @Test
    void refuses_the_stock_alerts_of_a_shop_not_yet_approved() {
        givenShop(ShopStatus.PENDING);

        assertThatThrownBy(() -> service.findStockAlerts(MERCHANT_ID, "MERCHANT"))
                .isInstanceOf(OperationNotAllowedException.class);
    }

    @Test
    void adds_an_article_with_an_alert_threshold() {
        Product product = service.add(MERCHANT_ID, "MERCHANT", "Croissant", null, new BigDecimal("1.10"), 30, 10);

        assertThat(product.lowStockThreshold()).isEqualTo(10);
    }

    @Test
    void changes_the_alert_threshold_and_keeps_the_stock() {
        when(productPort.findById(5L)).thenReturn(Optional.of(baguette(SHOP_ID)));

        Product updated = service.update(MERCHANT_ID, "MERCHANT", 5L, "Baguette tradition", null,
                new BigDecimal("1.30"), null, 15);

        assertThat(updated.lowStockThreshold()).isEqualTo(15);
        assertThat(updated.stock()).isEqualTo(40);
    }

    @Test
    void refuses_a_negative_alert_threshold() {
        assertThatThrownBy(() -> service.add(MERCHANT_ID, "MERCHANT", "Pain", null, new BigDecimal("2.00"), 5, -1))
                .isInstanceOf(IllegalArgumentException.class);
        verify(productPort, never()).save(any());
    }
}
