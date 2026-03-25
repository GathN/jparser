package com.example.jparser.Mapper;

import org.mapstruct.Mapper;

import com.example.jparser.DTO.QueryRequest;
import com.example.jparser.Entity.Query;

@Mapper(componentModel = "spring")
public interface QueryMapper {
    QueryRequest toRequest(Query query);
}
