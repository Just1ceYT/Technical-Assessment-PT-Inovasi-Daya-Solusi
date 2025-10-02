import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;

public class Main {
    private static final int PORT = 8080;
    private static final int MAX_THREADS = 10;
    private static final ExecutorService threadPool = Executors.newFixedThreadPool(MAX_THREADS);
    private static DatabaseService databaseService;

    public static void main(String[] args) {
        // Initialize database service
        databaseService = new DatabaseService();

        // Add shutdown hook to close database connection
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            if (databaseService != null) {
                databaseService.closeConnection();
            }
            threadPool.shutdown();
        }));

        try {
            ServerSocket serverSocket = new ServerSocket(PORT);
            System.out.println("🚀 Transaction API Server started on http://localhost:" + PORT);
            System.out.println("📋 Raw Data: http://localhost:" + PORT + "/api/viewData");
            System.out.println("🔗 Database: MySQL connected");

            while (true) {
                Socket clientSocket = serverSocket.accept();
                threadPool.submit(() -> handleRequest(clientSocket));
            }
        } catch (IOException e) {
            System.err.println("Server error: " + e.getMessage());
        } finally {
            threadPool.shutdown();
        }
    }

    private static void handleRequest(Socket clientSocket) {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
             OutputStream out = clientSocket.getOutputStream()) {

            // Read request headers
            String requestLine = in.readLine();
            if (requestLine == null) return;

            System.out.println("Processing: " + requestLine);

            // Parse request
            String[] requestParts = requestLine.split(" ");
            if (requestParts.length < 2) {
                sendError(out, 400, "Bad Request");
                return;
            }

            String method = requestParts[0];
            String path = requestParts[1];

            // Validate request
            if (!"GET".equals(method)) {
                sendError(out, 405, "Method Not Allowed");
                return;
            }

            // Route requests
            if ("/".equals(path)) {
                serveHtmlTable(out);
            } else if ("/api/viewData".equals(path)) {
                serveJsonData(out);
            } else {
                sendError(out, 404, "Not Found");
            }
        } catch (IOException e) {
            System.err.println("Request handling error: " + e.getMessage());
        } finally {
            try {
                clientSocket.close();
            } catch (IOException e) {
                System.err.println("Error closing socket: " + e.getMessage());
            }
        }
    }

    private static void serveHtmlTable(OutputStream out) throws IOException {
        try {
            // Get data from database
            List<Transaction> transactions = databaseService.getAllTransactions();
            Map<Integer, String> statusMap = databaseService.getStatusMap(); // Get status mapping from database

            // Generate HTML table using actual status data
            String htmlResponse = HtmlResponse.createHtmlTable(transactions, statusMap);

            String httpResponse = "HTTP/1.1 200 OK\r\n" +
                    "Content-Type: text/html; charset=UTF-8\r\n" +
                    "Content-Length: " + htmlResponse.getBytes("UTF-8").length + "\r\n" +
                    "Connection: close\r\n" +
                    "\r\n" +
                    htmlResponse;

            out.write(httpResponse.getBytes("UTF-8"));
            out.flush();
        } catch (Exception e) {
            sendError(out, 500, "Internal Server Error: " + e.getMessage());
        }
    }

    private static void serveJsonData(OutputStream out) throws IOException {
        try {
            // Get data from database
            List<Transaction> transactions = databaseService.getAllTransactions();
            List<Status> statusList = databaseService.getAllStatus();

            // Create JSON response
            StringBuilder json = new StringBuilder();
            json.append("{\"data\":[");

            for (int i = 0; i < transactions.size(); i++) {
                Transaction t = transactions.get(i);
                if (i > 0) json.append(",");
                json.append("{")
                        .append("\"id\":").append(t.getId()).append(",")
                        .append("\"productID\":\"").append(escapeJson(t.getProductID())).append("\",")
                        .append("\"productName\":\"").append(escapeJson(t.getProductName())).append("\",")
                        .append("\"amount\":\"").append(escapeJson(t.getAmount())).append("\",")
                        .append("\"customerName\":").append(t.getCustomerName() != null ? "\"" + escapeJson(t.getCustomerName()) + "\"" : "null").append(",")
                        .append("\"status\":").append(t.getStatus()).append(",")
                        .append("\"transactionDate\":").append(t.getTransactionDate() != null ? "\"" + escapeJson(t.getTransactionDate()) + "\"" : "null").append(",")
                        .append("\"createBy\":").append(t.getCreateBy() != null ? "\"" + escapeJson(t.getCreateBy()) + "\"" : "null").append(",")
                        .append("\"createOn\":").append(t.getCreateOn() != null ? "\"" + escapeJson(t.getCreateOn()) + "\"" : "null")
                        .append("}");
            }

            json.append("],\"status\":[");

            for (int i = 0; i < statusList.size(); i++) {
                Status s = statusList.get(i);
                if (i > 0) json.append(",");
                json.append("{")
                        .append("\"id\":").append(s.getId()).append(",")
                        .append("\"name\":\"").append(escapeJson(s.getName())).append("\"")
                        .append("}");
            }

            json.append("]}");

            String jsonResponse = json.toString();

            String httpResponse = "HTTP/1.1 200 OK\r\n" +
                    "Content-Type: application/json; charset=UTF-8\r\n" +
                    "Content-Length: " + jsonResponse.getBytes("UTF-8").length + "\r\n" +
                    "Connection: close\r\n" +
                    "\r\n" +
                    jsonResponse;

            out.write(httpResponse.getBytes("UTF-8"));
            out.flush();
        } catch (Exception e) {
            sendError(out, 500, "Internal Server Error: " + e.getMessage());
        }
    }

    private static void sendError(OutputStream out, int statusCode, String message) throws IOException {
        String response = "HTTP/1.1 " + statusCode + " " + message + "\r\n" +
                "Content-Type: text/plain\r\n" +
                "Connection: close\r\n" +
                "\r\n" +
                message;
        out.write(response.getBytes("UTF-8"));
    }

    private static String escapeJson(String str) {
        if (str == null) return "";
        return str.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\b", "\\b")
                .replace("\f", "\\f")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }
}