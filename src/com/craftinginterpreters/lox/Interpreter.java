package com.craftinginterpreters.lox;

public class Interpreter implements Node.Visitor<Object> {
    void interpret(Node root) {
        try {
            Object result = evaluate(root);
            String stringified;
            if (result instanceof String) {
                stringified = String.format("\"%s\"", result);
            } else {
                stringified = stringify(result);
            }
            System.out.println(stringified);
        } catch (RuntimeError error) {
            Lox.runtimeError(error);
        }
    }

    private Object evaluate(Node root) {
        return root.accept(this);
    }

    private String stringify(Object value) {
        if (value == null) return "nil";
        if (value instanceof Double) {
            String text = value.toString();
            if (text.endsWith(".0")) {
                text = text.substring(0, text.length() - 2);
            }
            return text;
        }
        return value.toString();
    }

    @Override
    public Object visit(Node.Binary node) {
        final Object left = evaluate(node.left);
        final Object right = evaluate(node.right);
        return switch (node.operator.type) {
            case EQUAL_EQUAL -> operatorEquals(left, right);
            case BANG_EQUAL -> !operatorEquals(left, right);
            case PLUS -> operatorPlus(node.operator, left, right);
            case STAR -> operatorStar(node.operator, left, right);
            case GREATER -> tryCastNumber(node.operator, left) > tryCastNumber(node.operator, right);
            case GREATER_EQUAL -> tryCastNumber(node.operator, left) >= tryCastNumber(node.operator, right);
            case LESS -> tryCastNumber(node.operator, left) < tryCastNumber(node.operator, right);
            case LESS_EQUAL -> tryCastNumber(node.operator, left) <= tryCastNumber(node.operator, right);
            case MINUS -> tryCastNumber(node.operator, left) - tryCastNumber(node.operator, right);
            case SLASH -> tryCastNumber(node.operator, left) / tryCastNumber(node.operator, right);
            default -> null;
        };
    }

    private boolean operatorEquals(Object left, Object right) {
        if (left == null && right == null) return true;
        if (left == null) return false;
        return left.equals(right);
    }

    private Object operatorPlus(Token operator, Object left, Object right) {
        if (canBeNumeric(left) && canBeNumeric(right)) {
            return toNumeric(operator, left) + toNumeric(operator, right);
        }
        if (left instanceof String || right instanceof String) {
            return stringify(left) + stringify(right);
        }
        throw new RuntimeError(operator, String.format("Operator `+` is not valid for %s+%s", getTypeName(left), getTypeName(right)));
    }

    private Object operatorStar(Token operator, Object left, Object right) {
        if (canBeNumeric(left) && canBeNumeric(right)) {
            return toNumeric(operator, left) * toNumeric(operator, right);
        }
        if ((canBeNumeric(left) || canBeNumeric(right)) && (left instanceof String || right instanceof String)) {
            int repeat;
            String base;
            if (canBeNumeric(left)) {
                repeat = (int)toNumeric(operator, left);
                base = (String)right;
            } else {
                repeat =  (int)toNumeric(operator, right);
                base = (String)left;
            }
            return base.repeat(repeat);
        }
        throw new RuntimeError(operator, String.format("Operator `*` is not valid for %s*%s", getTypeName(left), getTypeName(right)));
    }

    private boolean toBoolean(Object value) {
        if (value == null) return false;
        if (value instanceof Boolean) return (boolean)value;
        if (value instanceof Double) return ((double)value) != 0;
        if (value instanceof String) return !value.equals("");
        return false;
    }

    private boolean canBeNumeric(Object value) {
        return (value instanceof Double || value instanceof Boolean);
    }

    private double toNumeric(Token source, Object value) {
        if (value instanceof Double) return (double)value;
        if (value instanceof Boolean) return ((boolean)value) ? 1.0 : 0.0;
        throw new RuntimeError(source, String.format("Cannot implicitly convert %s to a number", getTypeName(value)));
    }

    @Override
    public Object visit(Node.Grouping node) {
        return evaluate(node.expression);
    }

    @Override
    public Object visit(Node.Literal node) {
        return node.value;
    }

    @Override
    public Object visit(Node.Unary node) {
        final Object right = evaluate(node.right);
        return switch (node.operator.type) {
            case BANG -> !toBoolean(right);
            case MINUS -> -tryCastNumber(node.operator, right);
            default -> null;
        };
    }

    private double tryCastNumber(Token source, Object value) {
        if (canBeNumeric(value)) return toNumeric(source, value);
        throw new RuntimeError(source, String.format("Expected number, got %s instead.", getTypeName(value)));
    }

    private String getTypeName(Object value) {
        if (value == null) {
            return "nil";
        } else if (value instanceof Double) {
            return "number";
        } else if (value instanceof String) {
            return "string";
        } else if (value instanceof Boolean) {
            return "boolean";
        } else {
            return "unknown";
        }
    }
}
