package com.example.jparser.Converter;

import com.example.jparser.generated.ExprBaseVisitor;
import com.example.jparser.generated.ExprParser;

public class SqlConvert extends ExprBaseVisitor<String>{
    @Override
    public String visitRootQuery(ExprParser.RootQueryContext c){
        return visit(c.expr());
    }

    @Override
    public String visitLogicalExpr(ExprParser.LogicalExprContext c){
        return visit(c.left) + " " + c.op.getText() + " " + visit(c.right);
    }
    
    @Override
    public String visitInclComparison(ExprParser.InclComparisonContext c){
        if (c.right.getText().startsWith("'") && c.right.getText().endsWith("'")) {
            return c.left.getText() + " " + "IN" + " " + c.right.getText();
        } else {
            return c.left.getText() + " " + "IN" + " " + "'" + c.right.getText() + "'";    
        }
        
    }

    @Override
    public String visitQStrComparison(ExprParser.QStrComparisonContext c){
        return c.left.getText() + " " + c.op.getText() + " " + c.right.getText();
    }

    @Override
    public String visitStrComparison(ExprParser.StrComparisonContext c){
        System.out.println("Token Type: " + c.right.getType() + " Text: " + c.right.getText());
        return c.left.getText() + " " + c.op.getText() + " " + "'" + c.right.getText() + "'";
    }
    @Override
    public String visitBoolComparison(ExprParser.BoolComparisonContext c){
        return c.left.getText() + " " + c.op.getText() + " " + c.right.getText();
    }


    @Override
    public String visitNumComparison(ExprParser.NumComparisonContext c){
        System.out.println("Token Type: " + c.op.getType() + " Text: " + c.op.getText());
        return c.left.getText() + " " + c.op.getText() + " " + c.right.getText();
    }

    @Override
    public String visitSepNumComparison(ExprParser.SepNumComparisonContext c){
        System.out.println("Token Type: " + c.right.getType() + " Text: " + c.right.getText());
        String noDots = c.right.getText().replace(".", "");
        return c.left.getText() + " " + c.op.getText() + " " + noDots;
    }

    @Override
    public String visitDecComparison(ExprParser.DecComparisonContext c){
        String dec = c.right.getText().replace(",", ".");
        return c.left.getText() + " " + c.op.getText() + " " + dec;
    }


    @Override
    public String visitParenExpr(ExprParser.ParenExprContext c){
        return "(" + visit(c.expr(  )) + ")";
    }

}
