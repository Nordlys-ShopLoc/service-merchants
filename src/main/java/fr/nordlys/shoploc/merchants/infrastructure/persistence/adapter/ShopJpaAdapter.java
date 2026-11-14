package fr.nordlys.shoploc.merchants.infrastructure.persistence.adapter;

import fr.nordlys.shoploc.merchants.domain.model.Shop;
import fr.nordlys.shoploc.merchants.domain.model.ShopStatus;
import fr.nordlys.shoploc.merchants.domain.port.ShopPort;
import fr.nordlys.shoploc.merchants.infrastructure.persistence.entity.ShopEntity;
import fr.nordlys.shoploc.merchants.infrastructure.persistence.repository.ShopJpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/** Adapter: binds the domain port to JPA persistence. */
@Repository
public class ShopJpaAdapter implements ShopPort {

    private final ShopJpaRepository jpaRepository;

    public ShopJpaAdapter(ShopJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public List<Shop> findByStatus(ShopStatus status) {
        return jpaRepository.findByStatusOrderByCreatedAtAsc(status).stream()
                .map(ShopEntity::toDomain)
                .toList();
    }

    @Override
    public Shop save(Shop shop) {
        return jpaRepository.save(ShopEntity.fromDomain(shop)).toDomain();
    }
}
