package fr.nordlys.shoploc.merchants.infrastructure.persistence.repository;

import fr.nordlys.shoploc.merchants.infrastructure.persistence.entity.ProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductJpaRepository extends JpaRepository<ProductEntity, Long> {

    List<ProductEntity> findByShopIdOrderByIdAsc(Long shopId);
}
