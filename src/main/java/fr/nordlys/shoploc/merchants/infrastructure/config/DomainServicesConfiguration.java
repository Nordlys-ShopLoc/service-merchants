package fr.nordlys.shoploc.merchants.infrastructure.config;

import fr.nordlys.shoploc.merchants.domain.port.OpeningHourPort;
import fr.nordlys.shoploc.merchants.domain.port.ProductPort;
import fr.nordlys.shoploc.merchants.domain.port.ShopPort;
import fr.nordlys.shoploc.merchants.domain.service.OpeningHoursService;
import fr.nordlys.shoploc.merchants.domain.service.ProductService;
import fr.nordlys.shoploc.merchants.domain.service.ShopService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Declares the domain services as Spring beans.
 * The domain has no Spring annotation (see README), so services are created here.
 */
@Configuration
public class DomainServicesConfiguration {

    @Bean
    ShopService shopService(ShopPort shopPort) {
        return new ShopService(shopPort);
    }

    @Bean
    OpeningHoursService openingHoursService(OpeningHourPort openingHourPort, ShopService shopService) {
        return new OpeningHoursService(openingHourPort, shopService);
    }

    @Bean
    ProductService productService(ProductPort productPort, ShopService shopService) {
        return new ProductService(productPort, shopService);
    }
}
