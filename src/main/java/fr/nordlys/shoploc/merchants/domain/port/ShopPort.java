package fr.nordlys.shoploc.merchants.domain.port;

import fr.nordlys.shoploc.merchants.domain.model.Shop;
import java.util.List;

public interface ShopPort {
    List<Shop> findAll();
}