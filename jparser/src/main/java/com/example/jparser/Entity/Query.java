package com.example.jparser.Entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
@Entity
@Getter
@Setter
@NoArgsConstructor
public class Query {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private String query;
    @Lob
    @Column(name = "query_sql", columnDefinition = "TEXT")
    private String querySql;
    @Lob
    @Column(name = "query_es", columnDefinition = "TEXT")
    private String queryEs;
}
