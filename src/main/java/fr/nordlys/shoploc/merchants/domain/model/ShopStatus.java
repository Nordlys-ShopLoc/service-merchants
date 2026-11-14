package fr.nordlys.shoploc.merchants.domain.model;

/** State of a shop in the partner program. */
public enum ShopStatus {
    /** Registered by the merchant, waiting for the administrator (US-S13). */
    PENDING,
    /** Accepted by the administrator: visible to clients (US-A01). */
    APPROVED,
    /** Refused by the administrator (US-A01). */
    REJECTED
}
