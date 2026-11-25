package fr.nordlys.shoploc.merchants.api.controller;

import fr.nordlys.shoploc.merchants.api.dto.ProductRequest;
import fr.nordlys.shoploc.merchants.api.dto.ProductResponse;
import fr.nordlys.shoploc.merchants.domain.model.Product;
import fr.nordlys.shoploc.merchants.domain.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static fr.nordlys.shoploc.merchants.api.controller.ShopController.USER_ID_HEADER;
import static fr.nordlys.shoploc.merchants.api.controller.ShopController.USER_ROLE_HEADER;

/**
 * US-S03: the merchant manages the articles of her shop.
 * GET is public in the gateway, so the role and the owner are always checked in ProductService.
 */
@RestController
@RequestMapping("/api/v1/shops/mine/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    /** All the articles of the shop, withdrawn ones included. */
    @GetMapping
    public List<ProductResponse> findMine(@RequestHeader(value = USER_ID_HEADER, required = false) Long userId,
                                          @RequestHeader(value = USER_ROLE_HEADER, required = false) String role) {
        return productService.findMine(userId, role).stream().map(ProductResponse::from).toList();
    }

    /** Adds an article. */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProductResponse add(@RequestHeader(value = USER_ID_HEADER, required = false) Long userId,
                               @RequestHeader(value = USER_ROLE_HEADER, required = false) String role,
                               @Valid @RequestBody ProductRequest request) {
        Product product = productService.add(userId, role, request.name(), request.description(),
                request.price(), request.stock());
        return ProductResponse.from(product);
    }

    /** Changes the name, description, price and stock of an article. */
    @PutMapping("/{id}")
    public ProductResponse update(@PathVariable Long id,
                                  @RequestHeader(value = USER_ID_HEADER, required = false) Long userId,
                                  @RequestHeader(value = USER_ROLE_HEADER, required = false) String role,
                                  @Valid @RequestBody ProductRequest request) {
        Product product = productService.update(userId, role, id, request.name(), request.description(),
                request.price(), request.stock());
        return ProductResponse.from(product);
    }

    /** Withdraws an article from sale: it is kept, with available = false. */
    @DeleteMapping("/{id}")
    public ProductResponse withdraw(@PathVariable Long id,
                                    @RequestHeader(value = USER_ID_HEADER, required = false) Long userId,
                                    @RequestHeader(value = USER_ROLE_HEADER, required = false) String role) {
        return ProductResponse.from(productService.withdraw(userId, role, id));
    }

    /** Marks an article as out of stock: stock becomes 0 while staying visible to clients. */
    @PostMapping("/{id}/out-of-stock")
    public ProductResponse markOutOfStock(@PathVariable Long id,
                                          @RequestHeader(value = USER_ID_HEADER, required = false) Long userId,
                                          @RequestHeader(value = USER_ROLE_HEADER, required = false) String role) {
        return ProductResponse.from(productService.markOutOfStock(userId, role, id));
    }
}
