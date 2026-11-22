package fr.nordlys.shoploc.merchants;

import fr.nordlys.shoploc.merchants.domain.model.Product;
import fr.nordlys.shoploc.merchants.domain.port.ProductPort;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

/** US-S03: articles stored in the products table of a real PostgreSQL database. Each test is rolled back. */
@Import(TestcontainersConfiguration.class)
@SpringBootTest
@Transactional
class ProductPersistenceTest {

    @Autowired
    private ProductPort productPort;

    @Autowired
    private JdbcTemplate jdbc;

    private Long fournilId() {
        return jdbc.queryForObject("SELECT id FROM shops WHERE name = 'Le Fournil'", Long.class);
    }

    private Product newArticle() {
        return new Product(null, fournilId(), "Pain de seigle", null, new BigDecimal("2.50"), 12, 0, true);
    }

    @Test
    void reads_the_demo_articles_of_a_shop() {
        assertThat(productPort.findByShop(fournilId())).extracting(Product::name)
                .containsExactlyInAnyOrder("Baguette tradition", "Pain de campagne", "Croissant pur beurre");
    }

    @Test
    void saves_a_new_article() {
        Product saved = productPort.save(newArticle());

        assertThat(saved.id()).isNotNull();
        Product read = productPort.findById(saved.id()).orElseThrow();
        assertThat(read.price()).isEqualByComparingTo("2.50");
        assertThat(read.stock()).isEqualTo(12);
        assertThat(read.available()).isTrue();
    }

    @Test
    void keeps_a_withdrawn_article_in_the_list() {
        Product saved = productPort.save(newArticle());

        productPort.save(saved.withdrawn());

        assertThat(productPort.findByShop(fournilId()))
                .filteredOn(product -> product.id().equals(saved.id()))
                .singleElement()
                .extracting(Product::available)
                .isEqualTo(false);
    }
}
