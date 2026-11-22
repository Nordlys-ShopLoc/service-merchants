package fr.nordlys.shoploc.merchants.infrastructure.persistence.entity;

import fr.nordlys.shoploc.merchants.domain.model.Product;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

/** Table products, created by migration V3 (socle M2). */
@Entity
@Table(name = "products")
@Getter
@Setter
@NoArgsConstructor
public class ProductEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "shop_id", nullable = false)
    private Long shopId;

    @Column(nullable = false, length = 120)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false, precision = 8, scale = 2)
    private BigDecimal price;

    @Column(nullable = false)
    private int stock;

    @Column(name = "low_stock_threshold", nullable = false)
    private int lowStockThreshold;

    @Column(nullable = false)
    private boolean available;

    public Product toDomain() {
        return new Product(id, shopId, name, description, price, stock, lowStockThreshold, available);
    }

    public static ProductEntity fromDomain(Product product) {
        ProductEntity entity = new ProductEntity();
        entity.id = product.id();
        entity.shopId = product.shopId();
        entity.name = product.name();
        entity.description = product.description();
        entity.price = product.price();
        entity.stock = product.stock();
        entity.lowStockThreshold = product.lowStockThreshold();
        entity.available = product.available();
        return entity;
    }
}
