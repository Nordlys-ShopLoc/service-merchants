package fr.nordlys.shoploc.merchants.infrastructure.persistence.entity;

import fr.nordlys.shoploc.merchants.domain.model.Shop;
import fr.nordlys.shoploc.merchants.domain.model.ShopStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "shops")
@Getter
@Setter
@NoArgsConstructor
public class ShopEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Merchant's account in service-accounts. Null for the shops created by V1. */
    @Column(name = "owner_account_id")
    private Long ownerAccountId;

    @Column(nullable = false, length = 120)
    private String name;

    @Column(nullable = false, length = 255)
    private String address;

    @Column(nullable = false, length = 60)
    private String category;

    @Column(length = 1000)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ShopStatus status;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    void onCreate() {
        if (createdAt == null) createdAt = LocalDateTime.now();
        if (status == null) status = ShopStatus.PENDING;
    }

    public Shop toDomain() {
        return new Shop(id, ownerAccountId, name, address, category, description, status, createdAt);
    }

    public static ShopEntity fromDomain(Shop shop) {
        ShopEntity entity = new ShopEntity();
        entity.id = shop.id();
        entity.ownerAccountId = shop.ownerAccountId();
        entity.name = shop.name();
        entity.address = shop.address();
        entity.category = shop.category();
        entity.description = shop.description();
        entity.status = shop.status();
        entity.createdAt = shop.createdAt();
        return entity;
    }
}
