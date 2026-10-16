package fr.nordlys.shoploc.merchants.repository.jpa;

import fr.nordlys.shoploc.merchants.domain.Shop;
import jakarta.persistence.*;

@Entity
@Table(name = "shops")
public class ShopEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String address;
    private String category;

    protected ShopEntity() {
    }

    public Shop toDomain() {
        return new Shop(id, name, address, category);
    }
}