package fr.nordlys.shoploc.merchants.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/** JSON sent by the merchant to register a shop (US-S13). */
public record ShopRegistrationRequest(
        @NotBlank @Size(max = 120) String name,
        @NotBlank @Size(max = 255) String address,
        @NotBlank @Size(max = 60) String category,
        @Size(max = 1000) String description
) {
}
