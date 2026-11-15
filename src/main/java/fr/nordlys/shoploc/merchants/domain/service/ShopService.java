package fr.nordlys.shoploc.merchants.domain.service;

import fr.nordlys.shoploc.merchants.domain.exception.OperationNotAllowedException;
import fr.nordlys.shoploc.merchants.domain.exception.ShopAlreadyReviewedException;
import fr.nordlys.shoploc.merchants.domain.exception.ShopNotFoundException;
import fr.nordlys.shoploc.merchants.domain.model.Shop;
import fr.nordlys.shoploc.merchants.domain.model.ShopStatus;
import fr.nordlys.shoploc.merchants.domain.port.ShopPort;
import java.util.List;

/** Shops of the partner program: listing, registration (US-S13) and approval (US-A01). */
public class ShopService {

    /** Roles sent by the gateway in the X-User-Role header. */
    static final String MERCHANT = "MERCHANT";
    static final String ADMIN = "ADMIN";

    private final ShopPort shopPort;

    public ShopService(ShopPort shopPort) {
        this.shopPort = shopPort;
    }

    /** Shops shown to clients: only the approved ones. */
    public List<Shop> findPartners() {
        return shopPort.findByStatus(ShopStatus.APPROVED);
    }

    /** US-S13: the merchant registers a shop, which waits for the administrator. */
    public Shop register(Long merchantId, String role, String name, String address,
                         String category, String description) {
        if (merchantId == null || !MERCHANT.equals(role)) {
            throw new OperationNotAllowedException("Only a merchant can register a shop");
        }
        if (isBlank(name) || isBlank(address) || isBlank(category)) {
            throw new IllegalArgumentException("Name, address and category are required");
        }
        String cleanDescription = isBlank(description) ? null : description.trim();

        Shop shop = new Shop(null, merchantId, name.trim(), address.trim(), category.trim(),
                cleanDescription, ShopStatus.PENDING, null);
        return shopPort.save(shop);
    }

    /** US-A01: shops waiting for a decision. */
    public List<Shop> findPending(String role) {
        checkIsAdmin(role);
        return shopPort.findByStatus(ShopStatus.PENDING);
    }

    /** US-A01: the administrator accepts the shop. */
    public Shop approve(Long shopId, String role) {
        return review(shopId, role, ShopStatus.APPROVED);
    }

    /** US-A01: the administrator refuses the shop. */
    public Shop reject(Long shopId, String role) {
        return review(shopId, role, ShopStatus.REJECTED);
    }

    private Shop review(Long shopId, String role, ShopStatus decision) {
        checkIsAdmin(role);
        Shop shop = shopPort.findById(shopId)
                .orElseThrow(() -> new ShopNotFoundException(shopId));
        if (!shop.isPending()) {
            throw new ShopAlreadyReviewedException(shopId);
        }
        return shopPort.save(shop.withStatus(decision));
    }

    private void checkIsAdmin(String role) {
        if (!ADMIN.equals(role)) {
            throw new OperationNotAllowedException("Only the ShopLoc administrator can review shops");
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
