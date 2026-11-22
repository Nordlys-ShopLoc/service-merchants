package fr.nordlys.shoploc.merchants.domain.service;

import fr.nordlys.shoploc.merchants.domain.exception.ShopNotFoundException;
import fr.nordlys.shoploc.merchants.domain.model.Product;
import fr.nordlys.shoploc.merchants.domain.model.Shop;
import fr.nordlys.shoploc.merchants.domain.model.ShopStatus;
import fr.nordlys.shoploc.merchants.domain.port.ProductPort;
import fr.nordlys.shoploc.merchants.domain.port.ShopPort;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

class ClientProductServiceTest {

    private final ShopPort shopPort = mock(ShopPort.class);
    private final ProductPort productPort = mock(ProductPort.class);
    private final ProductService service = new ProductService(productPort, new ShopService(shopPort));

    private Shop shop(ShopStatus status) {
        return new Shop(1L, 10L, "Le Fournil", "Lille", "Boulangerie",
                null, status, LocalDateTime.now());
    }

    private Product product(Long id, int stock, boolean available) {
        return new Product(id, 1L, "Baguette", "Pain frais",
                new BigDecimal("1.50"), stock, 5, available);
    }

    @Test
    void shows_only_articles_available_for_sale() {
        when(shopPort.findById(1L)).thenReturn(Optional.of(shop(ShopStatus.APPROVED)));
        when(productPort.findByShop(1L)).thenReturn(List.of(
                product(1L, 10, true),
                product(2L, 5, false)
        ));

        List<Product> products = service.findPublicByShop(1L);

        assertThat(products).extracting(Product::id).containsExactly(1L);
    }

    @Test
    void keeps_out_of_stock_articles_visible() {
        when(shopPort.findById(1L)).thenReturn(Optional.of(shop(ShopStatus.APPROVED)));
        when(productPort.findByShop(1L)).thenReturn(List.of(product(1L, 0, true)));

        List<Product> products = service.findPublicByShop(1L);

        assertThat(products).hasSize(1);
        assertThat(products.get(0).stock()).isZero();
    }

    @Test
    void refuses_a_shop_not_approved() {
        when(shopPort.findById(1L)).thenReturn(Optional.of(shop(ShopStatus.PENDING)));

        assertThatThrownBy(() -> service.findPublicByShop(1L))
                .isInstanceOf(ShopNotFoundException.class);

        verify(productPort, never()).findByShop(1L);
    }

    @Test
    void returns_empty_list_when_shop_has_no_articles() {
        when(shopPort.findById(1L)).thenReturn(Optional.of(shop(ShopStatus.APPROVED)));
        when(productPort.findByShop(1L)).thenReturn(List.of());

        assertThat(service.findPublicByShop(1L)).isEmpty();
    }
}
