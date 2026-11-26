package fr.nordlys.shoploc.merchants.domain.model;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

/** US-S04: when does an article need a stock alert. */
class ProductTest {

    private static Product article(int stock, int threshold, boolean available) {
        return new Product(1L, 1L, "Baguette", null, new BigDecimal("1.30"), stock, threshold, available);
    }

    @Test
    void an_article_without_stock_is_out_of_stock() {
        Product product = article(0, 0, true);

        assertThat(product.isOutOfStock()).isTrue();
        assertThat(product.needsStockAlert()).isTrue();
    }

    @Test
    void an_article_at_its_threshold_is_low_stock() {
        Product product = article(3, 3, true);

        assertThat(product.isLowStock()).isTrue();
        assertThat(product.needsStockAlert()).isTrue();
    }

    @Test
    void an_article_above_its_threshold_needs_no_alert() {
        assertThat(article(4, 3, true).needsStockAlert()).isFalse();
    }

    @Test
    void the_default_threshold_only_warns_when_the_stock_is_empty() {
        assertThat(article(1, 0, true).needsStockAlert()).isFalse();
        assertThat(article(0, 0, true).needsStockAlert()).isTrue();
    }

    @Test
    void a_withdrawn_article_needs_no_alert() {
        assertThat(article(0, 5, false).needsStockAlert()).isFalse();
    }
}