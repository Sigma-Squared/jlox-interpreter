package com.craftinginterpreters.lox;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class Lox {
    static boolean hadError = false;
    static boolean hadRuntimeError = false;

    private static final Interpreter interpreter = new Interpreter();
    private static final ASTPrinter ASTPrinter = new ASTPrinter();

    public static void main(String[] args) throws IOException {
        if (args.length > 1) {
            System.out.println("Usage: jlox [script]");
            System.exit(64);
        } else if (args.length == 1) {
            runFile(args[0]);
        } else {
            runPrompt();
        }
    }

    private static void runFile(String path) throws IOException {
        byte[] bytes = Files.readAllBytes(Paths.get(path));
        run(new String(bytes, Charset.defaultCharset()));
        if (hadError) System.exit(65);
        if (hadRuntimeError) System.exit(70);
    }

    private static void runPrompt() throws IOException {
        InputStreamReader input = new InputStreamReader(System.in);
        BufferedReader reader = new BufferedReader(input);

        while (true) {
            System.out.print("> ");
            String line = reader.readLine();
            if (line == null) break;
            run(line);
            hadError = false;
        }
    }

    private static void run(String script) {
        Tokenizer tokenizer = new Tokenizer(script);
        List<Token> tokens = tokenizer.tokenize();

        Parser parser = new Parser(tokens);
        Node root = parser.parse();

        if (hadError) return; // Stop if an error has occurred previously.

        System.out.println(ASTPrinter.stringify(root));

        interpreter.interpret(root);
        System.err.flush();
        System.out.flush();
    }

    static void error(int line, String message) {
        report(line, "", message);
    }

    static void error(Token token, String message) {
        if (token.type == TokenType.EOF) {
            report(token.line, "at end", message);
        } else {
            report(token.line, "at '" + token.lexeme + "'", message);
        }
    }

    private static void report(int line, String where, String message) {
        System.out.printf("\u001B[31m[line %d]: Error %s: %s%n\u001B[0m", line, where, message);
        hadError = true;
    }

    static void runtimeError(RuntimeError error) {
        System.out.printf("\u001B[31m[line %d]: %s\u001B[0m\n", error.token.line, error.getMessage());
        hadRuntimeError = true;
    }
}