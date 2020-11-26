package com.craftinginterpreters.lox;

public class AstPrinter implements Node.Visitor<String> {
    String print(Node root) {
        return root.accept(this);
    }

    private String parenthesize(String name, Node... nodes) {
        StringBuilder builder = new StringBuilder();
        builder.append("(").append(name);
        for (Node node: nodes) {
            builder.append(" ");
            builder.append(print(node));
        }
        builder.append(")");

        return builder.toString();
    }

    @Override
    public String visit(Node.Binary node) {
        return parenthesize(node.operator.lexeme,
                node.left, node.right);
    }

    @Override
    public String visit(Node.Grouping node) {
        return parenthesize("group", node.expression);
    }

    @Override
    public String visit(Node.Literal node) {
        if (node.value == null) return "nil";
        return node.value.toString();
    }

    @Override
    public String visit(Node.Unary node) {
        return parenthesize(node.operator.lexeme, node.right);
    }
}
