import tools.JsonParser;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Map;

public class Main {
    public static void main(String[] args) throws IOException {
        if (args.length == 0) {
            System.out.println("No argument provided");
            return;
        }
        String command = args[0];
        String filePath;
        if(command.equals("jsonParser")) {
            filePath = args[1];
            String jsonString = readTextasJson(filePath);
            try {
                JsonParser parser = new JsonParser(jsonString);
                Map<String, Object> result = parser.parse();

                System.out.println("Parsed JSON object:");
                for (Map.Entry<String, Object> entry : result.entrySet()) {
                    System.out.println(entry.getKey() + " (" +
                            (entry.getValue() == null ? "null" : entry.getValue().getClass().getSimpleName()) +
                            "): " + entry.getValue());
                }

            } catch (IllegalArgumentException e) {
                System.err.println("Error parsing JSON: " + e.getMessage());
            }
        }
    }

    public static String readTextasJson(String filePath) {
        StringBuilder content = new StringBuilder();

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line).append("\n");
            }
        } catch (FileNotFoundException e) {
            System.err.println("File not found: " + filePath);
            return "";
        } catch (IOException e) {
            System.err.println("Error reading file: " + e.getMessage());
            return "";
        }

        return content.toString().trim();
    }
}