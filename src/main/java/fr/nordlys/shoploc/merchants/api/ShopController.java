package fr.nordlys.shoploc.merchants.api;

import fr.nordlys.shoploc.merchants.domain.Shop;
import fr.nordlys.shoploc.merchants.domain.ShopService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequestMapping("/api/v1/shops")
public class ShopController {

    private final ShopService shopService;

    public ShopController(ShopService shopService) {
        this.shopService = shopService;
    }

    @GetMapping
    public List<Shop> findAll() {
        return shopService.findAll();
    }
}