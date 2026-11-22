package fr.nordlys.shoploc.merchants.domain.service;

import fr.nordlys.shoploc.merchants.domain.exception.ProductNotFoundException;
import fr.nordlys.shoploc.merchants.domain.model.Product;
import fr.nordlys.shoploc.merchants.domain.model.Shop;
import fr.nordlys.shoploc.merchants.domain.port.ProductPort;

import java.math.BigDecimal;
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
    public Product add(Long merchantId, String role, String name, String description,
                       BigDecimal price, Integer stock) {
        Shop shop = shopService.requireOwnedApprovedShop(merchantId, role);
        int newStock = stock == null ? 0 : stock;
        checkDetails(name, price, newStock);

        Product product = new Product(null, shop.id(), name.trim(), clean(description), price, newStock, 0, true);
        return productPort.save(product);
    }

    /** Changes the name, description, price and stock; the stock is kept when it is not given. */
    public Product update(Long merchantId, String role, Long productId, String name, String description,
                          BigDecimal price, Integer stock) {
        Product product = findOwned(merchantId, role, productId);
        int newStock = stock == null ? product.stock() : stock;
        checkDetails(name, price, newStock);

        return productPort.save(product.withDetails(name.trim(), clean(description), price, newStock));
    }

    /** Withdraws the article from sale: clients no longer see it, the merchant still does. */
    public Product withdraw(Long merchantId, String role, Long productId) {
        Product product = findOwned(merchantId, role, productId);
        return productPort.save(product.withdrawn());
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

    private void checkDetails(String name, BigDecimal price, int stock) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("The name is required");
        }
        if (price == null || price.signum() <= 0) {
            throw new IllegalArgumentException("The price must be greater than zero");
        }
        if (stock < 0) {
            throw new IllegalArgumentException("The stock cannot be negative");
        }
    }

    private String clean(String description) {
        return description == null || description.isBlank() ? null : description.trim();
    }
}
