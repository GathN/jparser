package com.example.jparser.DTO;

import java.util.Map;

public record QueryRequest(String title, String query, String querySql, String queryEs, Map<String, Object> queryEsMap) {
}
