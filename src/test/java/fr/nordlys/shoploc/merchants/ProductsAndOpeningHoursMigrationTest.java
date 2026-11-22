package fr.nordlys.shoploc.merchants;

import fr.nordlys.shoploc.merchants.domain.model.Shop;
import fr.nordlys.shoploc.merchants.domain.port.ShopPort;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/** Socle M2: migration V3 on a real PostgreSQL database. Each test is rolled back. */
@Import(TestcontainersConfiguration.class)
@SpringBootTest
@Transactional
class ProductsAndOpeningHoursMigrationTest {

    @Autowired
    private JdbcTemplate jdbc;

    @Autowired
    private ShopPort shopPort;

    private Integer count(String sql) {
        return jdbc.queryForObject(sql, Integer.class);
    }

    private void insertProduct(String price, int stock) {
        jdbc.update("INSERT INTO products (shop_id, name, price, stock) "
                + "SELECT id, 'Test', CAST(? AS NUMERIC), ? FROM shops WHERE name = 'Le Fournil'", price, stock);
    }

    private void insertSlot(int day, String opensAt, String closesAt) {
        jdbc.update("INSERT INTO opening_hours (shop_id, day_of_week, opens_at, closes_at) "
                + "SELECT id, ?, CAST(? AS TIME), CAST(? AS TIME) FROM shops WHERE name = 'Le Fournil'",
                day, opensAt, closesAt);
    }

    @Test
    void gives_demo_articles_to_the_three_partner_shops() {
        assertThat(count("SELECT COUNT(DISTINCT shop_id) FROM products")).isEqualTo(3);
        assertThat(count("SELECT COUNT(*) FROM products p JOIN shops s ON s.id = p.shop_id "
                + "WHERE s.name = 'Le Fournil'")).isEqualTo(3);
    }

    @Test
    void allows_two_slots_a_day_for_the_lunch_break() {
        String fournil = "SELECT COUNT(*) FROM opening_hours h JOIN shops s ON s.id = h.shop_id "
                + "WHERE s.name = 'Le Fournil' AND h.day_of_week = ";
        assertThat(count(fournil + "2")).isEqualTo(2);
        assertThat(count(fournil + "1")).isZero();
    }

    @Test
    void refuses_a_price_of_zero() {
        assertThatThrownBy(() -> insertProduct("0.00", 5)).isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    void refuses_a_negative_stock() {
        assertThatThrownBy(() -> insertProduct("1.00", -1)).isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    void refuses_a_slot_that_closes_before_it_opens() {
        assertThatThrownBy(() -> insertSlot(2, "19:00", "08:00")).isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    void refuses_a_day_outside_monday_to_sunday() {
        assertThatThrownBy(() -> insertSlot(8, "08:00", "12:00")).isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    void finds_the_latest_shop_of_a_merchant() {
        jdbc.update("INSERT INTO shops (name, address, category, owner_account_id, status, created_at) VALUES "
                + "('Ancien commerce', '1 rue A, Lille', 'Test', 77, 'REJECTED', TIMESTAMP '2026-01-01 10:00'), "
                + "('Nouveau commerce', '2 rue B, Lille', 'Test', 77, 'PENDING', TIMESTAMP '2026-02-01 10:00')");

        assertThat(shopPort.findLatestByOwner(77L)).map(Shop::name).contains("Nouveau commerce");
    }
}
