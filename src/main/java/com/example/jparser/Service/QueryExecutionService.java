package com.example.jparser.Service;

import java.io.IOException;
import java.io.StringReader;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.jparser.DTO.QueryRequest;
import com.example.jparser.Entity.Product;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.ElasticsearchException;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;

@Service
public class QueryExecutionService {

    @Autowired
    private ElasticsearchClient client; 
    @Autowired
    private QueryService queryService;

    public List<Product> executeByTitle(String title) throws IOException{
        List<QueryRequest> q = queryService.getQueryByTitle(title);
        if (q.isEmpty()) {
        throw new RuntimeException("Query not found: " + title);
        }
        QueryRequest req = q.getFirst();
        Map<String, Object> queryMap =  req.queryEsMap();
        return executeQuery(queryMap);
    }

    public List<Product> executeQuery(Map<String, Object> queryMap) throws JsonProcessingException  {

            String generatedJson = new ObjectMapper().writeValueAsString(queryMap);
            SearchRequest request = SearchRequest.of(s -> s
                .index("products")
                .withJson(new StringReader(generatedJson))
            );

            SearchResponse<Product> response;
            try {
                response = client.search(request, Product.class);
            } catch (ElasticsearchException e) {
                e.printStackTrace();
                response = null;
            } catch (IOException e) {
                e.printStackTrace();
                response = null;
            }
            
            return response.hits().hits().stream()
                        .map(hit -> hit.source())
                        .collect(Collectors.toList());
        }
    
}
