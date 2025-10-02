import java.sql.*;
import java.util.*;

public class DatabaseService {
    private Connection connection;
    private String url = "jdbc:mysql://localhost:3306/transaction_db";
    private String username = "root";
    private String password = "";

    public DatabaseService() {
        initializeDatabase();
    }

    private void initializeDatabase() {
        try {
            // Load MySQL JDBC Driver
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Create connection
            connection = DriverManager.getConnection(url, username, password);
            System.out.println("✅ Connected to MySQL database successfully!");

            // Create tables if they don't exist
            createTables();

        } catch (Exception e) {
            System.err.println("❌ Database connection failed: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void createTables() {
        try {
            Statement stmt = connection.createStatement();

            // Create status table
            String createStatusTable = "CREATE TABLE IF NOT EXISTS status (" +
                    "id INT PRIMARY KEY, " +
                    "name VARCHAR(255) NOT NULL)";
            stmt.execute(createStatusTable);

            // Create transactions table
            String createTransactionTable = "CREATE TABLE IF NOT EXISTS transactions (" +
                    "id BIGINT PRIMARY KEY, " +
                    "productID VARCHAR(255), " +
                    "productName VARCHAR(255), " +
                    "amount VARCHAR(255), " +
                    "customerName VARCHAR(255), " +
                    "status INT, " +
                    "transactionDate DATETIME, " +
                    "createBy VARCHAR(255), " +
                    "createOn DATETIME)";
            stmt.execute(createTransactionTable);

            // Clear existing data and insert fresh data from JSON
            clearAndInsertDataFromJson();

            stmt.close();

        } catch (SQLException e) {
            System.err.println("Error creating tables: " + e.getMessage());
        }
    }

    private void clearAndInsertDataFromJson() {
        try {
            // Load data from JSON file
            List<Transaction> transactions = JsonDataLoader.loadTransactionsFromJson();
            List<Status> statusList = JsonDataLoader.loadStatusFromJson();

            // Clear existing data
            Statement clearStmt = connection.createStatement();
            clearStmt.execute("DELETE FROM transactions");
            clearStmt.execute("DELETE FROM status");
            clearStmt.close();

            System.out.println("✅ Cleared existing data");

            // Insert status data - FIXED: Insert actual status data from JSON
            if (!statusList.isEmpty()) {
                PreparedStatement statusStmt = connection.prepareStatement(
                        "INSERT INTO status (id, name) VALUES (?, ?)"
                );

                for (Status status : statusList) {
                    try {
                        statusStmt.setInt(1, status.getId());
                        statusStmt.setString(2, status.getName());
                        statusStmt.executeUpdate();
                    } catch (SQLException e) {
                        System.err.println("❌ Error inserting status ID " + status.getId() + ": " + e.getMessage());
                    }
                }
                statusStmt.close();
            } else {
                System.err.println("⚠️ No status data found in JSON, inserting default status values");
                PreparedStatement defaultStatusStmt = connection.prepareStatement(
                        "INSERT INTO status (id, name) VALUES (?, ?)"
                );
                defaultStatusStmt.setInt(1, 0);
                defaultStatusStmt.setString(2, "SUCCESS");
                defaultStatusStmt.executeUpdate();

                defaultStatusStmt.setInt(1, 1);
                defaultStatusStmt.setString(2, "FAILED");
                defaultStatusStmt.executeUpdate();

                defaultStatusStmt.close();
                System.out.println("✅ Inserted default status records");
            }

            // Insert transaction data (existing code remains the same)
            if (transactions.isEmpty()) {
                System.err.println("❌ No transaction data loaded from JSON!");
            } else {
                int successCount = 0;
                int errorCount = 0;

                PreparedStatement transactionStmt = connection.prepareStatement(
                        "INSERT INTO transactions (id, productID, productName, amount, customerName, status, transactionDate, createBy, createOn) " +
                                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)"
                );

                for (Transaction t : transactions) {
                    try {
                        transactionStmt.setLong(1, t.getId());
                        transactionStmt.setString(2, t.getProductID());
                        transactionStmt.setString(3, t.getProductName());
                        transactionStmt.setString(4, t.getAmount());
                        transactionStmt.setString(5, t.getCustomerName());
                        transactionStmt.setInt(6, t.getStatus());

                        // Handle datetime fields
                        if (t.getTransactionDate() != null && !t.getTransactionDate().isEmpty()) {
                            transactionStmt.setTimestamp(7, Timestamp.valueOf(t.getTransactionDate()));
                        } else {
                            transactionStmt.setNull(7, Types.TIMESTAMP);
                        }

                        transactionStmt.setString(8, t.getCreateBy());

                        if (t.getCreateOn() != null && !t.getCreateOn().isEmpty()) {
                            transactionStmt.setTimestamp(9, Timestamp.valueOf(t.getCreateOn()));
                        } else {
                            transactionStmt.setNull(9, Types.TIMESTAMP);
                        }

                        transactionStmt.executeUpdate();
                        successCount++;

                    } catch (SQLException e) {
                        System.err.println("❌ Error inserting transaction ID " + t.getId() + ": " + e.getMessage());
                        errorCount++;
                    }
                }

                transactionStmt.close();
                System.out.println("✅ Transaction data inserted - Success: " + successCount + ", Errors: " + errorCount);
            }

        } catch (SQLException e) {
            System.err.println("Error inserting data from JSON: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public List<Transaction> getAllTransactions() {
        List<Transaction> transactions = new ArrayList<>();
        try {
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM transactions ORDER BY id");

            while (rs.next()) {
                Transaction transaction = new Transaction(
                        rs.getLong("id"),
                        rs.getString("productID"),
                        rs.getString("productName"),
                        rs.getString("amount"),
                        rs.getString("customerName"),
                        rs.getInt("status"),
                        formatTimestamp(rs.getTimestamp("transactionDate")),
                        rs.getString("createBy"),
                        formatTimestamp(rs.getTimestamp("createOn"))
                );
                transactions.add(transaction);
            }

            rs.close();
            stmt.close();

        } catch (SQLException e) {
            System.err.println("Error fetching transactions: " + e.getMessage());
        }
        return transactions;
    }

    public Map<Integer, String> getStatusMap() {
        Map<Integer, String> statusMap = new HashMap<>();
        try {
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM status");

            while (rs.next()) {
                statusMap.put(rs.getInt("id"), rs.getString("name"));
            }

            rs.close();
            stmt.close();

        } catch (SQLException e) {
            System.err.println("Error fetching status map: " + e.getMessage());
        }
        return statusMap;
    }

    private String formatTimestamp(Timestamp timestamp) {
        if (timestamp == null) return null;
        return timestamp.toString().substring(0, 19); // Format as "yyyy-MM-dd HH:mm:ss"
    }

    public List<Status> getAllStatus() {
        List<Status> statusList = new ArrayList<>();
        try {
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM status");

            while (rs.next()) {
                Status status = new Status(
                        rs.getInt("id"),
                        rs.getString("name")
                );
                statusList.add(status);
            }

            rs.close();
            stmt.close();

        } catch (SQLException e) {
            System.err.println("Error fetching status: " + e.getMessage());
        }
        return statusList;
    }

    public void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("✅ Database connection closed");
            }
        } catch (SQLException e) {
            System.err.println("Error closing connection: " + e.getMessage());
        }
    }
}