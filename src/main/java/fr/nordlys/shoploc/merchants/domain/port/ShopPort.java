package fr.nordlys.shoploc.merchants.domain.port;

import fr.nordlys.shoploc.merchants.domain.model.Shop;
import fr.nordlys.shoploc.merchants.domain.model.ShopStatus;
import java.util.List;
import java.util.Optional;

/** Persistence port for shops. Implemented by an adapter (hexagonal). */
public interface ShopPort {

    List<Shop> findByStatus(ShopStatus status);

    Optional<Shop> findById(Long id);

    Shop save(Shop shop);
}
