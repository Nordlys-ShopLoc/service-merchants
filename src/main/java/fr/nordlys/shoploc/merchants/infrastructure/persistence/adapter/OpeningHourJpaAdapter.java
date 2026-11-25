package fr.nordlys.shoploc.merchants.infrastructure.persistence.adapter;

import fr.nordlys.shoploc.merchants.domain.model.OpeningHour;
import fr.nordlys.shoploc.merchants.domain.port.OpeningHourPort;
import fr.nordlys.shoploc.merchants.infrastructure.persistence.entity.OpeningHourEntity;
import fr.nordlys.shoploc.merchants.infrastructure.persistence.repository.OpeningHourJpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/** Adapter: binds the opening hours port to JPA persistence. */
@Repository
public class OpeningHourJpaAdapter implements OpeningHourPort {

    private final OpeningHourJpaRepository jpaRepository;

    public OpeningHourJpaAdapter(OpeningHourJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public List<OpeningHour> findByShop(Long shopId) {
        return jpaRepository.findByShopIdOrderByDayOfWeekAscOpensAtAsc(shopId).stream()
                .map(OpeningHourEntity::toDomain)
                .toList();
    }

    @Override
    @Transactional
    public void replaceForShop(Long shopId, List<OpeningHour> hours) {
        jpaRepository.deleteByShopId(shopId);
        jpaRepository.flush();
        jpaRepository.saveAll(hours.stream().map(OpeningHourEntity::fromDomain).toList());
    }
}
