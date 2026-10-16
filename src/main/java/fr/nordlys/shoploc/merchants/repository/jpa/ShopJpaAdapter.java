package fr.nordlys.shoploc.merchants.repository.jpa;

import fr.nordlys.shoploc.merchants.domain.Shop;
import fr.nordlys.shoploc.merchants.domain.ShopPort;
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