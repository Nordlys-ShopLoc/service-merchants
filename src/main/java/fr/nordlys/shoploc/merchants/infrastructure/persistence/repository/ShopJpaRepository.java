package fr.nordlys.shoploc.merchants.infrastructure.persistence.repository;

import fr.nordlys.shoploc.merchants.domain.model.ShopStatus;
import fr.nordlys.shoploc.merchants.infrastructure.persistence.entity.ShopEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ShopJpaRepository extends JpaRepository<ShopEntity, Long> {

    List<ShopEntity> findByStatusOrderByCreatedAtAsc(ShopStatus status);
}
