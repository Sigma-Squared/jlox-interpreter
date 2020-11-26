package com.craftinginterpreters.lox;

public class ASTPrinter implements Node.Visitor<String> {
    String stringify(Node root) {
        return root.accept(this);
    }

    @Override
    public String visit(Node.Binary node) {
        return "( " + stringify(node.left) + " " + node.operator.lexeme + " " + stringify(node.right)  + " )";
    }

    @Override
    public String visit(Node.Grouping node) {
        return "(" + stringify(node.expression) + ")";
    }

    @Override
    public String visit(Node.Literal node) {
        if (node.value == null) return "nil";
        if (node.value instanceof String) return String.format("\"%s\"", node.value);
        return node.value.toString();
    }

    @Override
    public String visit(Node.Unary node) {
        return "( " + node.operator.lexeme + " " + stringify(node.right) + ")";
    }
}
