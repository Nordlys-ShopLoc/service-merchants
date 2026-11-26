package fr.nordlys.shoploc.merchants.domain.service;

import fr.nordlys.shoploc.merchants.domain.exception.ProductNotFoundException;
import fr.nordlys.shoploc.merchants.domain.model.Product;
import fr.nordlys.shoploc.merchants.domain.model.Shop;
import fr.nordlys.shoploc.merchants.domain.port.ProductPort;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;

/** US-S03: the merchant manages the articles of her approved shop. */
public class ProductService {

    private final ProductPort productPort;
    private final ShopService shopService;

    public ProductService(ProductPort productPort, ShopService shopService) {
        this.productPort = productPort;
        this.shopService = shopService;
    }

    // retourne les articles visibles d'un commerce valide
    public List<Product> findPublicByShop(Long shopId) {
        shopService.requireApprovedShop(shopId);

        return productPort.findByShop(shopId).stream()
                .filter(Product::available)
                .toList();
    }

    /** All the articles of the merchant's shop, withdrawn ones included. */
    public List<Product> findMine(Long merchantId, String role) {
        Shop shop = shopService.requireOwnedApprovedShop(merchantId, role);
        return productPort.findByShop(shop.id());
    }

    /** Adds an article; the stock is 0 when it is not given. */
    /** Adds an article; the stock and the alert threshold (US-S04) are 0 when they are not given. */
    public Product add(Long merchantId, String role, String name, String description,
                       BigDecimal price, Integer stock, Integer lowStockThreshold) {
        Shop shop = shopService.requireOwnedApprovedShop(merchantId, role);
        int newStock = stock == null ? 0 : stock;
        int newThreshold = lowStockThreshold == null ? 0 : lowStockThreshold;
        checkDetails(name, price, newStock, newThreshold);

        Product product = new Product(null, shop.id(), name.trim(), clean(description), price,
                newStock, newThreshold, true);
        return productPort.save(product);
    }

    /** Changes the details of an article; the stock and the alert threshold are kept when they are not given. */
    public Product update(Long merchantId, String role, Long productId, String name, String description,
                          BigDecimal price, Integer stock, Integer lowStockThreshold) {
        Product product = findOwned(merchantId, role, productId);
        int newStock = stock == null ? product.stock() : stock;
        int newThreshold = lowStockThreshold == null ? product.lowStockThreshold() : lowStockThreshold;
        checkDetails(name, price, newStock, newThreshold);

        return productPort.save(product.withDetails(name.trim(), clean(description), price, newStock, newThreshold));
    }

    /** Withdraws the article from sale: clients no longer see it, the merchant still does. */
    public Product withdraw(Long merchantId, String role, Long productId) {
        Product product = findOwned(merchantId, role, productId);
        return productPort.save(product.withdrawn());
    }

    /** US-S04: articles on sale that are out of stock or under their threshold, the most urgent first. */
    public List<Product> findStockAlerts(Long merchantId, String role) {
        Shop shop = shopService.requireOwnedApprovedShop(merchantId, role);
        return productPort.findByShop(shop.id()).stream()
                .filter(Product::needsStockAlert)
                .sorted(Comparator.comparingInt(Product::stock))
                .toList();
    }

    private Product findOwned(Long merchantId, String role, Long productId) {
        Shop shop = shopService.requireOwnedApprovedShop(merchantId, role);
        Product product = productPort.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException(productId));
        // An article of another shop is answered as "not found": its existence is not revealed
        if (!product.belongsTo(shop)) {
            throw new ProductNotFoundException(productId);
        }
        return product;
    }

    private void checkDetails(String name, BigDecimal price, int stock, int lowStockThreshold) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("The name is required");
        }
        if (price == null || price.signum() <= 0) {
            throw new IllegalArgumentException("The price must be greater than zero");
        }
        if (stock < 0) {
            throw new IllegalArgumentException("The stock cannot be negative");
        }
        if (lowStockThreshold < 0) {
            throw new IllegalArgumentException("The alert threshold cannot be negative");
        }
    }

    private String clean(String description) {
        return description == null || description.isBlank() ? null : description.trim();
    }
}
