package fr.nordlys.shoploc.merchants.infrastructure.persistence.adapter;

import fr.nordlys.shoploc.merchants.domain.model.Product;
import fr.nordlys.shoploc.merchants.domain.port.ProductPort;
import fr.nordlys.shoploc.merchants.infrastructure.persistence.entity.ProductEntity;
import fr.nordlys.shoploc.merchants.infrastructure.persistence.repository.ProductJpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/** Adapter: binds the articles port to JPA persistence. */
@Repository
public class ProductJpaAdapter implements ProductPort {

    private final ProductJpaRepository jpaRepository;

    public ProductJpaAdapter(ProductJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public List<Product> findByShop(Long shopId) {
        return jpaRepository.findByShopIdOrderByIdAsc(shopId).stream()
                .map(ProductEntity::toDomain)
                .toList();
    }

    @Override
    public Optional<Product> findById(Long id) {
        return jpaRepository.findById(id).map(ProductEntity::toDomain);
    }

    @Override
    public Product save(Product product) {
        return jpaRepository.save(ProductEntity.fromDomain(product)).toDomain();
    }
}
