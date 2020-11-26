package com.craftinginterpreters.lox;

import java.util.List;

abstract class Node {
    interface Visitor<R> {
        //R visit(Assign node);
        R visit(Binary node);
        //R visit(Call node);
        //R visit(Get node);
        R visit(Grouping node);
        R visit(Literal node);
        //R visit(Logical node);
        //R visit(Set node);
        //R visit(Super node);
        //R visit(This node);
        R visit(Unary node);
        //R visit(Variable node);
    }

//    static class Assign extends Node {
//        Assign(Token name, Node value) {
//            this.name = name;
//            this.value = value;
//        }
//
//        @Override
//        <R> R accept(Visitor<R> visitor) {
//            return visitor.visit(this);
//        }
//
//        final Token name;
//        final Node value;
//    }

    static class Binary extends Node {
        Binary(Node left, Token operator, Node right) {
            this.left = left;
            this.operator = operator;
            this.right = right;
        }

        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visit(this);
        }

        final Node left;
        final Token operator;
        final Node right;
    }

//    static class Call extends Node {
//        Call(Node callee, Token paren, List<Node> arguments) {
//            this.callee = callee;
//            this.paren = paren;
//            this.arguments = arguments;
//        }
//
//        @Override
//        <R> R accept(Visitor<R> visitor) {
//            return visitor.visit(this);
//        }
//
//        final Node callee;
//        final Token paren;
//        final List<Node> arguments;
//    }
//
//    static class Get extends Node {
//        Get(Node object, Token name) {
//            this.object = object;
//            this.name = name;
//        }
//
//        @Override
//        <R> R accept(Visitor<R> visitor) {
//            return visitor.visit(this);
//        }
//
//        final Node object;
//        final Token name;
//    }

    static class Grouping extends Node {
        Grouping(Node expression) {
            this.expression = expression;
        }

        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visit(this);
        }

        final Node expression;
    }

    static class Literal extends Node {
        Literal(Object value) {
            this.value = value;
        }

        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visit(this);
        }

        final Object value;
    }

//    static class Logical extends Node {
//        Logical(Node left, Token operator, Node right) {
//            this.left = left;
//            this.operator = operator;
//            this.right = right;
//        }
//
//        @Override
//        <R> R accept(Visitor<R> visitor) {
//            return visitor.visit(this);
//        }
//
//        final Node left;
//        final Token operator;
//        final Node right;
//    }
//
//    static class Set extends Node {
//        Set(Node object, Token name, Node value) {
//            this.object = object;
//            this.name = name;
//            this.value = value;
//        }
//
//        @Override
//        <R> R accept(Visitor<R> visitor) {
//            return visitor.visit(this);
//        }
//
//        final Node object;
//        final Token name;
//        final Node value;
//    }
//
//    static class Super extends Node {
//        Super(Token keyword, Token method) {
//            this.keyword = keyword;
//            this.method = method;
//        }
//
//        @Override
//        <R> R accept(Visitor<R> visitor) {
//            return visitor.visit(this);
//        }
//
//        final Token keyword;
//        final Token method;
//    }
//
//    static class This extends Node {
//        This(Token keyword) {
//            this.keyword = keyword;
//        }
//
//        @Override
//        <R> R accept(Visitor<R> visitor) {
//            return visitor.visit(this);
//        }
//
//        final Token keyword;
//    }

    static class Unary extends Node {
        Unary(Token operator, Node right) {
            this.operator = operator;
            this.right = right;
        }

        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visit(this);
        }

        final Token operator;
        final Node right;
    }

//    static class Variable extends Node {
//        Variable(Token name) {
//            this.name = name;
//        }
//
//        @Override
//        <R> R accept(Visitor<R> visitor) {
//            return visitor.visit(this);
//        }
//
//        final Token name;
//    }

    abstract <R> R accept(Visitor<R> visitor);
}