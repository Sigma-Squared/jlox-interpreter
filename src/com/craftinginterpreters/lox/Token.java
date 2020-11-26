package com.craftinginterpreters.lox;

public class Token {
    final TokenType type;
    final String lexeme;
    final Object literal;
    final int line;

    Token(TokenType type, String lexeme, Object literal, int line) {
        this.type = type;
        this.lexeme = lexeme;
        this.literal = literal;
        this.line = line;
    }

    public String toString() {
        if (literal != null)
            return String.format("%s %s %s", type, lexeme, literal);
        return String.format("%s %s", type, lexeme);
    }
}
