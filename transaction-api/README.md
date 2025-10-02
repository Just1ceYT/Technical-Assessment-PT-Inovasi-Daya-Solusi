# Transaction API Server

A lightweight Java-based HTTP API server that provides transaction data in JSON format, built for the PT Inovasi Daya Solusi Back End Developer Intern Technical Assessment.

## 📋 Project Overview
This application is a RESTful API server that:
- Serves transaction data via HTTP protocol
- Returns JSON responses
- Uses MySQL database for data storage
- Implements Object-Oriented Programming principles in Java

## 🚀 Features
- RESTful API - Clean HTTP endpoints for data access
- Database Integration - MySQL backend with automatic table creation
- JSON Data Support - Full JSON request/response handling
- Multi-format Responses - HTML table view and raw JSON API
- Thread Pooling - Efficient request handling with concurrent processing
- Automatic Data Seeding - Initial data loaded from JSON file

## 🛠 Technology Stack
- Language: Java
- Database: MySQL
- Protocol: HTTP/1.1
- Data Format: JSON
- Architecture: Multi-threaded Server

## 📁 Project Structure
```
transaction-api/
├── src/
│   ├── Main.java              # Server entry point
│   ├── DatabaseService.java   # Database operations
│   ├── Transaction.java       # Transaction entity
│   ├── Status.java            # Status entity
│   ├── JsonDataLoader.java    # JSON file parser
│   └── HtmlResponse.java      # HTML table generator
├── lib/
│   └── mysql-connector-java-8.0.33.jar
├── viewData.json   # Sample data file
├── run.bat         # Windows execution script
├── run.sh          # Linux/Mac execution script
└── README.md
```

## ⚙️ Prerequisites
Before running this application, ensure you have:
### Software Requirements
1. **Java Development Kit (JDK)**
   - Download from [Oracle](https://www.oracle.com/java/technologies/downloads) or [OpenJDK](https://jdk.java.net/25)
   - Verify installation: ```java -version```
2. **MySQL Database Server**
   - Download from [MySQL Official Site](https://www.mysql.com/downloads) or [XAMPP](https://www.apachefriends.org/download.html)
   - Ensure MySQL service is running

## 🗄 Database Setup
1. Start MySQL Server and access MySQL command line
2. Create Database:
   ```
   CREATE DATABASE transaction_db;
   ```
3. Update Database Configuration (if needed):
   - Modify ```DatabaseService.java``` if your MySQL credentials differ:
   ```
   private String url = "jdbc:mysql://localhost:3306/transaction_db";
   private String username = "your_username";  // Default: "root"
   private String password = "your_password";  // Default: ""
   ```

## 🚀 Quick Start
### Using Provided Scripts (Recommended)
Windows Users:
```
run.bat
```
### Manual Setup (Alternative)
1. **Compile the application:**
   ```
   javac -cp "lib/mysql-connector-j-8.0.33.jar" -d out src/*.java
   ```
2. **Run the application:**
   ```
   java -cp "out;lib/mysql-connector-j-8.0.33.jar" Main
   ```

## 📊 API Endpoints
1. **HTML Table View**
   - URL: ```http://localhost:8080/```
   - Method: GET
   - Response: HTML page with formatted transaction table
   - Description: Visual representation of transaction data with summary statistics
2. **JSON API Endpoint**
   - URL: ```http://localhost:8080/api/viewData```
   - Method: GET
   - Response: JSON object containing transactions and status data
   - Content-Type: ```application/json```

## 📋 Expected Response Format
```
{
  "data": [
    {
      "id": 1372,
      "productID": "10001",
      "productName": "Test 1",
      "amount": "1000",
      "customerName": "abc",
      "status": 0,
      "transactionDate": "2022-07-10 11:14:52",
      "createBy": "abc",
      "createOn": "2022-07-10 11:14:52"
    }
  ],
  "status": [
    {
      "id": 0,
      "name": "SUCCESS"
    },
    {
      "id": 1,
      "name": "FAILED"
    }
  ]
}
```

## 🗃 Database Schema
The application automatically creates these tables:
**```transactions``` Table**
| Column        | Type          | Description |
| ------------- | ------------- | ------------- |
| id | BIGINT PRIMARY KEY | Transaction ID |
| productID | VARCHAR(255) | Product identifier |
| productName | VARCHAR(255) | Product name |
| amount | VARCHAR(255) | Transaction amount |
| customerName | VARCHAR(255) | Customer name |
| status | INT | Transaction status (0=Success, 1=Failed) |
| transactionDate | DATETIME | Transaction timestamp |
| createBy | VARCHAR(255) | Creator username |
| createOn | DATETIME | Creation timestamp |

**```status``` Table**
| Column        | Type          | Description |
| ------------- | ------------- | ------------- |
| id | INT PRIMARY KEY | Status code |
| name | VARCHAR(255) | Status name |

## ✅ Success Indicators
When running successfully, you should see:
```
✅ Connected to MySQL database successfully!
✅ Loaded 12 transactions from JSON file
✅ Loaded 2 status types from JSON file
✅ Cleared existing data
✅ Transaction data inserted - Success: 12, Errors: 0
🚀 Transaction API Server started on http://localhost:8080
📊 HTML Table: http://localhost:8080/
📋 Raw Data: http://localhost:8080/api/viewData
🔗 Database: MySQL connected
```

## 🐛 Troubleshooting
### Common Issues:
1. **Database Connection Failed**
   - Verify MySQL service is running: ```sudo systemctl start mysql``` (Linux) or start MySQL service (Windows)
   - Check database credentials in ```DatabaseService.java```
   - Ensure ```transaction_db``` database exists
2. **JSON File Not Found**
   - Ensure ```viewData.json``` is in the project root directory
   - Check file permissions
3. **Port 8080 Already in Use**
   - Change port in ```Main.java```: ```private static final int PORT = 8080```;
   - Kill existing process using port 8080
4. **JDBC Driver Missing**
   - Download MySQL Connector/J and place in ```lib/``` directory
   - Ensure the JAR file is named ```mysql-connector-j-8.0.33.jar```
5. **Class Not Found Errors**
   - Use the provided run scripts
   - Ensure proper package structure is maintained
### Error Messages and Solutions:
| Error | Solution |
| ------------- | ------------- |
| ```ClassNotFoundException: com.mysql.cj.jdbc.Driver``` | MySQL connector JAR missing from classpath |
| ```Could not find or load main class``` | Use run scripts or proper compilation steps |
| ```Access denied for user``` | Check MySQL credentials in ```DatabaseService.java``` |
| ```Communications link failure``` | MySQL service not running |

## 📝 Development Notes
- This application demonstrates core OOP principles: encapsulation, inheritance, and polymorphism
- Implements proper separation of concerns with dedicated classes for different responsibilities
- Uses prepared statements to prevent SQL injection
- Includes proper error handling and logging
- Implements resource cleanup with shutdown hooks

## 👨‍💻 Author Note
Developed as part of the Technical Assessment for Back End Developer Intern position at PT Inovasi Daya Solusi.

## 📄 License
This project is created for assessment purposes.
