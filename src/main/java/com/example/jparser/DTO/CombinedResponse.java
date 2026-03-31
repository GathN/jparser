package com.example.jparser.DTO;

import java.util.List;

import com.example.jparser.Entity.Product;

public record CombinedResponse(QueryRequest query, List<Product> result) {
    
}
