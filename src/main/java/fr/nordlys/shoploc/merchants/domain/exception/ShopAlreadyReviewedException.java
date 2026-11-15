package fr.nordlys.shoploc.merchants.domain.exception;

/** Only a PENDING shop can be approved or rejected. */
public class ShopAlreadyReviewedException extends RuntimeException {

    public ShopAlreadyReviewedException(Long shopId) {
        super("Shop " + shopId + " has already been reviewed");
    }
}
