package fr.nordlys.shoploc.merchants.api.dto;

import fr.nordlys.shoploc.merchants.domain.model.OpeningHour;

import java.time.LocalTime;

/** One opening slot sent by the merchant, for example { "dayOfWeek": 2, "opensAt": "08:00", "closesAt": "12:30" }. */
public record OpeningHourRequest(Integer dayOfWeek, LocalTime opensAt, LocalTime closesAt) {

    public OpeningHour toDomain() {
        return new OpeningHour(null, null, dayOfWeek == null ? 0 : dayOfWeek, opensAt, closesAt);
    }
}
