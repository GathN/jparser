package com.example.jparser.Converter;
import java.util.*;

import org.antlr.v4.runtime.tree.ParseTree;

import com.example.jparser.generated.ExprBaseVisitor;
import com.example.jparser.generated.ExprParser;

public class EsConvert extends ExprBaseVisitor<Object> {

    @Override
    public Object visitRootQuery(ExprParser.RootQueryContext c) {
        return Map.of("query", visit(c.expr()));
    }

    @Override
public Object visitLogicalExpr(ExprParser.LogicalExprContext c) {
    String type = c.op.getText().equalsIgnoreCase("AND") ? "must" : "should";
    List<Object> conditions = new ArrayList<>();

    //Extract conditions from children
    addConditions(c.left, type, conditions);
    addConditions(c.right, type, conditions);

    return Map.of("bool", Map.of(type, conditions));
}

//flatten the tree
private void addConditions(ParseTree tree, String currentType, List<Object> collector) {
    Object result = visit(tree);
    
    if (result instanceof Map) {
        Map<?, ?> map = (Map<?, ?>) result;
        if (map.containsKey("bool")) {
            Map<?, ?> boolMap = (Map<?, ?>) map.get("bool");
            if (boolMap.containsKey(currentType)) {
                collector.addAll((List<?>) boolMap.get(currentType));
                return;
            }
        }
    }
    collector.add(result);
}

    @Override
    public Object visitInclComparison(ExprParser.InclComparisonContext c) {
        String cleanVal = "*" + c.right.getText().replace("'", "") + "*";
        return buildEsMap(c.left.getText(), "=", cleanVal);
    }

    @Override
    public Object visitStrComparison(ExprParser.StrComparisonContext c) {
        return buildEsMap(c.left.getText(), c.op.getText(), c.right.getText());
    }

    @Override
    public Object visitQStrComparison(ExprParser.QStrComparisonContext c) {
        String cleanVal = c.right.getText().replace("'", "");
        return buildEsMap(c.left.getText(), c.op.getText(), cleanVal);
    }
    

    @Override
    public Object visitNumComparison(ExprParser.NumComparisonContext c) {
        Integer val = Integer.parseInt(c.right.getText());
        return buildEsMap(c.left.getText(), c.op.getText(), val);
    }

    @Override
    public Object visitSepNumComparison(ExprParser.SepNumComparisonContext c) {
        String str = c.right.getText().replace(".", "");
        Integer val = Integer.parseInt(str);
        return buildEsMap(c.left.getText(), c.op.getText(), val);
    }

    @Override
    public Object visitDecComparison(ExprParser.DecComparisonContext c) {
        String dec = c.right.getText().replace(",", ".");
        Double val = Double.parseDouble(dec);
        return buildEsMap(c.left.getText(), c.op.getText(), val);
    }

    @Override
    public Object visitBoolComparison(ExprParser.BoolComparisonContext c) {
        Boolean val = Boolean.parseBoolean(c.right.getText().toLowerCase());
        return buildEsMap(c.left.getText(), c.op.getText(), val);
    }

    private Map<String, Object> buildEsMap(String field, String op, Object value) {
        if ("=".equals(op)) {
            return Map.of("term", Map.of(field, value));
        }
        
        // Handle range operators
        String esOp = switch (op) {
            case ">" -> "gt";
            case "<" -> "lt";
            case ">=" -> "gte";
            case "<=" -> "lte";
            default -> "eq";
        };
        return Map.of("range", Map.of(field, Map.of(esOp, value)));
    }

    @Override
    public Object visitParenExpr(ExprParser.ParenExprContext c) {
        return visit(c.expr());
    }
}
