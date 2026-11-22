package fr.nordlys.shoploc.merchants.domain.port;

import fr.nordlys.shoploc.merchants.domain.model.Product;

import java.util.List;
import java.util.Optional;

/** Storage of the articles, implemented in the infrastructure layer. */
public interface ProductPort {

    List<Product> findByShop(Long shopId);

    Optional<Product> findById(Long id);

    Product save(Product product);
}
