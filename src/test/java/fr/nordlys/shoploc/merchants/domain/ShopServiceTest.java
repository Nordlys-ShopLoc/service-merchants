package fr.nordlys.shoploc.merchants.domain;

import org.junit.jupiter.api.Test;
import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;

class ShopServiceTest {

    @Test
    void retourne_les_commerces_fournis_par_le_port() {
        ShopPort port = () -> List.of(
                new Shop(1L, "Le Fournil", "12 rue Solferino, Lille", "Boulangerie"),
                new Shop(2L, "Boucherie Flamande", "28 rue Solferino, Lille", "Boucherie")
        );

        ShopService service = new ShopService(port);

        List<Shop> shops = service.findAll();

        assertThat(shops).hasSize(2);
        assertThat(shops.getFirst().name()).isEqualTo("Le Fournil");
    }

    @Test
    void retourne_une_liste_vide_quand_aucun_commerce() {
        ShopPort port = List::of;

        ShopService service = new ShopService(port);

        assertThat(service.findAll()).isEmpty();
    }
}