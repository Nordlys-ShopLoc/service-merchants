package fr.nordlys.shoploc.merchants.domain.port;

import fr.nordlys.shoploc.merchants.domain.model.OpeningHour;

import java.util.List;

/** Storage of the opening hours, implemented in the infrastructure layer. */
public interface OpeningHourPort {

    List<OpeningHour> findByShop(Long shopId);

    void replaceForShop(Long shopId, List<OpeningHour> hours);
}
