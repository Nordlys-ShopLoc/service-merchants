package fr.nordlys.shoploc.merchants.domain.service;

import fr.nordlys.shoploc.merchants.domain.exception.OperationNotAllowedException;
import fr.nordlys.shoploc.merchants.domain.model.Shop;
import fr.nordlys.shoploc.merchants.domain.model.ShopStatus;
import fr.nordlys.shoploc.merchants.domain.port.ShopPort;
import java.util.List;

/** Shops of the partner program: listing, registration (US-S13) and approval (US-A01). */
public class ShopService {

    /** Role sent by the gateway in the X-User-Role header. */
    static final String MERCHANT = "MERCHANT";

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

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
