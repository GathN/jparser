package com.example.jparser.Repository;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.jparser.Entity.Query;

public interface QueryRepository extends JpaRepository<Query, Long> {
    List<Query> findByTitle(String title);
}