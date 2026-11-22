package fr.nordlys.shoploc.merchants.api.controller;

import fr.nordlys.shoploc.merchants.api.dto.ClientProductResponse;
import fr.nordlys.shoploc.merchants.domain.service.ProductService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/shops/{shopId}/products")
public class ClientProductController {

    private final ProductService productService;

    public ClientProductController(ProductService productService) {
        this.productService = productService;
    }

    // retourne les articles visibles par les clients
    @GetMapping
    public List<ClientProductResponse> findByShop(@PathVariable Long shopId) {
        return productService.findPublicByShop(shopId).stream()
                .map(ClientProductResponse::from)
                .toList();
    }
}
