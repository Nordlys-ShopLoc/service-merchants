package fr.nordlys.shoploc.merchants.infrastructure.persistence.repository;

import fr.nordlys.shoploc.merchants.infrastructure.persistence.entity.OpeningHourEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OpeningHourJpaRepository extends JpaRepository<OpeningHourEntity, Long> {

    List<OpeningHourEntity> findByShopIdOrderByDayOfWeekAscOpensAtAsc(Long shopId);

    void deleteByShopId(Long shopId);
}
