package com.example.jparser.Controller;

import com.example.jparser.Service.QueryExecutionService;
import org.springframework.web.bind.annotation.RestController;

import com.example.jparser.DTO.CombinedResponse;
import com.example.jparser.DTO.QueryRequest;
import com.example.jparser.Entity.Product;
import com.example.jparser.Service.QueryService;
import com.fasterxml.jackson.core.JsonProcessingException;

import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;



@RestController
@RequiredArgsConstructor
@RequestMapping("query")
public class QueryController {
        private final QueryExecutionService queryExecutionService;
        private final QueryService queryService;

        
        @PostMapping("create")
        public ResponseEntity<QueryRequest> create(@RequestBody QueryRequest query) {
            QueryRequest req = queryService.createQuery(query.title(), query.query());
            return ResponseEntity.ok(req);
        }

        @GetMapping("get")
        public ResponseEntity<List<QueryRequest>> getAll() {
            return ResponseEntity.ok(queryService.getAllQueries());
        }

        @GetMapping("search")
        public ResponseEntity<List<QueryRequest>> search(@RequestParam String title) {
            List<QueryRequest> res = queryService.getQueryByTitle(title);
            return ResponseEntity.ok(res);
        }

        //this is unused
        @PostMapping("createAndExec")
        public ResponseEntity<CombinedResponse> createExecute(@RequestBody QueryRequest query) throws JsonProcessingException {
            QueryRequest req = queryService.createQuery(query.title(), query.query());
            List<Product> res = queryExecutionService.executeQuery(req.queryEsMap());
            return ResponseEntity.ok(new CombinedResponse(req, res));
        }
        
        @DeleteMapping("delete/{id}")
        public ResponseEntity<Void> deleteNote(@PathVariable Long id) {
            queryService.deleteQuery(id);
            return ResponseEntity.noContent().build();
        }
        
}

