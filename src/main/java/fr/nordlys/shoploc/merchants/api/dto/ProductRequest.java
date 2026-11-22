package fr.nordlys.shoploc.merchants.api.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

/** JSON sent by the merchant to add or change an article (US-S03). The stock is optional. */
public record ProductRequest(
        @NotBlank @Size(max = 120) String name,
        @Size(max = 1000) String description,
        @NotNull @DecimalMin("0.01") @Digits(integer = 6, fraction = 2) BigDecimal price,
        @PositiveOrZero Integer stock
) {
}
