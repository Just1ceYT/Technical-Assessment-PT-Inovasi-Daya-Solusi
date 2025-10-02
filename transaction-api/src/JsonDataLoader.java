import java.io.*;
import java.nio.file.*;
import java.util.*;

public class JsonDataLoader {
    public static List<Transaction> loadTransactionsFromJson() {
        List<Transaction> transactions = new ArrayList<>();

        try {
            // Read the JSON file
            String jsonContent = new String(Files.readAllBytes(Paths.get("viewData.json")));

            // Simple JSON parsing
            parseJsonAndCreateTransactions(jsonContent, transactions);

            System.out.println("✅ Loaded " + transactions.size() + " transactions from JSON file");
        } catch (IOException e) {
            System.err.println("❌ Error reading JSON file: " + e.getMessage());
        }

        return transactions;
    }

    public static List<Status> loadStatusFromJson() {
        List<Status> statusList = new ArrayList<>();

        try {
            String jsonContent = new String(Files.readAllBytes(Paths.get("viewData.json")));
            parseJsonAndCreateStatus(jsonContent, statusList);

            System.out.println("✅ Loaded " + statusList.size() + " status types from JSON file");
        } catch (IOException e) {
            System.err.println("❌ Error reading JSON file: " + e.getMessage());
        }

        return statusList;
    }

    private static void parseJsonAndCreateTransactions(String jsonContent, List<Transaction> transactions) {
        try {
            // Find the data array section
            int dataStart = jsonContent.indexOf("\"data\"");
            if (dataStart == -1) {
                System.err.println("❌ 'data' field not found in JSON");
                return;
            }

            int arrayStart = jsonContent.indexOf("[", dataStart);
            int arrayEnd = findMatchingBracket(jsonContent, arrayStart);

            if (arrayStart == -1 || arrayEnd == -1) {
                System.err.println("❌ Could not find data array boundaries");
                return;
            }

            String dataArray = jsonContent.substring(arrayStart, arrayEnd + 1);

            // Split into individual transaction objects
            String[] objects = splitJsonObjects(dataArray);

            for (String obj : objects) {
                if (obj.trim().isEmpty()) continue;

                Transaction transaction = parseTransactionObject(obj);
                if (transaction != null) {
                    transactions.add(transaction);
                }
            }

        } catch (Exception e) {
            System.err.println("❌ Error parsing JSON transactions: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static Transaction parseTransactionObject(String objectJson) {
        try {
            // Extract fields using string operations - try multiple field name variations
            Long id = extractLongValue(objectJson, "id");
            String productID = extractStringValue(objectJson, "productID");
            String productName = extractStringValue(objectJson, "productName");
            String amount = extractStringValue(objectJson, "amount");
            String customerName = extractStringValue(objectJson, "customerName");
            Integer status = extractIntValue(objectJson, "status");
            String transactionDate = extractStringValue(objectJson, "transactionDate");
            String createBy = extractStringValue(objectJson, "createBy");
            String createOn = extractStringValue(objectJson, "createOn");

            // Validate required fields - only require id, productID, and status
            if (id == null || productID == null || status == null) {
                System.err.println("❌ Skipping transaction - missing required fields");
                return null;
            }

            // Use actual values from JSON, no fallbacks for customerName and createBy
            return new Transaction(id, productID, productName, amount, customerName,
                    status, transactionDate, createBy, createOn);

        } catch (Exception e) {
            System.err.println("❌ Error parsing transaction object: " + e.getMessage());
            return null;
        }
    }

    private static void parseJsonAndCreateStatus(String jsonContent, List<Status> statusList) {
        try {
            // Find the status array section
            int statusStart = jsonContent.indexOf("\"status\"");
            if (statusStart == -1) {
                System.err.println("❌ 'status' field not found in JSON");
                return;
            }

            int arrayStart = jsonContent.indexOf("[", statusStart);
            int arrayEnd = findMatchingBracket(jsonContent, arrayStart);

            if (arrayStart == -1 || arrayEnd == -1) {
                System.err.println("❌ Could not find status array boundaries");
                return;
            }

            String statusArray = jsonContent.substring(arrayStart, arrayEnd + 1);

            // Split into individual status objects
            String[] objects = splitJsonObjects(statusArray);

            for (String obj : objects) {
                if (obj.trim().isEmpty()) continue;

                Status status = parseStatusObject(obj);
                if (status != null) {
                    statusList.add(status);
                }
            }

        } catch (Exception e) {
            System.err.println("❌ Error parsing JSON status: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static Status parseStatusObject(String objectJson) {
        try {
            Integer id = extractIntValue(objectJson, "id");
            String name = extractStringValue(objectJson, "name");

            if (id != null && name != null) {
                return new Status(id, name);
            } else {
                System.err.println("❌ Invalid status object - ID: " + id + ", Name: " + name);
            }
            return null;
        } catch (Exception e) {
            System.err.println("❌ Error parsing status object: " + e.getMessage());
            return null;
        }
    }

    private static int findMatchingBracket(String json, int startIndex) {
        int count = 1;
        for (int i = startIndex + 1; i < json.length(); i++) {
            char c = json.charAt(i);
            if (c == '[') count++;
            else if (c == ']') count--;

            if (count == 0) return i;
        }
        return -1;
    }

    private static String[] splitJsonObjects(String arrayJson) {
        List<String> objects = new ArrayList<>();
        int depth = 0;
        StringBuilder currentObject = new StringBuilder();

        for (int i = 0; i < arrayJson.length(); i++) {
            char c = arrayJson.charAt(i);

            if (c == '{') {
                if (depth == 0) {
                    currentObject = new StringBuilder();
                }
                depth++;
            }

            if (depth > 0) {
                currentObject.append(c);
            }

            if (c == '}') {
                depth--;
                if (depth == 0) {
                    objects.add(currentObject.toString());
                }
            }
        }

        return objects.toArray(new String[0]);
    }

    private static Long extractLongValue(String json, String fieldName) {
        try {
            String value = extractRawValue(json, fieldName);
            return value != null ? Long.parseLong(value) : null;
        } catch (NumberFormatException e) {
            System.err.println("❌ Error parsing long field " + fieldName);
            return null;
        }
    }

    private static Integer extractIntValue(String json, String fieldName) {
        try {
            String value = extractRawValue(json, fieldName);
            return value != null ? Integer.parseInt(value) : null;
        } catch (NumberFormatException e) {
            System.err.println("❌ Error parsing int field " + fieldName);
            return null;
        }
    }

    private static String extractStringValue(String json, String fieldName) {
        return extractRawValue(json, fieldName);
    }

    private static String extractRawValue(String json, String fieldName) {
        try {
            // Try different variations of the field name
            String[] fieldVariations = {
                    "\"" + fieldName + "\":",
                    "\"" + fieldName.toLowerCase() + "\":",
                    "\"" + fieldName.toUpperCase() + "\":",
                    "\"" + camelToSnake(fieldName) + "\":",
                    "\"" + fieldName + "\" :",
                    "\"" + fieldName + "\": "
            };

            for (String searchPattern : fieldVariations) {
                int startIndex = json.indexOf(searchPattern);
                if (startIndex != -1) {
                    startIndex += searchPattern.length();

                    // Find the value start (skip whitespace)
                    while (startIndex < json.length() && Character.isWhitespace(json.charAt(startIndex))) {
                        startIndex++;
                    }

                    if (startIndex >= json.length()) continue;

                    char firstChar = json.charAt(startIndex);
                    int endIndex;

                    if (firstChar == '"') {
                        // String value
                        startIndex++; // skip opening quote
                        endIndex = json.indexOf('"', startIndex);
                        if (endIndex == -1) continue;

                        return json.substring(startIndex, endIndex);
                    } else if (Character.isDigit(firstChar) || firstChar == '-') {
                        // Number value
                        endIndex = startIndex + 1;
                        while (endIndex < json.length() &&
                                (Character.isDigit(json.charAt(endIndex)) || json.charAt(endIndex) == '.')) {
                            endIndex++;
                        }
                        return json.substring(startIndex, endIndex);
                    } else if (json.startsWith("null", startIndex)) {
                        return null;
                    }
                }
            }

            // If we get here, none of the variations worked
            System.err.println("❌ Field '" + fieldName + "' not found with any variation");
        } catch (Exception e) {
            System.err.println("❌ Error extracting field " + fieldName);
        }
        return null;
    }

    private static String camelToSnake(String str) {
        // Convert camelCase to snake_case
        return str.replaceAll("([a-z])([A-Z]+)", "$1_$2").toLowerCase();
    }
}