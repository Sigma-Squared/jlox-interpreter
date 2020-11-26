package com.craftinginterpreters.lox;

import java.util.EnumSet;
import java.util.List;

import static com.craftinginterpreters.lox.TokenType.*;

public class Parser {
    private static class ParseError extends RuntimeException {};
    private static final EnumSet<TokenType> StatementBeginToken = EnumSet.of(CLASS, FUN, VAR, FOR, IF, WHILE, PRINT, RETURN);

    private final List<Token> tokens;
    private int current = 0;

    Parser(List<Token> tokens) {
        this.tokens = tokens;
    }

    Node parse() {
        try {
            return expression();
        } catch (ParseError error) {
            return null;
        }
    }

    /*
    Helper functions
    */
    private boolean isAtEnd() {
        return peek().type == EOF;
    }

    private Token peek() {
        return tokens.get(current);
    }

    private Token previous() {
        return tokens.get(current - 1);
    }

    private boolean isType(TokenType tokenType) {
        if (isAtEnd()) return false;
        return peek().type == tokenType;
    }

    private Token advance() {
        if (!isAtEnd()) current++;
        return previous();
    }

    private boolean matches(TokenType... tokenTypes) {
        for (TokenType type: tokenTypes) {
            if (isType(type)) {
                advance();
                return true;
            }
        }
        return false;
    }

    /*
    Recursive descent parsing implementing the following grammars,

    expression     → equality ;
    equality       → comparison ( ( "!=" | "==" ) comparison )* ;
    comparison     → term ( ( ">" | ">=" | "<" | "<=" ) term )* ;
    term           → factor ( ( "-" | "+" ) factor )* ;
    factor         → unary ( ( "/" | "*" ) unary )* ;
    unary          → ( "!" | "-" ) unary
                   | primary ;
    primary        → NUMBER | STRING | "true" | "false" | "nil"
                   | "(" expression ")" ;
     */
    private Node expression() {
        return equality();
    }

    private Node equality() {
        Node left = comparison();

        while (matches(EQUAL_EQUAL, BANG_EQUAL)) {
            Token operator = previous();
            Node right = comparison();
            left = new Node.Binary(left, operator, right);
        }

        return left;
    }

    private Node comparison() {
        Node left = term();

        while (matches(GREATER, GREATER_EQUAL, LESS, LESS_EQUAL)) {
            Token operator = previous();
            Node right = term();
            left = new Node.Binary(left, operator, right);
        }

        return left;
    }

    private Node term() {
        Node left = factor();

        while (matches(PLUS, MINUS)) {
            Token operator = previous();
            Node right = factor();
            left = new Node.Binary(left, operator, right);
        }

        return left;
    }

    private Node factor() {
        Node left = unary();

        while (matches(SLASH, STAR)) {
            Token operator = previous();
            Node right = unary();
            left = new Node.Binary(left, operator, right);
        }

        return left;
    }

    private Node unary() {
        if (matches(BANG, MINUS)) {
            Token operator = previous();
            Node right = unary();
            return new Node.Unary(operator, right);
        }

        return primary();
    }

    private Node primary() {
        if (matches(FALSE)) return new Node.Literal(false);
        if (matches(TRUE)) return new Node.Literal(true);
        if (matches(NIL)) return new Node.Literal(null);

        if (matches(NUMBER, STRING)) {
            return new Node.Literal(previous().literal);
        }

        if (matches(LEFT_PAREN)) {
            Node node = expression();
            consume(RIGHT_PAREN, "Expect ')' after (<Expression>");
            return new Node.Grouping(node);
        }

        throw createParseError(peek(), "Does not start valid expression or statement.");
    }

    /*
    Error handling and recovery
    */
    private Token consume(TokenType tokenType, String message) {
        if (isType(tokenType)) return advance();
        throw createParseError(peek(), message);
    }

    private ParseError createParseError(Token token, String message) {
        Lox.error(token, message);
        return new ParseError();
    }

    private void synchronize() {
        // Returns the parser to a good state after encountering an unexpected token
        advance();

        while (!isAtEnd()) {
            if (previous().type == SEMICOLON) return;
            if (StatementBeginToken.contains(peek().type)) return;

            advance();
        }
    }
}
