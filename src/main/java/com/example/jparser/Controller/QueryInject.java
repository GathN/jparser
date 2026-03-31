package com.example.jparser.Controller;

import java.io.IOException;
import java.util.List;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.jparser.Entity.Product;
import com.example.jparser.Service.QueryExecutionService;

import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;



@RestController
@RequestMapping("/api/")
@RequiredArgsConstructor
public class QueryInject {
    private final QueryExecutionService queryExecutionService;

        @PostMapping("execute")
        public List<Product> executeQuery(@RequestBody String queryName) throws IOException {
            return queryExecutionService.executeByTitle(queryName);
        }

        
    
}
