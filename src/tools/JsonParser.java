package tools;

import java.util.*;
import java.util.regex.*;

public class JsonParser {

    private String json;
    private int index;

    public JsonParser(String json) {
        this.json = json.trim();
        this.index = 0;
    }

    public Map<String, Object> parse() throws IllegalArgumentException {
        Map<String, Object> result = new LinkedHashMap<>();

        // Skip whitespace and check for opening brace
        skipWhitespace();
        if (json.charAt(index) != '{') {
            throw new IllegalArgumentException("JSON must start with '{'");
        }
        index++;

        skipWhitespace();

        // Empty object case
        if (json.charAt(index) == '}') {
            index++;
            return result;
        }

        // Parse key-value pairs
        while (true) {
            skipWhitespace();

            // Parse key
            if (json.charAt(index) != '"') {
                throw new IllegalArgumentException("Expected '\"' at position " + index);
            }
            String key = parseString();

            skipWhitespace();

            // Parse colon
            if (json.charAt(index) != ':') {
                throw new IllegalArgumentException("Expected ':' at position " + index);
            }
            index++;

            skipWhitespace();

            // Parse value
            Object value = parseValue();
            result.put(key, value);

            skipWhitespace();

            // Check for comma or closing brace
            char currentChar = json.charAt(index);
            if (currentChar == '}') {
                index++;
                break;
            } else if (currentChar == ',') {
                index++;
                // Continue to next key-value pair
            } else {
                throw new IllegalArgumentException("Expected ',' or '}' at position " + index);
            }
        }

        return result;
    }

    private Object parseValue() throws IllegalArgumentException {
        skipWhitespace();

        if (index >= json.length()) {
            throw new IllegalArgumentException("Unexpected end of JSON");
        }

        char currentChar = json.charAt(index);

        // Check for string
        if (currentChar == '"') {
            return parseString();
        }
        // Check for number
        else if (currentChar == '-' || Character.isDigit(currentChar)) {
            return parseNumber();
        }
        // Check for boolean or null
        else if (currentChar == 't' || currentChar == 'f' || currentChar == 'n') {
            return parseLiteral();
        }
        else {
            throw new IllegalArgumentException("Unexpected character '" + currentChar + "' at position " + index);
        }
    }

    private String parseString() throws IllegalArgumentException {
        if (json.charAt(index) != '"') {
            throw new IllegalArgumentException("Expected '\"' at position " + index);
        }
        index++; // Skip opening quote

        StringBuilder sb = new StringBuilder();

        while (index < json.length()) {
            char currentChar = json.charAt(index);

            if (currentChar == '"') {
                index++; // Skip closing quote
                return sb.toString();
            }

            // Handle escape sequences
            if (currentChar == '\\') {
                index++;
                if (index >= json.length()) {
                    throw new IllegalArgumentException("Unexpected end of string");
                }

                char escapedChar = json.charAt(index);
                switch (escapedChar) {
                    case '"':
                    case '\\':
                    case '/':
                        sb.append(escapedChar);
                        break;
                    case 'b':
                        sb.append('\b');
                        break;
                    case 'f':
                        sb.append('\f');
                        break;
                    case 'n':
                        sb.append('\n');
                        break;
                    case 'r':
                        sb.append('\r');
                        break;
                    case 't':
                        sb.append('\t');
                        break;
                    default:
                        throw new IllegalArgumentException("Invalid escape character '" + escapedChar + "'");
                }
                index++;
            } else {
                sb.append(currentChar);
                index++;
            }
        }

        throw new IllegalArgumentException("Unterminated string");
    }

    private Object parseNumber() throws IllegalArgumentException {
        int startIndex = index;

        // Parse integer part
        if (json.charAt(index) == '-') {
            index++;
        }

        // Parse digits
        while (index < json.length() && Character.isDigit(json.charAt(index))) {
            index++;
        }

        // Parse decimal part
        if (index < json.length() && json.charAt(index) == '.') {
            index++;
            while (index < json.length() && Character.isDigit(json.charAt(index))) {
                index++;
            }
        }

        // Parse exponent part
        if (index < json.length() && (json.charAt(index) == 'e' || json.charAt(index) == 'E')) {
            index++;
            if (index < json.length() && (json.charAt(index) == '+' || json.charAt(index) == '-')) {
                index++;
            }
            while (index < json.length() && Character.isDigit(json.charAt(index))) {
                index++;
            }
        }

        String numberStr = json.substring(startIndex, index);

        try {
            if (numberStr.contains(".") || numberStr.contains("e") || numberStr.contains("E")) {
                return Double.parseDouble(numberStr);
            } else {
                return Long.parseLong(numberStr);
            }
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid number format: " + numberStr);
        }
    }

    private Object parseLiteral() throws IllegalArgumentException {
        if (json.startsWith("true", index)) {
            index += 4;
            return true;
        } else if (json.startsWith("false", index)) {
            index += 5;
            return false;
        } else if (json.startsWith("null", index)) {
            index += 4;
            return null;
        } else {
            throw new IllegalArgumentException("Expected true, false, or null at position " + index);
        }
    }

    private void skipWhitespace() {
        while (index < json.length() && Character.isWhitespace(json.charAt(index))) {
            index++;
        }
    }

}