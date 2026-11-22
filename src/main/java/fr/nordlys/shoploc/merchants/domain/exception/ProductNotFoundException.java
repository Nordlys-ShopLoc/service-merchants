package fr.nordlys.shoploc.merchants.domain.exception;

/** No article with this id in the merchant's shop. */
public class ProductNotFoundException extends RuntimeException {

    public ProductNotFoundException(Long productId) {
        super("Product " + productId + " not found");
    }
}
