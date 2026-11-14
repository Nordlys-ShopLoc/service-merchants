package fr.nordlys.shoploc.merchants.domain.port;

import fr.nordlys.shoploc.merchants.domain.model.Shop;
import fr.nordlys.shoploc.merchants.domain.model.ShopStatus;
import java.util.List;

/** Persistence port for shops. Implemented by an adapter (hexagonal). */
public interface ShopPort {

    List<Shop> findByStatus(ShopStatus status);

    Shop save(Shop shop);
}
