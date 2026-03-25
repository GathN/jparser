package com.example.jparser.Service;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.Request;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class QueryTagService {
    private final QueryConvertService queryConvertService;
    private final JdbcTemplate jdbcTemplate;
    private final RestClient restClient;
    
    public String tagSqlData(String ruleset, String tagName) {
        String whereClause = queryConvertService.convertToSql(ruleset);
        whereClause = whereClause
        .replace("SELECT *", "SELECT id")
        .replace("FROM *", "FROM mytable");
        String outputSql = "UPDATE mytable SET tag = " + tagName + " WHERE " + whereClause;
        jdbcTemplate.execute(outputSql);
        return whereClause;
    }

        public void tagEsData(String ruleset, String tagName) {
        String esQuery = queryConvertService.convertToEs(ruleset);
        esQuery = esQuery.substring(1, esQuery.length() - 1);
        String updateBody = "{"
            + "  \"script\": { \"source\": \"ctx._source.tag = '" + tagName + "'\" },"
            + "  " + esQuery
            + "}";
        Request request = new Request("POST","/my_index/_update_by_query");
        request.addParameter("source", updateBody);
        request.setJsonEntity(updateBody);
        try {
        restClient.performRequest(request);
        } catch (Exception e) {
            e.printStackTrace();
        }
        
    }
}
