package fr.nordlys.shoploc.merchants.infrastructure.persistence.repository;

import fr.nordlys.shoploc.merchants.infrastructure.persistence.entity.ShopEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ShopJpaRepository extends JpaRepository<ShopEntity, Long> {
}