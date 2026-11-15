package fr.nordlys.shoploc.merchants.api.controller;

import fr.nordlys.shoploc.merchants.api.dto.ShopRegistrationRequest;
import fr.nordlys.shoploc.merchants.api.dto.ShopResponse;
import fr.nordlys.shoploc.merchants.domain.model.Shop;
import fr.nordlys.shoploc.merchants.domain.service.ShopService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/shops")
public class ShopController {

    // Added by the gateway after checking the token; it overwrites any value sent by the client.
    // Missing without a token: the service then answers 403.
    static final String USER_ID_HEADER = "X-User-Id";
    static final String USER_ROLE_HEADER = "X-User-Role";

    private final ShopService shopService;

    public ShopController(ShopService shopService) {
        this.shopService = shopService;
    }

    /** Partner shops shown to clients: approved only. Public route. */
    @GetMapping
    public List<ShopResponse> findPartners() {
        return toResponses(shopService.findPartners());
    }

    /** US-S13: the merchant registers a shop. */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ShopResponse register(@RequestHeader(value = USER_ID_HEADER, required = false) Long userId,
                                 @RequestHeader(value = USER_ROLE_HEADER, required = false) String role,
                                 @Valid @RequestBody ShopRegistrationRequest request) {
        Shop shop = shopService.register(userId, role, request.name(), request.address(),
                request.category(), request.description());
        return ShopResponse.from(shop);
    }

    /** US-A01: shops waiting for the administrator. */
    @GetMapping("/pending")
    public List<ShopResponse> findPending(@RequestHeader(value = USER_ROLE_HEADER, required = false) String role) {
        return toResponses(shopService.findPending(role));
    }

    /** US-A01: the administrator accepts the shop. */
    @PostMapping("/{id}/approve")
    public ShopResponse approve(@PathVariable Long id, @RequestHeader(value = USER_ROLE_HEADER, required = false) String role) {
        return ShopResponse.from(shopService.approve(id, role));
    }

    /** US-A01: the administrator refuses the shop. */
    @PostMapping("/{id}/reject")
    public ShopResponse reject(@PathVariable Long id, @RequestHeader(value = USER_ROLE_HEADER, required = false) String role) {
        return ShopResponse.from(shopService.reject(id, role));
    }

    private List<ShopResponse> toResponses(List<Shop> shops) {
        return shops.stream().map(ShopResponse::from).toList();
    }
}
