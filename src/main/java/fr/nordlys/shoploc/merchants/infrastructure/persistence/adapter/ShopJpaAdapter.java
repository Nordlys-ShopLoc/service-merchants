package fr.nordlys.shoploc.merchants.infrastructure.persistence.adapter;

import fr.nordlys.shoploc.merchants.infrastructure.persistence.entity.ShopEntity;
import fr.nordlys.shoploc.merchants.infrastructure.persistence.repository.ShopJpaRepository;
import fr.nordlys.shoploc.merchants.domain.model.Shop;
import fr.nordlys.shoploc.merchants.domain.port.ShopPort;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public class ShopJpaAdapter implements ShopPort {

    private final ShopJpaRepository jpaRepository;

    public ShopJpaAdapter(ShopJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public List<Shop> findAll() {
        return jpaRepository.findAll().stream()
                .map(ShopEntity::toDomain)
                .toList();
    }
}