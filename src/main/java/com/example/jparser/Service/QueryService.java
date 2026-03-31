package com.example.jparser.Service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.jparser.DTO.QueryRequest;
import com.example.jparser.Entity.Query;
import com.example.jparser.Mapper.QueryMapper;
import com.example.jparser.Repository.QueryRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class QueryService {
    private final QueryRepository queryRepository;
    private final QueryMapper queryMapper;
    private final QueryConvertService queryConvertService;

    @Transactional
    public QueryRequest createQuery(String title, String query) {
        Query queryRequest = new Query();
        queryRequest.setTitle(title);
        queryRequest.setQuery(query);
        queryRequest.setQuerySql(queryConvertService.convertToSql(query));
        queryRequest.setQueryEs(queryConvertService.convertToEs(query));
        queryRequest.setQueryEsMap(queryConvertService.convertToEsJSON(query));
        Query savedQuery = queryRepository.save(queryRequest);
        return queryMapper.toRequest(savedQuery);
    }


    public List<QueryRequest> getAllQueries() {
        List<Query> queries = queryRepository.findAll();
        return queries.stream().map(queryMapper::toRequest).toList();
    }

    public List<QueryRequest> getQueryByTitle(String title) {
        
        return queryRepository.findByTitle(title).stream().map(queryMapper::toRequest).toList();

    }


    public void deleteQuery(Long id) {
        queryRepository.deleteById(id);
    }

    
}