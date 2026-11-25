package fr.nordlys.shoploc.merchants.api.controller;

import fr.nordlys.shoploc.merchants.api.dto.OpeningHourRequest;
import fr.nordlys.shoploc.merchants.api.dto.OpeningHourResponse;
import fr.nordlys.shoploc.merchants.domain.model.OpeningHour;
import fr.nordlys.shoploc.merchants.domain.service.OpeningHoursService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static fr.nordlys.shoploc.merchants.api.controller.ShopController.USER_ID_HEADER;
import static fr.nordlys.shoploc.merchants.api.controller.ShopController.USER_ROLE_HEADER;

/** US-S10: opening hours of the shops. */
@RestController
@RequestMapping("/api/v1/shops")
public class OpeningHoursController {

    private final OpeningHoursService openingHoursService;

    public OpeningHoursController(OpeningHoursService openingHoursService) {
        this.openingHoursService = openingHoursService;
    }

    /** Public route (US-C05): opening hours of a partner shop, from Monday to Sunday. */
    @GetMapping("/{shopId}/opening-hours")
    public List<OpeningHourResponse> findPublic(@PathVariable Long shopId) {
        return toResponses(openingHoursService.findPublic(shopId));
    }

    @GetMapping("/mine/opening-hours")
    public List<OpeningHourResponse> findMine(@RequestHeader(value = USER_ID_HEADER, required = false) Long userId,
                                              @RequestHeader(value = USER_ROLE_HEADER, required = false) String role) {
        return toResponses(openingHoursService.findMine(userId, role));
    }

    @PutMapping("/mine/opening-hours")
    public List<OpeningHourResponse> replaceMine(@RequestHeader(value = USER_ID_HEADER, required = false) Long userId,
                                                 @RequestHeader(value = USER_ROLE_HEADER, required = false) String role,
                                                 @RequestBody List<OpeningHourRequest> request) {
        List<OpeningHour> slots = request == null ? null : request.stream().map(OpeningHourRequest::toDomain).toList();
        return toResponses(openingHoursService.replaceMine(userId, role, slots));
    }

    private List<OpeningHourResponse> toResponses(List<OpeningHour> hours) {
        return hours.stream().map(OpeningHourResponse::from).toList();
    }
}
