package fr.nordlys.shoploc.merchants.domain.service;

import fr.nordlys.shoploc.merchants.domain.model.Shop;
import fr.nordlys.shoploc.merchants.domain.port.ShopPort;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ShopService {

    private final ShopPort shopPort;

    public ShopService(ShopPort shopPort) {
        this.shopPort = shopPort;
    }

    public List<Shop> findAll() {
        return shopPort.findAll();
    }
}