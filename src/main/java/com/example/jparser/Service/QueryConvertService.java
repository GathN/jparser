package com.example.jparser.Service;

import java.util.Map;

import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.springframework.stereotype.Service;

import com.example.jparser.Converter.EsConvert;
import com.example.jparser.Converter.SqlConvert;
import com.example.jparser.generated.ExprLexer;
import com.example.jparser.generated.ExprParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.antlr.v4.runtime.tree.ParseTree;


@Service
public class QueryConvertService {
    public String convertToSql(String query) {
        ExprLexer lexer = new ExprLexer(CharStreams.fromString(query));
        CommonTokenStream tokens =  new CommonTokenStream(lexer);
        ExprParser parser = new ExprParser(tokens);


        ParseTree tree = parser.query();
        SqlConvert visitor = new SqlConvert();
        return visitor.visit(tree);
    }

    public Map<String, Object> convertToEsJSON(String query) {
        ExprLexer lexer = new ExprLexer(CharStreams.fromString(query));
        CommonTokenStream tokens =  new CommonTokenStream(lexer);
        ExprParser parser = new ExprParser(tokens);       
        EsConvert visitor = new EsConvert();
        Object resultObj = visitor.visit(parser.query());
        try {
            return new ObjectMapper().convertValue(resultObj, new TypeReference<Map<String, Object>>() {});
        } catch (Exception e) {
            return null;
        }
    }

    public String convertToEs(String query) {
        ExprLexer lexer = new ExprLexer(CharStreams.fromString(query));
        CommonTokenStream tokens =  new CommonTokenStream(lexer);
        ExprParser parser = new ExprParser(tokens);       
        EsConvert visitor = new EsConvert();
        Object resultObj = visitor.visit(parser.query());
        try {
            return new ObjectMapper().writeValueAsString(resultObj);
        } catch (Exception e) {
            return "{}";
        }
    }
    
    
}
    
