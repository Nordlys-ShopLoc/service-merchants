package fr.nordlys.shoploc.merchants.api.dto;

import fr.nordlys.shoploc.merchants.domain.model.OpeningHour;

import java.time.LocalTime;

/** One opening slot; dayOfWeek: 1 = Monday ... 7 = Sunday. */
public record OpeningHourResponse(int dayOfWeek, LocalTime opensAt, LocalTime closesAt) {

    public static OpeningHourResponse from(OpeningHour hour) {
        return new OpeningHourResponse(hour.dayOfWeek(), hour.opensAt(), hour.closesAt());
    }
}
