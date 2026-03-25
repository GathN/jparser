package com.example.jparser.Service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;

import java.io.IOException;

import org.elasticsearch.client.RestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import jakarta.transaction.Transactional;


@SpringBootTest
@ActiveProfiles("test") // Uses the H2 configuration
public class QueryTagServiceTest {

    @Autowired
    private QueryTagService queryTagService;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private QueryConvertService queryConvertService;

    @MockitoBean
    private RestClient restClient;

    @BeforeEach
    void setup() {
    jdbcTemplate.execute("DROP TABLE IF EXISTS mytable");
    jdbcTemplate.execute("""
        CREATE TABLE mytable (
            id INT PRIMARY KEY,
            tag VARCHAR(255),
            stock INT,
            country VARCHAR(50),
            count_ry VARCHAR(50),
            release_date VARCHAR(50),
            bruh BIGINT,
            bro BIGINT,
            type DECIMAL(10,2),
            imgay BOOLEAN
        )
    """);
    jdbcTemplate.execute("""
        INSERT INTO mytable (id, tag, stock, country, count_ry, release_date, bruh, bro, type, imgay) 
        VALUES (1229000, 'none', 10, 'en_US', 'US', '1999-05-02', 1229000, 1229000, 5.86, true)
    """);
    jdbcTemplate.execute("""
        INSERT INTO mytable (id, tag, stock, country, count_ry, release_date, bruh, bro, type, imgay) 
        VALUES (2, 'original', 50, 'fr_FR', 'FR', '2023-01-01', 0, 0, 10.00, false)
    """);
    }

        @Test
        @Transactional
        void testTagSqlData() {
            String ruleset = "stock < 20 AND country = 'en_US' OR count_ry = CA-US AND release_date = '1999-05-02' AND bruh = 1229000 OR bro = 1.229.000 AND type = 5.86 OR type = 51,29 AND imgay = true";
            queryTagService.tagSqlData(ruleset, "'new_tag'");
            String cleanedRuleset = queryConvertService.convertToSql(ruleset); 
            int count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM mytable WHERE tag = 'new_tag' AND " + cleanedRuleset, Integer.class);
            System.out.println("Rows successfully tagged: " + count);
            String updatedTag = jdbcTemplate.queryForObject(
                "SELECT tag FROM mytable WHERE " + cleanedRuleset, String.class);
            assertEquals("new_tag", updatedTag);
        }

    @Test
    void debugAntlrOutput() {
        String ruleset = "stock < 20 AND country = 'en_US' OR count_ry = CA-US AND release_date = '1999-05-02' AND bruh = 1229000 OR bro = 1.229.000 AND type = 5.86 OR type = 51,29 AND imgay = true";
        String sqlResult = queryConvertService.convertToSql(ruleset);
        System.out.println("ANTLR TRANSLATED THIS TO: " + sqlResult);
    }
    @Test
    @Transactional
    void testTagEsData() throws Exception {
        String ruleset = "stock < 20 AND country = 'en_US' OR count_ry = CA-US AND release_date = '1999-05-02' AND bruh = 1229000 OR bro = 1.229.000 AND type = 5.86 OR type = 51,29 AND imgay = true";
        queryTagService.tagEsData(ruleset, "es_tag");
        
        verify(restClient).performRequest(argThat(request -> {
            try {
                java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
                request.getEntity().writeTo(out);
                String body = out.toString();
                System.out.println("DEBUG ES JSON: " + body);
                return request.getEndpoint().contains("_update_by_query") &&
                body.contains("es_tag");
            } catch (UnsupportedOperationException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return false;
        }));
    }
}
