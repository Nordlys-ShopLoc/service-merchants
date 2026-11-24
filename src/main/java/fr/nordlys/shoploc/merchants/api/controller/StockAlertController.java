package fr.nordlys.shoploc.merchants.api.controller;

import fr.nordlys.shoploc.merchants.api.dto.StockAlertResponse;
import fr.nordlys.shoploc.merchants.domain.service.ProductService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static fr.nordlys.shoploc.merchants.api.controller.ShopController.USER_ID_HEADER;
import static fr.nordlys.shoploc.merchants.api.controller.ShopController.USER_ROLE_HEADER;

/**
 * US-S04: articles of the merchant's shop that are out of stock or under their alert threshold.
 * GET is public in the gateway, so the role and the owner are checked in ProductService.
 */
@RestController
@RequestMapping("/api/v1/shops/mine/stock-alerts")
public class StockAlertController {

    private final ProductService productService;

    public StockAlertController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public List<StockAlertResponse> findMine(@RequestHeader(value = USER_ID_HEADER, required = false) Long userId,
                                             @RequestHeader(value = USER_ROLE_HEADER, required = false) String role) {
        return productService.findStockAlerts(userId, role).stream().map(StockAlertResponse::from).toList();
    }
}