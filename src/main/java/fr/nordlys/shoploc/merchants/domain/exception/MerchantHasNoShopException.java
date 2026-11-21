package fr.nordlys.shoploc.merchants.domain.exception;

/** The merchant has not registered a shop yet: the merchant space offers to register one (US-S13). */
public class MerchantHasNoShopException extends RuntimeException {

    public MerchantHasNoShopException(Long merchantId) {
        super("No shop registered for merchant " + merchantId);
    }
}
