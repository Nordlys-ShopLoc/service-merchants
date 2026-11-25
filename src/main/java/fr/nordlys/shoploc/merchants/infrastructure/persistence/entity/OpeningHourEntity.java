package fr.nordlys.shoploc.merchants.infrastructure.persistence.entity;

import fr.nordlys.shoploc.merchants.domain.model.OpeningHour;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalTime;

/** Table opening_hours, created by migration V3 (socle M2). */
@Entity
@Table(name = "opening_hours")
@Getter
@Setter
@NoArgsConstructor
public class OpeningHourEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "shop_id", nullable = false)
    private Long shopId;

    @Column(name = "day_of_week", nullable = false)
    private short dayOfWeek;

    @Column(name = "opens_at", nullable = false)
    private LocalTime opensAt;

    @Column(name = "closes_at", nullable = false)
    private LocalTime closesAt;

    public OpeningHour toDomain() {
        return new OpeningHour(id, shopId, dayOfWeek, opensAt, closesAt);
    }

    public static OpeningHourEntity fromDomain(OpeningHour hour) {
        OpeningHourEntity entity = new OpeningHourEntity();
        entity.id = hour.id();
        entity.shopId = hour.shopId();
        entity.dayOfWeek = (short) hour.dayOfWeek();
        entity.opensAt = hour.opensAt();
        entity.closesAt = hour.closesAt();
        return entity;
    }
}
