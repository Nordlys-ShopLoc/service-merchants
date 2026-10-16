package fr.nordlys.shoploc.merchants.domain;

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