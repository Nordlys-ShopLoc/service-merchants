package fr.nordlys.shoploc.merchants.infrastructure.config;

import fr.nordlys.shoploc.merchants.domain.port.ShopPort;
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
}
