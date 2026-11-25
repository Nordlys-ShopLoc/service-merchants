package fr.nordlys.shoploc.merchants;

import fr.nordlys.shoploc.merchants.domain.model.OpeningHour;
import fr.nordlys.shoploc.merchants.domain.port.OpeningHourPort;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Import(TestcontainersConfiguration.class)
@SpringBootTest
@Transactional
class OpeningHoursPersistenceTest {

    @Autowired
    private JdbcTemplate jdbc;

    @Autowired
    private OpeningHourPort port;

    @Test
    void replaces_old_slots_for_a_shop() {
        Long shopId = jdbc.queryForObject("SELECT id FROM shops WHERE name = 'Le Fournil'", Long.class);

        port.replaceForShop(shopId, List.of(new OpeningHour(null, shopId, 1, LocalTime.of(9, 0), LocalTime.of(18, 0))));

        assertThat(port.findByShop(shopId)).hasSize(1);
    }
}
